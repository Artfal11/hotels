package org.example.hotelbooking.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomUpdateDTO {
    private String name;
    private String description;
    private Integer capacity;
    private BigDecimal price;
    private MultipartFile image;
}
