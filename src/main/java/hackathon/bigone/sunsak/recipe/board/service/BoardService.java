package hackathon.bigone.sunsak.recipe.board.service;

import hackathon.bigone.sunsak.accounts.user.entity.SiteUser;
import hackathon.bigone.sunsak.recipe.board.dto.BoardDto;
import hackathon.bigone.sunsak.recipe.board.dto.StepDto; // 추가
import hackathon.bigone.sunsak.recipe.board.entity.Board;
import hackathon.bigone.sunsak.recipe.board.entity.Step; // 추가
import hackathon.bigone.sunsak.recipe.board.repository.BoardRepository;
import hackathon.bigone.sunsak.recipe.board.repository.StepRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor; // 추가
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 생성자 자동 생성
public class BoardService {

    private final BoardRepository boardRepository;
    private final StepRepository stepRepository;

    @Transactional
    public Board create(BoardDto boardDto, SiteUser author) { // 중괄호 추가
        Board newBoard = new Board();
        newBoard.setTitle(boardDto.getTitle());
        newBoard.setAuthor(author);

        if (boardDto.getSteps() != null && !boardDto.getSteps().isEmpty()) {
            for (StepDto stepDto : boardDto.getSteps()) {
                Step step = new Step();
                step.setStepNumber(stepDto.getStepNumber());
                step.setDescription(stepDto.getDescription());
                step.setBoard(newBoard);

                newBoard.getSteps().add(step);
            }
        }
        return boardRepository.save(newBoard);
    } // 중괄호 추가
}