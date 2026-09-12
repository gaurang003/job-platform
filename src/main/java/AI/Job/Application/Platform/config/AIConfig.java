package AI.Job.Application.Platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Value("${ai.api.key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}