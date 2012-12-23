package de.jback;

import java.io.File;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class Backup {

	public static void main(String[] args) {
		Backup b = new Backup("/data/media", "/data/media");
		
		long starttime = System.currentTimeMillis();
		
		b.updateIndex();
		
		System.out.printf("needed %d ms\n", System.currentTimeMillis() - starttime);
		
		starttime = System.currentTimeMillis();
		
		b.updateIndexRecursively();
		
		System.out.printf("needed %d ms\n", System.currentTimeMillis() - starttime);
	}

	private final String _backupDir;
	private final String _sourceDir;
	private Element _index;

	public Backup(String backupDir, String sourceDir, Element root) {
		_backupDir = backupDir;
		_sourceDir = sourceDir;
		_index = root;
	}

	public void updateIndex() {
		File root = new File(_sourceDir);

		Stack<File> filestack = new Stack<File>();
		filestack.push(root);

		while (!filestack.isEmpty()) {
			File file = filestack.pop();
			// do stuff with the file
			
			// add other files
			if (file.isDirectory() && file.listFiles() != null) {
				for (File f : file.listFiles()) {
					filestack.push(f);
				}
			}
		}
	}
	
	public void updateIndexRecursively()
	{
		File root = new File(_sourceDir);
		updateIndexRecursively(root, 0);
	}
	
	public void updateIndexRecursively(File root, Element xmlelement, int level)
	{
		File[] files = root.listFiles();
		if (!root.isDirectory() || files == null) {
			//System.out.println(root);
			return;
		}
		
		
		
		for (File file : files)
		{
			updateIndexRecursively(file, level+1);
		}
	}

	public void writeBackup() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
}
