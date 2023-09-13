package com.lcwd.user.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

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
       return userRepo.findById(userId).orElseThrow(()-> new ResourceAccessException("User with Given Id is not found on server!!:"+userId));
    }

}
