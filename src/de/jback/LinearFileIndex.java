package de.jback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LinearFileIndex extends FileIndex {

	private HashMap<String, String> _files;
	private ArrayList<String> _directories;

	public LinearFileIndex() {
		super();
		_files = new HashMap<String, String>();
		_directories = new ArrayList<String>();
	}

	public void addDirectory(String s)
	{
		_directories.add(s);
	}
	
	public void addFile(String s, String hash) {
		_files.put(s, hash);
	}

	public String search(File f) {
		return this.search(f.getAbsolutePath());
	}

	public String search(String s) {
		if (_files.containsKey(s))
			return _files.get(s);
		return "";
	}

	public boolean has_directory(String s) {
		return _directories.contains(s);
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
			String hash = index.search(file);
			if (hash != "") {
				if (!hash.equals(this.search(file))) {
					result.addFile(file, this.search(file));
				}
			} else {
				result.addFile(file, this.search(file));
			}
		}
		// add dirs
		for (String dir : _directories) {
			if (!index.has_directory(dir)) {
				result.addDirectory(dir);
			}
		}

		return result;
	}

	public void write(File f) {
		try {
			BufferedWriter sr = new BufferedWriter(new FileWriter(f));

			for (String dir : this._directories) {
				sr.write(dir + "\n");
			}
			sr.flush();

			sr.write("!\n");

			for (String path : this._files.keySet()) {
				sr.write(path + "##" + this.search(path) + "\n");
			}
			sr.flush();

			sr.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void read(File f) {
		try {
			BufferedReader sr = new BufferedReader(new FileReader(f));

			for (String line = sr.readLine(); !line.contains("!"); line = sr
					.readLine()) {
				this._directories.add(line);
				System.out.println(line);
			}
			for (String line = sr.readLine(); line != null; line = sr
					.readLine()) {
				String[] parts = line.split("##");
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
		return this._files.values().toArray(new String[] {});
	}
	
	public String[] directories() {
		return this._directories.toArray(new String[] {});
	}

}
