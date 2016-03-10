package posmicanomaly.AotCG.Factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Jesse Pospisil on 1/7/2016.
 */
public class MapSymbols {
    // Defaults
    // Interior
    public char FLOOR = 249;
    char WALL = '#';
    char WALL_SECRET = '*';
    char PATH = 249;
    char BUILD_FLOOD = 247;
    char WATER = 247;
    char CAVE_GRASS = '"';
    char LOW_GRASS = ',';
    char DOOR = 239;
    char STAIRS_UP = '<';
    char STAIRS_DOWN = '>';

    // Exterior
    char WORLD_GRASS = 247;
    char CAVE_OPENING = 15;
    char FOREST = 5;
    char FOREST_ALT = 6;
    char MOUNTAIN = 30;
    char SAND = 247;
    char JUNGLE = 20;
    char PLAINS = 247;
    char BRUSH = '%';
    char HILL = 239;
    char TOWN = 4;
    char DEFAULT = '?';

    public MapSymbols(String fileName) {
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //loadSymbol(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("symbols not found");
        }
    }

    private void loadSymbol(String line) {
        String splitLine[] = line.split("=");
        switch(splitLine[0]) {
            case "FLOOR":
                FLOOR = splitLine[1].toCharArray()[0];
                break;
            case "WALL":
                WALL = splitLine[1].toCharArray()[0];
                break;
            case "WALL_SECRET":
                WALL_SECRET = splitLine[1].toCharArray()[0];
                break;
            case "PATH":
                PATH = splitLine[1].toCharArray()[0];
                break;
            case "BUILD_FLOOD":
                BUILD_FLOOD = splitLine[1].toCharArray()[0];
                break;
            case "WATER":
                WATER = splitLine[1].toCharArray()[0];
                break;
            case "CAVE_GRASS":
                CAVE_GRASS = splitLine[1].toCharArray()[0];
                break;
            case "DOOR":
                DOOR = splitLine[1].toCharArray()[0];
                break;
            case "STAIRS_UP":
                STAIRS_UP = splitLine[1].toCharArray()[0];
                break;
            case "STAIRS_DOWN":
                STAIRS_DOWN = splitLine[1].toCharArray()[0];
                break;
            case "WORLD_GRASS":
                WORLD_GRASS = splitLine[1].toCharArray()[0];
                break;
            case "CAVE_OPENING":
                CAVE_OPENING = splitLine[1].toCharArray()[0];
                break;
            case "FOREST":
                FOREST = splitLine[1].toCharArray()[0];
                break;
            case "FOREST_ALT":
                FOREST_ALT = splitLine[1].toCharArray()[0];
                break;
            case "MOUNTAIN":
                MOUNTAIN = splitLine[1].toCharArray()[0];
                break;
            case "SAND":
                SAND = splitLine[1].toCharArray()[0];
                break;
            case "DEFAULT":
                DEFAULT = splitLine[1].toCharArray()[0];
        }
    }
}
