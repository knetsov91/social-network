package social.com.userservice.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotEmpty
    @Size(min = 2, message = "Username must be at least 2 symbols.")
    private String username;

    @NotEmpty
    @Size(min = 8, message = "Password must be at least 8 symbols.")
    private String password;
}