package com.ra2.Aav2.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLogging {

    private static final String LOG_DIR = "logs";

    private static String getLogFileName() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return LOG_DIR + File.separator + "aplicacio-" + date + ".log";
    }

    private static void ensureLogFileExists() throws IOException {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File logFile = new File(getLogFileName());
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
    }

    private static void writeLog(String level, String clazz, String method, String description) {
        try {
            ensureLogFileExists();
            String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String line = String.format("[%s] %s - %s - %s - %s", datetime, level, clazz, method, description);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(getLogFileName(), true))) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }

    public static void info(String clase, String method, String description) {
        writeLog("INFO", clase, method, description);
    }

    public static void error(String clase, String method, String description) {
        writeLog("ERROR", clase, method, description);
    }
}
