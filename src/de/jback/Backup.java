package de.jback;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

public final class Backup {

	public static void main(String[] args) {
		LinearFileIndex index = new LinearFileIndex();

		Backup b = new Backup(new File("/data/docs"), new File("/data/docs"), index, 0);

		long starttime = System.currentTimeMillis();

		b.updateIndex();

		System.out.printf("needed %d ms\n", System.currentTimeMillis() - starttime);

		starttime = System.currentTimeMillis();

		// b.updateIndexRecursively();

		System.out.printf("needed %d ms\n", System.currentTimeMillis() - starttime);

		index.write(new File("index3.txt"));

		LinearFileIndex index_old = new LinearFileIndex();
		index_old.read(new File("index2.txt"));

		LinearFileIndex diff = index.diff(index_old);

		diff.write(new File("diff.txt"));
	}

	public static boolean existsBackup(File backupDir, int n) {
		return new File(backupDir, getBackupName(n)).exists();
	}

	private final File _backupDir;
	private final File _sourceDir;
	private final LinearFileIndex _index;
	private final LinearFileIndex _lastIndex;
	private final int _number;

	public Backup(File backupDir, File sourceDir, LinearFileIndex index, LinearFileIndex lastIndex, int n) {

		_backupDir = backupDir;
		_sourceDir = sourceDir;
		_index = index;
		_number = n;
		_lastIndex = lastIndex;
	}

	public LinearFileIndex getIndex() {
		return _index;
	}

	public Backup(File backupDir, File sourceDir, LinearFileIndex last_index, int n) {
		this(backupDir, sourceDir, new LinearFileIndex(), last_index, n);
		_index.read(new File(_backupDir, this.getIndexName()));
	}

	public static String getBackupName(int n) {
		return "backup_" + n + ".zip";
	}

	public static String getIndexName(int n) {
		return ".index_" + n;
	}

	public String getBackupName() {
		return Backup.getBackupName(_number);
	}

	public String getIndexName() {
		return Backup.getIndexName(_number);
	}

	public void updateIndex() {
		File root = _sourceDir;

		Stack<RelativeFile> filestack = new Stack<RelativeFile>();
		filestack.push(new RelativeFile(root, true));

		while (!filestack.isEmpty()) {
			RelativeFile file = filestack.pop();
			if (file.getFile().isDirectory()) {
				File[] content = file.getFile().listFiles();
				if (content != null && content.length > 0) {
					for (File f : content) {
						System.out.println(new RelativeFile(f, file).getRelativePath());
						filestack.push(new RelativeFile(f, file));
					}
				} else {
					_index.addDirectory(file.getRelativePath());
				}
			} else {
				try {
					_index.addFile(file.getRelativePath(), Tools.SHA1Hash(file.getFile().getAbsolutePath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class RelativeFile {
		private final File _file;
		private StringBuilder _relPath;
		private final boolean _isRoot;

		public RelativeFile(File f, boolean isRoot) {
			_file = f;
			_relPath = new StringBuilder();
			_isRoot = isRoot;
		}

		public RelativeFile(File f, RelativeFile parent) {
			this(f, false);
			System.out.println(parent.getRelativePath());
			appendPath(parent.getRelativePath());
		}

		private void appendPath(String path) {
			if (!path.isEmpty()) {
				_relPath.append(path + "/");
			}
		}

		public File getFile() {
			return _file;
		}

		public String getRelativePath() {
			return _relPath.toString() + (_isRoot ? "" : _file.getName());
		}
	}

	// public void updateIndexRecursively() {
	// File root = _sourceDir;
	// updateIndexRecursively(root, 0);
	// }

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

	public void writeBackup() throws IOException {
		_index.write(new File(_backupDir, this.getIndexName()));
		LinearFileIndex index = _index;
		if (_lastIndex != null) {
			index = index.diff(_lastIndex);
		}
		Tools.copyToZIP(index, _sourceDir, new File(_backupDir, this.getBackupName()).getAbsolutePath());
	}
}
