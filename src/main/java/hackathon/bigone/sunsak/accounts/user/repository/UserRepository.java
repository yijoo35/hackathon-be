package hackathon.bigone.sunsak.accounts.user.repository;

import hackathon.bigone.sunsak.accounts.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SiteUser, Long> {
    boolean existsByUsername(String username);

    Optional<SiteUser> findByUsername(String username);
}
