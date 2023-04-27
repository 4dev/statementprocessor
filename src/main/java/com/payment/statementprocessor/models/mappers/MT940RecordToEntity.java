package com.payment.statementprocessor.models.mappers;

import com.payment.statementprocessor.models.MT940_Record;
import com.payment.statementprocessor.models.entities.MT940_Entity;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface MT940RecordToEntity {
  MT940_Entity mapRecordToEntity(MT940_Record mt940Record);
}
