package posmicanomaly.AotCG.Component;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Actor extends Entity {
    private int maxHp;
    private int currentHp;

    private String corpseName;
    private boolean alive;
    private boolean onCooldown;

    private int power;
    private int level;
    private int speed;
    private int mod;

    private ArrayList<Tile> visibleTiles;

    public void setCurrentPath(ArrayList<Tile> currentPath) {
        this.currentPath = currentPath;
    }

    private ArrayList<Tile> currentPath;

    private int experience;

    public Actor(char symbol, Color color, Tile tile) {
        super(symbol, color, tile);
        visibleTiles = new ArrayList<Tile>();
        currentPath = new ArrayList<>();
        setLevel(1);
        setMaxHp(1);
        setCurrentHp(getMaxHp());
        setAlive(true);
        onCooldown = false;
        setSpeed(1);
        setPower(1);

        setName("Default Actor");
        setCorpseName(getName() + "'s corpse");
    }

    public boolean isAlive() {
        return alive;
    }

    public void initStats() {
        // Calculate true power
        power = 0 + level;
        maxHp = power * mod;
        currentHp = maxHp;
    }
    public boolean isOnCooldown() {
        return onCooldown;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }

    public int getMod() {
        return mod;
    }
    public void setOnCooldown(boolean b) {
        onCooldown = b;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }


    public int getSpeed() {
        return this.speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        setCorpseName(this.getName() +"'s corpse");
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public String getCorpseName() {
        return corpseName;
    }

    public void setCorpseName(String corpseName) {
        this.corpseName = corpseName;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void addExperience(int experience) {
        this.experience += experience;
    }

    public int getExperienceCap(int level) {
        // 10 exp base per kill
        // balance based on kills to level

        int averageKillsToLevel = level * 4;
        return (averageKillsToLevel * 10);
    }
    public void evaulateLevel() {
        while(getExperience() >= getExperienceCap(getLevel())) {
            setExperience(getExperience() - getExperienceCap(getLevel()));
            setLevel(getLevel() + 1);
            initStats();
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void clearVisibleTiles() {
        visibleTiles = new ArrayList<Tile>();
    }

    public void addVisibleTile(Tile t) {
        for(Tile tile : visibleTiles) {
            if(tile == t) {
                return;
            }
        }
        visibleTiles.add(t);
    }

    public ArrayList<Tile> getVisibleTiles() {
        return visibleTiles;
    }

    public ArrayList<Tile> getCurrentPath() {
        return currentPath;
    }
}
