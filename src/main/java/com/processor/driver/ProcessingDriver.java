package com.processor.driver;

import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.processor.engine.DependencyEngine;
import com.processor.exception.CyclicDependencyException;
import com.processor.util.Utils;

/**
 * @author anantg
 *
 */
public class ProcessingDriver {

	final static Logger logger = Logger.getLogger(ProcessingDriver.class);

	private String inputFile;
	private String outputFile;
	private DependencyEngine dependencyEngine;

	private static final int ERROR_EXIT_CODE_CYCLIC = -1;
	private static final int ERROR_EXIT_CODE_FILE = -2;

	public ProcessingDriver(String input, String output) {
		this.inputFile = input;
		this.outputFile = output;
		init();
	}

	public void init() {
		Properties props = new Properties();
		props = Utils.loadProperties("src/main/resources/log4j.properties");
		PropertyConfigurator.configure(props);
	}

	public static void main(String[] args) {
		CommandLine cmd = Utils.cliParser(args);
		ProcessingDriver driver = new ProcessingDriver(cmd.getOptionValue("input"), cmd.getOptionValue("output"));
		driver.run();
	}

	public void run() {
		logger.info("processing csv to generate in-memory dependency graph");
		try {
			dependencyEngine = new DependencyEngine();
			dependencyEngine.generateDependencyGraph(this.inputFile);
		} catch (CyclicDependencyException e) {
			logger.fatal(e.getMessage());
			System.exit(ERROR_EXIT_CODE_CYCLIC);
		} catch (FileNotFoundException e) {
			logger.error("input file not found. existing program");
			logger.error(e.getMessage());
			System.exit(ERROR_EXIT_CODE_FILE);
		} catch (Exception e) {
			logger.error("exception running the program...");
			logger.error(e.getMessage());
		}
		logger.info("dependency graph generated");
		logger.info("starting computation engine.......");
		dependencyEngine.computeValues(this.outputFile);

		logger.info("Run completed !!!!!");
		logger.info("*********************************");
	}

}