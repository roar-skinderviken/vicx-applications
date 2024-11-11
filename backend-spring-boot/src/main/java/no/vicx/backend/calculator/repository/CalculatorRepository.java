package no.vicx.backend.calculator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CalculatorRepository extends CrudRepository<CalculatorEntity, Long> {
    Page<CalculatorEntity> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT c.id FROM calc_entry c WHERE c.username = :username")
    Set<Long> findAllIdsByUsername(@Param("username") String username);

    void deleteByIdIn(List<Long> ids);
}
