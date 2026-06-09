package com.jobportal.backend.util;

import org.apache.tika.Tika;
import java.io.InputStream;
import java.net.URI;

public class ResumeParser {

    public static String extractText(String fileUrl) {

        try {

            System.out.println("Reading URL = " + fileUrl);

            Tika tika = new Tika();

            InputStream stream =
                    URI.create(fileUrl)
                            .toURL()
                            .openStream();

            String text =
                    tika.parseToString(stream);

            System.out.println("TEXT LENGTH = " + text.length());

            return text;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error parsing resume: "
                            + e.getMessage()
            );
        }
    }
}