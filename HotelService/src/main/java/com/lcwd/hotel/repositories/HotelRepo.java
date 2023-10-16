package com.lcwd.hotel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.hotel.entities.hotel;

public interface HotelRepo extends JpaRepository<hotel,String> {
    
}
