package com.company.warriors;

/**
 * Created by BN on 01.04.2017.
 */
public class Tower extends Unit {
    public Tower() {
        super(30, 1, 20);
    }

    protected Tower(int health, int initiative) {
        super(health, initiative);
    }
}
