package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Actor extends Entity {
    private int maxHp;
    private int currentHp;
    private String corpseName;
    private boolean alive;

    private int level;

    private int experience;

    public Actor(char symbol, Color color, Tile tile) {
        super(symbol, color, tile);

        setLevel(1);
        setMaxHp(20);
        setCurrentHp(getMaxHp());
        setAlive(true);

        setName("Default Actor");
        setCorpseName(getName() + "'s corpse");
    }

    public boolean isAlive() {
        return alive;
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
        // e.g. level 1 * 10 kills * 10 xp each = 100
        //      level 2 * 20 kills * 10 xp each =
        int averageKillsToLevel = level * 4;
        return (averageKillsToLevel * 10);
    }
    public void evaulateLevel() {
        while(getExperience() >= getExperienceCap(getLevel())) {
            setMaxHp((int) (getMaxHp() * 1.5));
            setCurrentHp(getMaxHp());
            setExperience(getExperience() - getExperienceCap(getLevel()));

            setLevel(getLevel() + 1);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
