package posts.social.com.apigateway.config;

import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class Config {

    @Value("${auth.url}")
    private String authUrl;

    private ResourceLoader resourceLoader;
    @Value("${jks.webclientstore.key}")
    private String keystoreKey;

    public Config(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public WebClient webClient() throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        try ( InputStream fis = resourceLoader.getResource("classpath:keystore/webclientstore").getInputStream()) {
            keystore.load(fis,keystoreKey.toCharArray());
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keystore);

        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().trustManager(tmf);

        HttpClient client = HttpClient.create()
                .secure(sslContextSpec -> {
                    try {
                        sslContextSpec.sslContext(sslContextBuilder.build());
                    } catch (SSLException e) {
                        throw new RuntimeException(e);
                    }
                });
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(authUrl)
                .build();
    }
}
