package hackathon.bigone.sunsak.foodbox.nlp.service;

import hackathon.bigone.sunsak.foodbox.ocr.dto.OcrExtractedItem;
import jakarta.annotation.PostConstruct;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class NlpService {
    private Komoran komoran;

    @PostConstruct
    public void initKomoran() {
        komoran = new Komoran(DEFAULT_MODEL.FULL);
        try {
            // ClassPath에서 사전 파일 읽기
            ClassPathResource resource = new ClassPathResource("data/user_dict.txt");

            // 임시 파일 생성 후 복사 (jar 내부 리소스는 File 객체로 접근 불가하기 때문)
            File tempFile = File.createTempFile("user_dict", ".txt");
            tempFile.deleteOnExit(); // JVM 종료 시 자동 삭제

            try (InputStream in = resource.getInputStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            // Komoran에 사용자 사전 경로 설정
            komoran.setUserDic(tempFile.getAbsolutePath());
            System.out.println("KOMORAN 사용자 사전 로드 완료: " + tempFile.getAbsolutePath());
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

            for(String noun: nouns){ //명사, 수량
                result.add(new OcrExtractedItem(noun, item.getQuantity()));
                System.out.println("["+noun+","+item.getQuantity()+"]");
            }

        }

        return result;
    }

}
