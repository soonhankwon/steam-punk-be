package dev.steampunkgame.service;

import dev.steampunkgame.common.enumtype.ErrorCode;
import dev.steampunkgame.common.exception.ApiException;
import dev.steampunkgame.domain.UserGameHistory;
import dev.steampunkgame.dto.GameDTO;
import dev.steampunkgame.dto.request.UserGameAddRequest;
import dev.steampunkgame.dto.request.UserGamePlayRequest;
import dev.steampunkgame.dto.response.UserGameAddResponse;
import dev.steampunkgame.dto.response.UserGameHistoryGetResponse;
import dev.steampunkgame.dto.response.UserGamePlayResponse;
import dev.steampunkgame.repository.UserGameHistoryRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserGameService {

    private final UserGameHistoryRepository userGameHistoryRepository;

    // 유저의 게임 주문 결제 후 호출될 비즈니스 로직: 게임을 플레이하지 않은 기본 이력으로 만들어짐
    @Transactional
    public UserGameAddResponse addUserGame(UserGameAddRequest request) {
        List<UserGameHistory> userGameHistories = new ArrayList<>();
        Long userId = request.userId();
        request.productIds().forEach(id -> {
            if (userGameHistoryRepository.existsByUserIdAndProductId(userId, id)) {
                throw new ApiException(ErrorCode.EXISTS_USER_GAME_HISTORY);
            }
            UserGameHistory userGameHistory = UserGameHistory.ofNotPlayedState(id, request);
            userGameHistories.add(userGameHistory);
        });
        userGameHistoryRepository.saveAll(userGameHistories);

        List<GameDTO> gameDTOS = userGameHistories.stream()
                .map(GameDTO::from)
                .toList();
        return UserGameAddResponse.of(userId, gameDTOS);
    }

    @Transactional
    public UserGamePlayResponse playGame(UserGamePlayRequest request) {
        UserGameHistory userGameHistory = userGameHistoryRepository.findByProductIdAndUserId(request.productId(),
                        request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_GAME_HISTORY));

        userGameHistory.played();
        return UserGamePlayResponse.from(userGameHistory);
    }

    @Transactional(readOnly = true)
    public UserGameHistoryGetResponse findUserGameHistory(Long userId, Long productId) {
        UserGameHistory userGameHistory = userGameHistoryRepository.findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_USER_GAME_HISTORY));

        return UserGameHistoryGetResponse.from(userGameHistory);
    }
}
