package com.company.warriors;

import com.company.FightController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by BN on 01.04.2017.
 */
public interface IHealer {
    default void heal(List<Unit> units) {
        String log = ((Unit)this).getName() + " heals { ";

        Stream<Unit> sorted = units.stream().
                                        filter((unit ->
                                                        !unit.getClass().isAssignableFrom(Tower.class) && //не хиляем себя, башню и фуловых
                                                        unit.getLostedHealth() != 0 &&
                                                        unit != this)).
                                        sorted(Comparator.comparingInt(u -> u.getLostedHealth())); //хиляем самых хилых
        //хилер хиляет максимум n-юнитов
        List<Unit> unitsForHeal = sorted.collect(Collectors.toList());

        int healed = 0;
        int i = 0;
        //Хиляем пока можем
        while(healed < getHealPower() && i < getMaxHealCount() && i < unitsForHeal.size())
        {
            Unit unit = unitsForHeal.get(i);
            //System.out.println("Sync " + unit.getName());
            synchronized (unit) {
                if(unit.getHealth() > 0)
                {
                    log += " " + unit.getName() + " (" + unit.getHealth() + ") -> ";
                    //Хиляем максимум на величину потерянного хп
                    int heal = Math.min(unit.getLostedHealth(), getHealPower());
                    unit.setHealth(unit.getHealth() + heal);
                    log += unit.getHealth() + ", ";
                    healed += heal;
                }
            }
            //System.out.println("Realized " + unit.getName());

            i++;
        }

        log += "}";
        FightController.writeLog(log);
    }

    int getHealPower();

    int getMaxHealCount();
}
