package org.example.hotelbooking.service;

import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.model.Room;

import java.util.List;

public interface RoomService {
    Room save(Room room);
    List<Room> findByHotel(Hotel hotel);
    Room findById(Long id);
    void deleteById(Long id);
}
