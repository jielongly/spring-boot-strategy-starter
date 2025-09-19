package org.jl.strategy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainApplicationTests {

    @Autowired
    StrategyServiceFactory strategyServiceFactory;

    @Test
    void contextLoads() {
        IPlayService playService = strategyServiceFactory.get(PlayEnum.USER, IPlayService.class);
        playService.play();
    }
}
