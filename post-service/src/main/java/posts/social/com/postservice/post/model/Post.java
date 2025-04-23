package posts.social.com.postservice.post.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private UUID authorId;

    @UpdateTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<UUID> likes;
}