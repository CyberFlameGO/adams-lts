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
 * AbstractSetupOptionPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup;

import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

/**
 * Ancestor for panels that get added to a {@link AbstractSetupPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSetupOptionPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -8401733616002637499L;
  
  /** the setup panel this option panel belongs to. */
  protected AbstractSetupPanel m_Owner;
  
  /** whether to ignored changes. */
  protected boolean m_IgnoreChanges;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_IgnoreChanges = false;
  }
  
  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }
  
  /**
   * Sets the setup panel this option panel belongs to.
   * 
   * @param value	the owner
   * @see		#ownerChanged()
   */
  public void setOwner(AbstractSetupPanel value) {
    m_Owner = value;
    ownerChanged();
  }
  
  /**
   * Gets called when the owner changes.
   * <br><br>
   * Default implementation does nothing
   */
  protected void ownerChanged() {
  }
  
  /**
   * Returns the setup panel this option panel belongs to.
   * 
   * @return		the owner
   */
  public AbstractSetupPanel getOwner() {
    return m_Owner;
  }
  
  /**
   * Sets the modified flag in the owner.
   */
  protected void modified() {
    if (m_IgnoreChanges)
      return;
    if (m_Owner != null)
      m_Owner.setModified(true);
  }
  
  /**
   * Performs GUI updates.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void update() {
  }
  
  /**
   * Logs the message.
   * 
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    if (getOwner() != null)
      getOwner().logMessage(msg);
    else
      System.out.println(msg);
  }
  
  /**
   * Logs the error message and also displays an error dialog.
   * 
   * @param msg		the error message
   */
  public void logError(String msg, String title) {
    if (getOwner() != null) {
      getOwner().logError(msg, title);
    }
    else {
      System.err.println(msg);
      GUIHelper.showErrorMessage(this,
	  msg,
	  title);
    }
  }
}
