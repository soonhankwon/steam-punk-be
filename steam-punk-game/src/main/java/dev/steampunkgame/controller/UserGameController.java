package dev.steampunkgame.controller;

import dev.steampunkgame.dto.request.UserGameAddRequest;
import dev.steampunkgame.dto.request.UserGamePlayRequest;
import dev.steampunkgame.dto.response.UserGameAddResponse;
import dev.steampunkgame.dto.response.UserGameHistoryGetResponse;
import dev.steampunkgame.dto.response.UserGamePlayResponse;
import dev.steampunkgame.service.UserGameService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/games")
public class UserGameController {

    private final UserGameService userGameService;

    @PostMapping
    public ResponseEntity<List<UserGameAddResponse>> addUserGame(@RequestBody UserGameAddRequest request) {
        List<UserGameAddResponse> res = userGameService.addUserGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping
    public ResponseEntity<UserGamePlayResponse> playGame(@RequestBody UserGamePlayRequest request) {
        UserGamePlayResponse res = userGameService.playGame(request);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserGameHistoryGetResponse> getUserGameHistory(@PathVariable Long userId,
                                                                         @RequestParam(value = "productId") Long productId) {
        UserGameHistoryGetResponse res = userGameService.findUserGameHistory(userId, productId);
        return ResponseEntity.ok().body(res);
    }
}
