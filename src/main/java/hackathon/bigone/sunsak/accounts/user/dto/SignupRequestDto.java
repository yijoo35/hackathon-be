package hackathon.bigone.sunsak.accounts.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequestDto { //요청
    @JsonProperty("user_id") //json에서 user_id로 보이게
    private Long id;

    @NotNull(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "아이디는 필수입니다.")
    private String username; //중복 금지
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String repeatPw;
}
