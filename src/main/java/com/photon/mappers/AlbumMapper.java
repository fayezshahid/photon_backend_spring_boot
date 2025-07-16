package com.photon.mappers;

import com.photon.dtos.AlbumDTO;
import com.photon.entities.Album;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumMapper {
    public AlbumDTO toDTO(Album album) {
        return AlbumDTO.builder()
                .id(album.getId())
                .name(album.getName())
                .size(album.getSize())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }

    public List<AlbumDTO> toDTOList(List<Album> albums) {
        return albums.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
