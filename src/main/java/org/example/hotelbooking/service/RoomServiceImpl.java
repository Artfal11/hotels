package org.example.hotelbooking.service;

import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.model.Room;
import org.example.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public List<Room> findByHotel(Hotel hotel) {
        return roomRepository.findByHotel(hotel);
    }

    @Override
    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        roomRepository.deleteById(id);
    }
}
