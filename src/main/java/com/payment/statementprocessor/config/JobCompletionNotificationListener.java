package com.payment.statementprocessor.config;

import com.payment.statementprocessor.models.entities.MT940Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import static org.springframework.batch.core.BatchStatus.COMPLETED;

@Log4j2
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

  private final MT940Repository mt940Repository;

  @Override
  public void afterJob(JobExecution jobExecution) {
    if(jobExecution.getStatus() == COMPLETED) {
      log.info("\n----------Monthly Statement Run Report-----------------");
      log.info("\n-------------------------------------------------------");
      log.info("\n-------------------------------------------------------");
      log.info("\n------Records found with invalid calculations----------");

      mt940Repository.findByValidMutationFalse().forEach(mt940Entity -> log.info(
              """
                      --
                      Record Transaction Reference : {}
                      Transaction Description : {}
                      --""", mt940Entity.getReference(), mt940Entity.getDescription())
      );

      log.info("\n-------------------------------------------------------");
      log.info("\n------Records found with non unique references---------");

      mt940Repository.findByHasDuplicateReferenceTrue().forEach(mt940Entity -> log.info(
              """        
                      --
                      Record Transaction Reference : {}
                      Transaction Description : {}
                      Duplicate Count : {}
                      --""", mt940Entity.getReference(), mt940Entity.getDescription(), mt940Entity.getDuplicateReferenceCount())
      );
    }
  }
}
