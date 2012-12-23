package de.jback;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tools {
	private static final int CHUNK_SIZE = 512;

	public String SHA1Hash(String file) throws IOException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			FileInputStream filestream = new FileInputStream(file);

			byte[] data = new byte[CHUNK_SIZE];

			int read = 0;
			while ((read = filestream.read(data)) != -1) {
				md.update(data, 0, read);
			}
			filestream.close();

			byte[] bytes = md.digest();

			StringBuffer hexStr = new StringBuffer();
			for (byte b : bytes) {
				String hex = Integer.toHexString(b & 0xff);
				if (hex.length() == 1)
					hexStr.append('0');
				hexStr.append(hex);
			}
			return hexStr.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
}
