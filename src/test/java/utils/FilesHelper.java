package utils;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.File;

import java.nio.charset.StandardCharsets;

public class FilesHelper {

    private String pathToFile = "C:\\token\\token.txt";

    public String getToken() {
        File file = new File(pathToFile);
        return readFileToString(file);

    }

    private String readFileToString(@NotNull File file) {
        try {
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
