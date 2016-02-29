package posmicanomaly.AotCG.Component;

import posmicanomaly.AotCG.Game.Input;

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
    private double speed;
    private int mod;
    private int energy;

    private ArrayList<Tile> visibleTiles;

    public void setCurrentPath(ArrayList<Tile> currentPath) {
        this.currentPath = currentPath;
    }

    private ArrayList<Tile> currentPath;
    private ArrayList<Item> inventory;

    private int experience;

    public Actor(char symbol, Color color, Tile tile) {
        super(symbol, color, tile);
        visibleTiles = new ArrayList<Tile>();
        inventory = new ArrayList<>();
        //currentPath = new ArrayList<>();
        setLevel(1);
        setMaxHp(1);
        setCurrentHp(getMaxHp());
        setAlive(true);
        onCooldown = false;
        setSpeed(1);
        setPower(1);
        energy = 1000;

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

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public boolean addInventoryItem(Item item) {
        inventory.add(item);
        return true;
    }

    public boolean removeInventoryItem(Item item) {
        for(Item i : inventory) {
            if(i.equals(item)) {
                inventory.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean useItem(Item item, Input.ItemUse itemUse) {
        return useItem(item, this, itemUse);
    }

    public boolean useItem(Item item, Actor target, Input.ItemUse itemUse) {
        for(Item i : inventory) {
            if(i.equals(item)) {
                switch(itemUse) {
                    case CONSUME:
                        if(item.consume(target)) {

                            return true;
                        }
                        break;
                    case DROP:
                        if(item.drop(target)) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    public boolean hasItemString(String itemName) {
        for(Item i : inventory) {
            if(i.getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public Item getItem(String itemName) {
        for(Item i : inventory) {
            if(i.getName().equals(itemName)) {
                return i;
            }
        }
        return null;
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


    public double getSpeed() {
        return this.speed;
    }
    public void setSpeed(double speed) {
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

    public boolean canTakeTurn() {
        return energy >= 1000 / speed;
    }

    public void recoverEnergy(int amount) {
        energy += amount;
        //System.out.println(name + " recoverEngery() current is " + energy);
        if(energy > 1000) {
            energy = 1000;
        }
    }

    public void depleteEnergy(int amount) {
        energy -= amount;
        //System.out.println(name + " depleteEngery() current is " + energy);
        if(energy < 0) {
            energy = 0;
        }
    }
}
