package com.payment.statementprocessor.processor;

import com.payment.statementprocessor.models.MT940_Record;
import io.micrometer.core.instrument.util.IOUtils;
import jakarta.xml.bind.JAXBContext;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class SatementValidationTest {

  private SatementValidation satementValidation = new SatementValidation();
  private List<MT940_Record> mt940Records = new ArrayList<>();

  @SneakyThrows
  @BeforeAll
  static void beforeAll() {
    JAXBContext context = JAXBContext.newInstance(MT940_Record.class);
    String xml = IOUtils.toString(
            SatementValidationTest.class.getResourceAsStream("/input/records.xml"));
    List<MT940_Record> unmarshal = (List<MT940_Record>) context.createUnmarshaller()
            .unmarshal(new StringReader(xml));
  }


  @Test
  void testStringContainsPlus() {
    assertTrue(satementValidation.something());
    log.info("test works");
  }



}