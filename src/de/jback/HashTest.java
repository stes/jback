package de.jback;

import java.io.IOException;

public class HashTest {
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			int loop = 100;
			for(int i = 0; i < loop; i++)
				System.out.println(Tools.SHA1Hash("testdata/test2.txt"));
			System.out.println((System.currentTimeMillis()-start)/loop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
