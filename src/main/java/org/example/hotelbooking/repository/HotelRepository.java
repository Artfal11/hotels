package org.example.hotelbooking.repository;

import org.example.hotelbooking.model.Hotel;
import org.example.hotelbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User owner);
    List<Hotel> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);
}
