package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "witneses")
public class Witness {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
}
