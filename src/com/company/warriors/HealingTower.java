package com.company.warriors;

/**
 * Created by BN on 01.04.2017.
 */
public class HealingTower extends Tower implements IHealer {
    public HealingTower() {
        super(30, 1);
    }

    @Override
    public int getHealPower() {
        return 15;
    }

    @Override
    public int getMaxHealCount() {
        return 3;
    }


}
