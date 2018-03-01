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
 * WekaInstancesStatisticDataType.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

/**
 * Defines what data to retrieve from an Instances object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 16049 $
 */
public enum WekaInstancesStatisticDataType {
  /** obtains rows. */
  ROW_BY_INDEX,
  /** obtains columns (by index). */
  COLUMN_BY_INDEX,
  /** obtains columns (by reg exp). */
  COLUMN_BY_REGEXP
}
