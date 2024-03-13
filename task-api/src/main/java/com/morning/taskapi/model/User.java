package com.morning.taskapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "user",  schema = "num_note")
@Data
public class User extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birthday")
    private Date birthday;

//    @Column(name = "weight")
//    private Float weight;
//
//    @Column(name = "height")
//    private Float height;

//    @Column(name = "telegram_id")
//    private String telegramId;

//    @Column(name = "average_mark")
//    private Float averageMark;
}
