package com.payment.statementprocessor.processor;

import com.payment.statementprocessor.models.MT940_Record;
import com.payment.statementprocessor.models.entities.MT940Repository;
import com.payment.statementprocessor.models.entities.MT940_Entity;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferenceValidatorTest {

  @Mock
  private MT940Repository repository;
  @InjectMocks
  private ReferenceValidator referenceValidator;

  @Test
  @DisplayName("Repeat status is set to FINISHED when record are found in DB")
  void repeatStatusSetToFinishedRecordFound() {
    when(repository.findAll()).thenReturn(entityList());
    RepeatStatus execute = this.referenceValidator.execute(null, null);
    assertEquals(RepeatStatus.FINISHED, execute);
  }

  @Test
  @DisplayName("Repeat status is set to FINISHED when no records are found in DB")
  void repeatStatusSetToFinishedEmptyRecords() {
    when(repository.findAll()).thenReturn(new ArrayList<>());
    RepeatStatus execute = this.referenceValidator.execute(null, null);
    assertEquals(RepeatStatus.FINISHED, execute);
  }

  @Test
  @DisplayName("Values are saved in DB when process completed")
  void saveAllisCalled() {
    List<MT940_Entity> entityList = entityListWithDuplicates();
    when(repository.findAll()).thenReturn(entityList);
    when(repository.findByReference("176186")).thenReturn(entityList);
    RepeatStatus execute = this.referenceValidator.execute(null, null);
    verify(repository, times(1)).saveAll(any());
  }

  private List<MT940_Entity> entityListWithDuplicates() {
    ArrayList<MT940_Entity> entities = new ArrayList<>();
    entities.addAll(entityList());
    entities.addAll(entityList());
    return entities;
  }

  private List<MT940_Entity> entityList() {
    ArrayList<MT940_Entity> entities = new ArrayList<>();
    entities.add(getValidEntity().build());
    entities.add(getNonValidEntity().build());
    return entities;
  }

  private MT940_Entity.MT940_EntityBuilder getValidEntity() {
    return MT940_Entity.builder()
            .reference("176186")
            .accountNumber("NL32RABO0195610843")
            .description("A valid record")
            .startBalance("6368")
            .mutation("-939")
            .endBalance("5429")
            .validMutation(true);
  }

  private MT940_Entity.MT940_EntityBuilder getNonValidEntity() {
    return MT940_Entity.builder()
            .reference("176186")
            .accountNumber("NL32RABO0195610843")
            .description("A non valid record")
            .startBalance("5429")
            .mutation("-939")
            .endBalance("6368")
            .validMutation(false);
  }



  @XmlRootElement(name = "records")
  @XmlAccessorType(XmlAccessType.FIELD)
  static class Records
  {
    @XmlElement(name = "record")
    private List<MT940_Record> records = null;

    public List<MT940_Record> getRecords() {
      return records;
    }

    public void setRecords(List<MT940_Record> records) {
      this.records = records;
    }
  }

}