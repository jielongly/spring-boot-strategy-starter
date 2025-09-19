package org.jl.strategy;


import java.util.Arrays;

@StrategyService
public class DemoStrategyService implements IStrategyService<PlayEnum>, IPlayService {


    @Override
    public PlayEnum getEnum() {
        return PlayEnum.USER;
    }

    @Override
    public void play() {
        //do nothing
        System.out.println("hello");
    }
}

