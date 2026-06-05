package com.jobportal.backend.util;

import java.io.File;
import org.apache.tika.Tika;

public class ResumeParser {

    public static String extractText(String filePath) {
        try {
            Tika tika = new Tika();
            return tika.parseToString(new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing resume");
        }
    }
}
