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
 * AbstractSetupPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup;

import adams.core.ClassLister;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekamultiexperimenter.AbstractExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.io.AbstractExperimentIO;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ancestor for setup panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of experiment to handle
 */
public abstract class AbstractSetupPanel<T>
  extends AbstractExperimenterPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7551590918482897687L;

  /**
   * Document listener that just sets the modified flag.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public class ModificationDocumentListener
    implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
      setModified(true);
    }
    @Override
    public void removeUpdate(DocumentEvent e) {
      setModified(true);
    }
    @Override
    public void changedUpdate(DocumentEvent e) {
      setModified(true);
    }
  }

  /**
   * Change listener that just sets the modified flag.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public class ModificationChangeListener
    implements ChangeListener {
    @Override
    public void stateChanged(ChangeEvent e) {
      setModified(true);
    }
  }

  /**
   * Action listener that just sets the modified flag.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public class ModificationActionListener
    implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      setModified(true);
    }
  }

  /** the handler for loading/saving experiments. */
  protected AbstractExperimentIO<T> m_ExperimentIO;
  
  /** whether the setup has been modified. */
  protected boolean m_Modified;
  
  /** whether to ignored changes. */
  protected boolean m_IgnoreChanges;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ExperimentIO  = createExperimentIO();
    m_Modified      = false;
    m_IgnoreChanges = false;
  }
  
  /**
   * Returns the name for this setup panel.
   * 
   * @return		the name
   */
  public abstract String getSetupName();

  /**
   * Creates the handler for the IO, i.e., loading/saving of experiments.
   * 
   * @return		the handler
   */
  protected abstract AbstractExperimentIO<T> createExperimentIO();
  
  /**
   * Returns the handler for the IO, i.e., loading/saving of experiments.
   * 
   * @return		the handler
   */
  public AbstractExperimentIO<T> getExperimentIO() {
    return m_ExperimentIO;
  }
  
  /**
   * Returns the current experiment.
   * 
   * @return		the experiment
   */
  public abstract T getExperiment();

  /**
   * Sets whether to ignore changes, ie don't set the modified flag.
   *
   * @param value	true if to ignore changes
   */
  public void setIgnoreChanges(boolean value) {
    m_IgnoreChanges = value;
  }

  /**
   * Sets the experiment to use.
   * 
   * @param value	the experiment
   */
  public abstract void setExperiment(T value);
  
  /**
   * Checks whether the experiment can be handled.
   * 
   * @param exp		the experiment to check
   * @return		null if can handle, otherwise error message
   */
  public abstract String handlesExperiment(T exp);

  /**
   * Sets the modified state.
   * 
   * @param value	the modified state
   * @see		#m_IgnoreChanges
   */
  public void setModified(boolean value) {
    if (m_IgnoreChanges)
      return;
    m_Modified = value;
    if (getOwner() != null)
      getOwner().update();
  }
  
  /**
   * Returns whether the setup has been modified.
   * 
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the icon to use in the tabbed pane.
   *
   * @return		the icon
   */
  public Icon getTabIcon() {
    return GUIHelper.getIcon("settings.png");
  }
  
  /**
   * Returns a list with classnames of panels.
   *
   * @return		the panel classnames
   */
  public static String[] getPanels() {
    return ClassLister.getSingleton().getClassnames(AbstractSetupPanel.class);
  }
}
