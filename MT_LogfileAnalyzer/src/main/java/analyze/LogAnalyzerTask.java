package analyze;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class LogAnalyzerTask implements Callable<Map<String, Integer>> {

    private final Path filePath;

    public LogAnalyzerTask(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        Map<String, Integer> logLevelCounts = new HashMap<>();
        for (String level : new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR"}) {
            logLevelCounts.put(level, 0);
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String level : logLevelCounts.keySet()) {
                    if (line.contains(" " + level + " ")) {
                        logLevelCounts.put(level, logLevelCounts.get(level) + 1);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen von " + filePath + ": " + e.getMessage());
        }

        return logLevelCounts;
    }
}