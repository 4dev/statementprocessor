package com.payment.statementprocessor.processor;

import com.payment.statementprocessor.models.MT940_Record;
import com.payment.statementprocessor.models.entities.MT940_Entity;
import com.payment.statementprocessor.models.mappers.MT940RecordToEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MT940_RecordProcessor implements ItemProcessor<MT940_Record, MT940_Entity> {

  private final MT940RecordToEntity mapper;

  @Override
  public MT940_Entity process(MT940_Record item) {
    return mapper.mapRecordToEntity(item);
  }
}
