package org.example.hotelbooking.controller;


import org.example.hotelbooking.dto.BookingDTO;
import org.example.hotelbooking.service.BookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.*;


@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(value = "/ajax", produces = "text/plain")
    @ResponseBody
    public String createBooking(@ModelAttribute BookingDTO bookingDTO,
                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            bookingService.createBooking(bookingDTO, userDetails.getUsername());
            return "Бронирование успешно!";
        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage();
        }

    }
}
