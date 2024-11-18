package no.vicx.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    Optional<UserImage> findByUserUsername(String username);

    @Modifying
    @Query("DELETE FROM UserImage ui WHERE ui.user.username = :username")
    void deleteByUserUsername(String username);
}