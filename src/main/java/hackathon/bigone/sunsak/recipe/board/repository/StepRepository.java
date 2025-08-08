package hackathon.bigone.sunsak.recipe.board.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import hackathon.bigone.sunsak.recipe.board.entity.Step;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepository extends JpaRepository<Step,Long> {
}
