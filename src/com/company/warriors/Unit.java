package com.company.warriors;

import java.util.UUID;

/**
 * Created by BN on 01.04.2017.
 */
public abstract class Unit {

    static int number = 0;

    private int health;

    private int maxHealth;

    private int initiative;

    private int power;

    private int id;


    public Unit(int health, int initiative, int power)
    {
        this.health = health;
        this.maxHealth = health;
        this.initiative = initiative;
        this.power = power;

        this.id = number++;
    }

    public Unit(int health, int initiative)
    {
        this.health = health;
        this.maxHealth = health;
        this.initiative = initiative;
        this.id = number++;
    }

    public boolean isHealer() {
        return IHealer.class.isAssignableFrom(this.getClass());
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getLostedHealth() {
        return this.maxHealth - this.health;
    }

    public int getInitiative() {
        return this.initiative;
    }

    public int getPower() { return this.power; }

    public String getName() { return this.getClass().getSimpleName() + this.initiative + this.id;}
}
