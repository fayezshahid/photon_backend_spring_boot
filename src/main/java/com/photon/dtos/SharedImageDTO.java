package com.photon.dtos;

import com.photon.entities.Image;
import com.photon.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedImageDTO {
    // Image fields
    private Long imageId;
    private String image;
    private String name;
    private LocalDateTime createdAt;

    // Owner fields
    private Long ownerId;
    private String ownerEmail;
}