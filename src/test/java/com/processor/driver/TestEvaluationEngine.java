package com.processor.driver;

import javax.script.ScriptException;

import com.processor.engine.ComputationEngine;

public class TestEvaluationEngine {

	
	public static void main(String[] args) {

		String expr = "5+40.0/(2.0+4)*1.0";
		ComputationEngine engine = new ComputationEngine();
		//System.out.println(engine.eval(expr).toString());
		//System.out.println(TestEngine.evaluateExpression(expr));
		// engine.evaluateExpression(engine.replaceVariables(map, expr));
		//System.out.println(TestEngine.getCellIndex("B3"));

	}
	
//	public void test(String expr) {
//		try {
//		//	System.out.println(engine.eval(expr).toString());
//		} catch (ScriptException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
