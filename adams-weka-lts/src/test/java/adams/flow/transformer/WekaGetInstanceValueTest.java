/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * WekaGetInstanceValueTest.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Index;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;

/**
 * Tests the WekaGetInstanceValue actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGetInstanceValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaGetInstanceValueTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("labor.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile1.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile2.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("labor.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile1.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile2.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("labor.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.INCREMENTAL);

    WekaClassSelector cs = new WekaClassSelector();

    WekaGetInstanceValue get1 = new WekaGetInstanceValue();
    get1.setIndex(new Index("last"));

    DumpFile df1 = new DumpFile();
    df1.setAppend(true);
    df1.setOutputFile(new TmpFile("dumpfile1.txt"));

    Sequence seq1 = new Sequence();
    seq1.setActors(new Actor[]{
	get1, df1
    });

    WekaGetInstanceValue get2 = new WekaGetInstanceValue();
    get2.setIndex(new Index("first"));

    DumpFile df2 = new DumpFile();
    df2.setAppend(true);
    df2.setOutputFile(new TmpFile("dumpfile2.txt"));

    Sequence seq2 = new Sequence();
    seq2.setActors(new Actor[]{
	get2, df2
    });

    Branch br = new Branch();
    br.setBranches(new Actor[]{
	seq1,
	seq2
    });

    Flow flow = new Flow();
    flow.setActors(new Actor[]{sfs, fr, cs, br});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile[]{
	    new TmpFile("dumpfile1.txt"),
	    new TmpFile("dumpfile2.txt")
	});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaGetInstanceValueTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
