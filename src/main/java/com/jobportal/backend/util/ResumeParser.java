package com.jobportal.backend.util;

import java.net.URL;

import org.apache.tika.Tika;

public class ResumeParser {

    public static String extractText(String fileUrl) {

        try {

            Tika tika = new Tika();

            return tika.parseToString(new URL(fileUrl));

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error parsing resume"
            );
        }
    }
}