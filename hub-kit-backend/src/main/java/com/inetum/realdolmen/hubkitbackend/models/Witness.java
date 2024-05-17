package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
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
    @NonNull
    @NotEmpty
    private String name;
    @NonNull
    @NotEmpty
    private String address;
    @NonNull
    @NotEmpty
    private String phoneNumber;
}
