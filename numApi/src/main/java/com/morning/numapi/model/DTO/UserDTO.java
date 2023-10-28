package com.morning.numapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morning.numapi.model.Record;
import com.morning.numapi.model.User;
import com.morning.numapi.model.enums.Status;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private Date birthday;
    private Float weight;
    private Float height;
    private Float averageMark;

/*    public User toUser(){
        User result = new User;

    }



    public UserDTO fromUser(User user){
        return null;
    }*/


}
