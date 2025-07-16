package com.photon.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AlbumDTO {
    private Long id;
    private String name;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
