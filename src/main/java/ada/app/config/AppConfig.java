package ada.app.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    static {
        System.setProperty("java.awt.headless", "false");
    }
}
