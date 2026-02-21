package io.github.some_example_name.tests.Demo;

public final class RunStats {
    private static int lastScore;
    private static float lastSurvivalSeconds;
    private static boolean lastRunWon;
    private static int bestScore;

    private RunStats() {}

    public static void recordRun(int score, float survivalSeconds, boolean won) {
        lastScore = score;
        lastSurvivalSeconds = survivalSeconds;
        lastRunWon = won;
        if (score > bestScore) bestScore = score;
    }

    public static int getLastScore() { return lastScore; }
    public static float getLastSurvivalSeconds() { return lastSurvivalSeconds; }
    public static boolean wasLastRunWon() { return lastRunWon; }
    public static int getBestScore() { return bestScore; }
}