package dev.steampunkorder.repository;

import dev.steampunkorder.domain.WishList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findAllByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<WishList> findByIdAndUserId(Long wishListId, Long userId);
}
