package posts.social.com.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

    @Value("${auth.url}")
    private String authUrl;

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .baseUrl(authUrl)
                .build();
    }
}
