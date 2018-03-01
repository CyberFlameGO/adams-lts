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
 * AbstractAdamsFileConverterTest.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package weka.core.converters;

import weka.test.AdamsTestHelper;
import adams.env.Environment;

/**
 * Abstract test for converters within the ADAMS framework.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5635 $
 */
public abstract class AbstractAdamsFileConverterTest
  extends AbstractFileConverterTest {
  
  static {
    AdamsTestHelper.setRegressionRoot();
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractAdamsFileConverterTest(String name) {
    super(name);
    setUpEnvironment();
  }

  /**
   * Sets up the environment.
   */
  protected void setUpEnvironment() {
    Environment.setEnvironmentClass(adams.env.Environment.class);
  }
}
