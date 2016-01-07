package posmicanomaly.AotCG.Component;

import posmicanomaly.AotCG.Game.Roguelike;
import posmicanomaly.libjsrte.Console.Symbol;
import posmicanomaly.libjsrte.Util.ColorTools;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Created by Jesse Pospisil on 1/7/2016.
 */
public class MapSymbols {
    // Defaults
    char FLOOR = Symbol.MIDDLE_DOT;
    char WALL = '#';
    char WALL_SECRET = '*';
    char PATH = Symbol.MIDDLE_DOT;
    char BUILD_FLOOD = Symbol.ALMOST_EQUAL_TO;
    char WATER = Symbol.ALMOST_EQUAL_TO;
    char CAVE_GRASS = '"';
    char DOOR = '\u041f';
    char STAIRS_UP = '<';
    char STAIRS_DOWN = '>';
    char WORLD_GRASS = Symbol.ALMOST_EQUAL_TO;
    char CAVE_OPENING = 'O';
    char FOREST = '\u2663';
    char FOREST_ALT = '\u2660';
    char MOUNTAIN = '\u25B2';
    char SAND = '\u2261';
    char DEFAULT = '?';

    public MapSymbols(String fileName) {
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                loadSymbol(line);
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
