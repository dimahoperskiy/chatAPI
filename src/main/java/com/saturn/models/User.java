package com.saturn.models;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public User() {
    }


    public User(String name, String status, Boolean follow, String country, String city) {
        this.name = name;
        this.status = status;
        this.follow = follow;
        this.country = country;
        this.city = city;
    }

    private String name;
    private String status;
    private Boolean follow;
    private String country;
    private String city;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}



