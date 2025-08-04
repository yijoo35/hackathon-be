package hackathon.bigone.sunsak.accounts.user.controller;

import hackathon.bigone.sunsak.accounts.user.dto.SignupRequestDto;
import hackathon.bigone.sunsak.accounts.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/user")
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signup(@RequestBody SignupRequestDto rqDto){
        userService.create(rqDto);

        return ResponseEntity.ok(
                Map.of("message", "회원가입이 완료되었습니다.")
        );
    }
}
