package hackathon.bigone.sunsak.accounts.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponseDto { //응답
    @JsonProperty("user_id") //json에서 user_id로 보이게
    private Long id;
    private String nickname;

    private String username; //중복 금지

    private String message; //확인 메시지
}
