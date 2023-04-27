package com.payment.statementprocessor.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class MT940_Entity {

  @Id
  @GeneratedValue
  private Long id;
  private String reference;
  private String description;
  private String accountNumber;
  private String startBalance;
  private String mutation;
  private String endBalance;

}
