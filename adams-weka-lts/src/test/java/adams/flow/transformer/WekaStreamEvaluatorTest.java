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
 * WekaStreamEvaluatorTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for WekaStreamEvaluator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaStreamEvaluatorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaStreamEvaluatorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("anneal.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("anneal.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaStreamEvaluatorTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[7];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors3 = new adams.flow.core.Actor[1];

      // Flow.CallableActors.WekaClassifierSetup
      adams.flow.source.WekaClassifierSetup wekaclassifiersetup4 = new adams.flow.source.WekaClassifierSetup();
      argOption = (AbstractArgumentOption) wekaclassifiersetup4.getOptionManager().findByProperty("classifier");
      weka.classifiers.bayes.NaiveBayesUpdateable naivebayesupdateable6 = new weka.classifiers.bayes.NaiveBayesUpdateable();
      wekaclassifiersetup4.setClassifier(naivebayesupdateable6);

      actors3[0] = wekaclassifiersetup4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier7.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files8 = new adams.core.io.PlaceholderFile[1];
      files8[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal.arff");
      filesupplier7.setFiles(files8);
      actors1[1] = filesupplier7;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.AArffLoader aarffloader11 = new weka.core.converters.AArffLoader();
      wekafilereader9.setCustomLoader(aarffloader11);

      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("outputType");
      wekafilereader9.setOutputType((adams.flow.transformer.WekaFileReader.OutputType) argOption.valueOf("INCREMENTAL"));
      actors1[2] = wekafilereader9;

      // Flow.WekaClassSelector
      adams.flow.transformer.WekaClassSelector wekaclassselector13 = new adams.flow.transformer.WekaClassSelector();
      actors1[3] = wekaclassselector13;

      // Flow.WekaStreamEvaluator
      adams.flow.transformer.WekaStreamEvaluator wekastreamevaluator14 = new adams.flow.transformer.WekaStreamEvaluator();
      argOption = (AbstractArgumentOption) wekastreamevaluator14.getOptionManager().findByProperty("output");
      weka.classifiers.evaluation.output.prediction.Null null16 = new weka.classifiers.evaluation.output.prediction.Null();
      wekastreamevaluator14.setOutput(null16);

      actors1[4] = wekastreamevaluator14;

      // Flow.WekaEvaluationSummary
      adams.flow.transformer.WekaEvaluationSummary wekaevaluationsummary17 = new adams.flow.transformer.WekaEvaluationSummary();
      wekaevaluationsummary17.setConfusionMatrix(true);

      wekaevaluationsummary17.setClassDetails(true);

      actors1[5] = wekaevaluationsummary17;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile18 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile18.getOptionManager().findByProperty("outputFile");
      dumpfile18.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile18.setAppend(true);

      actors1[6] = dumpfile18;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener21 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener21);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

