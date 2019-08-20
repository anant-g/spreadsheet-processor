package com.processor.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.processor.driver.Cell;

/**
 * @author anantg
 *
 */
public class Utils {

	final static Logger logger = Logger.getLogger(Utils.class);

	public static int countLinesNew(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];

			int readChars = is.read(c);
			if (readChars == -1) {
				// bail out if nothing to read
				return 0;
			}

			// make it easy for the optimizer to tune this loop
			int count = 1;
			while (readChars == 1024) {
				for (int i = 0; i < 1024;) {
					if (c[i++] == '\n') {
						++count;
					}
				}
				readChars = is.read(c);
			}

			// count remaining characters
			while (readChars != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
				readChars = is.read(c);
			}

			return count == 0 ? 1 : count;
		} finally {
			is.close();
		}
	}

	public static Matcher extractVariablesFromExpression(String expression) {
		Pattern p = Pattern.compile("[A-Z]\\d+");
		return p.matcher(expression);
	}

	/**
	 * loading properties file
	 * 
	 * @param prop
	 * @param propFileName
	 */
	public static Properties loadProperties(String propFileName) {

		Properties prop = new Properties();
		try {
			// FileInputStream inputStream = new FileInputStream(propFileName);
			InputStream inputStream = Utils.class.getResourceAsStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			}
		} catch (IOException e) {
			logger.error("property file '" + propFileName + "' could not be loaded");
			e.printStackTrace();
		}
		return prop;
	}

	/**
	 * @param cell
	 *            whose index is to be found in the csv matrix
	 * @return returns the index of the cell based on location of the cell in the
	 *         matrix.
	 * 
	 *         e.g. in a 3*4 matrix ( 3 rows , 4 columns) the index corresponding to
	 *         cell C3 will be 11
	 */
	public static int getCellIndex(String variable, int numColumns, String pattern) {
		if (!variable.matches(pattern))
			return -1;
		int rowIntId = variable.charAt(0) - 'A';
		int colId = Integer.parseInt(variable.substring(1));
		return (Math.max(rowIntId * numColumns + colId, 0));
	}

	public static void writeCSV(List<Cell> cells, int numRows, String outputFile) {

		// partition the List into multiple sub-lists
		List<List<Cell>> lists = Lists.partition(cells, numRows);

		try {
			FileWriter csvWriter = new FileWriter(outputFile);
			for (List<Cell> list : lists) {
				List<String> finalList = new ArrayList<String>();
				for (Cell cell : list) {
					finalList.add(String.format("%.5f", cell.getValue()));
				}
				csvWriter.append(String.join(",", finalList));
				csvWriter.append("\n");
			}

			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			logger.error("error writing output file");
			logger.error(e.getMessage());
		}
	}

	public static CommandLine cliParser(String[] args) {
		Options options = new Options();

		Option input = new Option("i", "input", true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file");
		output.setRequired(true);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			formatter.printHelp("spreadsheet-processor", options);
			System.exit(1);
		}

		return cmd;
	}
}
