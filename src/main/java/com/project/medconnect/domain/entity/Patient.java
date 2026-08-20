package com.project.medconnect.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "patient")
@PrimaryKeyJoinColumn(name = "user_id")

public class Patient extends User {
    private String bloodgroup;
    private String dateOfBirth;

}
