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
 * WekaExperimentEvaluation.java
 * Copyright (C) 2009-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.core.classmanager.ClassManager;
import adams.flow.core.ExperimentStatistic;
import adams.flow.core.Token;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.CSVResultListener;
import weka.experiment.DatabaseResultListener;
import weka.experiment.Experiment;
import weka.experiment.InstanceQuery;
import weka.experiment.PairedCorrectedTTester;
import weka.experiment.ResultMatrix;
import weka.experiment.Tester;
import weka.gui.experiment.ExperimenterDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Generates evaluation output of an experiment that was run previously.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.experiment.Experiment<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaExperimentEvaluation
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-tester &lt;weka.experiment.Tester&gt; (property: tester)
 * &nbsp;&nbsp;&nbsp;The testing algorithm to use for performing the evaluations.
 * &nbsp;&nbsp;&nbsp;default: weka.experiment.PairedCorrectedTTester -R 0 -S 0.05
 * </pre>
 * 
 * <pre>-comparison &lt;ELAPSED_TIME_TRAINING|ELAPSED_TIME_TESTING|USERCPU_TIME_TRAINING|USERCPU_TIME_TESTING|SERIALIZED_MODEL_SIZE|SERIALIZED_TRAIN_SET_SIZE|SERIALIZED_TEST_SET_SIZE|NUMBER_OF_TRAINING_INSTANCES|NUMBER_OF_TESTING_INSTANCES|NUMBER_CORRECT|NUMBER_INCORRECT|NUMBER_UNCLASSIFIED|PERCENT_CORRECT|PERCENT_INCORRECT|PERCENT_UNCLASSIFIED|KAPPA_STATISTIC|MEAN_ABSOLUTE_ERROR|ROOT_MEAN_SQUARED_ERROR|RELATIVE_ABSOLUTE_ERROR|ROOT_RELATIVE_SQUARED_ERROR|CORRELATION_COEFFICIENT|SF_PRIOR_ENTROPY|SF_SCHEME_ENTROPY|SF_ENTROPY_GAIN|SF_MEAN_PRIOR_ENTROPY|SF_MEAN_SCHEME_ENTROPY|SF_MEAN_ENTROPY_GAIN|KB_INFORMATION|KB_MEAN_INFORMATION|KB_RELATIVE_INFORMATION|TRUE_POSITIVE_RATE|NUM_TRUE_POSITIVES|FALSE_POSITIVE_RATE|NUM_FALSE_POSITIVES|TRUE_NEGATIVE_RATE|NUM_TRUE_NEGATIVES|FALSE_NEGATIVE_RATE|NUM_FALSE_NEGATIVES|IR_PRECISION|IR_RECALL|F_MEASURE|MATTHEWS_CORRELATION_COEFFICIENT|AREA_UNDER_ROC|AREA_UNDER_PRC|WEIGHTED_TRUE_POSITIVE_RATE|WEIGHTED_FALSE_POSITIVE_RATE|WEIGHTED_TRUE_NEGATIVE_RATE|WEIGHTED_FALSE_NEGATIVE_RATE|WEIGHTED_IR_PRECISION|WEIGHTED_IR_RECALL|WEIGHTED_F_MEASURE|WEIGHTED_MATTHEWS_CORRELATION_COEFFICIENT|WEIGHTED_AREA_UNDER_ROC|WEIGHTED_AREA_UNDER_PRC&gt; (property: comparisonField)
 * &nbsp;&nbsp;&nbsp;The field to base the comparison of algorithms on.
 * &nbsp;&nbsp;&nbsp;default: PERCENT_CORRECT
 * </pre>
 * 
 * <pre>-significance &lt;double&gt; (property: significance)
 * &nbsp;&nbsp;&nbsp;The significance level (0-1).
 * &nbsp;&nbsp;&nbsp;default: 0.05
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-4
 * &nbsp;&nbsp;&nbsp;maximum: 0.9999
 * </pre>
 * 
 * <pre>-test &lt;int&gt; (property: testBase)
 * &nbsp;&nbsp;&nbsp;The index of the test base (normally the first classifier, ie '0').
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-row &lt;adams.core.base.BaseString&gt; [-row ...] (property: row)
 * &nbsp;&nbsp;&nbsp;The list of fields that define a row (normally the dataset).
 * &nbsp;&nbsp;&nbsp;default: Key_Dataset
 * </pre>
 * 
 * <pre>-col &lt;adams.core.base.BaseString&gt; [-col ...] (property: column)
 * &nbsp;&nbsp;&nbsp;The list of fields that define a column (normally the schemes).
 * &nbsp;&nbsp;&nbsp;default: Key_Scheme, Key_Scheme_options, Key_Scheme_version_ID
 * </pre>
 * 
 * <pre>-swap &lt;boolean&gt; (property: swapRowsAndColumns)
 * &nbsp;&nbsp;&nbsp;If set to true, rows and columns will be swapped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-format &lt;weka.experiment.ResultMatrix&gt; (property: outputFormat)
 * &nbsp;&nbsp;&nbsp;The output format for generating the output.
 * &nbsp;&nbsp;&nbsp;default: weka.experiment.ResultMatrixPlainText -mean-prec 2 -stddev-prec 2 -col-name-width 0 -row-name-width 25 -mean-width 0 -stddev-width 0 -sig-width 0 -count-width 5 -print-col-names -print-row-names -enum-col-names
 * </pre>
 * 
 * <pre>-header &lt;boolean&gt; (property: outputHeader)
 * &nbsp;&nbsp;&nbsp;If set to true, then a header describing the experiment evaluation will 
 * &nbsp;&nbsp;&nbsp;get output as well.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaExperimentEvaluation
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8186675646383734197L;

  /** the tester class to use. */
  protected Tester m_Tester;

  /** the comparison field. */
  protected ExperimentStatistic m_ComparisonField;

  /** the significance. */
  protected double m_Significance;

  /** the test base. */
  protected int m_TestBase;

  /** the row (= datasets). */
  protected BaseString[] m_Row;

  /** the column (= classifiers). */
  protected BaseString[] m_Column;

  /** whether to swap rows and columns. */
  protected boolean m_SwapRowsAndColumns;

  /** the output format. */
  protected ResultMatrix m_OutputFormat;

  /** whether to output the header. */
  protected boolean m_OutputHeader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates evaluation output of an experiment that was run previously.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    String[]		list;
    BaseString[]	str;
    int			i;

    super.defineOptions();

    m_OptionManager.add(
	    "tester", "tester",
	    new PairedCorrectedTTester());

    m_OptionManager.add(
	    "comparison", "comparisonField",
	    ExperimentStatistic.PERCENT_CORRECT);

    m_OptionManager.add(
	    "significance", "significance",
	    0.05, 0.0001, 0.9999);

    m_OptionManager.add(
	    "test", "testBase",
	    0, 0, null);

    list = ExperimenterDefaults.getRow().split(",");
    str  = new BaseString[list.length];
    for (i = 0; i < list.length; i++)
      str[i] = new BaseString(list[i]);
    m_OptionManager.add(
	    "row", "row",
	    str);

    list = ExperimenterDefaults.getColumn().split(",");
    str  = new BaseString[list.length];
    for (i = 0; i < list.length; i++)
      str[i] = new BaseString(list[i]);
    m_OptionManager.add(
	    "col", "column",
	    str);

    m_OptionManager.add(
	    "swap", "swapRowsAndColumns",
	    false);

    m_OptionManager.add(
	    "format", "outputFormat",
	    ExperimenterDefaults.getOutputFormat());

    m_OptionManager.add(
	    "header", "outputHeader",
	    true);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "comparisonField", m_ComparisonField);
    result += QuickInfoHelper.toString(this, "outputFormat", m_OutputFormat.getClass(), ", format: ");
    result += QuickInfoHelper.toString(this, "swapRowsAndColumns", m_SwapRowsAndColumns, "swapped", ", ");

    return result;
  }

  /**
   * Sets the Tester to use.
   *
   * @param value	the Tester
   */
  public void setTester(Tester value) {
    m_Tester = value;
    reset();
  }

  /**
   * Returns the Tester in use.
   *
   * @return		the Tester
   */
  public Tester getTester() {
    return m_Tester;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testerTipText() {
    return "The testing algorithm to use for performing the evaluations.";
  }

  /**
   * Sets the comparison field.
   *
   * @param value	the field
   */
  public void setComparisonField(ExperimentStatistic value) {
    m_ComparisonField = value;
    reset();
  }

  /**
   * Returns the comparison field.
   *
   * @return		the string
   */
  public ExperimentStatistic getComparisonField() {
    return m_ComparisonField;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String comparisonFieldTipText() {
    return "The field to base the comparison of algorithms on.";
  }

  /**
   * Sets the significance level (0-1).
   *
   * @param value	the significance
   */
  public void setSignificance(double value) {
    m_Significance = value;
    reset();
  }

  /**
   * Returns the current significance level (0-1).
   *
   * @return		the significance
   */
  public double getSignificance() {
    return m_Significance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String significanceTipText() {
    return "The significance level (0-1).";
  }

  /**
   * Sets the index of the test base.
   *
   * @param value	the index
   */
  public void setTestBase(int value) {
    m_TestBase = value;
    reset();
  }

  /**
   * Returns the index of the test base.
   *
   * @return		the index
   */
  public int getTestBase() {
    return m_TestBase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testBaseTipText() {
    return "The index of the test base (normally the first classifier, ie '0').";
  }

  /**
   * Sets the list of fields that identify a row.
   *
   * @param value	the list of fields
   */
  public void setRow(BaseString[] value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the list of fields that identify a row.
   *
   * @return		the list of fields
   */
  public BaseString[] getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The list of fields that define a row (normally the dataset).";
  }

  /**
   * Sets list of fields that identify a column.
   *
   * @param value	the list of fields
   */
  public void setColumn(BaseString[] value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the list of fields that identify a column.
   *
   * @return		the list of fields
   */
  public BaseString[] getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The list of fields that define a column (normally the schemes).";
  }

  /**
   * Sets whether to swap rows and columns.
   *
   * @param value 	true if to swap rows and columns
   */
  public void setSwapRowsAndColumns(boolean value) {
    m_SwapRowsAndColumns = value;
    reset();
  }

  /**
   * Returns whether to swap rows and columns.
   *
   * @return 		true if swapping rows and columns
   */
  public boolean getSwapRowsAndColumns() {
    return m_SwapRowsAndColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String swapRowsAndColumnsTipText() {
    return "If set to true, rows and columns will be swapped.";
  }

  /**
   * Sets the output format to use for generating the output.
   *
   * @param value	the format
   */
  public void setOutputFormat(ResultMatrix value) {
    m_OutputFormat = value;
    reset();
  }

  /**
   * Returns the output format in use for generating the output.
   *
   * @return		the format
   */
  public ResultMatrix getOutputFormat() {
    return m_OutputFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFormatTipText() {
    return "The output format for generating the output.";
  }

  /**
   * Sets whether to output the header of the result matrix as well.
   *
   * @param value 	true if to output the header as well
   */
  public void setOutputHeader(boolean value) {
    m_OutputHeader = value;
    reset();
  }

  /**
   * Returns whether to output the header of the result matrix as well.
   *
   * @return 		true if to output the header as well
   */
  public boolean getOutputHeader() {
    return m_OutputHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputHeaderTipText() {
    return "If set to true, then a header describing the experiment evaluation will get output as well.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.experiment.Experiment.class, weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Experiment.class, Instances.class};
  }

  /**
   * Returns a vector with column names of the dataset, listed in "list". If
   * a column cannot be found or the list is empty the ones from the default
   * list are returned.
   *
   * @param list           list of attribute names
   * @param defaultList    the default list of attribute names
   * @return               a vector containing attribute names
   */
  protected List<String> determineColumnNames(BaseString[] list, String defaultList) {
    List<String>	result;
    StringTokenizer	tok;
    int			i;

    // process list
    result = new ArrayList<String>();
    for (i = 0; i < list.length; i++)
      result.add(list[i].stringValue().toLowerCase());

    // do we have to return defaults?
    if (result.size() == 0) {
      tok = new StringTokenizer(defaultList, ",");
      while (tok.hasMoreTokens())
        result.add(tok.nextToken().toLowerCase());
    }

    return result;
  }

  /**
   * Loads the experimental results.
   *
   * @param exp		the experiment to evaluate
   * @return		the results
   * @throws Exception 	If reading fails.
   */
  protected Instances getData(weka.experiment.Experiment exp) throws Exception {
    Instances			result;
    String			tmpStr;
    DatabaseResultListener	dbListener;
    InstanceQuery		query;

    // load experiment data
    if (exp.getResultListener() instanceof CSVResultListener) {
      try {
	result = DataSource.read(
	    ((CSVResultListener) exp.getResultListener()).getOutputFile().getAbsolutePath());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to get instances from CSV:", e);
	throw new Exception("Error reading experiment from file:\n" + e);
      }
    }
    else if (exp.getResultListener() instanceof DatabaseResultListener) {
      dbListener = (DatabaseResultListener) exp.getResultListener();
      try {
	tmpStr = dbListener.getResultsTableName(exp.getResultProducer());
	query  = new InstanceQuery();
	query.setDatabaseURL(dbListener.getDatabaseURL());
	query.setUsername(dbListener.getUsername());
	query.setPassword(dbListener.getPassword());
	query.setDebug(dbListener.getDebug());
	query.connectToDatabase();
	result = query.retrieveInstances("SELECT * FROM " + tmpStr);
	query.disconnectFromDatabase();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to get instances from database:", e);
	throw new Exception("Error reading experiment from database:\n" + e);
      }
    }
    else {
      throw new Exception(
	  "Unsupported ResultListener '"
	  + exp.getResultListener().getClass().getName() + "'!");
    }

    return result;
  }

  /**
   * Sets up the testing algorithm and returns it.
   *
   * @param data	the experimental data
   * @return		the configured testing algorithm
   * @throws Exception 	If something goes wrong, like testing algorithm of
   * 			result matrix cannot be instantiated
   */
  protected Tester getTester(Instances data) throws Exception {
    Tester			ttester;
    ResultMatrix		matrix;
    String			tmpStr;
    weka.core.Attribute		att;
    List<String> 		rows;
    List<String> 		cols;
    String 			selectedList;
    String 			selectedListDataset;
    boolean 			comparisonFieldSet;
    int 			i;
    String 			name;
    Range 			generatorRange;

    ttester = (Tester) ClassManager.getSingleton().deepCopy(m_Tester);
    matrix  = (ResultMatrix) ClassManager.getSingleton().deepCopy(m_OutputFormat);
    ttester.setInstances(data);
    ttester.setSignificanceLevel(m_Significance);
    ttester.setShowStdDevs(matrix.getShowStdDev());
    ttester.setSortColumn(-1);

    if (!m_SwapRowsAndColumns) {
      rows = determineColumnNames(m_Row, ExperimenterDefaults.getRow());
      cols = determineColumnNames(m_Column, ExperimenterDefaults.getColumn());
    }
    else {
      cols = determineColumnNames(m_Row, ExperimenterDefaults.getRow());
      rows = determineColumnNames(m_Column, ExperimenterDefaults.getColumn());
    }
    selectedList = "";
    selectedListDataset = "";
    comparisonFieldSet = false;
    for (i = 0; i < data.numAttributes(); i++) {
      name = data.attribute(i).name();

      if (rows.contains(name.toLowerCase())) {
	selectedListDataset += "," + (i + 1);
      }
      else if (name.toLowerCase().equals("key_run")) {
	ttester.setRunColumn(i);
      }
      else if (name.toLowerCase().equals("key_fold")) {
	ttester.setFoldColumn(i);
      }
      else if (cols.contains(name.toLowerCase())) {
	selectedList += "," + (i + 1);
      }
      else if (name.toLowerCase().contains(ExperimenterDefaults.getComparisonField())) {
	comparisonFieldSet = true;
      }
      else if ((name.toLowerCase().contains("root_relative_squared_error")) && (!comparisonFieldSet)) {
	comparisonFieldSet = true;
      }
    }
    generatorRange = new Range();
    if (selectedList.length() != 0) {
      try {
	generatorRange.setRanges(selectedList);
      }
      catch (Exception ex) {
	handleException("Failed to set ranges: " + selectedList, ex);
      }
    }
    ttester.setResultsetKeyColumns(generatorRange);

    generatorRange = new Range();
    if (selectedListDataset.length() != 0) {
      try {
	generatorRange.setRanges(selectedListDataset);
      }
      catch (Exception ex) {
	handleException("Failed to set dataset ranges: " + selectedListDataset, ex);
      }
    }
    ttester.setDatasetKeyColumns(generatorRange);

    tmpStr = m_ComparisonField.getField();
    att    = data.attribute(tmpStr);
    if (att == null)
      throw new Exception("Cannot find comparison field '" + tmpStr + "' in data!");
    ttester.setDisplayedResultsets(null);  // all
    ttester.setResultMatrix(matrix);

    return ttester;
  }

  /**
   * Evaluates the experiment data.
   *
   * @param data	the data to evaluate
   * @throws Exception 	If something goes wrong, like loading
   * 			data fails or comparison field invalid
   */
  protected void evaluateExperiment(Instances data) throws Exception {
    Tester			ttester;
    StringBuilder 		outBuff;
    int 			compareCol;
    int 			tType;
    String			tmpStr;
    weka.core.Attribute		att;

    // setup testing algorithm
    ttester = getTester(data);

    // evaluate experiment
    tmpStr = m_ComparisonField.getField();
    att    = data.attribute(tmpStr);
    if (att == null)
      throw new Exception("Cannot find comparison field '" + tmpStr + "' in data!");
    compareCol = att.index();
    tType      = m_TestBase;
    outBuff    = new StringBuilder();
    if (m_OutputHeader) {
      outBuff.append(ttester.header(compareCol));
      outBuff.append("\n");
    }
    try {
      if (tType < ttester.getNumResultsets())
	outBuff.append(ttester.multiResultsetFull(tType, compareCol));
      else if (tType == ttester.getNumResultsets())
	outBuff.append(ttester.multiResultsetSummary(compareCol));
      else
	outBuff.append(ttester.multiResultsetRanking(compareCol));
      outBuff.append("\n");
    }
    catch (Exception ex) {
      outBuff.append(ex.getMessage() + "\n");
    }

    // broadcast evaluation
    m_OutputToken = new Token(outBuff.toString());
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      if (m_InputToken.getPayload() instanceof weka.experiment.Experiment)
        evaluateExperiment(getData((weka.experiment.Experiment) m_InputToken.getPayload()));
      else
        evaluateExperiment((Instances) m_InputToken.getPayload());
    }
    catch (Exception e) {
      result = handleException("Error evaluating the experiment: ", e);
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }
}
