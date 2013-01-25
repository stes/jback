package de.jback;

import java.io.IOException;

public class ZIPTest {
	public static void main(String[] args) {
		try {
			//Tools.copyToZIP(new String[]{"D:/Projekte/jback/testdata/test/", "D:/Projekte/jback/testdata/test.txt", "D:/Projekte/jback/testdata/file.xml"}, "testdata/test.zip");
			Tools.copyFromZIP(new LinearFileIndex(), "testdata/test.zip", "testdata/out/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
