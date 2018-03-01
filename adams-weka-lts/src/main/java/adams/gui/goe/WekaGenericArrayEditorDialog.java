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
 * WekaGenericArrayEditorDialog.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.env.Environment;
import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;

import javax.swing.JFileChooser;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Displays a GenericArrayEditor.
 * <br><br>
 * Calling code needs to dispose the dialog manually or enable automatic
 * disposal:
 * <pre>
 * GenericArrayEditorDialog dialog = new ...
 * dialog.setDefaultCloseOperation(GenericArrayEditorDialog.DISPOSE_ON_CLOSE);
 * </pre>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGenericArrayEditorDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 6595810269220104762L;

  /** constant for dialog cancellation. */
  public final static int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;

  /** constant for dialog approval. */
  public final static int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

  /** the underlying editor. */
  protected weka.gui.GenericArrayEditor m_Editor;

  /** the current object. */
  protected Object m_Current;

  /** whether the dialog was cancelled or ok'ed. */
  protected int m_Result;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public WekaGenericArrayEditorDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public WekaGenericArrayEditorDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public WekaGenericArrayEditorDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public WekaGenericArrayEditorDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public WekaGenericArrayEditorDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public WekaGenericArrayEditorDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public WekaGenericArrayEditorDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public WekaGenericArrayEditorDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Editor  = new weka.gui.GenericArrayEditor();
    m_Current = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setDefaultCloseOperation(GenericArrayEditorDialog.HIDE_ON_CLOSE);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(m_Editor.getCustomEditor(), BorderLayout.CENTER);

    if (GUIHelper.getParentDialog(m_Editor.getCustomEditor()) != null) {
      GUIHelper.getParentDialog(m_Editor.getCustomEditor()).addWindowListener(new WindowAdapter() {
	@Override
	public void windowClosed(WindowEvent e) {
	  m_Result  = APPROVE_OPTION;
	  m_Current = m_Editor.getValue();
	  setVisible(false);
	  super.windowClosed(e);
	}
      });
    }

    pack();
  }

  /**
   * Returns the underlying editor.
   *
   * @return		the editor in use
   */
  public weka.gui.GenericArrayEditor getEditor() {
    return m_Editor;
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();

    m_Current = m_Editor.getValue();
    m_Result  = CANCEL_OPTION;

    GUIHelper.adjustSize(this);
  }

  /**
   * Sets the current object.
   *
   * @param value	the current object
   */
  public void setCurrent(Object value) {
    m_Editor.setValue(value);
    m_Current = value;
    GUIHelper.adjustSize(this);
  }

  /**
   * Returns the current object.
   *
   * @return		the current object
   */
  public Object getCurrent() {
    return m_Current;
  }

  /**
   * Returns whether the dialog got cancelled or approved.
   *
   * @return		the result
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  public int getResult() {
    return m_Result;
  }

  /**
   * Creates a modal dialog for the parent.
   *
   * @param parent	the parent to make the dialog modal
   * @return		the dialog
   */
  public static WekaGenericArrayEditorDialog createDialog(Container parent) {
    return createDialog(parent, null);
  }

  /**
   * Creates a modal dialog for the parent with the provided editor and initial value.
   *
   * @param parent	the parent to make the dialog modal
   * @param value	the value to use, ignored if null
   * @return		the dialog
   */
  public static WekaGenericArrayEditorDialog createDialog(Container parent, Object value) {
    WekaGenericArrayEditorDialog	result;

    if (GUIHelper.getParentDialog(parent) != null)
      result = new WekaGenericArrayEditorDialog(GUIHelper.getParentDialog(parent));
    else
      result = new WekaGenericArrayEditorDialog(GUIHelper.getParentFrame(parent));
    result.setModalityType(ModalityType.DOCUMENT_MODAL);
    result.setTitle("Array editor");

    // initial value?
    if (value != null)
      result.setCurrent(value);

    return result;
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    GenericArrayEditorDialog dialog = new GenericArrayEditorDialog((Frame) null, "Array editor", true);
    dialog.setDefaultCloseOperation(GenericArrayEditorDialog.DISPOSE_ON_CLOSE);
    dialog.setCurrent(new weka.filters.Filter[]{new weka.filters.AllFilter()});
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getResult() == APPROVE_OPTION)
      System.out.println(Utils.arrayToString(dialog.getCurrent()));
  }
}
