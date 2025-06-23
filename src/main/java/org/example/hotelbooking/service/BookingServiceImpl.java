package org.example.hotelbooking.service;

import org.example.hotelbooking.dto.BookingDTO;
import org.example.hotelbooking.dto.EmailNotificationRequest;
import org.example.hotelbooking.model.Booking;
import org.example.hotelbooking.model.Room;
import org.example.hotelbooking.model.User;
import org.example.hotelbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final NotificationService notificationService;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomService roomService, UserService userService, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public Booking createBooking(BookingDTO dto, String userEmail) {
        Room room = roomService.findById(dto.getRoomId());
        User user = userService.findByEmail(userEmail);

        long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (days <= 0) {
            throw new IllegalArgumentException("Неверный диапазон дат");
        }

        BigDecimal total = room.getPrice().multiply(BigDecimal.valueOf(days));

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setTotalPrice(total);

        Booking savedBooking = bookingRepository.save(booking);

        logger.debug("Захожу в функцию sendBookingEmail");

        System.out.println("Захожу в функцию sendBookingEmail");

        sendBookingConfirmationEmail(userEmail, room, savedBooking);

        return savedBooking;
    }

    @Override
    public List<Booking> findBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public List<Booking> findBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    private void sendBookingConfirmationEmail(String email, Room room, Booking booking) {
        logger.debug("Зашел в функцию");
        EmailNotificationRequest request = new EmailNotificationRequest();
        request.setEmailTo(email);
        request.setSubject("Уведомление о бронировании");
        request.setTemplateName("booking_confirmation.html");

        Map<String, Object> context = new HashMap<>();
        context.put("roomNumber", 1);
        context.put("checkInDate", booking.getCheckInDate());
        context.put("checkOutDate", booking.getCheckOutDate());
        context.put("totalPrice", booking.getTotalPrice());

        request.setContext(context);

        logger.debug("Захожу в notificationService");
        notificationService.sendEmailNotification(request);
    }
}
