package hackathon.bigone.sunsak.recipe.board.service;

import hackathon.bigone.sunsak.recipe.board.dto.BoardDto;
import hackathon.bigone.sunsak.recipe.board.entity.Board;
import hackathon.bigone.sunsak.recipe.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    public Board createBoard(BoardDto boardDto){
        Board board = new Board();
        board.setContent(boardDto.getContent());
        return boardRepository.save(board);
    }
}
