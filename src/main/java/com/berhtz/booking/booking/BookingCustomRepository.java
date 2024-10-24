package com.berhtz.booking.booking;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookingCustomRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int countClientsCountByDateAndTime(LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(DISTINCT bc.client_id) AS client_count " +
                "FROM bookings b " +
                "JOIN booking_clients bc ON b.id = bc.booking_id " +
                "WHERE b.date = ? AND b.time = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, date, time);
    }

    public Integer countClientInBooking(Long bookingId, Long clientId) {
        String sql = "SELECT COUNT(*) " +
                "FROM booking_clients " +
                "WHERE booking_id = ? " +
                "AND client_id = ?;";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookingId, clientId);
        return count;
    }

    public Integer deleteClient(Long bookingId, Long clientId) {
        String sql = "DELETE FROM booking_clients " +
                "WHERE booking_id = ? AND client_id = ?;";

        int count = jdbcTemplate.update(sql, bookingId, clientId);
        // count > 0 значи запись удалена
        return count;
    }

    public Integer countClientBookingsOnDate(Long clientId, LocalDate date) {
        String sql = "SELECT COUNT(*) " +
                "FROM bookings b " +
                "JOIN booking_clients c ON b.id = c.booking_id " +
                "WHERE c.client_id = ? " +
                "AND b.date = ?;";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, clientId, date);
        return count;
    }
}
