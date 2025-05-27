package social.com.userservice.follow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private UUID followerId;

    @Column
    private UUID followeeId;

    @Column
    private LocalDateTime createdAt;

}