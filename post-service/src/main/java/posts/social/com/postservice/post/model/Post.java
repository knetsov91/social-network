package posts.social.com.postservice.post.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="posts_likes", joinColumns = @JoinColumn(name="post_id"))
    @Column(name="user_id")
    private List<UUID> likes = new ArrayList<>();
}