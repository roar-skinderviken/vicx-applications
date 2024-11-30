package no.vicx.database.calculator;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a calculation entry that records a mathematical operation and its result.
 */
@Data
@NoArgsConstructor
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
}
