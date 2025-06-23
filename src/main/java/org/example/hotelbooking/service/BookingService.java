package org.example.hotelbooking.service;

import org.example.hotelbooking.dto.BookingDTO;
import org.example.hotelbooking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDTO dto, String userEmail);
    List<Booking> findBookingsByRoomId(Long roomId);
    List<Booking> findBookingsByUserId(Long userId);
}
