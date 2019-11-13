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
 * AllFinder.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.rowfinder;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Dummy finder, finds all rows.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AllFinder
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = -7945692540826937829L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy finder, finds all rows.";
  }
  
  /**
   * Returns the rows of interest in the dataset.
   * 
   * @param data	the dataset to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(Instances data) {
    TIntList result;
    int		i;

    result = new TIntArrayList();
    for (i = 0; i < data.numInstances(); i++)
      result.add(i);

    return result.toArray();
  }
}
