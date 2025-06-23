package org.example.hotelbooking.controller;

import org.example.hotelbooking.dto.HotelCreateDTO;
import org.example.hotelbooking.dto.HotelUpdateDTO;
import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.model.Room;
import org.example.hotelbooking.model.User;
import org.example.hotelbooking.model.UserRole;
import org.example.hotelbooking.service.HotelService;
import org.example.hotelbooking.service.RoomService;
import org.example.hotelbooking.service.UserService;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final UserService userService;
    private final RoomService roomService;

    public HotelController(HotelService hotelService, UserService userService, RoomService roomService) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping("/{id}")
    public String showHotelDetails(@PathVariable Long id, Model model) {
        Hotel hotel = hotelService.findById(id);
        List<Room> rooms = roomService.findByHotel(hotel);

        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", rooms);
        return "hotel/hotel-details";
    }

    @GetMapping("/new")
    public String showHotelForm(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (!(user.getRole().equals(UserRole.OWNER))) {
            return "redirect:/access-denied";
        }
        model.addAttribute("hotel", new Hotel());
        return "hotel/hotel-form";
    }

    @PostMapping
    public String registerHotel(@ModelAttribute HotelCreateDTO hotelDto,
                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User user = userService.findByEmail(userDetails.getUsername());
        if (!(user.getRole().equals(UserRole.OWNER))) {
            return "redirect:/access-denied";
        }

        String imageUrl = null;
        MultipartFile image = hotelDto.getImage();
        if (image != null && !image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(image.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            imageUrl = "/uploads/" + filename;
        }

        Hotel hotel = new Hotel();
        hotel.setName(hotelDto.getName());
        hotel.setAddress(hotelDto.getAddress());
        hotel.setDescription(hotelDto.getDescription());
        hotel.setImageUrl(imageUrl);
        hotel.setOwner(user);

        hotelService.saveHotel(hotel);
        return "redirect:/profile";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Hotel hotel = hotelService.findById(id);
        if (!hotel.getOwner().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/access-denied";
        }
        model.addAttribute("hotel", hotel);
        return "hotel/hotel-edit-form";
    }

    @PostMapping("/edit/{id}")
    public String updateHotel(@PathVariable Long id,
                              @ModelAttribute HotelUpdateDTO hotelDto,
                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User owner = userService.findByEmail(userDetails.getUsername());
        Hotel hotel = hotelService.findById(id);

        if (!hotel.getOwner().getId().equals(owner.getId())) {
            return "redirect:/access-denied";
        }

        MultipartFile image = hotelDto.getImage();
        if (image != null && !image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(image.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            hotel.setImageUrl("/uploads/" + filename);
        }

        hotel.setName(hotelDto.getName());
        hotel.setAddress(hotelDto.getAddress());
        hotel.setDescription(hotelDto.getDescription());

        hotelService.saveHotel(hotel);
        return "redirect:/profile";
    }


    @PostMapping("/delete/{id}")
    public String deleteHotel(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails) {
        Hotel hotel = hotelService.findById(id);
        if (!hotel.getOwner().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/access-denied";
        }

        hotelService.delete(id);
        return "redirect:/profile";
    }
}
