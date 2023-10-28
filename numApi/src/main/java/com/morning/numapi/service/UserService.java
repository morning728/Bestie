package com.morning.numapi.service;


import com.morning.numapi.model.DTO.ProfileDTO;
import com.morning.numapi.model.User;

public interface UserService {

    User findByUsername(String username);

    ProfileDTO convertToProfileDTO(String username);

}
