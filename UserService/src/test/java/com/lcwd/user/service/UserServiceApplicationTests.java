package com.lcwd.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.external.services.RatingService;

@SpringBootTest
@Service
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RatingService ratingService;

	@Test
	void createRating(){
		Rating rating = Rating.builder().rating(10).userId("").hotelId("").feedback("Test Feign Client").build();
		Rating savedRating = ratingService.createRating(rating);
		System.out.println("new rating created");
	}

}
