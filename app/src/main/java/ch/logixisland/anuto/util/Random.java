package ch.logixisland.anuto.util;

public final class Random {

    private static final java.util.Random sRandom = new java.util.Random();

    private Random() {
    }

    public static int next(int max) {
        return sRandom.nextInt(max);
    }

    public static int next(int min, int max) {
        return sRandom.nextInt(max - min) + min;
    }

    public static float next(float max) {
        return sRandom.nextFloat() * max;
    }

    public static float next(float min, float max) {
        return sRandom.nextFloat() * (max - min) + min;
    }

}
