package org.example.hotelbooking.service;

import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.model.User;

import java.util.List;

public interface HotelService {
    void saveHotel(Hotel hotel);
    Hotel save(Hotel hotel);
    List<Hotel> getHotelsByOwner(User owner);
    List<Hotel> findAll();
    Hotel findById(Long id);
    void delete(Long id);
    List<Hotel> searchByAddressOrName(String query);
}
