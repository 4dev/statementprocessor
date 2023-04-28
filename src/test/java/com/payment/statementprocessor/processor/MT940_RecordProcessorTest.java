package com.payment.statementprocessor.processor;

import com.payment.statementprocessor.models.MT940_Record;
import com.payment.statementprocessor.models.entities.MT940_Entity;
import com.payment.statementprocessor.models.mappers.MT940RecordToEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class MT940_RecordProcessorTest {

  private static MT940_RecordProcessor recordProcessor = new MT940_RecordProcessor(Mappers.getMapper(MT940RecordToEntity.class));

  @Test
  @DisplayName("Maps valid MT940_Record to MT940_Entity")
  void mapValidRecordToEntity() {

    MT940_Record record = getValidRecord().build();

    MT940_Entity entity = recordProcessor.process(record);

    assertNotNull(entity);
    assertEquals(entity.getReference(), record.getReference());
    assertEquals(entity.getAccountNumber(), record.getAccountNumber());
    assertEquals(entity.getDescription(), record.getDescription());
    assertEquals(entity.getStartBalance(), record.getStartBalance());
    assertEquals(entity.getMutation(), record.getMutation());
    assertEquals(entity.getEndBalance(), record.getEndBalance());
  }

  @Test
  @DisplayName("Valid record should map to an entity with validMutation - true")
  void validRecordHasValidMutation() {
    MT940_Record record = getValidRecord().build();

    MT940_Entity entity = recordProcessor.process(record);
    assertNotNull(entity);
    assertTrue(entity.getValidMutation());
  }

  @Test
  @DisplayName("Non valid record should map to an entity with validMutation - false")
  void nonValidRecordHasInValidMutation() {
    MT940_Record record = getNonValidRecord().build();

    MT940_Entity entity = recordProcessor.process(record);
    assertNotNull(entity);
    assertFalse(entity.getValidMutation());
  }

  @Test
  @DisplayName("A non numeric value for mutation should map to an entity with validMutation - false")
  void nonNumericMutationHasInvalidMutation() {
    MT940_Record record = getNonValidRecord()
            .mutation("bad value")
            .build();

    MT940_Entity entity = recordProcessor.process(record);
    assertNotNull(entity);
    assertFalse(entity.getValidMutation());
  }

  @Test
  @DisplayName("A non numeric value for startBalance should map to an entity with validMutation - false")
  void nonNumericStartBalanceHasInvalidMutation() {
    MT940_Record record = getNonValidRecord()
            .startBalance("bad value")
            .build();

    MT940_Entity entity = recordProcessor.process(record);
    assertNotNull(entity);
    assertFalse(entity.getValidMutation());
  }

  @Test
  @DisplayName("A non numeric value for endBalance should map to an entity with validMutation - false")
  void nonNumericEndBalanceHasInvalidMutation() {
    MT940_Record record = getNonValidRecord()
            .endBalance("bad value")
            .build();

    MT940_Entity entity = recordProcessor.process(record);
    assertNotNull(entity);
    assertFalse(entity.getValidMutation());
  }

  @Test
  @DisplayName("A negative endBalance with a valid mutation should map to an entity with validMutation - true")
  void nonNumericAmountsHasInvalidMutation() {
    MT940_Record record = getNonValidRecord()
            .startBalance("bad value")
            .mutation("bad value")
            .endBalance("bad value")
            .build();

    MT940_Entity entity = recordProcessor.process(record);
    assertNotNull(entity);
    assertFalse(entity.getValidMutation());
  }

  @Test
  @DisplayName("A non numeric value for all amounts should map to an entity with validMutation - true")
  void validNegativeEndBalanceHasValidMutation() {
    MT940_Record record = getNonValidRecord()
            .startBalance("19.56")
            .mutation("-44.84")
            .endBalance("-25.28")
            .build();

    MT940_Entity entity = recordProcessor.process(record);
    assertNotNull(entity);
    assertTrue(entity.getValidMutation());
  }

  private MT940_Record.MT940_RecordBuilder getValidRecord() {
    return MT940_Record.builder()
            .reference("176186")
            .accountNumber("NL32RABO0195610843")
            .description("A valid record")
            .startBalance("6368")
            .mutation("-939")
            .endBalance("5429");
  }

  private MT940_Record.MT940_RecordBuilder getNonValidRecord() {
    return MT940_Record.builder()
            .reference("176186")
            .accountNumber("NL32RABO0195610843")
            .description("A non valid record")
            .startBalance("5429")
            .mutation("-939")
            .endBalance("6368");
  }

}