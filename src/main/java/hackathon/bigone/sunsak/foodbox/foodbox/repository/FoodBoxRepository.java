package hackathon.bigone.sunsak.foodbox.foodbox.repository;

import hackathon.bigone.sunsak.foodbox.foodbox.entity.FoodBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodBoxRepository extends JpaRepository<FoodBox, Long> {
    Optional<FoodBox> findByUserIdAndName(Long userId, String name);

    List<FoodBox> findAllByUserIdAndNameIn(Long userId, Collection<String> names);

    @Query("""
        SELECT f FROM FoodBox f
        WHERE f.userId = :userId
        ORDER BY 
          CASE WHEN f.expiryDate IS NULL THEN 1 ELSE 0 END,
          f.expiryDate ASC,
          f.name ASC,
          f.id ASC
    """) //날짜 순 정렬 쿼리
    List<FoodBox> findAllSortedByUserId(@Param("userId") Long userId);
}
