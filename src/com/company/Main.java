package com.company;

public class Main {

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        FightController ctrl = new FightController();
        try {
            ctrl.createArmies(100);
            ctrl.fight();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
    }
}
