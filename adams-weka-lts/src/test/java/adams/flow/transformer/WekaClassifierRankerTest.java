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
 * WekaClassifierRankerTest.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.conversion.AnyToCommandline;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.source.SequenceSource;
import adams.flow.source.WekaClassifierGenerator;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaClassifierRanker.Measure;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.MathParameter;

/**
 * Tests the WekaClassifierRanker actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierRankerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaClassifierRankerTest(String name) {
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

    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

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
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("vote.arff")});

    WekaFileReader fr = new WekaFileReader();

    WekaClassSelector cs = new WekaClassSelector();

    SequenceSource seq = new SequenceSource();
    seq.setName("dataset");
    seq.setActors(new Actor[]{
	sfs,
	fr,
	cs
    });

    CallableActors ga = new CallableActors();
    ga.setActors(new Actor[]{
	seq
    });

    WekaClassifierGenerator cg = new WekaClassifierGenerator();
    cg.setOutputArray(true);
    cg.setSetup(new weka.classifiers.trees.J48());
    MathParameter param = new MathParameter();
    param.setProperty("confidenceFactor");
    param.setBase(10.0);
    param.setMin(0.1);
    param.setMax(0.5);
    param.setStep(0.1);
    param.setExpression("I");
    cg.setParameters(new AbstractParameter[]{
	param
    });

    WekaClassifierRanker cr = new WekaClassifierRanker();
    cr.setNumThreads(1);
    cr.setMax(3);
    cr.setFolds(10);
    cr.setMeasure(Measure.RMSE);
    cr.setTrain(new CallableActorReference("dataset"));

    ArrayToSequence a2s = new ArrayToSequence();

    Convert conv = new Convert();
    AnyToCommandline a2c = new AnyToCommandline();
    conv.setConversion(a2c);

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ga, cg, cr, a2s, conv, df});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaClassifierRankerTest.class);
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
