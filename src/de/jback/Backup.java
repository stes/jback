package de.jback;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

public final class Backup {

	public static void main(String[] args) {
		LinearFileIndex index = new LinearFileIndex();

		Backup b = new Backup(new File("/data/docs"), new File("/data/docs"), index);

		long starttime = System.currentTimeMillis();

		b.updateIndex();

		System.out.printf("needed %d ms\n", System.currentTimeMillis()
				- starttime);

		starttime = System.currentTimeMillis();

		//b.updateIndexRecursively();

		System.out.printf("needed %d ms\n", System.currentTimeMillis()
				- starttime);

		index.write(new File("index3.txt"));
		
		LinearFileIndex index_old = new LinearFileIndex();
		index_old.read(new File("index2.txt"));
		
		LinearFileIndex diff = index.diff(index_old);
		
		diff.write(new File("diff.txt"));
	}

	private final File _backupDir;
	private final File _sourceDir;
	private final LinearFileIndex _index;

	public Backup(File backupDir, File sourceDir, LinearFileIndex index) {
		_backupDir = backupDir;
		_sourceDir = sourceDir;
		_index = index;
	}

	public void updateIndex() {
		File root = _sourceDir;

		Stack<File> filestack = new Stack<File>();
		filestack.push(root);

		while (!filestack.isEmpty()) {
			File file = filestack.pop();
			if (file.isDirectory()) {
				File[] content = file.listFiles();
				if (content != null && content.length > 0) {
					for (File f : content) {
						filestack.push(f);
					}
				} else {
					_index.addDirectory(file.getAbsolutePath());
				}
			} else {
				try {
					_index.addFile(file.getAbsolutePath(), Tools.SHA1Hash(file.getAbsolutePath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

//	public void updateIndexRecursively() {
//		File root = _sourceDir;
//		updateIndexRecursively(root, 0);
//	}

	public void updateIndexRecursively(File root, int level) {
		File[] files = root.listFiles();
		if (!root.isDirectory() || files == null) {
			// System.out.println(root);
			return;
		}

		for (File file : files) {
			updateIndexRecursively(file, level + 1);
		}
	}

	public void writeBackup() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public LinearFileIndex getLastBackup()
	{
		throw new UnsupportedOperationException("Not implemented yet.");
	}
}
