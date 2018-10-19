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
 * PlainTextResultsPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.analysis;

import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Displays the results in plain text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlainTextResultsPanel
  extends AbstractResultsPanel {

  /** for displaying the results. */
  protected BaseTextAreaWithButtons m_TextAreaResults;

  private static final long serialVersionUID = 3608852939358175057L;

  /** the copy button. */
  protected BaseButton m_ButtonCopy;

  /** the save button. */
  protected BaseButton m_ButtonSave;

  /** the filechooser for saving the output. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter filter;

    super.initialize();

    filter        = ExtensionFileFilter.getTextFileFilter();
    m_FileChooser = new BaseFileChooser();
    m_FileChooser.addChoosableFileFilter(filter);
    m_FileChooser.setFileFilter(filter);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextAreaResults = new BaseTextAreaWithButtons();
    m_TextAreaResults.setTextFont(Fonts.getMonospacedFont());
    add(m_TextAreaResults, BorderLayout.CENTER);

    m_ButtonCopy = new BaseButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.addActionListener((ActionEvent e) -> {
      if (m_TextAreaResults.getSelectedText() != null)
	ClipboardHelper.copyToClipboard(m_TextAreaResults.getSelectedText());
      else
	ClipboardHelper.copyToClipboard(m_TextAreaResults.getText());
    });
    m_TextAreaResults.addToButtonsPanel(m_ButtonCopy);

    m_ButtonSave = new BaseButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonSave.addActionListener((ActionEvent e) -> {
      int retVal = m_FileChooser.showSaveDialog(PlainTextResultsPanel.this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;
      if (!FileUtils.writeToFile(m_FileChooser.getSelectedFile().getAbsolutePath(), m_TextAreaResults.getText(), false))
	GUIHelper.showErrorMessage(PlainTextResultsPanel.this, "Failed to save output to:\n" + m_FileChooser.getSelectedFile());
    });
    m_TextAreaResults.addToButtonsPanel(m_ButtonSave);
  }

  /**
   * Returns the name to display in the GUI.
   *
   * @return		the name
   */
  public String getResultsName() {
    return "Plain text";
  }

  /**
   * Displays the results.
   */
  protected void doDisplay() {
    StringBuilder	results;

    results = new StringBuilder();
    results.append(m_Matrix.toStringMatrix());
    results.append(m_Matrix.toStringKey());

    m_TextAreaResults.setText(results.toString());
    m_TextAreaResults.setCaretPosition(0);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public PlainTextResultsPanel getClone() {
    return new PlainTextResultsPanel();
  }
}
