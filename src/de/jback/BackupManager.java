package de.jback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/*
 * TODO:
 * 
 * recovery: wipe all & recover vs. hash all & recover vs. diff & overwrite
 */

public class BackupManager {

	public static void main(String[] args) {

		BackupManager backman = BackupManager.getInstance("testing/backup", "testing/data");
//		try {
//			backman.performBackup();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			backman.close();
//		}

		backman.performRecovery();
	}

	private static Properties getProps(String backupDir) {
		Properties props = new Properties();
		File f = new File(backupDir, ".source");
		FileReader fr;
		try {
			fr = new FileReader(f);
			props.load(fr);
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return props;
	}

	private static Properties saveProps(Properties props, String backupDir) {
		File f = new File(backupDir, ".source");
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			props.store(fw, "jBack Property File. Do not change anything by hand!");
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return props;
	}

	/**
	 * Creates a new backup manager using a previously used backup directory
	 * 
	 * @param backupDir
	 * @return
	 */
	public static BackupManager getInstance(String backupDir) {
		Properties props = BackupManager.getProps(backupDir);
		return new BackupManager(new File(backupDir), props);
	}

	/**
	 * Creates a new backup manager using a new empty backup directory
	 * 
	 * @param backupDir
	 * @param sourceDir
	 * @return
	 */
	public static BackupManager getInstance(String backupDir, String sourceDir) {
		File fileBackup = new File(backupDir);
		File fileSource = new File(sourceDir);

		Properties props = new Properties();
		props.setProperty("source", fileSource.getAbsolutePath());
		props.setProperty("number", "0");

		if (!fileBackup.isDirectory() || fileBackup.listFiles().length > 0) {
			if (new File(backupDir, ".source").exists()) {
				return getInstance(backupDir);
			}
			throw new IllegalArgumentException("Choose an empty directory.");
		}

		return new BackupManager(fileBackup, props);
	}

	private final File _backupDir;
	private final File _sourceDir;
	private int _backupCount;
	private Properties _props;

	private BackupManager(File backupDir, Properties props) {
		_backupDir = backupDir;
		_props = props;
		_sourceDir = new File(props.getProperty("source"));
		_backupCount = Integer.parseInt(props.getProperty("number", "0"));
	}

	public void close() {
		_props.setProperty("number", _sourceDir.getAbsolutePath());
		_props.setProperty("number", _backupCount + "");
		BackupManager.saveProps(_props, _backupDir.getAbsolutePath());
	}

	public LinearFileIndex getIndex(int number) {
		if (_backupCount == 0 || !(new File(_backupDir, Backup.getIndexName(number)).exists())) {
			return null;
		}
		LinearFileIndex last_index = new LinearFileIndex();
		last_index.read(new File(_backupDir, Backup.getIndexName(number)));
		return last_index;
	}

	public Backup getBackup(int number) {
		Backup backup = new Backup(_backupDir, _sourceDir, getIndex(number - 1), number);
		return backup;
	}

	public void performBackup() throws IOException{
		LinearFileIndex lastIndex = getIndex(_backupCount);
		Backup backup = new Backup(_backupDir, _sourceDir, new LinearFileIndex(), lastIndex, _backupCount + 1);
		backup.updateIndex();
		try {
			backup.writeBackup();
			_backupCount++;
		} catch (IOException e) {
			throw e;
		}
	}

	public void performRecovery() {
		LinearFileIndex index = this.getBackup(_backupCount).getIndex();
		try {
			for (int n = _backupCount; !index.isEmpty() && n >= 0; n--) {
				if (Backup.existsBackup(_backupDir, n)) {
					System.out.println("blubb");
					Backup backup = getBackup(n);
					Tools.copyFromZIP(index, new File(_backupDir, backup.getBackupName()).getAbsolutePath(), "testing/recovery/");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!index.isEmpty()) {
				// TODO: throw adequate exception
				for (String s : index.directories())
				{
					System.out.println(s);
				}
				throw new UnsupportedOperationException("Not implemented yet.");
			}
		}

	}
}
