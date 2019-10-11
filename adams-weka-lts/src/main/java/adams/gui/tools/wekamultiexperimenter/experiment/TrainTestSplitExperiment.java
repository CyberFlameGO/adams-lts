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
 * TrainTestSplitExperiment.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.classifiers.Classifier;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;

/**
 * Performs train-test splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainTestSplitExperiment
  extends AbstractExperiment {

  private static final long serialVersionUID = -4147644361063132314L;

  public static class TrainTestSplitExperimentJob
    extends AbstractExperimentJob<TrainTestSplitExperiment> {

    private static final long serialVersionUID = 4735723305880372881L;

    /**
     * Initializes the run.
     *
     * @param owner      the owning experiment
     * @param run        the current run
     * @param classifier the classifier to evaluate
     * @param data       the data to use for evaluation
     */
    public TrainTestSplitExperimentJob(TrainTestSplitExperiment owner, int run, Classifier classifier, Instances data) {
      super(owner, run, classifier, data);
    }

    /**
     * Performs the evaluation.
     */
    @Override
    protected void evaluate() {
      RandomSplitGenerator 	generator;
      WekaTrainTestSetContainer	cont;
      Instances			train;
      Instances			test;
      Classifier		classifier;
      Evaluation 		eval;
      SpreadSheet 		results;

      m_Owner.log("Run " + m_Run + " [start]: " + m_Data.relationName() + " on " + shortenCommandLine(m_Classifier));

      generator = (RandomSplitGenerator) OptionUtils.shallowCopy(m_Owner.getGenerator());
      generator.setData(m_Data);
      generator.setSeed(m_Run);
      generator.setPercentage(m_Owner.getPercentage() / 100.0);
      generator.setPreserveOrder(m_Owner.getPreserveOrder());
      cont  = generator.next();
      train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
      test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
      try {
	classifier = (Classifier) OptionUtils.shallowCopy(m_Classifier);
	classifier.buildClassifier(train);
	eval       = new Evaluation(train);
	eval.evaluateModel(classifier, test);
	results = new DefaultSpreadSheet();
	addMetrics(results, m_Run, m_Classifier, m_Data, eval);
	m_Owner.appendResults(results);
      }
      catch (Exception e) {
	LoggingHelper.handleException(m_Owner, "Failed to evaluate classifier on train/test split!", e);
      }

      m_Owner.log("Run " + m_Run + " [end]: " + m_Data.relationName() + " on " + shortenCommandLine(m_Classifier));
    }
  }

  /** the split percentage. */
  protected double m_Percentage;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /** the split generator to use. */
  protected RandomSplitGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs train-test splits for each classifier/dataset combination.\n"
	+ "Order can be preserved in the datasets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "percentage", "percentage",
      66.0, 0.0, 100.0);

    m_OptionManager.add(
      "preserve-order", "preserveOrder",
      false);

    m_OptionManager.add(
      "generator", "generator",
      new DefaultRandomSplitGenerator());
  }

  /**
   * Sets the split percentage.
   *
   * @param value	the percentage
   */
  public void setPercentage(double value) {
    m_Percentage = value;
    reset();
  }

  /**
   * Returns the split percentage.
   *
   * @return		the percentage
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return "The split percentage to use.";
  }

  /**
   * Sets whether to preserve the order.
   *
   * @param value 	true if to preserve
   */
  public void setPreserveOrder(boolean value) {
    m_PreserveOrder = value;
    reset();
  }

  /**
   * Returns whether to preserve the order.
   *
   * @return 		true if to preserve
   */
  public boolean getPreserveOrder() {
    return m_PreserveOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preserveOrderTipText() {
    return "If enabled, no data randomization is occurring before splitting the data into train and test set.";
  }

  /**
   * Sets the scheme for generating the split.
   *
   * @param value	the generator
   */
  public void setGenerator(RandomSplitGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the scheme for generating the split.
   *
   * @return		the generator
   */
  public RandomSplitGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The scheme to use for generating the split; the actor options take precedence over the scheme's ones.";
  }

  /**
   * Evaluates the classifier on the dataset.
   *
   * @param currentRun	the current run
   * @param cls		the classifier to evaluate
   * @param data	the dataset to evaluate on
   * @return		null if successful, otherwise error message
   */
  @Override
  protected synchronized TrainTestSplitExperimentJob evaluate(int currentRun, Classifier cls, Instances data) {
    return new TrainTestSplitExperimentJob(this, currentRun, cls, data);
  }
}
