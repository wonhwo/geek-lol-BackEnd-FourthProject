package com.nat.geeklolspring.shorts.shortsboard.controller;

import com.nat.geeklolspring.exception.DTONotFoundException;
import com.nat.geeklolspring.shorts.shortsboard.dto.request.ShortsPostRequestDTO;
import com.nat.geeklolspring.shorts.shortsboard.dto.response.ShortsListResponseDTO;
import com.nat.geeklolspring.shorts.shortsboard.service.ShortsService;
import com.nat.geeklolspring.utils.upload.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
//@CrossOrigin(origins = {"http://localhost:3000","",""})
@RequestMapping("/api/shorts")
public class ShortsController {
    // 업로드한 shorts를 저장할 로컬 위치
    @Value("D:/geek-lol/upload/shorts/video")
    private String rootShortsPath;

    @Value("D:/geek-lol/upload/shorts/thumbnail")
    private String rootThumbnailPath;

    private final ShortsService shortsService;

    @GetMapping()
    public ResponseEntity<?> shortsList() {
        log.info("/api/shorts : Get!");

        try {
            // 모든 쇼츠 목록 가져오기
            ShortsListResponseDTO shortsList = shortsService.retrieve();

            // 가져온 shortsList가 비어있을 경우 아직 업로드된 동영상이 없다는 뜻
            if(shortsList.getShorts().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(ShortsListResponseDTO
                                .builder()
                                .error("아직 업로드된 동영상이 없습니다!")
                                .build());
            }

            return ResponseEntity.ok().body(shortsList);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ShortsListResponseDTO
                            .builder()
                            .error(e.getMessage()));
        }
    }

    // 쇼츠 생성
    @PostMapping()
    public ResponseEntity<?> addShorts(
            @RequestPart("videoInfo") ShortsPostRequestDTO dto,
            @RequestPart("videoUrl") MultipartFile fileUrl,
            @RequestPart("thumbnail")MultipartFile thumbnail,
            BindingResult result
    ) {
        // 입력값 검증에 걸리면 400번 코드와 함께 메시지를 클라이언트에 전송
        if(result.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(result.toString())
                    ;
        }

        log.info("/api/shorts : POST");
        log.warn("request parameter : {}", dto);

        // 따로 가져온 파일들을 dto안에 세팅하기
        dto.setVideoLink(fileUrl);
        dto.setVideoThumbnail(thumbnail);

        try {
            // 필요한 정보를 전달받지 못하면 커스텀 에러인 DTONotFoundException 발생
            if (dto.getTitle().isEmpty() || dto.getVideoLink().isEmpty() || dto.getVideoThumbnail().isEmpty() || dto.getUploaderId().isEmpty())
                throw new DTONotFoundException("필요한 정보가 입력되지 않았습니다.");

            // 동영상과 섬네일 이미지를 가공해 로컬폴더에 저장하고 경로를 리턴받기
            // 동영상 가공
            Map<String, String> videoMap = FileUtil.uploadVideo(fileUrl, rootShortsPath);
            String videoPath = videoMap.get("filePath");
            // 이미지 가공
            Map<String, String> profileImgMap = FileUtil.uploadVideo(thumbnail, rootThumbnailPath);
            String thumbnailPath = profileImgMap.get("filePath");
            
            // dto와 파일경로를 DB에 저장하는 서비스 실행
            // return : 전달받은 파일들이 DB에 저장된 새 동영상 리스트들
            ShortsListResponseDTO shortsList = shortsService.insertVideo(dto, videoPath, thumbnailPath);
            
            return ResponseEntity.ok().body(shortsList);

        } catch (DTONotFoundException e) {
            log.warn("필요한 정보를 전달받지 못했습니다.");
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 쇼츠 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShorts(@PathVariable Long id) {

        log.info("/api/shorts/{} DELETE !!", id);

        // 데이터를 전달받지 못했다면 실행
        if(id == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ShortsListResponseDTO
                            .builder()
                            .error("ID값을 보내주세요!")
                            .build());
        }

        try {
            // id에 해당하는 동영상을 지우는 서비스 실행
            // return : id에 해당하는 동영상이 삭제된 DB에서 동영상 리스트 새로 가져오기
            ShortsListResponseDTO shortsList = shortsService.deleteShorts(id);

            return ResponseEntity.ok().body(shortsList);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ShortsListResponseDTO
                            .builder()
                            .error(e.getMessage())
                            .build());
        }
    }
}
