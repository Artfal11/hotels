package org.example.hotelbooking.repository;

import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel(Hotel hotel);
}
