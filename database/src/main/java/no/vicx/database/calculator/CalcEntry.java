package no.vicx.database.calculator;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a calculation entry that records a mathematical operation and its result.
 */
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

    // Constants for null-check messages
    static final String OPERATION_MUST_NOT_BE_NULL = "Operation must not be null";

    /**
     * Default constructor.
     */
    public CalcEntry() {
    }

    /**
     * Constructs a new {@code CalcEntry} instance with the specified values.
     *
     * @param firstValue  the first operand of the calculation.
     * @param secondValue the second operand of the calculation.
     * @param operation   the mathematical operation performed; must not be null.
     * @param result      the result of the calculation.
     * @param username    the username of the user who performed the calculation; may be null.
     * @throws NullPointerException if {@code operation} is null.
     */
    public CalcEntry(
            long firstValue,
            long secondValue,
            CalculatorOperation operation,
            long result,
            String username) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.operation = Objects.requireNonNull(operation, OPERATION_MUST_NOT_BE_NULL);
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
