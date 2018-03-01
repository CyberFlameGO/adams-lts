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
 * WekaBootstrappingTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseDouble;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.EvaluationStatistic;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.source.WekaClassifierSetup;
import adams.flow.standalone.CallableActors;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.classifiers.functions.LinearRegressionJ;
import weka.core.converters.SimpleArffLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for WekaBootstrapping actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaBootstrappingTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaBootstrappingTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaBootstrappingTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.WekaClassifierSetup
      WekaClassifierSetup wekaclassifiersetup = new WekaClassifierSetup();
      LinearRegressionJ linearregressionj = new LinearRegressionJ();
      linearregressionj.setOptions(OptionUtils.splitOptions("-S 1 -C -R 1.0E-8 -num-decimal-places 4"));
      wekaclassifiersetup.setClassifier(linearregressionj);

      actors2.add(wekaclassifiersetup);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/bolts.arff"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.WekaFileReader
      WekaFileReader wekafilereader = new WekaFileReader();
      SimpleArffLoader simplearffloader = new SimpleArffLoader();
      wekafilereader.setCustomLoader(simplearffloader);

      actors.add(wekafilereader);

      // Flow.WekaClassSelector
      WekaClassSelector wekaclassselector = new WekaClassSelector();
      actors.add(wekaclassselector);

      // Flow.WekaCrossValidationEvaluator
      WekaCrossValidationEvaluator wekacrossvalidationevaluator = new WekaCrossValidationEvaluator();
      Null null_ = new Null();
      wekacrossvalidationevaluator.setOutput(null_);

      actors.add(wekacrossvalidationevaluator);

      // Flow.WekaBootstrapping
      WekaBootstrapping wekabootstrapping = new WekaBootstrapping();
      argOption = (AbstractArgumentOption) wekabootstrapping.getOptionManager().findByProperty("numSubSamples");
      wekabootstrapping.setNumSubSamples((Integer) argOption.valueOf("20"));
      argOption = (AbstractArgumentOption) wekabootstrapping.getOptionManager().findByProperty("percentiles");
      List<BaseDouble> percentiles = new ArrayList<>();
      percentiles.add((BaseDouble) argOption.valueOf("0.95"));
      wekabootstrapping.setPercentiles(percentiles.toArray(new BaseDouble[0]));
      actors.add(wekabootstrapping);
      wekabootstrapping.setWithReplacement(false);
      wekabootstrapping.setStatisticValues(new EvaluationStatistic[]{EvaluationStatistic.MEAN_ABSOLUTE_ERROR});

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors.add(dumpfile);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

