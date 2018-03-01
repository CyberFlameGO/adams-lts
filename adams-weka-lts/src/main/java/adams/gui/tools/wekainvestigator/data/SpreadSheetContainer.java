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
 * FileContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.io.PlaceholderFile;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import weka.core.Instances;

import java.io.File;
import java.io.Serializable;
import java.util.logging.Level;

/**
 * SpreadSheet-based dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetContainer
  extends AbstractDataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the source. */
  protected File m_Source;

  /** the reader used to load the data. */
  protected SpreadSheetReader m_Reader;

  /**
   * Loads the data using the specified reader.
   *
   * @param reader	the reader to use
   * @param source	the file to load
   */
  public SpreadSheetContainer(SpreadSheetReader reader, File source) {
    this(reader, new PlaceholderFile(source));
  }

  /**
   * Loads the data using the specified reader.
   *
   * @param reader	the reader to use
   * @param source	the file to load
   */
  public SpreadSheetContainer(SpreadSheetReader reader, PlaceholderFile source) {
    super();
    try {
      SpreadSheet sheet = reader.read(source);
      SpreadSheetToWekaInstances conv = new SpreadSheetToWekaInstances();
      conv.setInput(sheet);
      String msg = conv.convert();
      if (msg != null)
        throw new IllegalArgumentException("Failed to convert spreadsheet!\n" + msg);
      m_Data   = (Instances) conv.getOutput();
      m_Source = source;
      m_Reader = reader;
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to load dataset: " + source, e);
    }
  }

  /**
   * Returns the source of the data item.
   *
   * @return		the source
   */
  @Override
  public String getSource() {
    if (m_Source == null)
      return "<unknown>";
    else
      return m_Source.toString();
  }

  /**
   * Whether it is possible to reload this item.
   *
   * @return		true if reloadable
   */
  @Override
  public boolean canReload() {
    return (m_Reader != null) && (m_Source != null) && (m_Source.exists());
  }

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  @Override
  protected boolean doReload() {
    SpreadSheet 		sheet;
    SpreadSheetToWekaInstances 	conv;
    String			msg;

    try {
      sheet = m_Reader.read(m_Source);
      conv  = new SpreadSheetToWekaInstances();
      conv.setInput(sheet);
      msg = conv.convert();
      if (msg != null)
        throw new IllegalArgumentException("Failed to convert spreadsheet!\n" + msg);
      m_Data = (Instances) conv.getOutput();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to reload: " + m_Source, e);
      return false;
    }
  }

  /**
   * Returns the data to store in the undo.
   *
   * @return		the undo point
   */
  protected Serializable[] getUndoData() {
    return new Serializable[]{
      m_Data,
      m_Modified,
      m_Reader,
      m_Source
    };
  }

  /**
   * Restores the data from the undo point.
   *
   * @param data	the undo point
   */
  protected void applyUndoData(Serializable[] data) {
    m_Data     = (Instances) data[0];
    m_Modified = (Boolean) data[1];
    m_Reader   = (SpreadSheetReader) data[2];
    m_Source   = (File) data[3];
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_Reader = null;
    m_Source = null;
  }
}
