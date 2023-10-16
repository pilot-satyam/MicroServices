package com.lcwd.hotel.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.hotel.entities.hotel;
import com.lcwd.hotel.services.hotelService;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private hotelService hotelService;

    //create
    @PostMapping
    public ResponseEntity<hotel> createHotel(@RequestBody hotel hotel){
        hotel newHotel = hotelService.createHotel(hotel);
        return ResponseEntity.status(HttpStatus.CREATED).body(newHotel);
    } 

    @PreAuthorize("hasAuthority('SCOPE_internal')")
    @GetMapping("/{id}")
    public ResponseEntity<hotel> getSingleHotel(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getSingle(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_internal') || hasAuthority('Admin')")
    @GetMapping
    public ResponseEntity<List<hotel>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getAll());
    }
    
    
}
