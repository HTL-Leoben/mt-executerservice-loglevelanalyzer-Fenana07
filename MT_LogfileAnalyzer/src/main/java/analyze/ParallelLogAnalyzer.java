package analyze;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class ParallelLogAnalyzer {
    public static void main(String[] args) throws Exception {
        Path dir = Paths.get(".");
        DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*.log");

        Map<String, Integer> totalCounts = new HashMap<>();
        for (String level : new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR"}) {
            totalCounts.put(level, 0);
        }

        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        long startTime = System.nanoTime();

        for (Path file : files) {
            futures.add(executor.submit(new LogAnalyzerTask(file)));
        }

        int index = 1;
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> result = future.get();
            System.out.println("Datei " + index + ": " + result);
            index++;
            for (String level : result.keySet()) {
                totalCounts.put(level, totalCounts.get(level) + result.get(level));
            }
        }

        long duration = System.nanoTime() - startTime;
        executor.shutdown();

        System.out.println("Gesamt: " + totalCounts);
        System.out.printf("Parallele Laufzeit: %.2f ms%n", duration / 1000000.0);
    }
}