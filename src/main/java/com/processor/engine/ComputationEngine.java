package com.processor.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.processor.driver.Cell;
import com.processor.util.Utils;

/**
 * @author anantg
 *
 */
public class ComputationEngine {

	final static Logger logger = Logger.getLogger(ComputationEngine.class);
	private static ScriptEngineManager manager;
	private static ScriptEngine engine;

	static {
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");
	}

	public static String evaluateExpression(String expression, List<Cell> cells, int numColumns) {

		String finalExpression = replaceVariables(expression, cells, numColumns);
		try {

			return engine.eval(finalExpression).toString();
		} catch (ScriptException e) {
			logger.error("exception in evaluation engine");
			logger.error(e.getMessage());
			return null;
		}
	}

	public static String replaceVariables(String expression, List<Cell> cells, int numColumns) {
		Pattern p = Pattern.compile(DependencyEngine.VARIABLE_PATTERN);
		Map<String, String> mapVariables = new HashMap<String, String>();
		Matcher m = p.matcher(expression);
		while (m.find()) {
			mapVariables.put(m.group(), Double.toString(cells
					.get(Utils.getCellIndex(m.group(), numColumns, DependencyEngine.VARIABLE_PATTERN)).getValue()));
		}
		Object[] keyArr = mapVariables.keySet().toArray();
		Object[] valueArr = mapVariables.values().toArray();
		final String[] keys = Arrays.copyOf(keyArr, keyArr.length, String[].class);
		final String[] values = Arrays.copyOf(valueArr, valueArr.length, String[].class);

		// remove "=" sign from the final expression for engine calculation
		return StringUtils.replaceEach(expression, keys, values).substring(1);

	}

}
