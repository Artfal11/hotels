package org.example.hotelbooking.controller;

import org.example.hotelbooking.dto.HotelCreateDTO;
import org.example.hotelbooking.model.User;
import org.example.hotelbooking.model.UserRole;
import org.example.hotelbooking.service.BookingService;
import org.example.hotelbooking.service.HotelService;
import org.example.hotelbooking.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final HotelService hotelService;
    private final UserService userService;
    private final BookingService bookingService;

    public ProfileController(HotelService hotelService, UserService userService, BookingService bookingService) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);

        if (user.getRole().equals(UserRole.OWNER)) {
            model.addAttribute("hotels", hotelService.getHotelsByOwner(user));
            model.addAttribute("newHotel", new HotelCreateDTO());
            return "profile/profile-owner";
        } else {
            model.addAttribute("bookings", bookingService.findBookingsByUserId(user.getId()));
            return "profile/profile-user";
        }
    }
}
