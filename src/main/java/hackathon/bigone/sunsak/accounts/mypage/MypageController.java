package hackathon.bigone.sunsak.accounts.mypage;

import hackathon.bigone.sunsak.global.security.jwt.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    @GetMapping("")
   public ResponseEntity<?> getMypage(Authentication authentication){
        if(authentication==null){
            return ResponseEntity.ok(
                    Map.of("message", "로그인 하고 순삭의 다양한 서비스를 경험해보세요!")
            );
        }
        CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "nickname", user.getNickname(),
                "username", user.getUsername()
        ));
   }
}
