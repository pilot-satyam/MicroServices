package com.lcwd.rating.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lcwd.rating.entities.Rating;
import java.util.List;


public interface RatingRepo extends MongoRepository<Rating,String> {

    //custom finder methods
    List<Rating> findByUserId(String userId);
    List<Rating> findByHotelId(String hotelId);
    
}
