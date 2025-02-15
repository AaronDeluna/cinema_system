package org.javaacademy.cinema.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.entity.Ticket;
import org.javaacademy.cinema.exception.DataMappingException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketRepository {
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

    public Ticket save(Ticket ticket) {
        Integer ticketId = jdbcTemplate.queryForObject(
                SAVE_TICKET_SQL,
                Integer.class,
                ticket.getPlace().getId(),
                ticket.getSession().getId(),
                false
        );
        ticket.setId(ticketId);
        return ticket;
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

    public List<Ticket> findAllByPaymentStatus(boolean isPaid) {
        try {
            return jdbcTemplate.query(FIND_TICKET_BY_PAID_STATUS, this::toTicket, isPaid);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при поиске билетов по статусу: {}", e.getMessage());
            return emptyList();
        }
    }

    public List<Ticket> findAllBySessionId(int sessionId) {
        try {
            return jdbcTemplate.query(FIND_ALL_TICKET_BY_SESSION_ID, this::toTicket, sessionId);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при поиске билета по sessionId: {}", sessionId);
            return emptyList();
        }
    }

    private Ticket toTicket(ResultSet rs, int rowNum) {
        try {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            if (rs.getString("session_id") != null) {
                int sessionId = Integer.parseInt(rs.getString("session_id"));
                ticket.setSession(sessionRepository.findById(sessionId).orElse(null));
            }
            if (rs.getString("place_id") != null) {
                int placeId = Integer.parseInt(rs.getString("place_id"));
                ticket.setPlace(placeRepository.findById(placeId).orElse(null));
            }
            ticket.setPaid(rs.getBoolean("paid"));
            return ticket;
        } catch (SQLException e) {
            throw new DataMappingException(e);
        }
    }
}
