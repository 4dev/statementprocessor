package com.payment.statementprocessor.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="PROCESSED_STATEMENTS")
public class MT940_Entity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String reference;
  private String description;
  private String accountNumber;
  private String startBalance;
  private String mutation;
  private String endBalance;
  private Boolean validMutation;
  private Boolean hasDuplicateReference;
  private Integer duplicateReferenceCount;

}
