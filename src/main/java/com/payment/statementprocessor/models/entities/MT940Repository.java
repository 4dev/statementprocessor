package com.payment.statementprocessor.models.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MT940Repository extends CrudRepository<MT940_Entity, Long> {
}
