package com.berhtz.booking.holiday;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.berhtz.booking.booking.Booking;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "holidays")
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate holidayDate;
    private String description;

    @OneToMany(mappedBy = "holiday", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();
}
