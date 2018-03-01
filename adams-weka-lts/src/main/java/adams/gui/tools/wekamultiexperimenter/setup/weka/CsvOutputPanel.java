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
 * CsvOutputPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup.weka;

import adams.core.io.PlaceholderFile;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.ParameterPanel;
import weka.experiment.CSVResultListener;
import weka.experiment.ResultListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Stores the results in a CSV file.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CsvOutputPanel
  extends AbstractOutputPanel {

  /** for serialization. */
  private static final long serialVersionUID = -221969681088761768L;
  
  /** the file chooser panel. */
  protected FileChooserPanel m_PanelFile;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    ParameterPanel	panel;
    
    super.initGUI();
    
    panel = new ParameterPanel();
    m_PanelFile = new FileChooserPanel(new PlaceholderFile("${TMP}"));
    m_PanelFile.addChoosableFileFilter(new ExtensionFileFilter("CSV file", "csv"));
    m_PanelFile.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	m_Owner.setModified(true);
      }
    });
    panel.addParameter("File", m_PanelFile);
    add(panel, BorderLayout.CENTER);
  }

  /**
   * Returns the name to display in the GUI.
   * 
   * @return		the name
   */
  @Override
  public String getOutputName() {
    return "CSV";
  }
  
  /**
   * Returns whether this panel handles the specified {@link ResultListener}.
   * 
   * @param listener	the listener to check
   * @return		true if the panel handles this listener
   */
  @Override
  public boolean handlesResultListener(ResultListener listener) {
    return (listener.getClass() == CSVResultListener.class);
  }

  /**
   * Sets the {@link ResultListener}.
   * 
   * @param value	the listener
   */
  @Override
  public void setResultListener(ResultListener value) {
    m_PanelFile.setCurrent(((CSVResultListener) value).getOutputFile());
  }

  /**
   * Returns the configured {@link ResultListener}.
   * 
   * @return		the listener, null if failed to set up
   */
  @Override
  public ResultListener getResultListener() {
    CSVResultListener	result;
    File		file;
    
    result = new CSVResultListener();
    file   = m_PanelFile.getCurrent();
    if (!file.isDirectory())
      result.setOutputFile(file.getAbsoluteFile());
    
    return result;
  }
}
