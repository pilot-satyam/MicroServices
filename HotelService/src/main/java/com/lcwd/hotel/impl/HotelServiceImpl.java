package com.lcwd.hotel.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lcwd.hotel.entities.hotel;
import com.lcwd.hotel.exceptions.ResourceNotFoundException;
import com.lcwd.hotel.repositories.HotelRepo;
import com.lcwd.hotel.services.hotelService;



@Service
public class HotelServiceImpl implements hotelService {

    @Autowired
    private HotelRepo hotelRepo;

    @Override
    public hotel createHotel(hotel hotel) {
        String hotelId = UUID.randomUUID().toString();
        hotel.setId(hotelId);
        return hotelRepo.save(hotel);
    }

    @Override
    public List<hotel> getAll() {
       return hotelRepo.findAll();
    }

    @Override
    public hotel getSingle(String id) {
        return hotelRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Hotel with given id not found"));
    }    
}
