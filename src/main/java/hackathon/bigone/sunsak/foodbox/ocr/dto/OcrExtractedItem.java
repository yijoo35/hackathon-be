package hackathon.bigone.sunsak.foodbox.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OcrExtractedItem {
    private String name;
    private int quantity;
}
