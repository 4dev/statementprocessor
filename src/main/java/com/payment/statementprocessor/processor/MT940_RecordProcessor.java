package com.payment.statementprocessor.processor;

import com.payment.statementprocessor.models.MT940_Record;
import com.payment.statementprocessor.models.entities.MT940_Entity;
import com.payment.statementprocessor.models.mappers.MT940RecordToEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Log4j2
@Component
@RequiredArgsConstructor
public class MT940_RecordProcessor implements ItemProcessor<MT940_Record, MT940_Entity> {

  private final MT940RecordToEntity mapper;

  @Override
  public MT940_Entity process(MT940_Record item) {
    MT940_Entity mt940Entity = mapper.mapRecordToEntity(item);
    boolean isMutationValid = isValidMutation(item.getMutation(), item.getStartBalance(), item.getEndBalance());
    mt940Entity.setValidMutation(isMutationValid);
    return mt940Entity;
  }

  private boolean isValidMutation(String mutation, String startBalance, String endBalance) {
    try {
      BigDecimal calculatedBalance = new BigDecimal(startBalance).add(new BigDecimal(mutation));
      return calculatedBalance.equals(new BigDecimal(endBalance));
    } catch (NumberFormatException e) {
      log.error("Calculated balance could not be calculated because of a non numeric value", e);
      return Boolean.FALSE;
    }
  }
}
