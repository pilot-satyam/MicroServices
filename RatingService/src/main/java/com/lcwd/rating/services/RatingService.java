package com.lcwd.rating.services;

import java.util.List;

import com.lcwd.rating.entities.Rating;

public interface RatingService {
    Rating createRating(Rating rating);

    //getAll ratings
    List<Rating> getAllRatings();
    
    //get all by userId's
    List<Rating> getRatingByUserId(String userId);

    //get all by Hotel

    List<Rating> getRatingbyHotelId(String hotelId);

}
