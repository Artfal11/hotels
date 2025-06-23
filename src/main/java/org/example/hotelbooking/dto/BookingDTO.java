package org.example.hotelbooking.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class BookingDTO {
    private Long roomId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;
}
