package com.payment.statementprocessor.models.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MT940Repository extends JpaRepository<MT940_Entity, Long> {
  Iterable<MT940_Entity> findByValidMutationFalse();
  Iterable<MT940_Entity> findByHasDuplicateReferenceTrue();
  List<MT940_Entity> findByReference(String reference);
}
