package com.jobportal.backend.util;

import java.net.URI;

import org.apache.tika.Tika;

public class ResumeParser {

	public static String extractText(String fileUrl) {

	    try {

	        Tika tika = new Tika();

	        return tika.parseToString(
	                URI.create(fileUrl).toURL()
	        );

	    } catch (Exception e) {

	        e.printStackTrace();

	        throw new RuntimeException(
	                "Error parsing resume: " + e.getMessage()
	        );
	    }
	}

}