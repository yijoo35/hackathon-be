package hackathon.bigone.sunsak.foodbox.nlp.service;

import hackathon.bigone.sunsak.foodbox.ocr.OcrExtractedItem;
import jakarta.annotation.PostConstruct;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NlpService {
    private Komoran komoran;

    @PostConstruct
    public void initKomoran() {
        try {
            // Komoran 인스턴스 생성
            komoran = new Komoran(DEFAULT_MODEL.FULL);

            // 사용자 사전을 JAR 내부에서 임시 파일로 복사
            ClassPathResource resource = new ClassPathResource("data/user_dict.txt");
            try (InputStream userDictStream = resource.getInputStream()) {
                Path tempFile = Files.createTempFile("user_dict", ".txt");
                Files.copy(userDictStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

                // Komoran에 사용자 사전 설정
                komoran.setUserDic(tempFile.toAbsolutePath().toString());
                System.out.println("KOMORAN 사용자 사전 로드 완료: " + tempFile);

                // 애플리케이션 종료 시 임시 파일 삭제
                tempFile.toFile().deleteOnExit();
            }

        } catch (IOException e) {
            System.err.println("사용자 사전 파일 로드 실패: " + e.getMessage());
            throw new RuntimeException("사용자 사전 로딩 실패: data/user_dict.txt 파일을 확인하세요.", e);
        }
    }

    public List<OcrExtractedItem> extractNouns(List<OcrExtractedItem> rawItems) {
        List<OcrExtractedItem> result = new ArrayList<>();

        for (OcrExtractedItem item : rawItems) {
            KomoranResult komoranResult = komoran.analyze(item.getName());
            List<String> nouns = komoranResult.getNouns().stream()
                    .filter(noun -> noun.length() >= 1) // 필요에 따라 >=2로 변경
                    .collect(Collectors.toList());

            for (String noun : nouns) {
                result.add(new OcrExtractedItem(noun, item.getQuantity()));
                System.out.println("[" + noun + "," + item.getQuantity() + "]");
            }
        }
        return result;
    }
}
