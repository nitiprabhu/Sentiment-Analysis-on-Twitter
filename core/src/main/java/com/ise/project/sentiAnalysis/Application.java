
package com.ise.project.sentiAnalysis;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;



/**
  * @author Nitish
 */


@SpringBootApplication
public class Application {

    public static void main(String args[]) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class);

        Processor processor = (Processor) context.getBean("getProcessor");
        try {
            processor.analyzeTrends();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}