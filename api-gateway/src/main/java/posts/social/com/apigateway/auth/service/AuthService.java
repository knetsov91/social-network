package posts.social.com.apigateway.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import posts.social.com.apigateway.ServiceUnavailable;
import posts.social.com.apigateway.dto.TokenRequest;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private WebClient webClient;

    public AuthService( WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> validateToken(String token) {
        Mono<ResponseEntity<Void>> bodilessEntity = webClient.post().uri("/validate")
                .body(Mono.just(new TokenRequest(token)), TokenRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ex -> {
                    return Mono.error(new ServiceUnavailable("503"));
                })
                .onStatus(HttpStatusCode::isError, ex -> {
                    log.error(ex.toString());
                    return Mono.error(new RuntimeException(ex.toString()));

                })
                .toBodilessEntity();

        return bodilessEntity.doOnSuccess( resp -> {
            log.info(resp.getStatusCode().toString());
        }).doOnError(er -> {
            log.error(er.getMessage());
        }).then();
    }
}