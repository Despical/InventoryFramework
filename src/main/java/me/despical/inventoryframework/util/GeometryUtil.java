package me.despical.inventoryframework.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;

/**
 * An utility class for Geometry
 * 
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class GeometryUtil {

    /**
     * Calculates a clockwise rotation across a two dimensional grid
     *
     * @param x the standard x coordinate
     * @param y the standard y coordinate
     * @param length the length of the grid
     * @param height the height of the grid
     * @param rotation the rotation in degrees
     * @return a pair of new coordinates, with the x coordinate being the key and the y coordinate being the value
     */
    @NotNull
    @Contract(pure = true)
    public static Map.Entry<Integer, Integer> processClockwiseRotation(int x, int y, int length, int height,
                                                                       int rotation) {
        int newX = x, newY = y;

        if (rotation == 90) {
            newX = height - 1 - y;
            newY = x;
        } else if (rotation == 180) {
            newX = length - 1 - x;
            newY = height - 1 - y;
        } else if (rotation == 270) {
            newX = y;
            newY = length - 1 - x;
        }

        return new AbstractMap.SimpleEntry<>(newX, newY);
    }

    /**
     * Calculates a counter clockwise rotation across a two dimensional grid. This is the same as calling
     * {@link #processClockwiseRotation(int, int, int, int, int)} with 360 - rotation as the rotation.
     *
     * @param x the standard x coordinate
     * @param y the standard y coordinate
     * @param length the length of the grid
     * @param height the height of the grid
     * @param rotation the rotation in degrees
     * @return a pair of new coordinates, with the x coordinate being the key and the y coordinate being the value
     */
    @NotNull
    @Contract(pure = true)
    public static Map.Entry<Integer, Integer> processCounterClockwiseRotation(int x, int y, int length, int height,
                                                                              int rotation) {
        return processClockwiseRotation(x, y, length, height, 360 - rotation);
    }
}