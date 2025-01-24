package org.javaacademy.cinema.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.entity.PaymentStatus;
import org.javaacademy.cinema.entity.Ticket;
import org.javaacademy.cinema.exception.NotFoundException;
import org.javaacademy.cinema.util.DbUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketRepository {
    private static final String FIND_BY_ID_SQL = "select * from ticket where id = ?";
    private static final String SAVE_TICKET_SQL = "INSERT INTO ticket (name, description) values (?, ?) RETURNING id";
    private static final String UPDATE_TICKET_PAID_SQL = "update ticket set paid = ? where id = ?";
    private static final String FIND_TICKET_BY_PAID_STATUS = "select * from ticket where paid = ?";
    private final JdbcTemplate jdbcTemplate;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;

    public Optional<Ticket> save(Ticket ticket) {
        try {
            Integer ticketId = jdbcTemplate.queryForObject(
                    SAVE_TICKET_SQL,
                    new Object[] {ticket.getSession(), ticket.getPlace(), ticket.getPaymentStatus().isStatus()},
                    Integer.class
            );
            ticket.setId(ticketId);
            return Optional.of(ticket);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при сохранении: {}", e.getMessage());
            return empty();
        }
    }

    public void updatePurchaseStatusById(int id) {
       Ticket ticket = findById(id)
               .orElseThrow(() -> new NotFoundException("Билет с id: '%s' не найден".formatted(id)));
        if (!ticket.getPaymentStatus().isStatus()) {
            int countRows = jdbcTemplate.update(UPDATE_TICKET_PAID_SQL, ps -> {
                ps.setBoolean(1, true);
                ps.setInt(2, id);
            });
            if (countRows < 1) {
                throw new RuntimeException("Не обновлена ни одна строка!");
            }
        } else {
            throw new RuntimeException("Билет по id: '%s', уже оплачен".formatted(id));
        }
    }

    public Optional<List<Ticket>> findTicketsByPaymentStatus(PaymentStatus status) {
        try {
            List<Ticket> tickets = jdbcTemplate.query(FIND_TICKET_BY_PAID_STATUS, this::toTicket, status.isStatus());
            return Optional.of(tickets);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при поиске билетов по статусу: '{}', {}", status, e.getMessage());
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

    @SneakyThrows
    private Ticket toTicket(ResultSet rs, int rowNum) {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setSession(DbUtils.getEntityById(rs.getString("session_id"), sessionRepository::findById));
        ticket.setPlace(DbUtils.getEntityById(rs.getString("place_id"), placeRepository::findById));
        ticket.setPaymentStatus(PaymentStatus.fromBoolean(rs.getBoolean("paid")));
        return ticket;
    }
}
