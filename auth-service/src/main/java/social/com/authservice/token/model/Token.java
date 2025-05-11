package social.com.authservice.token.model;

import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.util.UUID;

@RedisHash("jwtBlacklist")
public class Token implements Serializable {
    private UUID id;
    private String token;

    public Token(UUID id, String token) {
        this.id = id;
        this.token = token;
    }
}
