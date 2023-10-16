package com.lcwd.user.service.external.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lcwd.user.service.entities.Hotel;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelService
{

    //not using public since the method under interface are already public
    @GetMapping("/hotels/{hotelId}")
    Hotel getHotel(@PathVariable("hotelId") String hotelId);
    
}
