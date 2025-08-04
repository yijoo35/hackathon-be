package hackathon.bigone.sunsak.accounts.user.repository;

import hackathon.bigone.sunsak.accounts.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<SiteUser, Long> {
}
