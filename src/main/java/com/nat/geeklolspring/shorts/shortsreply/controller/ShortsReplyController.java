package com.nat.geeklolspring.shorts.shortsreply.controller;

import com.nat.geeklolspring.exception.DTONotFoundException;
import com.nat.geeklolspring.shorts.shortsboard.dto.response.ShortsListResponseDTO;
import com.nat.geeklolspring.shorts.shortsreply.dto.request.ShortsPostRequestDTO;
import com.nat.geeklolspring.shorts.shortsreply.dto.request.ShortsUpdateRequestDTO;
import com.nat.geeklolspring.shorts.shortsreply.dto.response.ShortsReplyListResponseDTO;
import com.nat.geeklolspring.shorts.shortsreply.service.ShortsReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@Slf4j
@RequiredArgsConstructor
//@CrossOrigin(origins = {"http://localhost:3000","",""})
@RequestMapping("/api/shorts/reply")
public class ShortsReplyController {
    private final ShortsReplyService shortsReplyService;

    // 해당 쇼츠의 댓글 정보를 가져오는 부분
    @GetMapping("/{shortsId}")
    public ResponseEntity<?> replyList(@PathVariable Long shortsId) {
        log.info("/api/shorts/reply/{} : Get!", shortsId);

        try {
            ShortsReplyListResponseDTO replyList = shortsReplyService.retrieve(shortsId);

            if(replyList.getReply().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(ShortsReplyListResponseDTO
                                .builder()
                                .error("아직 댓글이 없습니다.")
                                .build());
            }

            return ResponseEntity.ok().body(replyList);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ShortsReplyListResponseDTO
                            .builder()
                            .error(e.getMessage()));
        }
    }

    // 해당 쇼츠에 댓글을 등록하는 부분
    @PostMapping("/{shortsId}")
    public ResponseEntity<?> addReply(
            @PathVariable Long shortsId,
            @RequestBody ShortsPostRequestDTO dto) {

        log.info("api/shorts/reply/{} : Post!", shortsId);
        log.warn("전달받은 데이터 : {}", dto);

        try {
            if (dto.getContext().isEmpty() || dto.getWriterId().isEmpty())
                throw new DTONotFoundException("필요한 정보가 입력되지 않았습니다.");

            ShortsReplyListResponseDTO replyList = shortsReplyService.insertShortsReply(shortsId, dto);
            return ResponseEntity.ok().body(replyList);

        } catch (DTONotFoundException e) {
            log.warn("필요한 정보를 전달받지 못했습니다.");
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 해당 쇼츠의 댓글을 삭제하는 부분
    @DeleteMapping("/{shortsId}/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long shortsId,
                                         @PathVariable Long replyId) {
        log.info("api/shorts/reply/{}/{} : Delete!", shortsId, replyId);

        if(shortsId == null || replyId == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ShortsReplyListResponseDTO
                            .builder()
                            .error("ID값을 보내주세요!")
                            .build());
        }

        try {
            ShortsReplyListResponseDTO replyList = shortsReplyService.deleteShortsReply(shortsId, replyId);

            return ResponseEntity.ok().body(replyList);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ShortsListResponseDTO
                            .builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @RequestMapping(method = {PUT, PATCH})
    public ResponseEntity<?> updateShortsReply(@RequestBody ShortsUpdateRequestDTO dto) {
        log.info("api/shorts/reply : PATCH");
        log.debug("서버에서 받은 값 : {}", dto);

        try {
            ShortsReplyListResponseDTO replyList = shortsReplyService.updateReply(dto);

            return ResponseEntity.ok().body(replyList);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ShortsListResponseDTO.builder().error(e.getMessage()));
        }
    }
}
