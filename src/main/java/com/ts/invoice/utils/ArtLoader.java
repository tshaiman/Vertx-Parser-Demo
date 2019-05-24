package com.ts.invoice.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class ArtLoader {

	public static void print() throws IOException {
		ArtLoader artLoader = new ArtLoader();
		printArt(artLoader.getFileFromResources("art.txt"));
	}
	private File getFileFromResources(String fileName) {

		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return new File(resource.getFile());
		}

	}
	private static void printArt(File file) throws IOException {

		try (FileReader reader = new FileReader(file);
			 BufferedReader br = new BufferedReader(reader)) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
	}
}
