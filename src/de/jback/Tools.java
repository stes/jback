package de.jback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Tools {
	private static final int CHUNK_SIZE = 4096;

	public static String SHA1Hash(String file) throws IOException {
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

	public static void copyToZIP(LinearFileIndex index, String output)
			throws IOException {
		String[] files = index.files();
		String[] dirs = index.directories();
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));

		byte[] buf = new byte[CHUNK_SIZE];
		for (String dir : dirs) {
			String entry = dir.replace(":", "");
			if (!entry.endsWith("/")) {
				entry += "/";
			}
			out.putNextEntry(new ZipEntry(entry));
		}

		for (String file : files) {
			out.putNextEntry(new ZipEntry(file.replace(":", "")));
			FileInputStream in = new FileInputStream(file);
			int read;
			while ((read = in.read(buf)) > 0) {
				out.write(buf, 0, read);
			}
			in.close();
		}

		out.close();
	}

	public static void copyFromZIP(LinearFileIndex index, String input,
			String output) throws IOException {
		ZipInputStream in = new ZipInputStream(new FileInputStream(input));

		byte[] buf = new byte[CHUNK_SIZE];

		ZipEntry entry = null;
		while ((entry = in.getNextEntry()) != null) {
			if (index.hasFile(entry.getName())) {
				if (entry.isDirectory())
					new File(output + entry.getName()).mkdirs();
				else {
					File file = new File(output + entry.getName());
					file.getParentFile().mkdirs();
					FileOutputStream out = new FileOutputStream(file);

					int read;
					while ((read = in.read(buf)) > 0) {
						out.write(buf, 0, read);
					}
					in.closeEntry();
					out.close();
				}
				index.remove(entry.getName());
			}
		}
		in.close();
	}
}
