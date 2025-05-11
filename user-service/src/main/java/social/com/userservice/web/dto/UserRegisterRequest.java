package social.com.userservice.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @NotEmpty(message = "Username is required")
    @Size(min = 2, message = "Username must be at least 2 symbols.")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 2, message = "Password must be at least 8 symbols.")
    private String password;

    @NotEmpty(message = "Password confirmation is required")
    @Size(min = 2, message = "Password confirmation must be at least 8 symbols.")
    private String confirmPassword;

}