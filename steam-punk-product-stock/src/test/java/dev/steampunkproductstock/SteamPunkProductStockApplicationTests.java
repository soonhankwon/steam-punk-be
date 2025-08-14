package dev.steampunkproductstock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("This test requires a running Redis instance and is an integration test.")
@SpringBootTest
class SteamPunkProductStockApplicationTests {

    @Test
    void contextLoads() {
    }

}
