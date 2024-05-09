package dev.steampunkapi.filter;

import dev.steampunkapi.parser.JwtParser;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtParser jwtParser;

    public JwtAuthenticationFilter(final JwtParser jwtParser) {
        super(Config.class);
        this.jwtParser = jwtParser;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            String accessToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (accessToken == null || accessToken.isEmpty()) {
                throw new JwtException("access token is null or empty");
            }

            jwtParser.getClaims(accessToken.substring(7));

            if (config.isPreLogger()) {
                log.info("PreFilter logging={}", config.getBaseMessage());
            }
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        if (config.isPostLogger()) {
                            log.info("PostFilter logging={}", config.getBaseMessage());
                        }
                    }));
        });
    }

    @Data
    @AllArgsConstructor
    public static class Config {
        // 로그 항목에 포함될 사용자 정의 메서지
        private String baseMessage;
        // 요청을 전달하기 전에 필터가 기록해야 하는지 여부 플래그
        private boolean preLogger;
        // 프록시 서비스로부터 응답을 받은 후 필터가 기록해야 하는지 여부를 나타내는 플래그
        private boolean postLogger;
    }
}
