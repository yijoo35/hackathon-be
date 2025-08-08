package hackathon.bigone.sunsak.foodbox.foodbox.repository;

import hackathon.bigone.sunsak.foodbox.foodbox.entity.FoodBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodBoxRepository extends JpaRepository<FoodBox, Long> {
}
