package social.com.userservice.common;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CurrentLocalDateTime implements TimeProvider{

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
