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
 * PassThrough.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaclassifiersetupprocessor;

import adams.core.ObjectCopyHelper;
import weka.classifiers.Classifier;

/**
 * Simply returns the same setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractClassifierSetupProcessor {

  private static final long serialVersionUID = 5224698983405582618L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply returns the same setups.";
  }

  /**
   * Processes the classifier array.
   *
   * @param classifiers	the classifiers to process
   * @return		the processed classifiers
   */
  @Override
  protected Classifier[] doProcess(Classifier[] classifiers) {
    return ObjectCopyHelper.copyObjects(classifiers);
  }
}
