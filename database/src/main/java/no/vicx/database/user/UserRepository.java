package no.vicx.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<VicxUser, Long> {
    @Query("SELECT u FROM VicxUser u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<VicxUser> findByUsername(String username);
}
