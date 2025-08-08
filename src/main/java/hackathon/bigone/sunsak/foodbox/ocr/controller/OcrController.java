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

import java.util.List;

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
        List<OcrExtractedItem> rawItems = ocrService.extractItemNamesFromImage(file);
        List<OcrExtractedItem> nouns = nlpService.extractNouns(rawItems);

        List<FoodItemResponse> result = nouns.stream()
                .map(item -> {
                    String noun = ocrNomalizationService.normalizeFromOcr(item.getName(), List.of(item.getName()));
                    return noun != null ? new FoodItemResponse(noun, item.getQuantity()) : null;
                })
                .filter(item -> item != null)
                .distinct()
                .toList();

        //return ResponseEntity.ok().body(rawItems);
        //return ResponseEntity.ok().body(nouns);
        return ResponseEntity.ok().body(result);
    }
}
