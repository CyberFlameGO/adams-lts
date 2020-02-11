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
 * WekaSpreadSheetToPredictions.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.DefaultCompare;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetUnorderedColumnRange;
import adams.flow.core.Token;
import weka.classifiers.AggregateEvaluations;
import weka.classifiers.Evaluation;

import java.io.Serializable;
import java.util.Comparator;

/**
 <!-- globalinfo-start -->
 * Turns the predictions stored in the incoming spreadsheet (actual and predicted) into a Weka weka.classifiers.Evaluation object.<br>
 * For recreating the predictions of a nominal class, the class distributions must be present in the spreadsheet as well.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.WekaPredictionsToSpreadSheet
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaSpreadSheetToPredictions
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-actual &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: actual)
 * &nbsp;&nbsp;&nbsp;The column with the actual values.
 * &nbsp;&nbsp;&nbsp;default: Actual
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-predicted &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: predicted)
 * &nbsp;&nbsp;&nbsp;The column with the predicted values.
 * &nbsp;&nbsp;&nbsp;default: Predicted
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-class-distribution &lt;adams.data.spreadsheet.SpreadSheetUnorderedColumnRange&gt; (property: classDistribution)
 * &nbsp;&nbsp;&nbsp;The columns containing the class distribution (nominal class).
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-column-names-as-class-labels &lt;boolean&gt; (property: useColumnNamesAsClassLabels)
 * &nbsp;&nbsp;&nbsp;If enabled, the names of the class distribution columns are used as labels
 * &nbsp;&nbsp;&nbsp;in the fake evaluation; automatically removes the surrounding 'Distribution
 * &nbsp;&nbsp;&nbsp;(...)'.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-weight &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: weight)
 * &nbsp;&nbsp;&nbsp;The (optional) column with the weights of the instances; 1.0 is assumed
 * &nbsp;&nbsp;&nbsp;by default.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-sort-labels &lt;boolean&gt; (property: sortLabels)
 * &nbsp;&nbsp;&nbsp;If enabled, the labels get sorted with the specified comparator.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-comparator &lt;java.util.Comparator&gt; (property: comparator)
 * &nbsp;&nbsp;&nbsp;The comparator to use; must implement java.util.Comparator and java.io.Serializable
 * &nbsp;&nbsp;&nbsp;default: adams.core.DefaultCompare
 * </pre>
 *
 * <pre>-reverse &lt;boolean&gt; (property: reverse)
 * &nbsp;&nbsp;&nbsp;If enabled, the sorting order gets reversed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaSpreadSheetToPredictions
  extends AbstractTransformer
  implements ClassCrossReference {

  private static final long serialVersionUID = -2097531874480331676L;

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** the columns with the class distributions. */
  protected SpreadSheetUnorderedColumnRange m_ClassDistribution;

  /** whether to use the column name as class labels. */
  protected boolean m_UseColumnNamesAsClassLabels;

  /** the (optional) column with the instance weights. */
  protected SpreadSheetColumnIndex m_Weight;

  /** whether to sort the labels. */
  protected boolean m_SortLabels;

  /** the comparator to use. */
  protected Comparator m_Comparator;

  /** whether to reverse the sorting. */
  protected boolean m_Reverse;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns the predictions stored in the incoming spreadsheet (actual and "
	+ "predicted) into a Weka " + Evaluation.class.getName() + " object.\n"
	+ "For recreating the predictions of a nominal class, the class distributions "
	+ "must be present in the spreadsheet as well.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{WekaPredictionsToSpreadSheet.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actual", "actual",
      new SpreadSheetColumnIndex("Actual"));

    m_OptionManager.add(
      "predicted", "predicted",
      new SpreadSheetColumnIndex("Predicted"));

    m_OptionManager.add(
      "class-distribution", "classDistribution",
      new SpreadSheetUnorderedColumnRange(""));

    m_OptionManager.add(
      "column-names-as-class-labels", "useColumnNamesAsClassLabels",
      false);

    m_OptionManager.add(
      "weight", "weight",
      new SpreadSheetColumnIndex(""));

    m_OptionManager.add(
      "sort-labels", "sortLabels",
      false);

    m_OptionManager.add(
      "comparator", "comparator",
      new DefaultCompare());

    m_OptionManager.add(
      "reverse", "reverse",
      false);
  }

  /**
   * Sets the column with the actual values.
   *
   * @param value	the column
   */
  public void setActual(SpreadSheetColumnIndex value) {
    m_Actual = value;
    reset();
  }

  /**
   * Returns the column with the actual values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getActual() {
    return m_Actual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualTipText() {
    return "The column with the actual values.";
  }

  /**
   * Sets the column with the predicted values.
   *
   * @param value	the column
   */
  public void setPredicted(SpreadSheetColumnIndex value) {
    m_Predicted = value;
    reset();
  }

  /**
   * Returns the column with the predicted values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getPredicted() {
    return m_Predicted;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedTipText() {
    return "The column with the predicted values.";
  }

  /**
   * Sets the columns with the class distribution (nominal class).
   *
   * @param value	the range
   */
  public void setClassDistribution(SpreadSheetUnorderedColumnRange value) {
    m_ClassDistribution = value;
    reset();
  }

  /**
   * Returns the columns with the class distribution (nominal class).
   *
   * @return		the range
   */
  public SpreadSheetUnorderedColumnRange getClassDistribution() {
    return m_ClassDistribution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classDistributionTipText() {
    return "The columns containing the class distribution (nominal class).";
  }

  /**
   * Sets whether to use the names of the class distribution columns as
   * labels in the fake evaluation.
   *
   * @param value	true if to use column names
   */
  public void setUseColumnNamesAsClassLabels(boolean value) {
    m_UseColumnNamesAsClassLabels = value;
    reset();
  }

  /**
   * Returns whether to use the names of the class distribution columns as
   * labels in the fake evaluation.
   *
   * @return		true if to use column names
   */
  public boolean getUseColumnNamesAsClassLabels() {
    return m_UseColumnNamesAsClassLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColumnNamesAsClassLabelsTipText() {
    return
      "If enabled, the names of the class distribution columns are used as "
	+ "labels in the fake evaluation; automatically removes the "
	+ "surrounding 'Distribution (...)'.";
  }

  /**
   * Sets the (optional) column with the instance weight values.
   *
   * @param value	the column
   */
  public void setWeight(SpreadSheetColumnIndex value) {
    m_Weight = value;
    reset();
  }

  /**
   * Returns the (optional) column with the instance weight values.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getWeight() {
    return m_Weight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String weightTipText() {
    return "The (optional) column with the weights of the instances; 1.0 is assumed by default.";
  }

  /**
   * Sets whether to sort the labels with the specified comparator.
   *
   * @param value	true if to sort
   */
  public void setSortLabels(boolean value) {
    m_SortLabels = value;
    reset();
  }

  /**
   * Returns whether to store the labels with the specified comparator.
   *
   * @return		true if to sort
   */
  public boolean getSortLabels() {
    return m_SortLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortLabelsTipText() {
    return "If enabled, the labels get sorted with the specified comparator.";
  }

  /**
   * Sets the comparator to use.
   *
   * @param value	the comparator
   */
  public void setComparator(Comparator value) {
    m_Comparator = value;
    reset();
  }

  /**
   * Returns the comparator to use.
   *
   * @return		the comparator
   */
  public Comparator getComparator() {
    return m_Comparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String comparatorTipText() {
    return "The comparator to use; must implement " + Comparator.class.getName() + " and " + Serializable.class.getName();
  }

  /**
   * Sets whether to reverse the sorting.
   *
   * @param value	true if to reverse
   */
  public void setReverse(boolean value) {
    m_Reverse = value;
    reset();
  }

  /**
   * Returns whether to reverse the sorting.
   *
   * @return		true if to reverse
   */
  public boolean getReverse() {
    return m_Reverse;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reverseTipText() {
    return "If enabled, the sorting order gets reversed.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "actual", m_Actual, "actual: ");
    result += QuickInfoHelper.toString(this, "predicted", m_Predicted, ", predicted: ");
    result += QuickInfoHelper.toString(this, "classDistribution", (m_ClassDistribution.isEmpty() ? "-none-" : m_ClassDistribution.getRange()), ", class: ");
    result += QuickInfoHelper.toString(this, "weight", (m_Weight.isEmpty() ? "-none-" : m_Weight.getIndex()), ", weight: ");
    if (m_SortLabels) {
      result += QuickInfoHelper.toString(this, "comparator", m_Comparator, ", sort: ");
      result += QuickInfoHelper.toString(this, "reverse", m_Reverse, "reverse", ", ");
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Evaluation.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;
    AggregateEvaluations 	aggregate;
    Evaluation 			agg;

    result = null;
    sheet  = (SpreadSheet) m_InputToken.getPayload();
    m_Actual.setData(sheet);
    m_Predicted.setData(sheet);
    m_ClassDistribution.setData(sheet);
    m_Weight.setData(sheet);
    if (m_Actual.getIntIndex() == -1)
      result = "'Actual' column not found: " + m_Actual;
    else if (m_Predicted.getIntIndex() == -1)
      result = "'Predicted' column not found: " + m_Predicted;

    if (result == null) {
      aggregate = new AggregateEvaluations();
      aggregate.setSortLabels(m_SortLabels);
      aggregate.setComparator(m_Comparator);
      aggregate.setReverse(m_Reverse);
      result    = aggregate.add(
	sheet,
	m_Actual.getIntIndex(),
	m_Predicted.getIntIndex(),
	m_Weight.getIntIndex(),
	m_ClassDistribution.getIntIndices(),
	m_UseColumnNamesAsClassLabels);

      if (result == null) {
        agg = aggregate.aggregated();
        if (agg == null) {
          if (aggregate.hasLastError())
	    result = aggregate.getLastError();
          else
            result = "Failed to aggregate predictions!";
	}
	if (agg != null)
	  m_OutputToken = new Token(agg);
      }
    }

    return result;
  }
}
