package de.jback;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Main {
	
	public static String source;
	public static String destination;
	
	public static void main(String[] args)
	{
		try {
			CLIParser parser = new CLIParser(args);
			
			Main.source = parser.getOptionValue("src");
			Main.destination = parser.getOptionValue("dir");
			
			BackupManager backman = BackupManager.getInstance(Main.destination, Main.source);
						
			if (parser.hasOption("gui")){
				Main.gui();
			}
			else if (parser.hasOption("backup"))
			{
				backman.performBackup();
			}
			else if (parser.hasOption("recover"))
			{
				backman.performRecovery();
			}
			
		} catch (Exception e) {
			System.err.println("An unhandled exception occurred:\n");
			e.printStackTrace();
		}
	}

	private static void gui() {
		System.out.printf("GUI: %s, %s\n", Main.source, Main.destination);
		
	}
	
}


class CLIParser {

	@SuppressWarnings("static-access")
	public static Options constructOptions()
	{
		// long option, description, num args, arg name, short name
		String[][] opts = new String[][] {
				{"G", "Opens a graphical interface", "gui"},
				{"S", "Specifies the source directory", "src", "", "1", "PATH"},
				{"D", "Specifies the backup directory","dir", "","1", "PATH"},
				{"b", "Performs a full backup", "backup"},
				{"R", "Performs a full recovery", "recover"}};		
		
		Options options = new Options();
		
		for (String[] opt : opts) {
			int i = 1;
			OptionBuilder builder = OptionBuilder.withDescription(opt[i++]);
			if (opt.length > 2) builder.withLongOpt(opt[i++]);
			if (opt.length > 3) builder.isRequired();
			if (opt.length > 4) builder.hasArgs(Integer.parseInt(opt[++i]));
			if (opt.length > 5) builder.withArgName(opt[++i]);
			options.addOption(builder.create(opt[0]));
		}
		
		options.addOption(new Option(null, "help", false, "prints this message"));

		return options;
	}
	
	private CommandLineParser _cliParser;
	private HelpFormatter _help;
	private CommandLine _cl;

	private Options _options;

	public CLIParser(String[] args) throws ParseException  {
		this._cliParser = new PosixParser();
		this._help = new HelpFormatter();
		this._options = CLIParser.constructOptions();
		this._cl = this._cliParser.parse(this._options, args);
	}

	public String getOptionValue(String option) {
		return this._cl.hasOption(option) ? this._cl.getOptionValue(option)
				: "";
	}

	public String[] getOptionValues(String option) {
		if (this._cl.hasOption(option)) {
			return this._cl.getOptionValues(option);
		}
		return new String[] {};
	}

	public boolean hasOption(String option) {
		return this._cl.hasOption(option);
	}

	public boolean help() {
		if (this._cl.hasOption("help") || this._cl.getOptions().length == 0) {
			this._help.printHelp("java -jar jback.jar [options]\nUse --help to get more information",
					this._options);
			return true;
		}
		return false;
	}
}
