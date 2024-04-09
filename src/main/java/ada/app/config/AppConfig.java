package ada.app.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    static {
        System.setProperty("java.awt.headless", "false");
    }

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf().setAppName("appName").setMaster("local[*]");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }
}
