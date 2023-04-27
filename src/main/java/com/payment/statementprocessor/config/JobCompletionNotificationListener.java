package com.payment.statementprocessor.config;

import com.payment.statementprocessor.models.entities.MT940Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

  private final MT940Repository mt940Repository;

  @Override
  public void afterJob(JobExecution jobExecution) {
    if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");

      mt940Repository.findAll().forEach(mt940Record -> log.info("Found <{{}}> in the database.", mt940Record));
    }
  }
}
