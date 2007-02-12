/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is part of the JavaPLT Ant extensions.
 * Download the current version of this project from http://www.drjava.org/
 * or http://sourceforge.net/projects/drjava/
 *
 * DrJava Open Source License
 * 
 * Copyright (C) 2001-2007 JavaPLT group at Rice University (javaplt@rice.edu).  All rights reserved.
 *
 * Developed by:   Java Programming Languages Team, Rice University, http://www.cs.rice.edu/~javaplt/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal with the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 *     - Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 *       following disclaimers.
 *     - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the 
 *       following disclaimers in the documentation and/or other materials provided with the distribution.
 *     - Neither the names of DrJava, the JavaPLT, Rice University, nor the names of its contributors may be used to 
 *       endorse or promote products derived from this Software without specific prior written permission.
 *     - Products derived from this software may not be called "DrJava" nor use the term "DrJava" as part of their 
 *       names without prior written permission from the JavaPLT group.  For permission, write to javaplt@rice.edu.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * WITH THE SOFTWARE.
 * 
 * This file is based on BriefJUnitResultFormatter from the Ant project.
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *END_COPYRIGHT_BLOCK*/

package edu.rice.cs.plt.ant;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

/**
 * Only prints out errors, no summaries.
 *
 * @see FormatterElement
 * @see PlainJUnitResultFormatter
 * @author Mathias Ricken
 */
public class QuietJUnitResultFormatter implements JUnitResultFormatter {

    /**
     * Where to write the log to.
     */
    private OutputStream out;

    /**
     * Used for writing the results.
     */
    private PrintWriter output;

    /**
     * Used as part of formatting the results.
     */
    private StringWriter results;

    /**
     * Used for writing formatted results to.
     */
    private PrintWriter resultWriter;

    /**
     * Formatter for timings.
     */
    private NumberFormat numberFormat = NumberFormat.getInstance();

    /**
     * Output suite has written to System.out
     */
    private String systemOutput = null;

    /**
     * Output suite has written to System.err
     */
    private String systemError = null;

    /**
     * Constructor for QuietJUnitResultFormatter.
     */
    public QuietJUnitResultFormatter() {
        results = new StringWriter();
        resultWriter = new PrintWriter(results);
    }

    /**
     * Sets the stream the formatter is supposed to write its results to.
     * @param out the output stream to write to
     */
    public void setOutput(OutputStream out) {
        this.out = out;
        output = new PrintWriter(out);
    }

    /**
     * @see JUnitResultFormatter#setSystemOutput(String)
     */
    /** {@inheritDoc}. */
    public void setSystemOutput(String out) {
        systemOutput = out;
    }

    /**
     * @see JUnitResultFormatter#setSystemError(String)
     */
    /** {@inheritDoc}. */
    public void setSystemError(String err) {
        systemError = err;
    }

    /**
     * String generated when the testsuite was started.
     */
    private String _startTestSuiteOutput = "";

    /**
     * The whole testsuite started.
     * @param suite the test suite
     */
    public void startTestSuite(JUnitTest suite) {
        if (output == null) {
            return; // Quick return - no output do nothing.
        }
        StringBuffer sb = new StringBuffer("Testsuite: ");
        sb.append(suite.getName());
        sb.append(StringUtils.LINE_SEP);
 _startTestSuiteOutput = sb.toString();
    }

    /**
     * The whole testsuite ended.
     * @param suite the test suite
     */
    public void endTestSuite(JUnitTest suite) {
        StringBuffer sb = new StringBuffer("Tests run: ");
        sb.append(suite.runCount());
        sb.append(", Failures: ");
        sb.append(suite.failureCount());
        sb.append(", Errors: ");
        sb.append(suite.errorCount());
        sb.append(", Time elapsed: ");
        sb.append(numberFormat.format(suite.getRunTime() / 1000.0));
        sb.append(" sec");
        sb.append(StringUtils.LINE_SEP);
        sb.append(StringUtils.LINE_SEP);

        // append the err and output streams to the log
        if (systemOutput != null && systemOutput.length() > 0) {
            sb.append("------------- Standard Output ---------------")
                    .append(StringUtils.LINE_SEP)
                    .append(systemOutput)
                    .append("------------- ---------------- ---------------")
                    .append(StringUtils.LINE_SEP);
        }

        if (systemError != null && systemError.length() > 0) {
            sb.append("------------- Standard Error -----------------")
                    .append(StringUtils.LINE_SEP)
                    .append(systemError)
                    .append("------------- ---------------- ---------------")
                    .append(StringUtils.LINE_SEP);
        }

        if (output != null) {
     if ((suite.failureCount()!=0) || (suite.errorCount()!=0)) {
  try {
      output.write(_startTestSuiteOutput);
      output.write(sb.toString());
      resultWriter.close();
      output.write(results.toString());
      output.flush();
  } finally {
      if (out != System.out && out != System.err) {
   FileUtils.close(out);
      }
  }
            }
        }
    }

    /**
     * A test started.
     * @param test a test
     */
    public void startTest(Test test) {
    }

    /**
     * A test ended.
     * @param test a test
     */
    public void endTest(Test test) {
    }

    /**
     * Interface TestListener for JUnit &lt;= 3.4.
     *
     * <p>A Test failed.
     * @param test a test
     * @param t    the exception thrown by the test
     */
    public void addFailure(Test test, Throwable t) {
        formatError("\tFAILED", test, t);
    }

    /**
     * Interface TestListener for JUnit &gt; 3.4.
     *
     * <p>A Test failed.
     * @param test a test
     * @param t    the assertion failed by the test
     */
    public void addFailure(Test test, AssertionFailedError t) {
        addFailure(test, (Throwable) t);
    }

    /**
     * A test caused an error.
     * @param test  a test
     * @param error the error thrown by the test
     */
    public void addError(Test test, Throwable error) {
        formatError("\tCaused an ERROR", test, error);
    }

    /**
     * Format the test for printing..
     * @param test a test
     * @return the formatted testname
     */
    protected String formatTest(Test test) {
        if (test == null) {
            return "Null Test: ";
        } else {
            return "Testcase: " + test.toString() + ":";
        }
    }

    /**
     * Format an error and print it.
     * @param type the type of error
     * @param test the test that failed
     * @param error the exception that the test threw
     */
    protected synchronized void formatError(String type, Test test,
                                            Throwable error) {
        if (test != null) {
            endTest(test);
        }

        resultWriter.println(formatTest(test) + type);
        resultWriter.println(error.getMessage());
        String strace = JUnitTestRunner.getFilteredTrace(error);
        resultWriter.println(strace);
        resultWriter.println();
    }
}
