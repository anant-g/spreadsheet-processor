package com.processor.driver;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTestProcessingDriver {

	private ProcessingDriver driver;
	private String inputPath;
	private String outputPath;
	private String referenceFile;

	@Before
	public void initialize() {
		this.inputPath = "src/test/resources/input.csv";
		this.outputPath = "src/test/resources/test_output.csv";
		this.referenceFile = "src/test/resources/sample_output.csv";
		driver = new ProcessingDriver(this.inputPath, this.outputPath);
	}

	@Test
	public void testProcessingDriver() {
		driver.run();
		File actualOutput = new File(outputPath);
		File sampleOutput = new File(referenceFile);
		boolean isEqual = false;
		try {
			isEqual = FileUtils.contentEquals(actualOutput, sampleOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("files are same", isEqual);
	}
}
