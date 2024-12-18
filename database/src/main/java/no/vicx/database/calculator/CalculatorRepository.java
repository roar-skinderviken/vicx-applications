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
    Page<CalcEntry> findAllBy(Pageable pageable);

    @Query("SELECT c.id FROM CalcEntry c WHERE c.username = :username")
    Set<Long> findAllIdsByUsername(@Param("username") String username);

    @Modifying
    void deleteByIdIn(List<Long> ids);

    /**
     * Deletes old calculation entries created by anonymous users.
     * <p>
     * This method uses a custom query to perform a bulk delete operation,
     * avoiding the need to fetch records into memory and delete them one by one.
     * </p>
     *
     * @param createdAt the threshold date/time; all records with a creation time
     *                  earlier than this will be deleted
     */
    @Modifying
    @Query("DELETE FROM CalcEntry c WHERE (c.username IS NULL OR c.username = 'anonymousUser') AND c.createdAt < :createdAt")
    void deleteAllByCreatedAtBeforeAndUsernameNull(LocalDateTime createdAt);
}
