package analyze;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class SequentialLogAnalyzer {
    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("."); // aktuelles Verzeichnis
        DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*.log");

        Map<String, Integer> totalCounts = initLogLevelMap();

        long startTime = System.nanoTime();

        for (Path file : files) {
            Map<String, Integer> counts = countLogLevels(file);
            System.out.println("Datei: " + file.getFileName() + " -> " + counts);
            addCounts(totalCounts, counts);
        }

        long duration = System.nanoTime() - startTime;
        System.out.println("Gesamt: " + totalCounts);
        System.out.printf("Sequentielle Laufzeit: %.2f ms%n", duration / 1000000.0);
    }

    private static Map<String, Integer> countLogLevels(Path path) throws IOException {
        Map<String, Integer> counts = initLogLevelMap();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String level : counts.keySet()) {
                    if (line.contains(" " + level + " ")) {
                        counts.put(level, counts.get(level) + 1);
                        break;
                    }
                }
            }
        }
        return counts;
    }

    private static Map<String, Integer> initLogLevelMap() {
        Map<String, Integer> map = new HashMap<>();
        for (String level : new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR"}) {
            map.put(level, 0);
        }
        return map;
    }

    private static void addCounts(Map<String, Integer> total, Map<String, Integer> addition) {
        for (String level : total.keySet()) {
            total.put(level, total.get(level) + addition.get(level));
        }
    }
}