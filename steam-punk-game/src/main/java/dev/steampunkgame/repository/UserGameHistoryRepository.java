package dev.steampunkgame.repository;

import dev.steampunkgame.domain.UserGameHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGameHistoryRepository extends JpaRepository<UserGameHistory, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<UserGameHistory> findByProductIdAndUserId(Long userGameHistoryId, Long userId);
}
