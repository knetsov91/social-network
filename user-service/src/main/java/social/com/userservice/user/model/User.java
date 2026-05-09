package social.com.userservice.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="`user`")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean isActive;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
