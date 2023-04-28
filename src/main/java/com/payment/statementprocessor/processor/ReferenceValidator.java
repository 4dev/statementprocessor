package com.payment.statementprocessor.processor;

import com.payment.statementprocessor.models.entities.MT940Repository;
import com.payment.statementprocessor.models.entities.MT940_Entity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Log4j2
@Component
@RequiredArgsConstructor
public class ReferenceValidator implements Tasklet {

  private final MT940Repository repository;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    List<MT940_Entity> mt940EntityList = new ArrayList<>();
    repository.findAll().forEach(e -> {
      int duplicateReferences = countDuplicateReferences(e);
      if (duplicateReferences > 1) {
        e.setHasDuplicateReference(TRUE);
        e.setDuplicateReferenceCount(duplicateReferences);
        mt940EntityList.add(e);
      }
    });
    repository.saveAll(mt940EntityList);
    return RepeatStatus.FINISHED;
  }

  private int countDuplicateReferences(MT940_Entity entity) {
    List<MT940_Entity> mt940EntityList = repository.findByReference(entity.getReference());
    return mt940EntityList.size();
  }
}
