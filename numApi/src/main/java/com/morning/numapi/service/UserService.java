package com.morning.numapi.service;


import com.morning.numapi.model.User;

public interface UserService {

    User findByUsername(String username);

}
