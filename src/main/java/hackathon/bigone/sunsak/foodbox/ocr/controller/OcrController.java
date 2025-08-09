package hackathon.bigone.sunsak.foodbox.ocr.controller;

import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodItemResponse;
import hackathon.bigone.sunsak.foodbox.nlp.service.NlpService;
import hackathon.bigone.sunsak.foodbox.ocr.dto.OcrExtractedItem;
import hackathon.bigone.sunsak.foodbox.ocr.service.OcrNomalizationService;
import hackathon.bigone.sunsak.foodbox.ocr.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/foodbox/receipt")
@RequiredArgsConstructor
public class OcrController {
    private final OcrService ocrService;
    private final NlpService nlpService;
    private final OcrNomalizationService ocrNomalizationService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FoodItemResponse>> sendReceiptOcr(@RequestParam("file") MultipartFile file) throws Exception {
        // OCR → 기본
        List<OcrExtractedItem> rawItems = ocrService.extractItemNamesFromImage(file);

        // 명사만 추출
        List<OcrExtractedItem> nouns = nlpService.extractNouns(rawItems);

        // bulk 정규화
        List<String> tokens = nouns.stream().map(OcrExtractedItem::getName).toList();
        Map<String, String> normalizedMap = ocrNomalizationService.bulkNormalizeFromOcr(tokens);

        // 최종명으로 수량 합산
        Map<String, Integer> aggregated = new LinkedHashMap<>();
        for (OcrExtractedItem item : nouns) {
            String original = item.getName();
            String finalName = normalizedMap.getOrDefault(original, original); // 폴백: 원문
            aggregated.merge(finalName, item.getQuantity(), Integer::sum);
        }

        // 응답 반환
        List<FoodItemResponse> result = aggregated.entrySet().stream()
                .map(e -> new FoodItemResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
