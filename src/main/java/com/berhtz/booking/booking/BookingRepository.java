package com.berhtz.booking.booking;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT count_booking_clients()", nativeQuery = true)
    Integer countBookingClients();

    List<Booking> findByDate(LocalDate date);

    List<Booking> findByTime(LocalTime time);

    Optional<Booking> findByDateAndTime(LocalDate date, LocalTime time);

    List<Booking> findByClients_Id(Long clientId);
}
