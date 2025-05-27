package posts.social.com.apigateway;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import posts.social.com.apigateway.dto.ErrorResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@Order(-100)
public class CustomErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                          WebProperties resources,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources.getResources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::handleError);
    }

    private Mono<ServerResponse> handleError(ServerRequest request) {

        Map<String, Object> errorAttr = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        Throwable error = getError(request);

        ErrorResponse errorResponse = new ErrorResponse(
                (String) errorAttr.get("error"),
                 Integer.parseInt(errorAttr.get("status").toString()),
                error.getMessage());

        return  ServerResponse.status(Integer.parseInt(errorAttr.get("status").toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}
