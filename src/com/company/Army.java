package com.company;

import com.company.warriors.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by BN on 01.04.2017.
 */
public class Army {

    private List<Unit> units = new ArrayList<>();
    private String name;

    public Army(int size, String name) {

        this.name = name;

        Class[] warriorTypes = {Archer.class, AttackingTower.class, Cavalry.class, CavalryArcher.class, Healer.class, HealingTower.class, Tower.class, Warrior.class};

        if(size <= 0)
            throw new IllegalArgumentException("Размер армии должен быть положительным");

        for(int i = 0; i < size; i++)
        {
            int warriorType = ThreadLocalRandom.current().nextInt(warriorTypes.length);
            try {
                Unit unit = (Unit) Class.forName(warriorTypes[warriorType].getName()).getConstructor().newInstance();
                units.add(unit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Unit> getUnits() {
        return this.units;
    }

    public void killUnit(Unit unit)
    {
        this.units.remove(unit);
    }

    public Unit getRandomWarrior() {
        int random = ThreadLocalRandom.current().nextInt(units.size());
        return units.get(random);
    }

    public int getHealPower() {
        return units.stream().filter(x -> x.isHealer()).mapToInt(x -> ((IHealer)x).getHealPower()).sum();
    }

    public int getAttackPower() {
        return units.stream().filter(x -> !x.isHealer()).mapToInt(x -> x.getPower()).sum();
    }

    public boolean isAlive() {
        return !units.isEmpty();
    }

    public String getName() {
        return name;
    }
}
