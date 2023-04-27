package com.payment.statementprocessor.processor;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Component
public class SatementValidation {

  public boolean something() {
    Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher("I am a string");
    return m.find();
  }

}
