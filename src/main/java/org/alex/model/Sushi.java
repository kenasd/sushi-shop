package org.alex.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "sushi")
public class Sushi {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "time_to_make")
    private int timeToMake;

}
