package de.jback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class BackupManager {
	
	public static void main(String[] args)
	{
		BackupManager backman = BackupManager.getInstance("/data/code/projects/Java/jBack/testing/backup");
		Backup backup = backman.createNewBackup();
		backup.updateIndex();
		try {
			backup.writeBackup();
			System.out.println("Backup finished successfully!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static Properties getProps(String backupDir)
	{
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
	
	/**
	 * Creates a new backup manager using a previously used backup directory
	 * @param backupDir
	 * @return
	 */
	public static BackupManager getInstance(String backupDir) {
		Properties props = BackupManager.getProps(backupDir);
		return getInstance(backupDir, props.getProperty("src"));
	}
	
	/**
	 * Creates a new backup manager using a new empty backup directory
	 * @param backupDir
	 * @param sourceDir
	 * @return
	 */
	public static BackupManager getInstance(String backupDir, String sourceDir)
	{
		File fileBackup = new File(backupDir);
		File fileSource = new File(sourceDir);
		
		if (!fileBackup.isDirectory() || fileBackup.listFiles().length > 0)
		{
			throw new IllegalArgumentException("Choose an empty directory.");
		}
		return new BackupManager(fileBackup, fileSource);
	}
	
	private final File _backupDir;
	private final File _sourceDir;
	private int _backupCount;
	
	private BackupManager(File backupDir, File sourceDir)
	{
		_backupDir = backupDir;
		_sourceDir = sourceDir;
	}

	public Backup createNewBackup()
	{
		LinearFileIndex index = new LinearFileIndex();
		Backup backup = new Backup(_backupDir, _sourceDir, index);
		_backupCount++;
		return backup;
	}
}
