package com.photon.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "album_images")
@IdClass(AlbumImageId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumImage implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @Id
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;
}