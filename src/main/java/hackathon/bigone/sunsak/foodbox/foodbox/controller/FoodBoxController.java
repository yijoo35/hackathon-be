package hackathon.bigone.sunsak.foodbox.foodbox.controller;

import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodBoxResponse;
import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodItemRequest;
import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodItemResponse;
import hackathon.bigone.sunsak.foodbox.foodbox.service.FoodBoxService;
import hackathon.bigone.sunsak.global.security.jwt.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/foodbox")
public class FoodBoxController {
    private final FoodBoxService foodBoxService;

    @PostMapping("/ocr/save") //영수증 인식 입력
    public ResponseEntity<List<FoodBoxResponse>> saveFoodsWithOCR(
            @RequestBody List<FoodItemResponse> items,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        if (userDetail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = userDetail.getId(); // JWT 인증 기반
        List<FoodBoxResponse> savedFoods = foodBoxService.saveSelectedFoods(userId, items);
        return ResponseEntity.ok(savedFoods);
    }

    //사용자 직접 입력
    @PostMapping("/save")
    public ResponseEntity<List<FoodBoxResponse>> saveFoods(
            @RequestBody List<FoodItemRequest> items,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ){
        if (userDetail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = userDetail.getId();

        for (int i = 0; i < items.size(); i++) {
            FoodItemRequest it = items.get(i);
            if (it == null || it.getName() == null || it.getName().isBlank()) {
                return ResponseEntity.badRequest().body(null);
            }
            if (it.getExpiryDate() == null) {
                // 어떤 라인에서 빠졌는지 알려주기
                throw new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "expiryDate is required at index " + i);
            }
        }

        return ResponseEntity.ok(foodBoxService.saveFoods(userId, items));
    }

    @GetMapping("") //로그인한 사용자의 식품 목록 보여주기
    public ResponseEntity<List<FoodBoxResponse>> getAllFoods(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ){
        Long userId = userDetail.getId();

        List<FoodBoxResponse> list = foodBoxService.getFoodsByUser(userId);
        return ResponseEntity.ok(list);
    }


}
