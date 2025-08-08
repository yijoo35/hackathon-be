package hackathon.bigone.sunsak.foodbox.nlp.service;

import hackathon.bigone.sunsak.foodbox.ocr.dto.OcrExtractedItem;
import jakarta.annotation.PostConstruct;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NlpService {
    private Komoran komoran;

    @PostConstruct
    public void initKomoran() {
        komoran = new Komoran(DEFAULT_MODEL.FULL);
        try {
            //사용자 사전 사용
            String userDictPath = new ClassPathResource("data/user_dict.txt").getFile().getAbsolutePath();
            komoran.setUserDic(userDictPath);
            System.out.println("KOMORAN 사용자 사전 로드 완료: " + userDictPath);
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
