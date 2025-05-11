package social.com.authservice.token.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import social.com.authservice.token.model.Token;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends CrudRepository<Token, UUID> {

    Optional<Token> findByToken(String token);
}