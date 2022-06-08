package kr.co.brique.baseball.api.game;


import java.time.LocalDateTime;

public class Game {

    private static Game instance;

    private boolean isStarted;

    public String name;
    public LocalDateTime startTime;
    public int length;
    public int remainChance;
    public int[] randomNum;
    public int wrongLengthCount;

    private Game(){}

    public static synchronized boolean checkAlreadyStarted() {
        if(instance==null) {instance = new Game();}
        return instance.isStarted;
    }

    public static synchronized Game startGame(String name) {
        if(instance==null) {instance = new Game();}
        instance.isStarted = true;
        instance.name = name;
        instance.length = 3;
        instance.remainChance = 5;
        instance.startTime = LocalDateTime.now();
        setRandomNum();
        instance.wrongLengthCount = 0;

        return instance;
    }

    public static Game getInstance() {
        return instance;
    }

    public static void endGame() {
        instance = new Game();
    }

    public static void setRandomNum() {
        int[] arr = new int[instance.length];
        int index = 0;
        for(int i = 0; i<arr.length;i++) {
            do {
                index = (int)(Math.random()*10);
            }while(exists(arr,index));
            arr[i] = index;
        }
        instance.randomNum = arr;
    }

    private static boolean exists(int n[], int index) {
        for (int i = 0; i < n.length; i++) {
            if(n[i] == index)
                return true;
        }
        return false;
    }

    public void initWrongNumCount() {
        instance.wrongLengthCount = 0;
    }

    public void addWrongNumCount() {
        instance.wrongLengthCount ++;
    }

    public void subRemainChance() {
        instance.remainChance -- ;
    }

}
