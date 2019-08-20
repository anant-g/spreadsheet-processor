package com.processor.exception;

public class CyclicDependencyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CyclicDependencyException(String message) {
		super(message);
	}
}