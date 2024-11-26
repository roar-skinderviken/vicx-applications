package no.vicx.database.calculator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface CalculatorRepository extends CrudRepository<CalcEntry, Long> {
    Page<CalcEntry> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT c.id FROM CalcEntry c WHERE c.username = :username")
    Set<Long> findAllIdsByUsername(@Param("username") String username);

    @Modifying
    void deleteByIdIn(List<Long> ids);

    @Modifying
    void deleteAllByCreatedAtBeforeAndUsernameNull(LocalDateTime createdAt);
}
