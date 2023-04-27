package com.payment.statementprocessor.config;

import com.payment.statementprocessor.models.MT940_Record;
import com.payment.statementprocessor.models.entities.MT940_Entity;
import com.payment.statementprocessor.processor.MT940_RecordProcessor;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

  @Value("${statements.input.directory}")
  private String inputPath;

  private final MT940_RecordProcessor entityMapper;
  private final EntityManagerFactory entityManagerFactory;

  @Bean
  @SneakyThrows
  public ItemReader<MT940_Record> csvCollector() {
    ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = patternResolver.getResources(inputPath+"/*.csv");
    MultiResourceItemReader<MT940_Record> reader = new MultiResourceItemReader<>();
    reader.setResources(resources);
    reader.setDelegate(csvReader());
    return reader;
  }

  @Bean
  public FlatFileItemReader<MT940_Record> csvReader() {
    FlatFileItemReader<MT940_Record> reader = new FlatFileItemReader<>();
    reader.setLineMapper(new DefaultLineMapper<>() {{
      setLineTokenizer(new DelimitedLineTokenizer() {{
        setNames("reference",
                "accountNumber",
                "description",
                "startBalance",
                "mutation",
                "endBalance");
      }});
      setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
        setTargetType(MT940_Record.class);
      }});
    }});
    return reader;
  }

  @Bean
  @SneakyThrows
  public MultiResourceItemReader xmlCollector() {
    ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = patternResolver.getResources(inputPath+"/*.xml");
    return new MultiResourceItemReaderBuilder()
            .name("xmlMultiReader")
            .resources(resources)
            .delegate(xmlReader())
            .build();
  }

  @Bean
  public StaxEventItemReader<MT940_Record> xmlReader() {
    Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
    studentMarshaller.setClassesToBeBound(MT940_Record.class);

    return new StaxEventItemReaderBuilder<MT940_Record>()
            .name("xmlReader")
            .resource(new ClassPathResource("input/records.xml"))
            .addFragmentRootElements("record")
            .unmarshaller(studentMarshaller)
            .build();
  }

  @Bean
  public JpaItemWriter<MT940_Entity> writer() {
    return new JpaItemWriterBuilder<MT940_Entity>()
            .entityManagerFactory(entityManagerFactory)
            .build();
  }

  @Bean
  public Job statementValidationJob(JobRepository jobRepository,
                           JobCompletionNotificationListener listener, Step csv_read_in, Step xml_read_in) {
    return new JobBuilder("statementValidationJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .start(csv_read_in).on("COMPLETED")
            .to(xml_read_in).on("COMPLETED")
            .end().build()
            .build();
  }

  @Bean
  public Step csv_read_in(JobRepository jobRepository,
                    PlatformTransactionManager transactionManager) {
    return new StepBuilder("csv_read_in", jobRepository)
            .<MT940_Record, MT940_Entity> chunk(10, transactionManager)
            .reader(csvCollector())
            .processor(entityMapper)
            .writer(writer())
            .build();
  }

  @Bean
  public Step xml_read_in(JobRepository jobRepository,
                    PlatformTransactionManager transactionManager) {
    return new StepBuilder("xml_read_in", jobRepository)
            .<MT940_Record, MT940_Entity> chunk(10, transactionManager)
            .reader(xmlCollector())
            .processor(entityMapper)
            .writer(writer())
            .build();
  }

  @Bean
  public Step step3(JobRepository jobRepository,
                    PlatformTransactionManager transactionManager) {
    return new StepBuilder("step3", jobRepository)
            .<MT940_Record, MT940_Entity> chunk(10, transactionManager)
            .reader(xmlReader())
            .processor(entityMapper)
            .writer(writer())
            .build();
  }
}
