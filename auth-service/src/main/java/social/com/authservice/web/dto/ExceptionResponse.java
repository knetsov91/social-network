package social.com.authservice.web.dto;

import lombok.Data;

@Data
public class ExceptionResponse {
    private String message;
    private int stausCode;
}