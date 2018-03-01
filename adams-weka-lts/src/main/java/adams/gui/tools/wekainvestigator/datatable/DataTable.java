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
 * DataTable.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable;

import adams.gui.core.BaseTable;
import adams.gui.core.SortableAndSearchableWrapperTableModel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import nz.ac.waikato.cms.locator.StringCompare;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Specialized table with custom cell editors for the class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTable
  extends BaseTable {

  private static final long serialVersionUID = -2329794525513037246L;

  /** the maximum column width. */
  public final static int MAX_COLUMN_WIDTH = 300;

  /** whether to sort the attributes alphabetically. */
  protected boolean m_SortAttributes;

  /**
   * Constructs a <code>DataTable</code> that is initialized with
   * <code>dm</code> as the data model, a default column model,
   * and a default selection model.
   *
   * @param dm        the data model for the table
   */
  public DataTable(DataTableModel dm) {
    super(dm);
  }

  /**
   * Initializes some GUI-related things.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_SortAttributes = true;

    addHeaderPopupMenuListener((MouseEvent e) -> {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem menuitem = new JMenuItem("Optimal width");
      menuitem.addActionListener((ActionEvent ae) -> setOptimalColumnWidthBounded(MAX_COLUMN_WIDTH));
      menu.add(menuitem);
      menu.show(getTableHeader(), e.getX(), e.getY());
    });
  }

  /**
   * Sets whether to sort the attributes alphabetically for the dropdown list.
   *
   * @param value	true if to sort
   */
  public void setSortAttributes(boolean value) {
    m_SortAttributes = value;
  }

  /**
   * Returns whether to sort the attributes alphabetically for the dropdown list.
   *
   * @return		true if to sort
   */
  public boolean getSortAttributes() {
    return m_SortAttributes;
  }

  /**
   * Returns the cell editor for the specified cell.
   *
   * @param row		the row
   * @param column	the column
   * @return		the cell editor
   */
  @Override
  public TableCellEditor getCellEditor(int row, int column) {
    TableCellEditor	result;
    DataContainer 	cont;
    List<String>	atts;
    int			i;
    JComboBox<String>	combobox;
    TableModel		model;
    DataTableModel	dmodel;

    result = null;
    if (column == 3) {
      model = getModel();
      if (model instanceof SortableAndSearchableWrapperTableModel)
        dmodel = (DataTableModel) ((SortableAndSearchableWrapperTableModel) model).getUnsortedModel();
      else
        dmodel = (DataTableModel) model;
      cont = dmodel.getData().get(row);
      atts = new ArrayList<>();
      for (i = 0; i < cont.getData().numAttributes(); i++)
        atts.add(cont.getData().attribute(i).name());
      if (m_SortAttributes)
	Collections.sort(atts, new StringCompare());
      atts.add(0, "");  // no class
      combobox = new JComboBox<>(atts.toArray(new String[atts.size()]));
      result   = new DefaultCellEditor(combobox);
    }

    // get default
    if (result == null)
      result = super.getCellEditor(row, column);

    return result;
  }
}
