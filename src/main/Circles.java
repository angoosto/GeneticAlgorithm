package main;

/*
 * This is a utility class to return the diameter of a circle, given a radius of either float or double
 */

public class Circles {
    public static float circle(double f) {
        return (float) (f * 2 * Math.PI);
    }

    public static float circle(float f) {
        return (float) (f * 2 * Math.PI);
    }
}
