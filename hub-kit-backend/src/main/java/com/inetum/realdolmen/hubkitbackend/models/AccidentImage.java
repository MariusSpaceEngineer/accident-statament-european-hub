package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "accident_images")
public class AccidentImage {
    @Id
    @GeneratedValue
    private Integer id;

    @Lob
    @Column(name = "image_data", columnDefinition="BLOB")
    private byte[] data;

}
