package hackathon.bigone.sunsak.foodbox.foodbox.controller;

import hackathon.bigone.sunsak.foodbox.foodbox.service.FoodBoxService;
import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodBoxResponse;
import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/foodbox")
public class FoodBoxController {
    private final FoodBoxService foodBoxService;

    @PostMapping("/save")
    public ResponseEntity<List<FoodBoxResponse>> saveFoods(@RequestBody List<FoodItemResponse> items){
        List<FoodBoxResponse> savedFoods = foodBoxService.saveSelectedFoods(items);
        return ResponseEntity.ok(savedFoods);
    }
}
