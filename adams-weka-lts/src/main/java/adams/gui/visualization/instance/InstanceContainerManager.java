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
 * InstanceContainerManager.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.data.instance.Instance;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.event.DataChangeListener;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ColorContainerManager;
import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.container.NamedContainerManager;
import adams.gui.visualization.container.VisibilityContainerManager;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.ColorProviderWithNameSupport;
import adams.gui.visualization.core.DefaultColorProvider;
import gnu.trove.list.array.TIntArrayList;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A handler for the Instance containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceContainerManager
  extends AbstractContainerManager<InstanceContainer>
  implements VisibilityContainerManager<InstanceContainer>, NamedContainerManager,
             ColorContainerManager<InstanceContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -4325235760470150191L;

  /** the owning panel. */
  protected ContainerListManager<InstanceContainerManager> m_Owner;

  /** the color provider for managing the colors. */
  protected ColorProvider m_ColorProvider;

  /**
   * Initializes the manager.
   *
   * @param owner	the owning panel
   */
  public InstanceContainerManager(ContainerListManager<InstanceContainerManager> owner) {
    super();

    m_Owner         = owner;
    m_ColorProvider = new DefaultColorProvider();

    if (owner instanceof DataChangeListener)
      addDataChangeListener((DataChangeListener) owner);
  }

  /**
   * Returns the owning panel.
   *
   * @return		the owner
   */
  public ContainerListManager getOwner() {
    return m_Owner;
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public synchronized void setColorProvider(ColorProvider value) {
    int		i;
    
    m_ColorProvider = value;
    for (i = 0; i < count(); i++)
      get(i).setColor(getColor(get(i)));
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider in use
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the color for the container.
   *
   * @param cont	the container to get the color for
   * @return		the color
   */
  public Color getColor(InstanceContainer cont) {
    if (m_ColorProvider instanceof ColorProviderWithNameSupport)
      return ((ColorProviderWithNameSupport) m_ColorProvider).next(cont.getID());
    else
      return m_ColorProvider.next();
  }

  /**
   * Clears the container list.
   */
  @Override
  public void clear() {
    super.clear();

    m_ColorProvider.resetColors();
  }

  /**
   * Returns a new container containing the given payload.
   *
   * @param o		the payload to encapsulate
   * @return		the new container
   */
  @Override
  public InstanceContainer newContainer(Comparable o) {
    return new InstanceContainer(this, (Instance) o);
  }

  /**
   * Adds the given container to the list. Duplicates are ignored.
   *
   * @param c		the container to add
   */
  @Override
  public void add(InstanceContainer c) {
    c.setColor(getColor(c));

    super.add(c);

    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();
  }

  /**
   * Replaces the container at the given position.
   *
   * @param index	the position to replace
   * @param c		the replacement
   * @return		the old container
   */
  @Override
  public InstanceContainer set(int index, InstanceContainer c) {
    InstanceContainer	result;

    result = super.set(index, c);

    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();

    return result;
  }

  /**
   * Removes the container at the specified position.
   *
   * @param index	the index of the container to remove
   * @return		the container that got removed
   */
  @Override
  public InstanceContainer remove(int index) {
    InstanceContainer		result;

    if (!m_AllowRemoval)
      return null;
    
    result = super.remove(index);

    m_ColorProvider.recycle(result.getColor());

    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();

    return result;
  }

  /**
   * Determines the index of the sequence with the specified ID.
   *
   * @param id	the ID of the sequence
   * @return		the index of the sequence or -1 if not found
   */
  public int indexOf(String id) {
    int	result;
    int	i;

    result = -1;

    for (i = 0; i < count(); i++) {
      if (get(i).getID().equals(id)) {
        result = i;
        break;
      }
    }

    return result;
  }

  /**
   * Returns the indices of all visible containers.
   *
   * @return		all containers
   */
  public int[] getVisibleIndices() {
    TIntArrayList	result;
    int			i;

    result = new TIntArrayList();

    for (i = 0; i < count(); i++) {
      if (!isVisible(i))
        continue;
      result.add(i);
    }

    return result.toArray();
  }

  /**
   * Returns (a copy of) all currently stored containers. Those containers
   * have no manager.
   *
   * @return		all containers
   */
  public List<InstanceContainer> getAllVisible() {
    List<InstanceContainer>	result;
    InstanceContainer		cont;
    int				i;

    result = new ArrayList<>();

    for (i = 0; i < count(); i++) {
      if (!isVisible(i))
        continue;
      cont = (InstanceContainer) get(i).copy();
      cont.setManager(null);
      result.add(cont);
    }

    return result;
  }

  /**
   * Returns whether the container at the specified position is visible.
   *
   * @param index	the container's position
   * @return		true if the container is visible
   */
  public boolean isVisible(int index) {
    return get(index).isVisible();
  }

  /**
   * Sets the specified container's visibility. Uses the scripting engine
   * if the owner is derived from SpectrumPanel.
   *
   * @param index	the index of the container
   * @param visible	if true then the container will be made visible
   */
  public void setVisible(int index, boolean visible) {
    get(index).setVisible(visible);

    notifyDataChangeListeners(new DataChangeEvent(this, Type.VISIBILITY, index));
  }

  /**
   * Returns the nth visible container.
   *
   * @param index	the index (relates only to the visible containers!)
   * @return		the container, null if index out of range
   */
  public InstanceContainer getVisible(int index) {
    InstanceContainer	result;
    int			i;
    int			count;

    result = null;
    count  = -1;

    for (i = 0; i < count(); i++) {
      if (isVisible(i))
	count++;
      if (count == index) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the number of visible containers.
   *
   * @return		the number of visible containers
   */
  public int countVisible() {
    int	result;
    int	i;

    result = 0;

    for (i = 0; i < count(); i++) {
      if (isVisible(i))
        result++;
    }

    return result;
  }

  /**
   * Returns whether the container matches the current search.
   *
   * @param cont	the container to check
   * @param search	the search string
   * @param regExp	whether to perform regular expression matching
   */
  protected boolean isMatch(InstanceContainer cont, String search, boolean regExp) {
    if (regExp)
      return cont.getID().matches(search);
    else
      return cont.getID().toLowerCase().contains(search);
  }
}