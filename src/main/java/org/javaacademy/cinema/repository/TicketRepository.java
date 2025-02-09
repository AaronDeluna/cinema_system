package org.javaacademy.cinema.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.entity.Ticket;
import org.javaacademy.cinema.exception.DataMappingException;
import org.javaacademy.cinema.util.DbUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketRepository {
    private static final String FIND_BY_ID_SQL = "select * from ticket where id = ?";
    private static final String SAVE_TICKET_SQL =
            "insert into ticket (place_id, session_id, paid) values (?, ?, ?) returning id";
    private static final String UPDATE_TICKET_PAID_SQL = "update ticket set paid = ? where id = ? and session_id = ?";
    private static final String FIND_TICKET_BY_PAID_STATUS = "select * from ticket where paid = ?";
    private static final String FIND_ALL_TICKET_BY_SESSION_ID = "select * from ticket where session_id = ?";
    private static final int PARAM_PAID_INDEX = 1;
    private static final int PARAM_ID_INDEX = 2;
    private static final int PARAM_SESSION_ID_INDEX = 3;

    private final JdbcTemplate jdbcTemplate;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;

    public Optional<Ticket> save(Ticket ticket) {
        try {
            Integer ticketId = jdbcTemplate.queryForObject(
                    SAVE_TICKET_SQL,
                    new Object[] {ticket.getPlace().getId(), ticket.getSession().getId(), false},
                    Integer.class
            );
            ticket.setId(ticketId);
            return Optional.of(ticket);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при сохранении: {}", e.getMessage());
            return empty();
        }
    }

    public void updatePurchaseStatusById(int id, int sessionId) {
        int countRows = jdbcTemplate.update(UPDATE_TICKET_PAID_SQL, ps -> {
            ps.setBoolean(PARAM_PAID_INDEX, true);
            ps.setInt(PARAM_ID_INDEX, id);
            ps.setInt(PARAM_SESSION_ID_INDEX, sessionId);
        });
        if (countRows < 1) {
            throw new IllegalStateException("Не обновлена ни одна строка!");
        }
    }

    public Optional<List<Ticket>> findAllByPaymentStatus(boolean isPaid) {
        try {
            List<Ticket> tickets = jdbcTemplate.query(FIND_TICKET_BY_PAID_STATUS, this::toTicket, isPaid);
            return Optional.of(tickets);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при поиске билетов по статусу: {}", e.getMessage());
            return empty();
        }
    }

    public Optional<List<Ticket>> findAllBySessionId(int sessionId) {
        try {
            List<Ticket> tickets = jdbcTemplate.query(FIND_ALL_TICKET_BY_SESSION_ID, this::toTicket, sessionId);
            return Optional.of(tickets);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при поиске билета по sessionId: {}", sessionId);
            return empty();
        }
    }

    public Optional<Ticket> findById(int id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_BY_ID_SQL, this::toTicket, id));
        } catch (EmptyResultDataAccessException e) {
            log.warn("Произошла ошибка при поиске по id {}, {}", id, e.getMessage());
            return empty();
        }
    }

    private Ticket toTicket(ResultSet rs, int rowNum) {
        try {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            ticket.setSession(DbUtils.getEntityById(
                    rs.getString("session_id"), sessionRepository::findById
            ));
            ticket.setPlace(DbUtils.getEntityById(
                    rs.getString("place_id"), placeRepository::findById
            ));
            ticket.setPaid(rs.getBoolean("paid"));
            return ticket;
        } catch (SQLException e) {
            throw new DataMappingException(e);
        }
    }
}
