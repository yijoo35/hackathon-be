package hackathon.bigone.sunsak.accounts.user.entity;

import hackathon.bigone.sunsak.base.entity.BaseTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="SiteUser")
@AttributeOverride(name = "id", column = @Column(name = "user_id")) //id를 user_id로 바꿈
public class SiteUser extends BaseTime { //id 상속
    private String nickname;

    @Column(unique = true)
    private String username; //중복 금지

    private String password;
}
