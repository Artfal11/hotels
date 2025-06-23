package org.example.hotelbooking.controller;

import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.service.HotelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final HotelService hotelService;

    public HomeController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/")
    public String showHomePage(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Hotel> hotels;

        if (query != null && !query.trim().isEmpty()) {
            hotels = hotelService.searchByAddressOrName(query.trim());
            model.addAttribute("query", query);
        } else {
            hotels = hotelService.findAll();
        }

        model.addAttribute("hotels", hotels);
        return "home";
    }
}
