package util;

public class Stopwatch {
    private final long start;
    private long end = -1;

    private Stopwatch() { this.start = System.nanoTime(); }
    public static Stopwatch startNew() { return new Stopwatch(); }
    public void stop() { if (end < 0) end = System.nanoTime(); }
    public long elapsedNanos() { return (end >= 0 ? end : System.nanoTime()) - start; }
}
