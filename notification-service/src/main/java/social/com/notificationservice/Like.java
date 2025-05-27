package social.com.notificationservice;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Like {

    private UUID post;
    private UUID user;
    private LocalDateTime date;

}