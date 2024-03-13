package com.morning.taskapi.service;


import com.morning.taskapi.model.DTO.ProfileDTO;
import com.morning.taskapi.model.User;

public interface UserService {

    User findByUsername(String username);

    ProfileDTO convertToProfileDTO(String username);

}
