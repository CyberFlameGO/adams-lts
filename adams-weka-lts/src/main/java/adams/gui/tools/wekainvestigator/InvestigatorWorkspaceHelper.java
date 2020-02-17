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
 * InvestigatorWorkspaceHelper.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator;

import adams.core.MessageCollection;
import adams.data.weka.classattribute.AbstractClassAttributeHeuristic;
import adams.data.weka.relationname.AbstractRelationNameHeuristic;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.workspace.AbstractSerializableWorkspaceManagerPanel;
import adams.gui.workspace.AbstractWorkspaceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Helper class for Weka Investigator workspaces.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InvestigatorWorkspaceHelper
  extends AbstractWorkspaceHelper<InvestigatorPanel, AbstractSerializableWorkspaceManagerPanel<InvestigatorPanel>> {

  public static final String KEY_DATA = "data";

  public static final String KEY_TABS = "tabs";

  public static final String KEY_CLASSATTRIBUTE = "class attribute";

  public static final String KEY_RELATIONNAME  = "relation name";

  public static final String KEY_UNDOENABLED = "undo enabled";

  /**
   * Generates a view of the panel that can be serialized.
   *
   * @param panel	the panel to serialize
   * @return		the data to serialize
   */
  @Override
  public Object serialize(InvestigatorPanel panel) {
    Map<String,Object> 		result;
    List<Object> 		list;
    int				i;
    AbstractInvestigatorTab 	tab;

    result = new HashMap<>();

    // data
    list = new ArrayList<>();
    list.addAll(panel.getData());
    result.put(KEY_DATA, list);

    // other
    result.put(KEY_CLASSATTRIBUTE, panel.getClassAttributeHeuristic());
    result.put(KEY_RELATIONNAME, panel.getRelationNameHeuristic());
    result.put(KEY_UNDOENABLED, panel.isUndoEnabled());

    // tabs
    list = new ArrayList<>();
    for (i = 0; i < panel.getTabbedPane().getTabCount(); i++) {
      tab = (AbstractInvestigatorTab) panel.getTabbedPane().getComponentAt(i);
      list.add(tab.getClass().getName());
      list.add(tab.serialize(new HashSet<>(Arrays.asList(SerializationOption.values()))));
    }
    result.put(KEY_TABS, list);

    return result;
  }

  /**
   * Deserializes the data and configures the panel.
   *
   * @param panel	the panel to update
   * @param data	the serialized data to restore the panel with
   * @param errors	for storing errors
   */
  @Override
  public void deserialize(InvestigatorPanel panel, Object data, MessageCollection errors) {
    Map<String,Object>			items;
    List<Object>			list;
    int					i;
    Class				cls;
    AbstractInvestigatorTab		tab;

    items = (Map<String,Object>) data;

    // data
    list = (List<Object>) items.get(KEY_DATA);
    for (Object obj: list)
      panel.getData().add((DataContainer) obj);

    // other
    if (items.containsKey(KEY_CLASSATTRIBUTE))
      panel.setClassAttributeHeuristic((AbstractClassAttributeHeuristic) items.get(KEY_CLASSATTRIBUTE));
    if (items.containsKey(KEY_RELATIONNAME))
      panel.setRelationNameHeuristic((AbstractRelationNameHeuristic) items.get(KEY_RELATIONNAME));
    if (items.containsKey(KEY_UNDOENABLED))
      panel.setUndoEnabled((Boolean) items.get(KEY_UNDOENABLED));

    // tabs
    list = (List<Object>) items.get(KEY_TABS);
    i = 0;
    while (i < list.size()) {
      try {
	cls = Class.forName((String) list.get(i));
	tab = (AbstractInvestigatorTab) cls.newInstance();
	panel.getTabbedPane().addTab(tab, false);
	tab.deserialize(list.get(i + 1), errors);
      }
      catch (Exception e) {
	errors.add("Failed to deserialize data (" + (i) + "-" + (i+2) + ")!", e);
      }
      i += 2;
    }

    panel.fireDataChange(new WekaInvestigatorDataEvent(panel, WekaInvestigatorDataEvent.DESERIALIZED));
  }
}
