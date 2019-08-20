package com.processor.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.processor.driver.Cell;
import com.processor.exception.CyclicDependencyException;
import com.processor.util.Utils;

import edu.princeton.cs.algorithms.Digraph;
import edu.princeton.cs.algorithms.Topological;

/**
 * @author anantg
 *
 */
public class DependencyEngine {

	final static Logger logger = Logger.getLogger(DependencyEngine.class);

	private int num_cols;
	private int num_rows;
	private Multimap<String, String> dependencyMap;
	private List<Cell> cells;
	private Digraph dependencyGraph;
	private Digraph inverseDependencyGraph;
	private Topological topologyOrder;

	public final static String VARIABLE_PATTERN = "[A-Z]\\d+";
	public final static String DELIMITER = ",";
	public final static char EXPRESSION_IDENTIFIER = '=';

	/**
	 * @param pathToCsv
	 * @throws IOException
	 * 
	 *             creates the dependency map from different expressions to clearly
	 *             identify which cell is dependent on which variables
	 */
	private void createDependencyMap(String pathToCsv) throws IOException {

		BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
		String row;
		this.num_rows = Utils.countLinesNew(pathToCsv);
		char pattern = DependencyEngine.EXPRESSION_IDENTIFIER;
		boolean firstPass = true;
		for (int i = 0; i < this.num_rows; i++) {
			row = csvReader.readLine();
			logger.debug(row);

			// to get number of rows and columns
			if (firstPass) {
				String[] data = row.split(DependencyEngine.DELIMITER);
				this.num_cols = data.length;
				this.cells = new ArrayList<Cell>(this.num_rows * this.num_cols);
				this.dependencyMap = HashMultimap.create();
				firstPass = false;
			}

			for (int j = 0; j < this.num_cols; j++) {
				String currCellStrId = Character.toString((char) ('A' + i)) + Integer.toString(j);
				String expression = row.split(DELIMITER)[j];
				Cell cell = new Cell(currCellStrId, expression);

				boolean hasDependency = false;

				// if the cell value starts with "=" , means it is an expression to be
				// evaluated.
				if (expression.charAt(0) == pattern) {
					Matcher m = Utils.extractVariablesFromExpression(expression);
					while (m.find()) {
						dependencyMap.put(currCellStrId, m.group());
					}

					hasDependency = true;
				}
				if (!hasDependency) {
					double cellValue = new Double(expression);
					cell.setValue(cellValue);
				}
				cells.add(cell);
			}

		}
		csvReader.close();
	}

	/**
	 * @param inputFile
	 *            : Input CSV file to generate dependency graph
	 * @throws Exception
	 * 
	 *             generates the reverse dependency graph of all variables in the
	 *             csv and detects cyclic dependency
	 */
	public void generateDependencyGraph(String inputFile) throws Exception {
		createDependencyMap(inputFile);
		int vertices = this.num_rows * this.num_cols;
		dependencyGraph = new Digraph(vertices);
		for (String key : dependencyMap.keySet()) {
			Set<String> dependencies = (Set<String>) dependencyMap.get(key);
			for (String dependency : dependencies) {
				dependencyGraph.addEdge(Utils.getCellIndex(key, this.num_cols, DependencyEngine.VARIABLE_PATTERN),
						Utils.getCellIndex(dependency, this.num_cols, DependencyEngine.VARIABLE_PATTERN));
			}
		}
		inverseDependencyGraph = dependencyGraph.reverse();
		this.topologyOrder = new Topological(inverseDependencyGraph);
		if (!this.topologyOrder.hasOrder()) {
			throw new CyclicDependencyException(
					"cyclic dependency detected in the given input file. existing program....");
		}
	}

	public void computeValues(String outputFile) {
		for (int index : this.topologyOrder.order()) {
			Cell cell = cells.get(index);
			double value = cell.getValue();

			// value of "0.0" is the initial value set for all expressions
			if (value == 0.0) {
				String expr = cell.getExpression();
				boolean hasVariable = false;
				if (expr != null) {
					if (expr.charAt(0) == EXPRESSION_IDENTIFIER) {
						hasVariable = true;
					}
				}
				cell.setValue(evaluateExpression(cell.getExpression(), hasVariable));
			}
		}
		logger.info("computation completed!");
		logger.info("writing output csv.....");
		Utils.writeCSV(this.cells, num_cols, outputFile);
	}

	private double evaluateExpression(String expression, boolean hasVariables) {
		if (expression == null)
			throw new NullPointerException("Expression is null");

		if (expression.isEmpty())
			throw new RuntimeException("Input line for a cell is blank");

		if (!expression.isEmpty() && !hasVariables)
			return Double.parseDouble(expression);
		else {

			return Double.parseDouble(ComputationEngine.evaluateExpression(expression, cells, this.num_cols));
		}
	}

}
