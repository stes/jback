package de.jback;

import java.io.File;
import java.util.Enumeration;

public abstract class FileIndex {

	public FileIndex() {

	}

	public FileIndex(File f)
	{
		this.read(f);
	}

	public abstract String search(File f);

	public abstract void write(File f);

	public abstract void read(File f);
	
	public abstract String[] toArray();

	public abstract void add(File f);
	
}
