package com.nat.geeklolspring.board.vote.dto.request;

import com.nat.geeklolspring.entity.BoardBulletin;
import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardVotePatchRequestDTO {
    // 좋아요를 수정하는데 필요한 정보들
    private BoardBulletin boardId;
}
