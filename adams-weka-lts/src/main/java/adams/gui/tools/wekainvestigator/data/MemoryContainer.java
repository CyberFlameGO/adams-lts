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
 * MemoryContainer.java
 * Copyright (C) 2016-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import weka.core.Instances;

/**
 * Dataset exists only in memory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MemoryContainer
  extends AbstractDataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /**
   * Uses the specified data.
   *
   * @param data	the data to use
   */
  public MemoryContainer(Instances data) {
    super(data);
  }

  /**
   * Returns the source of the data item.
   *
   * @return		the source
   */
  @Override
  public String getSource() {
    return "<memory>";
  }

  /**
   * Whether it is possible to reload this item.
   *
   * @return		true if reloadable
   */
  @Override
  public boolean canReload() {
    return false;
  }

  /**
   * Reloads the data.
   *
   * @return		null if successfully reloaded, otherwise error message
   */
  @Override
  protected String doReload() {
    return null;
  }
}
