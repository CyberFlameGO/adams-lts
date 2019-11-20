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
 * InstancesTablePopupMenuItemHelper.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.ClassLister;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.TableRowRange;
import adams.gui.visualization.instances.InstancesTable;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for constructing popup menus for the InstancesTable.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstancesTablePopupMenuItemHelper {

  /**
   * Container object for the table state used by the popup menu items.
   */
  public static class TableState {

    /** the table. */
    public InstancesTable table = null;

    /** the row range. */
    public TableRowRange range = TableRowRange.ALL;

    /** the selected row. */
    public int selRow = -1;

    /** the actual row. */
    public int actRow = -1;

    /** the selected rows. */
    public int[] selRows = new int[0];

    /** the actual rows. */
    public int[] actRows = new int[0];

    /** the selected column. */
    public int selCol = -1;

    /** the actual column. */
    public int actCol = -1;
  }

  /**
   * Determines the state of the table.
   *
   * @param table	the table to get the state for
   * @param range	the range to use
   * @return		the state
   */
  public static TableState getState(InstancesTable table, MouseEvent e, TableRowRange range) {
    TableState result;
    int		i;

    result = new TableState();
    result.table = table;
    result.range = range;
    result.selRow = table.getSelectedRow();
    if (result.selRow != -1)
      result.actRow = table.getActualRow(result.selRow);
    result.selRows = table.getSelectedRows();
    result.actRows = new int[result.selRows.length];
    for (i = 0; i < result.selRows.length; i++)
      result.actRows[i] = table.getActualRow(result.selRows[i]);
    result.selCol = table.columnAtPoint(e.getPoint());
    result.actCol = result.selCol - 1;

    return result;
  }

  /**
   * Returns a sorted list of popup menu items for the specified superclass.
   *
   * @param cls		the superclass
   * @return		the list
   */
  protected static List<InstancesTablePopupMenuItem> getItems(Class cls) {
    List<InstancesTablePopupMenuItem>		result;
    String[]					classes;

    result = new ArrayList<>();
    classes = ClassLister.getSingleton().getClassnames(cls);
    for (String c : classes) {
      try {
	result.add((InstancesTablePopupMenuItem) Class.forName(c).newInstance());
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append("Failed to instantiate InstancesTable menu item: " + c, e);
      }
    }

    if (result.size() > 1)
      Collections.sort(result);

    return result;
  }

  /**
   * Adds the appropriate action to the menuitem.
   *
   * @param state	the table state
   * @param isRow	whether this is for a row or a column
   * @param menuitem	the menuitem to add the action to
   * @param item	the menu item scheme
   */
  protected static void addAction(final TableState state, boolean isRow, final JMenuItem menuitem, final InstancesTablePopupMenuItem item) {
    PlotSelectedRows 	plotSelRows;
    ProcessSelectedRows procSelRows;
    boolean		enabled;

    if (isRow) {
      if (item instanceof PlotSelectedRows) {
        plotSelRows = (PlotSelectedRows) item;
	menuitem.addActionListener((ActionEvent e) -> ((PlotSelectedRows) item).plotSelectedRows(state));
	enabled = (state.actRows.length >= plotSelRows.minNumRows());
	if (plotSelRows.maxNumRows() != -1)
	  enabled = enabled && (state.actRows.length <= plotSelRows.maxNumRows());
 	menuitem.setEnabled(enabled);
      }
      else if (item instanceof PlotRow) {
	menuitem.addActionListener((ActionEvent e) -> ((PlotRow) item).plotRow(state));
	menuitem.setEnabled(state.actRows.length <= 1);
      }
      else if (item instanceof ProcessSelectedRows) {
        procSelRows = (ProcessSelectedRows) item;
	menuitem.addActionListener((ActionEvent e) -> ((ProcessSelectedRows) item).processSelectedRows(state));
	enabled = (state.actRows.length >= procSelRows.minNumRows());
	if (procSelRows.maxNumRows() != -1)
	  enabled = enabled && (state.actRows.length <= procSelRows.maxNumRows());
 	menuitem.setEnabled(enabled);
      }
      else if (item instanceof ProcessRow) {
	menuitem.addActionListener((ActionEvent e) -> ((ProcessRow) item).processRow(state));
	menuitem.setEnabled(state.actRows.length <= 1);
      }
      else if (item instanceof ProcessCell) {
        menuitem.setEnabled((state.selRow >= 0) && (state.selCol >= 0));
	menuitem.addActionListener((ActionEvent e) -> ((ProcessCell) item).processCell(state));
      }
    }
    else {
      if (item instanceof PlotColumn) {
        menuitem.setEnabled(((PlotColumn) item).handlesRowRange(state.range));
	menuitem.addActionListener((ActionEvent e) -> ((PlotColumn) item).plotColumn(state));
      }
      else if (item instanceof ProcessColumn) {
        menuitem.setEnabled(((ProcessColumn) item).handlesRowRange(state.range));
	menuitem.addActionListener((ActionEvent e) -> ((ProcessColumn) item).processColumn(state));
      }
    }
  }

  /**
   * Adds the available menu items to the menu.
   *
   * @param state	the table state
   * @param isRow	whether this is for a row or a column
   * @param menu	the menu to add the items to
   * @param items	the available schemes
   */
  protected static void addToPopupMenu(TableState state, boolean isRow, JPopupMenu menu, List<InstancesTablePopupMenuItem> items) {
    JMenuItem		menuitem;

    if (items.size() == 0)
      return;

    if (menu.getComponent(menu.getComponentCount() - 1) instanceof JMenuItem)
      menu.addSeparator();
    for (InstancesTablePopupMenuItem item: items) {
      if (!item.isAvailable(state))
        continue;
      menuitem = new JMenuItem(item.getMenuItem());
      if (item.getIconName() != null)
        menuitem.setIcon(GUIHelper.getIcon(item.getIconName()));
      addAction(state, isRow, menuitem, item);
      menu.add(menuitem);
    }
  }

  /**
   * Adds the available menu items to the menu.
   *
   * @param state	the table state
   * @param menu	the menu to add the items to
   * @param isRow	whether this is for a row or a column
   */
  public static void addToPopupMenu(TableState state, JPopupMenu menu, boolean isRow) {
    menu.addSeparator();
    if (isRow) {
      addToPopupMenu(state, true, menu, getItems(PlotRow.class));
      addToPopupMenu(state, true, menu, getItems(ProcessRow.class));
      addToPopupMenu(state, true, menu, getItems(ProcessSelectedRows.class));
      addToPopupMenu(state, true, menu, getItems(ProcessCell.class));
    }
    else {
      addToPopupMenu(state, false, menu, getItems(PlotColumn.class));
      addToPopupMenu(state, false, menu, getItems(ProcessColumn.class));
    }
  }
}
