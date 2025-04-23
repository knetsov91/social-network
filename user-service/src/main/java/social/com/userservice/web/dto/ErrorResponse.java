package social.com.userservice.web.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String code;
}
