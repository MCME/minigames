package com.mcmiddleearth.minigames.util;

import java.util.Random;

/**
 * Numbers utility class
 * @author Eriol_Eandur
 */
public class NumericUtil {

    public static int getInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        }
        catch(NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getShort(String str) {
        try {
            return Short.parseShort(str.trim());
        }
        catch(NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isShort(String s) {
        try {
            Short.parseShort(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static long getLong(String str) {
        try {
            return Long.parseLong(str.trim());
        }
        catch(NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }
}
