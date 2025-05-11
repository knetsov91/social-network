package social.com.userservice.config;

import feign.Client;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

@Configuration
public class Config {

    @Bean
    public Client feignClient(SslBundles sslBundles) {
        SSLContext sslContext = sslBundles.getBundle("client").createSslContext();
        return new Client.Default(sslContext.getSocketFactory(), HttpsURLConnection.getDefaultHostnameVerifier());
    }
}
