package com.berhtz.booking.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v0/pool/timetable/")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @GetMapping("/all/{date}")
    List<Booking> getAll(@PathVariable LocalDate date) {

        return bookingService.getAllBookings(date);
    }

    @GetMapping("/available/{date}")
    Map<LocalTime, Integer> getAvailable(@PathVariable LocalDate date) {
        // Получение доступных записей на определенную дату

        return bookingService.getAvailableBookings(date);
    }

    @PostMapping("/reserve")
    Long reserve(@RequestBody BookingDto booking) {
        // Добавить запись клиента на определенное время
        Long clientId = booking.getClientId();
        LocalDateTime dateTime = booking.getDateTime();
        System.out.println(clientId + " " + dateTime);

        return bookingService.addBooking(clientId, dateTime);
    }

    @DeleteMapping("/cancel")          
    @ResponseStatus(HttpStatus.NO_CONTENT)               
    ResponseEntity<Void> cancel(@RequestBody BookingDto booking) {
        // Отмена записи клиента на определенное время
        Long clientId = booking.getClientId();
        Long bookingId = booking.getBookingId();

        return bookingService.cancel(clientId, bookingId);
    }
}
