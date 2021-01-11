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
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import adams.data.padding.PaddingHelper;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.TestInstances;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

/**
 * Tests FFT. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.attribute.FFTTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FFTTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FFTTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_Instances = getFilteredClassifierData();
  }

  /**
   * returns data generated for the FilteredClassifier test.
   *
   * @return		the dataset for the FilteredClassifier
   * @throws Exception	if generation of data fails
   */
  protected Instances getFilteredClassifierData() throws Exception {
    TestInstances	testinst;

    testinst = new TestInstances();
    testinst.setNumNominal(0);
    testinst.setNumNumeric(20);
    testinst.setClassType(Attribute.NOMINAL);
    testinst.setNumInstances(50);

    return testinst.generate();
  }

  /**
   * Creates a default FFT.
   *
   * @return		the default filter
   */
  public Filter getFilter() {
    return new FFT();
  }

  /**
   * Creates a specialized FFT.
   *
   * @param inverse	inverse transform?
   * @param real	real or imaginary?
   * @return		the filter
   */
  public Filter getFilter(boolean inverse, boolean real) {
    FFT 	result;

    result = new FFT();
    result.setInverseTransform(inverse);
    result.setReal(real);

    return result;
  }

  /**
   * performs the actual test.
   */
  protected void performTest() {
    Instances icopy = new Instances(m_Instances);
    Instances result = null;
    try {
      m_Filter.setInputFormat(icopy);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception thrown on setInputFormat(): \n" + ex.getMessage());
    }
    try {
      result = Filter.useFilter(icopy, m_Filter);
      assertNotNull(result);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception thrown on useFilter(): \n" + ex.getMessage());
    }

    int	numAtts = icopy.numAttributes();
    if (icopy.classIndex() > -1)
      numAtts--;
    numAtts = PaddingHelper.nextPowerOf2(numAtts);
    if (icopy.classIndex() > -1)
      numAtts++;
    assertEquals(numAtts, result.numAttributes());
    assertEquals(icopy.numInstances(), m_Instances.numInstances());
  }

  /**
   * Test default.
   */
  public void testDefault() {
    m_Filter = getFilter();
    testBuffered();
    performTest();
  }

  /**
   * Test not inverse, imaginary.
   */
  public void testNotInverseImaginary() {
    m_Filter = getFilter(false, false);
    testBuffered();
    performTest();
  }

  /**
   * Test inverse, imaginary.
   */
  public void testInverseImaginary() {
    m_Filter = getFilter(true, false);
    testBuffered();
    performTest();
  }

  /**
   * Test not inverse, real.
   */
  public void testNotInverseTrue() {
    m_Filter = getFilter(false, true);
    testBuffered();
    performTest();
  }

  /**
   * Test inverse, real.
   */
  public void testInverseReal() {
    m_Filter = getFilter(true, true);
    testBuffered();
    performTest();
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(FFTTest.class);
  }

  /**
   * Runs the test from the commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    AdamsTestHelper.setRegressionRoot();
    TestRunner.run(suite());
  }
}
