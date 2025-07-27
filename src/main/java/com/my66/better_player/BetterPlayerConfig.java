package com.my66.better_player;

public class BetterPlayerConfig {
    private float days = 20;
    private boolean teleport = true;
    private int maxPoints = 60;

    public static BetterPlayerConfig INSTANCE = new BetterPlayerConfig();

    private BetterPlayerConfig() {

    }

    public void sync(String data) {
        if (data == null) return;
        data = data.trim();
        String[] ds = data.split("\n");
        if (ds.length != 3) {
            return;
        }
        setDays(Float.parseFloat(ds[0]));
        setTeleport(Boolean.getBoolean(ds[1]));
        setMaxPoints(Integer.parseInt(ds[2]));

        System.out.println("read config: " + data);
    }

    public float getDays() {
        return days;
    }

    public void setDays(float days) {
        this.days = days;
    }

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(boolean teleport) {
        this.teleport = teleport;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }
}
