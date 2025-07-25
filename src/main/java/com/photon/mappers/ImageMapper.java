package com.photon.mappers;

import com.photon.dtos.ImageDTO;
import com.photon.entities.Image;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageMapper {
    public ImageDTO toDTO(Image image) {
        List<Long> albumIds = null;
        if (image.getAlbumImages() != null && !image.getAlbumImages().isEmpty()) {
            albumIds = image.getAlbumImages()
                    .stream()
                    .map(albumImage -> albumImage.getAlbum().getId())
                    .collect(Collectors.toList());
        }

        List<Long> sharedWithFriendIds = null;
        if (image.getShares() != null && !image.getShares().isEmpty()) {
            sharedWithFriendIds = image.getShares()
                    .stream()
                    .map(share -> share.getViewer().getId())
                    .collect(Collectors.toList());
        }

        return ImageDTO.builder()
                .id(image.getId())
                .name(image.getName())
                .image(image.getImage())
                .size(image.getSize())
                .archived(image.isArchived())
                .favourite(image.isFavourite())
                .inTrash(image.isInTrash())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .albumIds(albumIds)
                .sharedWithFriendIds(sharedWithFriendIds)
                .build();
    }

    public List<ImageDTO> toDTOList(List<Image> images) {
        return images.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
