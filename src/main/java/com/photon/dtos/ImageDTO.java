package com.photon.dtos;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ImageDTO {
    private Long id;
    private String name;
    private String image;
    private Long size;
    private boolean archived;
    private boolean favourite;
    private boolean inTrash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> albumIds;
    private List<Long> sharedWithFriendIds;
}
