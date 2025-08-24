package com.koushik.generate_logs.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class HelloWorld {

   private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);

   public static void createLogs() throws InterruptedException {
       int k = 0;
       boolean b = true;
       Random random = new Random();
       while (b) {
           int level = random.nextInt(4); // 0: info, 1: warn, 2: debug, 3: error
           String time = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
           switch (level) {
               case 0:
                   logger.info("Hello World sent to Kafka topic at " + time);
                   break;
               case 1:
                   logger.warn("Something might be wrong at " + time);
                   break;
               case 2:
                   logger.debug("Debugging at " + time);
                   break;
               case 3:
                   logger.error("An error occurred at " + time);
                   break;
           }
           k++;
           b = k < Integer.MAX_VALUE;
           Thread.sleep(1000);
       }
   }
}