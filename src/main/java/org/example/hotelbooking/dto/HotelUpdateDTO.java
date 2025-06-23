package org.example.hotelbooking.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class HotelUpdateDTO {
    private String name;
    private String address;
    private String description;
    private MultipartFile image;
}
