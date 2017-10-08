package com.company;

import com.company.warriors.IHealer;
import com.company.warriors.Unit;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by BN on 01.04.2017.
 */
public class FightController  {

    private static PrintWriter logwriter;
    private Army army1;
    private Army army2;

    private static final int threadCount = 1;
    private Thread[] threads = new Thread[threadCount];

    public void createArmies(int size) throws FileNotFoundException, UnsupportedEncodingException {
        army1 = new Army(size, "1st");
        army2 = new Army(size, "2nd");

        logwriter = new PrintWriter("log.txt", "UTF-8");
        writeArmyDescr(army1);
        writeArmyDescr(army2);
    }

    public static void writeLog(String logMsg)
    {
        logwriter.println(logMsg);
        logwriter.flush();
    }

    private void writeArmyDescr(Army army)
    {
        writeLog(army.getName() + " army:");
        army.getUnits().forEach(x ->
                //Записываем в формате: <класс> <guid> - attack/heal power <сила атаки>, hp <количество здоровья>
                writeLog(
                        String.format("%s - %s power %s, hp %s",
                                x.getName(),
                                x.isHealer() ? "heal" : "attack",
                                x.isHealer() ? ((IHealer)x).getHealPower() : x.getPower(),
                                x.getHealth()
                        )));
        writeLog("\n");
    }

    private ArrayList<Pair<Army, Unit>> units = new ArrayList<>();

    private boolean canFight() {
        return
            army1.isAlive() && army2.isAlive() &&
                    (army1.getHealPower() < army2.getAttackPower() || army2.getHealPower() < army1.getAttackPower());
    }


    AtomicInteger num = new AtomicInteger(0);
    private Pair<Army, Unit> getNext() {
        Pair<Army, Unit> unit = units.get(num.getAndIncrement() % units.size());
        return unit;
    }

    private void runNextPairFight() {
        Pair<Army, Unit> armyUnit = getNext();
        Army army = armyUnit.getKey();
        Unit unit = armyUnit.getValue();

        //работает как lock(unit)
        //System.out.println("Sync " + unit.getName());
        synchronized (unit)
        {
            if(unit.isHealer())
            {
                ((IHealer)unit).heal(army.getUnits());
            }
            else
            {
                Army enemyArmy = army == army1 ? army2 : army1;
                Unit enemy = enemyArmy.getRandomWarrior();

                //System.out.println("Sync " + enemy.getName());
                synchronized (enemy)
                {
                    if(enemy.getHealth() <= 0)
                        return;

                    String log = String.format("%s (power %s) attacks %s (hp %s) -> ",
                            unit.getName(),
                            unit.getPower(),
                            enemy.getName(),
                            enemy.getHealth());

                    enemy.setHealth(enemy.getHealth() - unit.getPower());
                    //Если хп не осталось, убираем его из коллекции
                    if(enemy.getHealth() <= 0)
                    {
                        units.remove(units.stream().filter(x -> x.getValue() == enemy).findFirst());
                        enemyArmy.killUnit(enemy);

                        log += enemy.getName() + " is dead";
                    }
                    else
                    {
                        log += enemy.getName() + "(" + enemy.getHealth() + ")";
                    }

                    writeLog(log);
                }
                //System.out.println("Realized " + unit.getName());
            }
        }
        //System.out.println("Realized " + unit.getName());


    }

    public void fight() throws InterruptedException {
        //Объединяем армии в общую коллекцию и сортируем по инициативе
        units.addAll(army1.getUnits().stream().map((u) -> new Pair<>(army1, u)).collect(Collectors.toList()));
        units.addAll(army2.getUnits().stream().map((u) -> new Pair<>(army2, u)).collect(Collectors.toList()));
        Collections.sort(units, Comparator.comparingInt(x -> x.getValue().getInitiative()));

        if(canFight()) {

            ExecutorService service = Executors.newCachedThreadPool();
            for (int i = 0; i < threadCount; i++)
            {
                service.submit(new Thread() {
                    public void run() {
                        while (canFight())
                        {
                            runNextPairFight();
                        }
                    }
                });
            }

            service.shutdown();
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        }

        if(army1.isAlive() && !army2.isAlive())
        {
            System.out.println(army1.getName() + " army wins");
        }
        else if(!army1.isAlive() && army2.isAlive())
        {
            System.out.println(army2.getName() + " army wins");
        }
        else {
            System.out.println("Draw!");
        }

        writeLog("===========================");
        writeLog("Results:");
        writeArmyDescr(army1);
        writeArmyDescr(army2);

    }

}
