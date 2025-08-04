package hackathon.bigone.sunsak.accounts.user.controller;

import hackathon.bigone.sunsak.accounts.user.dto.LoginRequestDto;
import hackathon.bigone.sunsak.accounts.user.dto.SignupRequestDto;
import hackathon.bigone.sunsak.accounts.user.dto.SignupResponseDto;
import hackathon.bigone.sunsak.accounts.user.entity.SiteUser;
import hackathon.bigone.sunsak.accounts.user.service.SignupService;
import hackathon.bigone.sunsak.global.security.jwt.JwtTokenProvider;
import hackathon.bigone.sunsak.global.security.jwt.dto.JwtTokenDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@AllArgsConstructor
public class UserController {
    private final SignupService signupService;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto rqDto){
        SiteUser user = signupService.create(rqDto);

        SignupResponseDto rsDto = SignupResponseDto
                .builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .message("회원가입이 완료되었습니다.")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(rsDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );

            JwtTokenDto token = jwtTokenProvider.createToken(dto.getUsername());
            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("아이디 또는 비밀번호를 다시 확인하세요.");
        }
    }
}
