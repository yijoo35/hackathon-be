package hackathon.bigone.sunsak.recipe.board.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class BoardDto {
    private String title;
    private List<StepDto> steps;

}
