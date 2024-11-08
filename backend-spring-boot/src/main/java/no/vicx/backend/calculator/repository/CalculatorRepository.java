package no.vicx.backend.calculator.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CalculatorRepository extends CrudRepository<CalculatorEntity, Long> {

    List<CalculatorEntity> findByIdNotOrderByIdDesc(Long id);
}
