package com.morning.statisticsapi.service.interf;


import com.morning.statisticsapi.model.DTO.ProfileDTO;
import com.morning.statisticsapi.model.User;

public interface UserService {

    User findByUsername(String username);

    ProfileDTO convertToProfileDTO(String username);

}
