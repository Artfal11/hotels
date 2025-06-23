package org.example.hotelbooking.controller;

import org.example.hotelbooking.dto.RoomCreateDTO;
import org.example.hotelbooking.dto.RoomUpdateDTO;
import org.example.hotelbooking.model.Booking;
import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.model.Room;
import org.example.hotelbooking.model.User;
import org.example.hotelbooking.service.BookingService;
import org.example.hotelbooking.service.HotelService;
import org.example.hotelbooking.service.RoomService;
import org.example.hotelbooking.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final HotelService hotelService;
    private final UserService userService;
    private final BookingService bookingService;

    public RoomController(RoomService roomService, HotelService hotelService, UserService userService, BookingService bookingService) {
        this.roomService = roomService;
        this.hotelService = hotelService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public String showRoomDetails(@PathVariable Long id,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Room room = roomService.findById(id);
        model.addAttribute("room", room);

        if (userDetails != null) {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            model.addAttribute("currentUser", currentUser);
        }

        List<Booking> bookings = bookingService.findBookingsByRoomId(id);
        List<String> bookedDates = new ArrayList<>();
        for (Booking booking : bookings) {
            LocalDate current = booking.getCheckInDate();
            while (!current.isAfter(booking.getCheckOutDate().minusDays(1))) {
                bookedDates.add(current.toString());
                current = current.plusDays(1);
            }
        }
        model.addAttribute("bookedDates", bookedDates);

        return "room/room-details";
    }

    @GetMapping("/new/{hotelId}")
    public String showCreateForm(@PathVariable Long hotelId, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        Hotel hotel = hotelService.findById(hotelId);
        if (!hotel.getOwner().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/access-denied";
        }
        model.addAttribute("roomDto", new RoomCreateDTO());
        model.addAttribute("hotelId", hotelId);
        return "room/room-form";
    }

    @PostMapping("/new/{hotelId}")
    public String createRoom(@PathVariable Long hotelId,
                             @ModelAttribute RoomCreateDTO roomDto,
                             @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Hotel hotel = hotelService.findById(hotelId);
        if (!hotel.getOwner().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/access-denied";
        }

        String imageUrl = null;
        MultipartFile image = roomDto.getImage();
        if (image != null && !image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(image.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            imageUrl = "/uploads/" + filename;
        }

        Room room = new Room();
        room.setName(roomDto.getName());
        room.setDescription(roomDto.getDescription());
        room.setCapacity(roomDto.getCapacity());
        room.setPrice(roomDto.getPrice());
        room.setImageUrl(imageUrl);
        room.setHotel(hotel);

        roomService.save(room);
        return "redirect:/hotels/" + hotelId;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Room room = roomService.findById(id);
        Hotel hotel = room.getHotel();
        if (!hotel.getOwner().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/access-denied";
        }
        model.addAttribute("room", room);
        return "room/room-edit-form";
    }

    @PostMapping("/edit/{id}")
    public String updateRoom(@PathVariable Long id,
                             @ModelAttribute RoomUpdateDTO roomDto,
                             @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Room room = roomService.findById(id);
        Hotel hotel = room.getHotel();

        if (!hotel.getOwner().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/access-denied";
        }

        MultipartFile image = roomDto.getImage();
        if (image != null && !image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(image.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            room.setImageUrl("/uploads/" + filename);
        }

        room.setName(roomDto.getName());
        room.setDescription(roomDto.getDescription());
        room.setCapacity(roomDto.getCapacity());
        room.setPrice(roomDto.getPrice());

        roomService.save(room);
        return "redirect:/hotels/" + hotel.getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Room room = roomService.findById(id);
        Hotel hotel = room.getHotel();
        if (!hotel.getOwner().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/access-denied";
        }

        roomService.deleteById(id);
        return "redirect:/hotels/" + hotel.getId();
    }
}
