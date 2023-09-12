package com.morning.numapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "goal",  schema = "num_note")
@Data
public class Goal extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "description")
    private String description;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "weight_difference_per_day")
    private Float weightDifferencePerDay;

    @Column(name = "steps_per_day")
    private Integer stepsPerDay;

    @Column(name = "sheets_per_day")
    private Integer sheetsPerDay;

    @Column(name = "income")
    private Integer income;


}
