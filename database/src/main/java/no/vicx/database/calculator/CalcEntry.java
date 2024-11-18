package no.vicx.database.calculator;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class CalcEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long firstValue;

    private long secondValue;

    private CalculatorOperation operation;

    private long result;

    private String username;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public CalcEntry() {
    }

    /**
     * Value constructor.
     *
     * @param firstValue first value
     * @param secondValue second value
     * @param operation operation
     * @param result result
     * @param username username
     */
    public CalcEntry(
            long firstValue,
            long secondValue,
            CalculatorOperation operation,
            long result,
            String username) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.operation = operation;
        this.result = result;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(long firstValue) {
        this.firstValue = firstValue;
    }

    public long getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(long secondValue) {
        this.secondValue = secondValue;
    }

    public CalculatorOperation getOperation() {
        return operation;
    }

    public void setOperation(CalculatorOperation operation) {
        this.operation = operation;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
