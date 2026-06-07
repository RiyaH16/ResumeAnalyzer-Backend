package com.jobportal.backend.util;

import java.io.InputStream;
import java.net.URL;

import org.apache.tika.Tika;

public class ResumeParser {

    public static String extractText(String fileUrl) {

        try {

            Tika tika = new Tika();

            InputStream inputStream = new URL(fileUrl).openStream();

            return tika.parseToString(inputStream);

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error parsing resume"
            );
        }
    }
}