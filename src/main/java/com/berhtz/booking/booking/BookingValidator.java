package com.berhtz.booking.booking;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.berhtz.booking.config.Config;

@Component
public class BookingValidator {
    @Autowired
    private Config config;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookingCustomRepository bookingCustomRepository;

     public void validateBooking(Long clientId, LocalDateTime dateTime) {
        validateClientBookingLimit(clientId, dateTime.toLocalDate());
        validateWeekend(dateTime);
        validateWorkingHours(dateTime);
    }

    private void validateClientBookingLimit(Long clientId, LocalDate date) {
        if (bookingCustomRepository.countClientBookingsOnDate(clientId, date) >= config.getMaxBookingsForClient()) {
            throw new IllegalArgumentException("Exceeded number of bookings in a day");
        }
    }

    private void validateWeekend(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        if (config.getWeekends().contains(dayOfWeek.toString())) {
            throw new IllegalArgumentException("Can't reserve on weekends");
        }
    }

    private void validateWorkingHours(LocalDateTime dateTime) {
        List<String> workingTime = isHoliday(dateTime.toLocalDate()) 
        ? config.getWorkingTimeHoliday() 
        : config.getWorkingTimeRegular();

        if (!workingTime.contains(dateTime.toLocalTime().toString())) {
            throw new IllegalArgumentException("Can't reserve out of working hours");
        }
    }

    private boolean isHoliday(LocalDate date) {
        return config.getHolidays().contains(date);
    }
}
