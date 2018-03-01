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
 * RowFilteredColumnFinder.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import weka.core.Instance;
import weka.core.Instances;
import adams.data.weka.rowfinder.RowFinder;
import adams.data.weka.rowfinder.TrainableRowFinder;

/**
 * This column finder first filters the rows before finding any columns on
 * the subset of rows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RowFilteredColumnFinder
  extends AbstractFilteredColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = -2963065019052042099L;

  /** the RowFinder to use first. */
  protected RowFinder m_RowFinder;
  
  /** whether the column finder was trained on the subset. */
  protected boolean m_ColumnFinderTrained;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Creates a subset of rows using the provided RowFinder first before "
	+ "finding the columns using the subset.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row-finder", "rowFinder",
	    new adams.data.weka.rowfinder.NullFinder());
  }

  /**
   * Sets the row finder to use.
   *
   * @param value	the row finder
   */
  public void setRowFinder(RowFinder value) {
    m_RowFinder = value;
    reset();
  }

  /**
   * Returns the row finder in use.
   *
   * @return		the row finder
   */
  public RowFinder getRowFinder() {
    return m_RowFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rowFinderTipText() {
    return "The row finder to use for generating the subset for the column finder.";
  }

  /**
   * Performs the actual training of the column finder with the specified dataset.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  @Override
  protected boolean doTrainColumnFinder(Instances data) {
    boolean	result;
    
    result = true;
    
    if (m_RowFinder instanceof TrainableRowFinder)
      result = ((TrainableRowFinder) m_RowFinder).trainRowFinder(data);

    // column finder gets trained later
    m_ColumnFinderTrained = false;
    
    return result;
  }

  /**
   * Returns the columns of interest in the dataset.
   * 
   * @param data	the dataset to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(Instances data) {
    int[]		result;
    int[]		rows;
    Instances		subset;
    
    // create subset
    rows   = m_RowFinder.findRows(data);
    subset = new Instances(data, rows.length);
    for (int row: rows)
      subset.add((Instance) data.instance(row).copy());
    
    // train column finder if necessary
    if (!m_ColumnFinderTrained) {
      m_ColumnFinderTrained = true;
      if (m_ColumnFinder instanceof TrainableColumnFinder)
	m_ColumnFinderTrained = ((TrainableColumnFinder) m_ColumnFinder).trainColumnFinder(data);
      if (!m_ColumnFinderTrained)
	throw new IllegalStateException("Failed to train column finder on subset of rows!");
    }
    
    // find columns
    result = m_ColumnFinder.findColumns(subset);
    
    return result;
  }
}
