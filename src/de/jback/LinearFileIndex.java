package de.jback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class LinearFileIndex extends FileIndex {

	public static final String SEPERATOR_PATH_HASH = "##";

	private HashMap<String, String> _files;
	private ArrayList<String> _directories;

	public LinearFileIndex() {
		super();
		_files = new HashMap<String, String>();
		_directories = new ArrayList<String>();
	}

	public boolean isEmpty() {
		return _files.isEmpty() && _directories.isEmpty();
	}

	public void removeFile(String path) {
		_files.remove(path);
	}

	public void removeDirectory(String s) {
		if (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		_directories.remove(s);
	}

	public void addDirectory(String s) {
		_directories.add(s);
	}

	public void addFile(String s, String hash) {
		_files.put(s, hash);
	}

	public String getHash(File f) {
		return this.getHash(f.getAbsolutePath());
	}

	public String getHash(String s) {
		if (_files.containsKey(s))
			return _files.get(s);
		return "";
	}

	public boolean hasDirectory(String s) {
		if (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return _directories.contains(s);
	}

	public boolean hasFile(String s) {
		return _files.containsKey(s);
	}

	/**
	 * Generates the diff between the current and the given file index. The
	 * resulting index will contain all files and folders that occur in the
	 * current file index but NOT in the previous one
	 * 
	 * @param index
	 */
	public LinearFileIndex diff(LinearFileIndex index) {
		LinearFileIndex result = new LinearFileIndex();
		// add files
		for (String file : _files.keySet()) {
			String hash = index.getHash(file);
			if (hash != "") {
				if (!hash.equals(this.getHash(file))) {
					result.addFile(file, this.getHash(file));
				}
			} else {
				result.addFile(file, this.getHash(file));
			}
		}
		// add dirs
		for (String dir : _directories) {
			if (!index.hasDirectory(dir)) {
				result.addDirectory(dir);
			}
		}

		return result;
	}

	public void write(OutputStream stream) throws IOException {
		BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(stream));
		this.write(sw);
		sw.close();
	}

	public void write(File f) {
		try {
			BufferedWriter sw = new BufferedWriter(new FileWriter(f));
			this.write(sw);
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void write(BufferedWriter writer) {
		try {
			for (String dir : this._directories) {
				writer.write(dir + "\n");
				writer.flush();
			}
			writer.write("!\n");
			for (String path : this._files.keySet()) {
				writer.write(path + SEPERATOR_PATH_HASH + this.getHash(path) + "\n");
			}
			writer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read(File f) {
		try {
			BufferedReader sr = new BufferedReader(new FileReader(f));

			for (String line = sr.readLine(); !line.contains("!"); line = sr.readLine()) {
				this._directories.add(line);
			}
			for (String line = sr.readLine(); line != null; line = sr.readLine()) {
				String[] parts = line.split(SEPERATOR_PATH_HASH);
				this._files.put(parts[0], parts[1]);
			}

			sr.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] files() {
		return this._files.keySet().toArray(new String[] {});
	}

	public String[] directories() {
		return this._directories.toArray(new String[] {});
	}

}
