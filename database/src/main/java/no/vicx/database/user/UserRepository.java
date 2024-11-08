package no.vicx.database.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<VicxUser, Long> {
    Optional<VicxUser> findByUsername(String username);
}
