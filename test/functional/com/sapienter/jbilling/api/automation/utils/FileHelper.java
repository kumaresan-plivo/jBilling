package com.sapienter.jbilling.api.automation.utils;

import com.sapienter.jbilling.common.FormatLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojislav Stanojevikj
 * @since 17-Jun-2016.
 */
public final class FileHelper {

    private FileHelper() {
    }

    private static FormatLogger logger = new FormatLogger(FileHelper.class);

    public static void write(String filePath, String content) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // adds content to file
            fileWriter.append(content);

            logger.debug("file wrote " + filePath);
        } catch (IOException exp) {
            logger.error("Exception thrown during write!%n%s", exp.getMessage());
        }
    }

    public static void write(String filePath, String... lines) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (String line : lines) {
                fileWriter.append(line).append("\n");
            }

            logger.debug("file wrote " + filePath);

        } catch (IOException exp) {
            logger.error("Exception thrown during write!%n%s", exp.getMessage());
        }
    }

    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.lines().forEach(lines::add);
        } catch (IOException e) {
            logger.error("Exception thrown during read!%n%s", e.getMessage());
        }
        return lines;
    }

    public static void deleteFile(String filePath) {

        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            String path = file.getPath();
            boolean delete = file.delete();

            logger.debug("File %s delete %s!", path, delete ? "success" : "failed");
        }
    }

}
