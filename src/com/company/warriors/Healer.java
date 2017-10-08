package com.company.warriors;

/**
 * Created by BN on 01.04.2017.
 */
public class Healer extends Unit implements IHealer {
    public Healer() {
        super(3, 5);
    }

    @Override
    public int getHealPower() {
        return 2;
    }

    @Override
    public int getMaxHealCount() {
        return 1;
    }
}
