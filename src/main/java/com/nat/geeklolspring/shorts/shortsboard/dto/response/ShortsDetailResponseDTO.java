package com.nat.geeklolspring.shorts.shortsboard.dto.response;

import com.nat.geeklolspring.entity.BoardShorts;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortsDetailResponseDTO {
    private Long shortsId;
    private String uploaderId;
    private String title;
    private int viewCount;
    private int upCount;
    private String videoLink;
    private String thumbnail;
    private LocalDateTime uploadDate;

    public ShortsDetailResponseDTO(BoardShorts shorts) {
        this.shortsId = shorts.getShortsId();
        this.uploaderId = shorts.getUploaderId();
        this.title = shorts.getTitle();
        this.viewCount = shorts.getViewCount();
        this.upCount = shorts.getUpCount();
        this.videoLink = shorts.getVideoLink();
        this.thumbnail = shorts.getThumbnailImage();
        this.uploadDate = shorts.getUploadDate();
    }
}
