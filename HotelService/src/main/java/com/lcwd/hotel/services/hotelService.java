package com.lcwd.hotel.services;

import java.util.List;

import com.lcwd.hotel.entities.hotel;

public interface hotelService {
    //create
    hotel createHotel(hotel hotel);
    //get All
    List<hotel> getAll();
    //get single
    hotel getSingle(String id);
}
