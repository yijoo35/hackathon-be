package hackathon.bigone.sunsak.foodbox.nlp.service;

import hackathon.bigone.sunsak.foodbox.ocr.OcrExtractedItem;
import jakarta.annotation.PostConstruct;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import org.springframework.core.io.ClassPathResource;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
// ... (나머지 임포트)

@Service
public class NlpService {
    private Komoran komoran;

    @PostConstruct
    public void initKomoran() {
        try {
            // JAR 파일 내부에 있는 사용자 사전을 임시 파일로 복사
            ClassPathResource resource = new ClassPathResource("data/user_dict.txt");
            Path tempUserDict = Files.createTempFile("user_dict", ".txt");

            try (InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, tempUserDict, StandardCopyOption.REPLACE_EXISTING);
            }

            // Komoran 초기화 시 임시 파일 경로 사용
            komoran = new Komoran(DEFAULT_MODEL.FULL);
            komoran.setUserDic(tempUserDict.toString());

            System.out.println("KOMORAN 사용자 사전 로드 완료: " + tempUserDict.toString());

            // 임시 파일 삭제
            tempUserDict.toFile().deleteOnExit();

        } catch (Exception e) {
            throw new RuntimeException("사용자 사전 로딩 실패", e);
        }
    }

    public List<OcrExtractedItem> extractNouns(List<OcrExtractedItem> rawItems){
        List<OcrExtractedItem> result = new ArrayList<>();

        for(OcrExtractedItem item: rawItems){
            KomoranResult komoranResult = komoran.analyze(item.getName());

            List<String> nouns = komoranResult.getNouns().stream()
                    .filter(noun -> noun.length()>=1)//>=2?
                    .toList();
            for(String noun: nouns){
                result.add(new OcrExtractedItem(noun, item.getQuantity()));
                System.out.println("["+noun+","+item.getQuantity()+"]");
            }

        }

        return result;
    }

}
