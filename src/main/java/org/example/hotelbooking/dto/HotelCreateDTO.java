package org.example.hotelbooking.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class HotelCreateDTO {
    private String name;
    private String address;
    private String description;
    private MultipartFile image;
}