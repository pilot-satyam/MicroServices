package com.lcwd.hotel.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff")
public class Staffs {


    @GetMapping
    public ResponseEntity<List<String>> getStaff(){
       List<String> lists =  Arrays.asList("Deeu","Wahii","Paridhi");
        return new ResponseEntity<>(lists,HttpStatus.OK);
    }
    
}
