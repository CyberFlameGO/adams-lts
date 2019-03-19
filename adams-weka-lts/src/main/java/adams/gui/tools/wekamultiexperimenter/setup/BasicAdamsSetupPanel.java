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
 * BasicAdamsSetupPanel.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup;

import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.data.weka.classattribute.AbstractClassAttributeHeuristic;
import adams.data.weka.classattribute.LastAttribute;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractResultsHandler;
import adams.gui.tools.wekamultiexperimenter.experiment.CrossValidationExperiment;
import adams.gui.tools.wekamultiexperimenter.experiment.FileResultsHandler;
import adams.gui.tools.wekamultiexperimenter.experiment.TrainTestSplitExperiment;
import adams.gui.tools.wekamultiexperimenter.io.AbstractExperimentIO;
import adams.gui.tools.wekamultiexperimenter.io.DefaultAdamsExperimentIO;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.DefaultCrossValidationFoldGenerator;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.RandomSplitGenerator;
import weka.classifiers.SplitGenerator;
import weka.gui.experiment.ExperimenterDefaults;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Basic interface for setting up an ADAMS experiment.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BasicAdamsSetupPanel
  extends AbstractAdamsSetupPanel {
  
  /** for serialization. */
  private static final long serialVersionUID = -5412911620981798767L;

  /** for listing all the options. */
  protected ParameterPanel m_PanelParameters;
  
  /** the panel for the results handler. */
  protected GenericObjectEditorPanel m_PanelResultsHandler;

  /** the panel for the class attribute heuristic. */
  protected GenericObjectEditorPanel m_PanelClassAttribute;

  /** the number of repetitions. */
  protected NumberTextField m_TextRepetitions;

  /** the type of evaluation. */
  protected BaseComboBox<String> m_ComboBoxEvaluation;
  
  /** the evaluation parameter. */
  protected NumberTextField m_TextEvaluation;

  /** whether to use a custom split generator. */
  protected BaseCheckBox m_CheckBoxCustomSplitGenerator;

  /** the fold generator. */
  protected GenericObjectEditorPanel m_PanelGenerator;

  /** the JobRunner setup. */
  protected GenericObjectEditorPanel m_PanelJobRunner;

  /** the notes. */
  protected BaseTextArea m_TextNotes;

  /** the tabbed pane for datasets and classifiers. */
  protected BaseTabbedPane m_TabbedPane;
  
  /** for specifying the datasets. */
  protected DatasetPanel m_PanelDatasets;
  
  /** for specifying the classifiers. */
  protected ClassifierPanel m_PanelClassifiers;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    final int		evalIndex;
    JPanel		panel;

    super.initGUI();

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.NORTH);

    m_PanelParameters = new ParameterPanel();
    panel.add(m_PanelParameters);

    m_PanelResultsHandler = new GenericObjectEditorPanel(AbstractResultsHandler.class, new FileResultsHandler(), true);
    m_PanelResultsHandler.addChangeListener(new ModificationChangeListener());
    m_PanelParameters.addParameter("Results", m_PanelResultsHandler);

    m_PanelClassAttribute = new GenericObjectEditorPanel(AbstractClassAttributeHeuristic.class, new LastAttribute(), true);
    m_PanelClassAttribute.addChangeListener(new ModificationChangeListener());
    m_PanelParameters.addParameter("Class attribute", m_PanelClassAttribute);

    m_TextRepetitions = new NumberTextField(Type.INTEGER);
    m_TextRepetitions.setValue(ExperimenterDefaults.getRepetitions());
    m_TextRepetitions.getDocument().addDocumentListener(new ModificationDocumentListener());
    m_PanelParameters.addParameter("Repetitions", m_TextRepetitions);
    
    m_ComboBoxEvaluation = new BaseComboBox<>(new String[]{
	"Cross-validation",
	"Train/test split (randomized)",
	"Train/test split (order preserved)",
    });
    m_ComboBoxEvaluation.setSelectedIndex(0);
    evalIndex = m_PanelParameters.addParameter("Evaluation", m_ComboBoxEvaluation);
    m_ComboBoxEvaluation.addActionListener((ActionEvent e) -> {
      RandomSplitGenerator gen;
      setModified(true);
      switch (m_ComboBoxEvaluation.getSelectedIndex()) {
        case -1:
        case 0:
          m_PanelParameters.getLabel(evalIndex+1).setText("Number of folds");
	  m_PanelParameters.setToolTipText(evalIndex+1, "Use <2 for LOO-CV", true, true);
	  m_PanelGenerator.setClassType(CrossValidationFoldGenerator.class);
	  m_PanelGenerator.setCurrent(new DefaultCrossValidationFoldGenerator());
          break;
        case 1:
          m_PanelParameters.getLabel(evalIndex+1).setText("Split percentage");
	  m_PanelParameters.setToolTipText(evalIndex+1, "A percentage between 0 and 100", true, true);
	  m_PanelGenerator.setClassType(RandomSplitGenerator.class);
	  gen = new DefaultRandomSplitGenerator();
	  gen.setPreserveOrder(false);
	  m_PanelGenerator.setCurrent(gen);
          break;
        case 2:
          m_PanelParameters.getLabel(evalIndex+1).setText("Split percentage");
	  m_PanelParameters.setToolTipText(evalIndex+1, "A percentage between 0 and 100", true, true);
	  m_PanelGenerator.setClassType(RandomSplitGenerator.class);
	  gen = new DefaultRandomSplitGenerator();
	  gen.setPreserveOrder(true);
	  m_PanelGenerator.setCurrent(gen);
          break;
        default:
          throw new IllegalStateException("Unhandled evaluation type: " + m_ComboBoxEvaluation.getSelectedItem());
      }
    });
    
    m_TextEvaluation = new NumberTextField(Type.DOUBLE);
    m_TextEvaluation.getDocument().addDocumentListener(new ModificationDocumentListener());
    m_PanelParameters.addParameter("", m_TextEvaluation);

    m_CheckBoxCustomSplitGenerator = new BaseCheckBox();
    m_CheckBoxCustomSplitGenerator.addActionListener(new ModificationActionListener());
    m_PanelParameters.addParameter("Use custom split generator", m_CheckBoxCustomSplitGenerator);

    m_PanelGenerator = new GenericObjectEditorPanel(CrossValidationFoldGenerator.class, new DefaultCrossValidationFoldGenerator(), true);
    m_PanelGenerator.addChangeListener(new ModificationChangeListener());
    m_PanelParameters.addParameter("Split generator", m_PanelGenerator);

    m_PanelJobRunner = new GenericObjectEditorPanel(JobRunner.class, new LocalJobRunner(), true);
    m_PanelJobRunner.addChangeListener(new ModificationChangeListener());
    m_PanelParameters.addParameter("Job runner", m_PanelJobRunner);

    m_PanelDatasets = new DatasetPanel();
    m_PanelDatasets.setOwner(this);

    m_PanelClassifiers = new ClassifierPanel();
    m_PanelClassifiers.setOwner(this);

    m_TextNotes = new BaseTextArea();
    m_TextNotes.setTextFont(Fonts.getMonospacedFont());
    m_TextNotes.setLineWrap(true);
    m_TextNotes.setWrapStyleWord(true);

    m_TabbedPane       = new BaseTabbedPane();
    m_TabbedPane.addTab("Datasets", m_PanelDatasets);
    m_TabbedPane.addTab("Classifiers", m_PanelClassifiers);
    m_TabbedPane.addTab("Notes", new BaseScrollPane(m_TextNotes));
    add(m_TabbedPane, BorderLayout.CENTER);
  }
  
  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    m_TextRepetitions.setValue(10);
    m_ComboBoxEvaluation.setSelectedIndex(0);
    m_TextEvaluation.setValue(10);

    setModified(false);
  }
  
  /**
   * Returns the name for this setup panel.
   * 
   * @return		the name
   */
  @Override
  public String getSetupName() {
    return "Basic (Adams)";
  }

  /**
   * Creates the handler for the IO, i.e., loading/saving of experiments.
   * 
   * @return		the handler
   */
  @Override
  protected AbstractExperimentIO<AbstractExperiment> createExperimentIO() {
    return new DefaultAdamsExperimentIO();
  }

  /**
   * Returns the current experiment.
   * 
   * @return		the experiment
   */
  @Override
  public AbstractExperiment getExperiment() {
    AbstractExperiment		result;

    result = getExperimentIO().create();

    switch (m_ComboBoxEvaluation.getSelectedIndex()) {
      case 0:
	result = new CrossValidationExperiment();
	((CrossValidationExperiment) result).setFolds(m_TextEvaluation.getValue(10).intValue());
	if (m_CheckBoxCustomSplitGenerator.isSelected() && (m_PanelGenerator.getCurrent() instanceof CrossValidationFoldGenerator))
	  ((CrossValidationExperiment) result).setGenerator((CrossValidationFoldGenerator) m_PanelGenerator.getCurrent());
	break;
      case 1:
	result = new TrainTestSplitExperiment();
	((TrainTestSplitExperiment) result).setPercentage(m_TextEvaluation.getValue(66.0).doubleValue());
	((TrainTestSplitExperiment) result).setPreserveOrder(false);
	if (m_CheckBoxCustomSplitGenerator.isSelected() && (m_PanelGenerator.getCurrent() instanceof RandomSplitGenerator))
	  ((TrainTestSplitExperiment) result).setGenerator((RandomSplitGenerator) m_PanelGenerator.getCurrent());
	break;
      case 2:
	result = new TrainTestSplitExperiment();
	((TrainTestSplitExperiment) result).setPercentage(m_TextEvaluation.getValue(66.0).doubleValue());
	((TrainTestSplitExperiment) result).setPreserveOrder(true);
	if (m_CheckBoxCustomSplitGenerator.isSelected() && (m_PanelGenerator.getCurrent() instanceof RandomSplitGenerator))
	  ((TrainTestSplitExperiment) result).setGenerator((RandomSplitGenerator) m_PanelGenerator.getCurrent());
	break;
      default:
	logMessage("Unhandled evaluation type: " + m_ComboBoxEvaluation.getSelectedItem());
    }

    result.setResultsHandler((AbstractResultsHandler) m_PanelResultsHandler.getCurrent());
    result.setClassAttribute((AbstractClassAttributeHeuristic) m_PanelClassAttribute.getCurrent());
    result.setRuns(m_TextRepetitions.getValue().intValue());
    result.setJobRunner((JobRunner) m_PanelJobRunner.getCurrent());
    result.setClassifiers(m_PanelClassifiers.getClassifiers());
    result.setNotes(new BaseText(m_TextNotes.getText()));

    for (File file: m_PanelDatasets.getFiles())
      result.addDataset(new PlaceholderFile(file));

    return result;
  }

  /**
   * Sets the experiment to use.
   * 
   * @param value	the experiment
   */
  @Override
  public void setExperiment(AbstractExperiment value) {
    SplitGenerator	generator;

    if (handlesExperiment(value) == null) {
      if (value instanceof CrossValidationExperiment) {
	m_ComboBoxEvaluation.setSelectedIndex(0);
	m_TextEvaluation.setValue(((CrossValidationExperiment) value).getFolds());
	generator = ((CrossValidationExperiment) value).getGenerator();
	if (!generator.toCommandLine().equals(new DefaultCrossValidationFoldGenerator().toCommandLine())) {
	  m_CheckBoxCustomSplitGenerator.setSelected(true);
	  m_PanelGenerator.setCurrent(generator);
	}
	else {
	  m_CheckBoxCustomSplitGenerator.setSelected(false);
	  m_PanelGenerator.setCurrent(new DefaultCrossValidationFoldGenerator());
	}
      }
      else if (value instanceof TrainTestSplitExperiment) {
	if (((TrainTestSplitExperiment) value).getPreserveOrder())
	  m_ComboBoxEvaluation.setSelectedIndex(2);
	else
	  m_ComboBoxEvaluation.setSelectedIndex(1);
	m_TextEvaluation.setValue(((TrainTestSplitExperiment) value).getPercentage());
	generator = ((TrainTestSplitExperiment) value).getGenerator();
	if (!generator.toCommandLine().equals(new DefaultRandomSplitGenerator().toCommandLine())) {
	  m_CheckBoxCustomSplitGenerator.setSelected(true);
	  m_PanelGenerator.setCurrent(generator);
	}
	else {
	  m_CheckBoxCustomSplitGenerator.setSelected(false);
	  m_PanelGenerator.setCurrent(new DefaultRandomSplitGenerator());
	}
      }
      else {
	logMessage("Unhandled experiment type: " + value.getClass().getName());
      }
      m_PanelResultsHandler.setCurrent(value.getResultsHandler());
      m_PanelClassAttribute.setCurrent(value.getClassAttribute());
      m_TextRepetitions.setValue(value.getRuns());
      m_PanelJobRunner.setCurrent(value.getJobRunner());
      m_PanelDatasets.setFiles(value.getDatasets());
      m_PanelClassifiers.setClassifiers(value.getClassifiers());
      m_TextNotes.setText(value.getNotes().getValue());
    }
    else {
      throw new IllegalArgumentException("Cannot handle experiment: " + value.getClass().getName());
    }
  }

  /**
   * Checks whether the experiment can be handled.
   * 
   * @param exp		the experiment to check
   * @return		null if can handle, otherwise error message
   */
  @Override
  public String handlesExperiment(AbstractExperiment exp) {
    if (exp instanceof CrossValidationExperiment)
      return null;
    if (exp instanceof TrainTestSplitExperiment)
      return null;
    return "Unsupported experiment type: " + exp.getClass().getName();
  }
}
