Overview
============
This program takes an spreadsheet in the form of Input CSV and generates the computed output CSV.

1. DependencyGraph algorith used --> import edu.princeton.cs.algorithms.Digraph
2. Topological algorith used --> import edu.princeton.cs.algorithms.Topological
3. Expression computation used --> javax.script.ScriptEngineManager


source-code 
========================
spreadsheet-processor/src
spreadsheet-processor/pom.xml


Main program file
========================
ProcessingDriver.java


Integration test file
========================
IntegrationTestProcessingDriver.java

Runnable Jar
=======================
build the artifact from maven pom file
rename uber-spreadsheet-processor-0.0.1-SNAPSHOT.jar --> spreadsheet.jar

Running the program
========================
java -jar spreadsheet.jar -i <input_filepath> -o <output_filepath>

Log File Name
=========================
spreadsheet-processor.log

the log file will be generated in the same directory from which you run the program




Limitations and Possible Optimizations
=======================================
The code has not been fully unit tested due to time constraints. However an Integration test has been included.
The code might not scale well as we increase the number of columns in the CSV file as it will be limited by the memory allocated to the JVM

In order to scale the program to support several million columns , the in-memory cell matrix can be saved to a key-value database  and only specific keys will be read during computation of expressions , without keeping the whole cell matrix in memory.



