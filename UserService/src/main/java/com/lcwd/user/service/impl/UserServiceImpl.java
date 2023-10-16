package com.lcwd.user.service.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;
    

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepo.save(user);
    }

    @Override
    public List<User> getAllUser() {
      return userRepo.findAll();
    }

    @Override
    public User getUser(String userId) {
       User user =  userRepo.findById(userId).orElseThrow(()-> new ResourceAccessException("User with Given Id is not found on server!!:"+userId));

       //fetch rating of the above user from rating service
       //http://localhost:8083/ratings/users/408bf4d5-e985-4795-bef8-911fa1fad272

       Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(),Rating[].class);
       logger.info("{}",ratingsOfUser); 

       List<Rating> ratings = Arrays.stream(ratingsOfUser).toList(); //converting Rating array into list so that it can be iterated 

       List<Rating> ratingList  = ratings.stream().map(rating->{
            //api call to hotel service to get the hotel details

            //http://localhost:8082/hotels/533c7396-1a5b-450b-87c7-ab0308159f11
            // ResponseEntity<Hotel> forEntity =  restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(),Hotel.class);
          // Hotel hotel = forEntity.getBody();//we will get the hotel here
          Hotel hotel = hotelService.getHotel(rating.getHotelId());
          // logger.info("response status code: {}",forEntity.getStatusCode());

            //set the hotel to rating
            rating.setHotel(hotel);
            //return the rating
            return rating;
       }).collect(Collectors.toList()); //using collect since we are getting new list

      //  user.setRatings(ratingsOfUser);
      user.setRatings(ratingList);
       return user;
    }

}
