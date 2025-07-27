package com.my66.better_player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;

public class BetterPlayer {
    private String firstName;
    private String lastName;

    private boolean isMale = true;
    private boolean isEast = true;

    private int age = 18;
    private int weight = 125;

    private int points = 0;
    private int constitution = 20;
    private int endurance = 20;
    private int vitalcapacity = 20;

    private int water = 20;
    private int thirst = 20;

    private GameType gameType = GameType.DEFAULT_MODE;

    private BetterPlayer() {}

    private static final Map<String, BetterPlayer> ps = new HashMap<>();

    public static BetterPlayer getBetterPlayer(String uuid) {
        return ps.computeIfAbsent(uuid, k -> new BetterPlayer());
    }
    public static BetterPlayer getBetterPlayer(Player player) {
        if (player == null) {
            System.out.println("BetterPlayer.getBetterPlayer: player is null");
            return getBetterPlayer("test");
        }
        String uuid = player.getStringUUID();
        return getBetterPlayer(uuid);
    }

    public void setData(String data) {
        if (data == null) return;
        System.out.println("set data: " + data.replace("\n", ", "));
        data = data.trim();
        String[] ds = data.split("\n");
        if (ds.length < 9) {
            return;
        }
        this.isMale = ds[0].compareTo("M") == 0;
        this.isEast = ds[1].compareTo("E") == 0;

        this.firstName = ds[2];
        this.lastName = ds[3];

        this.age = Integer.parseInt(ds[4]);
        this.weight = Integer.parseInt(ds[5]);
        this.constitution = Integer.parseInt(ds[6]);
        this.endurance = Integer.parseInt(ds[7]);
        this.vitalcapacity = Integer.parseInt(ds[8]);
        if (ds.length > 9)
            this.water = Integer.parseInt(ds[9]);
        if (ds.length > 10)
            this.thirst = Integer.parseInt(ds[10]);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public void setEast(boolean east) {
        isEast = east;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }

    public void setEndurance(int endurance) {
        this.endurance = endurance;
    }

    public void setVitalcapacity(int vitalcapacity) {
        this.vitalcapacity = vitalcapacity;
    }

    public String getData() {
        String gender = isMale ? "M" : "F";
        String culture = isEast ? "E" : "W";

        return gender + "\n" + culture + "\n" + firstName + "\n" + lastName + "\n" + age + "\n" +
                weight + "\n" + constitution + "\n" + endurance + "\n" + vitalcapacity + "\n" + water + "\n" + thirst;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isMale() {
        return isMale;
    }

    public boolean isEast() {
        return isEast;
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public int getPoints() {
        return points;
    }

    private int getReal() {
        int real = 0;
        if (age > 30) real = -((age - 30) / 3);
        if (age < 25) real = -((25 - age)  /3);
        return real;
    }

    public int getConstitution() {
        return Math.max(constitution + getReal(), 1);
    }

    public int getEndurance() {
        return Math.max(endurance + getReal(), 1);
    }

    public int getVitalcapacity() {
        return Math.max(vitalcapacity + getReal(), 1);
    }

    public int get2530Constitution() {
        return constitution;
    }

    public int get2530Endurance() {
        return endurance;
    }

    public int get2530Vitalcapacity() {
        return vitalcapacity;
    }

    public int getAirSupply() {
        return this.getVitalcapacity() * 15;
    }

    public void setAirSupply(int air) {
        setVitalcapacity(air / 15);
    }

    public int getWater() {
        return Math.max(water + getReal(), 1);
    }

    public void setWater(int water) {
        this.water = water;
        this.thirst = Math.min(this.thirst, water);
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        this.thirst = Math.min(thirst, getWater());
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
