package ru.dins.testtask.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Column(name = "phone_number")
    @Pattern(regexp = "^\\d{11}$", message = "Invalid phone number")
    String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User owner;

    @PrePersist
    public void prePersist() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(this);
        if (!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }
}
