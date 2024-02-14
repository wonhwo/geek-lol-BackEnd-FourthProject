package com.nat.geeklolspring.board.bulletin.dto.request;

import com.nat.geeklolspring.entity.BoardBulletin;
import com.nat.geeklolspring.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardBulletinModifyRequestDTO {

    private Long bulletinId;
    private String title;
    private String content;
    private String boardMedia;
    private String posterId;
    private String posterName;
    private LocalDateTime boardDate;
    private int boardReportCount;
    private int viewCount;
    private int upCount;

    public BoardBulletin toEntity(String fileUrl, User user) {
        return BoardBulletin.builder()
                .bulletinId(bulletinId)
                .title(title)
                .boardContent(content)
                .boardMedia(fileUrl)
                .viewCount(this.viewCount)
                .boardDate(this.boardDate)
                .upCount(this.upCount)
                .user(user)
                .build();
    }

}
