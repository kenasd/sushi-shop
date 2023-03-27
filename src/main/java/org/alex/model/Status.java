package org.alex.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
}
