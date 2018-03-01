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
 * ExperimenterEntryPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter;

import adams.gui.workspace.AbstractWorkspaceListPanel;

/**
 * Allows the display of multiple Experimenter panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6682 $
 */
public class ExperimenterEntryPanel
  extends AbstractWorkspaceListPanel<ExperimenterPanel> {

  /** for serialization. */
  private static final long serialVersionUID = 1704390033157269580L;
  
  /**
   * Returns the default title to use for dialogs.
   *
   * @return		the title
   */
  protected String getDefaultDialogTitle() {
    return "Experimenter";
  }
}
