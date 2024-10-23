package com.berhtz.booking.booking;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    Long clientId;
    Long bookingId;
    LocalDateTime dateTime;
}
