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

/**
 * DefaultAnalysisPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.analysis;

import adams.core.ClassLister;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Properties;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.experiment.PairedCorrectedTTester;
import weka.experiment.ResultMatrix;
import weka.experiment.ResultMatrixPlainText;
import weka.experiment.Tester;

import javax.swing.DefaultComboBoxModel;
import adams.gui.core.BaseComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Default panel for analyzing results from experiments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultAnalysisPanel
  extends AbstractAnalysisPanel {

  /** for serialization. */
  private static final long serialVersionUID = 7850777725811230009L;

  public static final String PERCENT_CORRECT = "Percent_correct";

  public static final String CORRELATION_COEFFICIENT = "Correlation_coefficient";

  public static final String KEY_RUN = "Key_Run";

  public static final String KEY_FOLD = "Key_Fold";

  public static final String KEY_DATASET = "Key_Dataset";

  public static final String KEY_SCHEME = "Key_Scheme";

  public static final String KEY_SCHEME_OPTIONS = "Key_Scheme_options";

  public static final String KEY_SCHEME_VERSION_ID = "Key_Scheme_version_ID";

  /**
   * Customized history panel.
   */
  public static class HistoryPanel
    extends AbstractNamedHistoryPanel<AbstractResultsPanel> {

    private static final long serialVersionUID = 8740813441072965573L;

    /** the owner. */
    protected DefaultAnalysisPanel m_Owner;

    /**
     * Initializes the history.
     *
     * @param owner the owning panel
     */
    public HistoryPanel(DefaultAnalysisPanel owner) {
      super();
      m_Owner = owner;
      setAllowRemove(true);
      setAllowRename(false);
    }

    /**
     * Removes all entries and payloads.
     */
    public void clear() {
      for (AbstractResultsPanel panel : m_Entries.values())
	panel.cleanUp();
      super.clear();
    }

    /**
     * Removes the specified entry.
     *
     * @param name the name of the entry
     * @return the entry that was stored under this name or null if
     * no entry was stored with this name
     */
    public AbstractResultsPanel removeEntry(String name) {
      AbstractResultsPanel result;

      result = super.removeEntry(name);
      if (result != null)
	result.cleanUp();

      return result;
    }

    /**
     * Displays the specified entry.
     *
     * @param name the name of the entry, can be null to empty display
     */
    @Override
    protected void updateEntry(String name) {
      m_Owner.getPanelRight().removeAll();
      if (name != null) {
	if (hasEntry(name))
	  m_Owner.getPanelRight().add(getEntry(name));
      }
      m_Owner.getPanelRight().invalidate();
      m_Owner.getPanelRight().revalidate();
      m_Owner.getPanelRight().doLayout();
      m_Owner.getPanelRight().repaint();
    }
  }

  /** the GOE for the tester. */
  protected GenericObjectEditorPanel m_PanelTester;

  /** the GOE for the result matrix. */
  protected GenericObjectEditorPanel m_PanelMatrix;

  /** the significance. */
  protected NumberTextField m_TextSignificance;

  /** the resultset keys. */
  protected SelectOptionPanel m_SelectRows;

  /** the dataset keys. */
  protected SelectOptionPanel m_SelectColumns;

  /** for swapping rows/columns. */
  protected BaseCheckBox m_CheckBoxSwapRowsColumns;

  /** the comparison base. */
  protected SelectOptionPanel m_SelectComparisonBase;

  /** the combobox with the metric to evaluate. */
  protected BaseComboBox<String> m_ComboBoxMetric;

  /** the model for the metric. */
  protected DefaultComboBoxModel<String> m_ModelMetric;

  /** the combobox with the results panels. */
  protected BaseComboBox<AbstractResultsPanel> m_ComboBoxResults;

  /** the results panels. */
  protected List<AbstractResultsPanel> m_PanelsResults;

  /** the analyze button. */
  protected BaseButton m_ButtonAnalyze;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the panel on the left. */
  protected JPanel m_PanelLeft;

  /** the panel on the right. */
  protected JPanel m_PanelRight;

  /** the history. */
  protected HistoryPanel m_History;

  /** the formatter for the history entries. */
  protected DateFormat m_Formatter;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    AbstractResultsPanel panel;

    super.initialize();

    m_ModelMetric = new DefaultComboBoxModel<>();
    m_PanelsResults = new ArrayList<>();
    for (Class cls : ClassLister.getSingleton().getClasses(AbstractResultsPanel.class)) {
      try {
	panel = (AbstractResultsPanel) cls.newInstance();
	m_PanelsResults.add(panel);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate results panel: " + cls.getName(), e);
      }
    }

    m_Formatter = DateUtils.getTimeFormatter();
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel panelTop;
    ParameterPanel panelParams;
    JPanel panelButtons;
    Properties props;
    Tester tester;
    ResultMatrix matrix;
    double siglevel;

    super.initGUI();

    props = ExperimenterPanel.getProperties();

    setLayout(new BorderLayout());

    panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panelTop, BorderLayout.NORTH);

    // parameters
    panelParams = new ParameterPanel();
    panelTop.add(panelParams, BorderLayout.CENTER);

    // tester
    try {
      tester = (Tester) Class.forName(props.getProperty("Results.Tester", PairedCorrectedTTester.class.getName())).newInstance();
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate tester: " + props.getProperty("Results.Tester"), e);
      tester = new PairedCorrectedTTester();
    }
    m_PanelTester = new GenericObjectEditorPanel(Tester.class, tester, true);
    panelParams.addParameter("Tester", m_PanelTester);

    // matrix
    try {
      matrix = (ResultMatrix) Class.forName(props.getProperty("Results.ResultMatrix", ResultMatrixPlainText.class.getName())).newInstance();
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate matrix: " + props.getProperty("Results.ResultMatrix"), e);
      matrix = new ResultMatrixPlainText();
    }
    m_PanelMatrix = new GenericObjectEditorPanel(ResultMatrix.class, matrix, true);
    panelParams.addParameter("Output", m_PanelMatrix);

    // significance
    siglevel = props.getDouble("Results.SignificanceLevel", 0.05);
    m_TextSignificance = new NumberTextField(Type.DOUBLE);
    m_TextSignificance.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 0.0, 1.0, siglevel));
    m_TextSignificance.setValue(siglevel);
    panelParams.addParameter("Significance level", m_TextSignificance);

    // rows
    m_SelectRows = new SelectOptionPanel();
    m_SelectRows.setCurrent(new String[]{KEY_DATASET});
    m_SelectRows.setMultiSelect(true);
    m_SelectRows.setLenient(true);
    m_SelectRows.setDialogTitle("Select row identifiers");
    m_SelectRows.addChangeListener((ChangeEvent e) -> updateComparisonBase());
    panelParams.addParameter("Rows", m_SelectRows);

    // columns
    m_SelectColumns = new SelectOptionPanel();
    m_SelectColumns.setCurrent(new String[]{KEY_SCHEME, KEY_SCHEME_OPTIONS, KEY_SCHEME_VERSION_ID});
    m_SelectColumns.setMultiSelect(true);
    m_SelectColumns.setLenient(true);
    m_SelectColumns.setDialogTitle("Select column identifiers");
    m_SelectColumns.addChangeListener((ChangeEvent e) -> updateComparisonBase());
    panelParams.addParameter("Columns", m_SelectColumns);

    // swap
    m_CheckBoxSwapRowsColumns = new BaseCheckBox();
    m_CheckBoxSwapRowsColumns.addActionListener((ActionEvent e) -> updateComparisonBase());
    panelParams.addParameter("Swap rows/columns", m_CheckBoxSwapRowsColumns);

    // comparison base
    m_SelectComparisonBase = new SelectOptionPanel();
    m_SelectComparisonBase.setCurrent(new String[0]);
    m_SelectComparisonBase.setMultiSelect(false);
    m_SelectComparisonBase.setLenient(true);
    m_SelectComparisonBase.setDialogTitle("Select the base to compare against");
    m_SelectComparisonBase.addChangeListener((ChangeEvent e) -> updateButtons());
    panelParams.addParameter("Comparison base", m_SelectComparisonBase);

    // metric
    m_ComboBoxMetric = new BaseComboBox<>(m_ModelMetric);
    panelParams.addParameter("Metric", m_ComboBoxMetric);

    // results
    m_ComboBoxResults = new BaseComboBox<>(
      new DefaultComboBoxModel<>(
	m_PanelsResults.toArray(new AbstractResultsPanel[m_PanelsResults.size()])));
    panelParams.addParameter("Results", m_ComboBoxResults);

    // split pane
    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setDividerLocation(200);  // TODO preferences
    add(m_SplitPane, BorderLayout.CENTER);

    m_PanelLeft = new JPanel(new BorderLayout());
    m_SplitPane.setLeftComponent(m_PanelLeft);

    m_PanelRight = new JPanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PanelRight);

    // buttons
    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelLeft.add(panelButtons, BorderLayout.NORTH);

    m_ButtonAnalyze = new BaseButton("Analyze");
    m_ButtonAnalyze.addActionListener((ActionEvent e) -> analyze());
    panelButtons.add(m_ButtonAnalyze);

    // history
    m_History = new HistoryPanel(this);
    m_PanelLeft.add(m_History);
  }

  /**
   * Returns the name to display in the GUI.
   *
   * @return the name
   */
  @Override
  public String getAnalysisName() {
    return "Default";
  }

  /**
   * Returns the panel on the right (for the analysis display).
   *
   * @return the panel
   */
  public JPanel getPanelRight() {
    return m_PanelRight;
  }

  /**
   * Sets the matrix to use.
   *
   * @param value the matrix
   */
  public void setResultMatrix(ResultMatrix value) {
    m_PanelMatrix.setCurrent(value);
  }

  /**
   * Returns the result matrix.
   *
   * @return the matrix
   */
  public ResultMatrix getResultMatrix() {
    return (ResultMatrix) m_PanelMatrix.getCurrent();
  }

  /**
   * Sets the tester to use.
   *
   * @param value the tester
   */
  public void setTester(Tester value) {
    m_PanelTester.setCurrent(value);
  }

  /**
   * Returns the tester.
   *
   * @return the tester
   */
  public Tester getTester() {
    return (Tester) m_PanelTester.getCurrent();
  }

  /**
   * Checks whether the results can be handled at all.
   *
   * @param results the results to check
   * @return null if can handle, otherwise error message
   */
  @Override
  public String handlesResults(Instances results) {
    return null;
  }

  /**
   * Returns a clone of the object.
   *
   * @return the clone
   */
  public DefaultAnalysisPanel getClone() {
    DefaultAnalysisPanel result;

    result = new DefaultAnalysisPanel();
    result.setResultMatrix(getResultMatrix());
    result.setTester(getTester());

    return result;
  }

  /**
   * Updates the GUI.
   */
  protected void update() {
    DefaultComboBoxModel<String> metric;
    String oldValue;
    int index;
    List<String> names;

    // update metric
    oldValue = (String) m_ComboBoxMetric.getSelectedItem();
    metric = new DefaultComboBoxModel<>();
    if (m_Results != null) {
      for (index = 0; index < m_Results.numAttributes(); index++)
	metric.addElement(m_Results.attribute(index).name());
    }
    m_ModelMetric = metric;
    m_ComboBoxMetric.setModel(m_ModelMetric);
    index = -1;
    if (oldValue != null)
      index = m_ModelMetric.getIndexOf(oldValue);
    if (index == -1)
      index = m_ModelMetric.getIndexOf(PERCENT_CORRECT);
    if (index == -1)
      index = m_ModelMetric.getIndexOf(CORRELATION_COEFFICIENT);
    if (index > -1)
      m_ComboBoxMetric.setSelectedIndex(index);

    names = new ArrayList<>();
    if (m_Results != null) {
      for (index = 0; index < m_Results.numAttributes(); index++)
	names.add(m_Results.attribute(index).name());
    }
    m_SelectRows.setOptions(names.toArray(new String[names.size()]));
    m_SelectColumns.setOptions(names.toArray(new String[names.size()]));
    updateComparisonBase();
    updateButtons();
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    m_ButtonAnalyze.setEnabled((m_Results != null) && (m_SelectComparisonBase.getCurrentIndex() > -1));
  }

  /**
   * Returns the attribute index for the specified attribute name.
   *
   * @param attName 	the attribute name to look up
   * @return		the index, -1 if not found
   */
  protected int getAttributeIndex(String attName) {
    Attribute	att;

    if (m_Results == null)
      return -1;
    att = m_Results.attribute(attName);
    if (att == null)
      return -1;
    else
      return att.index();
  }

  /**
   * Turns the selected attributes into a range string.
   *
   * @param select	the panel with the selection
   * @return		the range
   */
  protected String getRange(SelectOptionPanel select) {
    StringBuilder	result;
    int[]		options;

    result  = new StringBuilder();
    options = select.getCurrentIndices();
    for (int option: options) {
      if (result.length() > 0)
	result.append(",");
      result.append("" + (option+1));
    }

    return result.toString();
  }

  /**
   * Updates the base of comparison.
   */
  protected void updateComparisonBase() {
    boolean 		swapped;
    List<String> 	options;
    int[] 		atts;
    Set<String> 	unique;
    StringBuilder 	combined;
    String		key;
    int			i;
    int			n;
    Instance		inst;

    if (m_Results == null) {
      m_SelectComparisonBase.setOptions(new String[0]);
      return;
    }

    swapped = m_CheckBoxSwapRowsColumns.isSelected();
    if (swapped)
      atts = m_SelectRows.getCurrentIndices();
    else
      atts = m_SelectColumns.getCurrentIndices();

    unique  = new HashSet<>();
    options = new ArrayList<>();
    for (i = 0; i < m_Results.numInstances(); i++) {
      inst     = m_Results.instance(i);
      combined = new StringBuilder();
      for (n = 0; n < atts.length; n++) {
	if (n > 0)
	  combined.append(" ");
	if (inst.isMissing(atts[n])) {
	  combined.append("?");
	}
	else {
	  switch (m_Results.attribute(atts[n]).type()) {
	    case Attribute.NUMERIC:
	      combined.append("" + inst.value(atts[n]));
	      break;
	    default:
	      combined.append(inst.stringValue(atts[n]));
	      break;
	  }
	}
      }
      key = combined.toString();
      if (unique.contains(key))
	continue;
      options.add(key);
      unique.add(key);
    }

    m_SelectComparisonBase.setOptions(options.toArray(new String[options.size()]));
    if (m_SelectComparisonBase.getCurrentIndex() == -1)
      m_SelectComparisonBase.setCurrentIndex(0);
  }

  /**
   * Performs the analysis.
   */
  protected void analyze() {
    String			metric;
    int				index;
    Tester			tester;
    ResultMatrix		matrix;
    AbstractResultsPanel	panel;

    if (m_Results == null)
      return;

    matrix = getResultMatrix();
    tester = getTester();
    tester.setInstances(m_Results);
    tester.setSortColumn(-1);
    tester.setRunColumn(getAttributeIndex(KEY_RUN));
    tester.setFoldColumn(getAttributeIndex(KEY_FOLD));
    if (m_CheckBoxSwapRowsColumns.isSelected()) {
      tester.setDatasetKeyColumns(new Range(getRange(m_SelectColumns)));
      tester.setResultsetKeyColumns(new Range(getRange(m_SelectRows)));
    }
    else {
      tester.setDatasetKeyColumns(new Range(getRange(m_SelectRows)));
      tester.setResultsetKeyColumns(new Range(getRange(m_SelectColumns)));
    }
    tester.setResultMatrix(matrix);
    tester.setDisplayedResultsets(null);
    tester.setSignificanceLevel(m_TextSignificance.getValue(0.05).doubleValue());
    tester.setShowStdDevs(matrix.getShowStdDev());
    metric = "" + m_ComboBoxMetric.getSelectedItem();
    index = getAttributeIndex(metric);
    if (index == -1)
      index = getAttributeIndex(PERCENT_CORRECT);
    if (index == -1)
      index = getAttributeIndex(CORRELATION_COEFFICIENT);
    try {
      tester.multiResultsetFull(m_SelectComparisonBase.getCurrentIndex(), index);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getOwner(), "Failed to analyze experiment!", e);
      return;
    }

    panel = ((AbstractResultsPanel) m_ComboBoxResults.getSelectedItem()).getClone();
    panel.display(tester.getResultMatrix());
    m_History.addEntry(m_Formatter.format(new Date()) + " - " + metric.replace("_", " "), panel);
    m_History.setSelectedIndex(m_History.count() - 1);
  }
}
