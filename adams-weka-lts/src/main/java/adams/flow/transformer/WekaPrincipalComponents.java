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
 * WekaPrincipalComponents.java
 * Copyright (C) 2014-2016 Dutch Sprouts, Wageningen, NL
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */
package adams.flow.transformer;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.data.instancesanalysis.PCA;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Performs principal components analysis on the incoming data and outputs the loadings and the transformed data as spreadsheet array.<br>
 * Automatically filters out attributes that cannot be handled by PCA.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaPrincipalComponents
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-variance-covered &lt;double&gt; (property: varianceCovered)
 * &nbsp;&nbsp;&nbsp;Retain enough PC attributes to account for this proportion of variance.
 * &nbsp;&nbsp;&nbsp;default: 0.95
 * </pre>
 * 
 * <pre>-max-attributes &lt;int&gt; (property: maximumAttributes)
 * &nbsp;&nbsp;&nbsp;The maximum number of PC attributes to retain.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-max-attribute-names &lt;int&gt; (property: maximumAttributeNames)
 * &nbsp;&nbsp;&nbsp;The maximum number of attribute names to use.
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * Actor that takes in an instances object containing TGA-MS data and outputs the coefficients from a principal components analysis
 *
 * @author michael.fowke
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Michael Fowke",
  license = License.GPL3,
  copyright = "2014 Dutch Sprouts, Wageningen, NL"
)
public class WekaPrincipalComponents
  extends AbstractTransformer{

  /** for serialization */
  private static final long serialVersionUID = -3079556702775500196L;

  /** the variance to cover. */
  protected double m_CoverVariance;

  /** the maximum number of attributes to keep. */
  protected int m_MaxAttributes;

  /** the maximum number of attribute names to use. */
  protected int m_MaxAttributeNames;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs principal components analysis on the incoming data and outputs "
	+ "the loadings and the transformed data as spreadsheet array.\n"
	+ "Automatically filters out attributes that cannot be handled by PCA.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "variance-covered", "varianceCovered",
      0.95);

    m_OptionManager.add(
      "max-attributes", "maximumAttributes",
      -1, -1, null);

    m_OptionManager.add(
      "max-attribute-names", "maximumAttributeNames",
      5, -1, null);
  }

  /**
   * Sets the amount of variance to account for when retaining
   * principal components.
   *
   * @param value 	the proportion of total variance to account for
   */
  public void setVarianceCovered(double value) {
    m_CoverVariance = value;
    reset();
  }

  /**
   * Gets the proportion of total variance to account for when
   * retaining principal components.
   *
   * @return 		the proportion of variance to account for
   */
  public double getVarianceCovered() {
    return m_CoverVariance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String varianceCoveredTipText() {
    return "Retain enough PC attributes to account for this proportion of variance.";
  }

  /**
   * Sets maximum number of PC attributes to retain.
   *
   * @param value 	the maximum number of attributes
   */
  public void setMaximumAttributes(int value) {
    m_MaxAttributes = value;
    reset();
  }

  /**
   * Gets maximum number of PC attributes to retain.
   *
   * @return 		the maximum number of attributes
   */
  public int getMaximumAttributes() {
    return m_MaxAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maximumAttributesTipText() {
    return "The maximum number of PC attributes to retain.";
  }

  /**
   * Sets maximum number of attribute names.
   *
   * @param value 	the maximum number of attribute names
   */
  public void setMaximumAttributeNames(int value) {
    m_MaxAttributeNames = value;
    reset();
  }

  /**
   * Gets maximum number of attribute names to use.
   *
   * @return 		the maximum number of attribute names
   */
  public int getMaximumAttributeNames() {
    return m_MaxAttributeNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maximumAttributeNamesTipText() {
    return "The maximum number of attribute names to use.";
  }

  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "varianceCovered", m_CoverVariance, "var: ");
    result += QuickInfoHelper.toString(this, "maxAttributes", m_MaxAttributes, ", max attr: ");
    result += QuickInfoHelper.toString(this, "maxAttributeNames", m_MaxAttributeNames, ", max names: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute()  {
    String 	result;
    Instances 	input;
    PCA		pca;

    input = (Instances) m_InputToken.getPayload();
    pca   = new PCA();
    pca.setVariance(m_CoverVariance);
    pca.setMaxAttributes(m_MaxAttributes);
    pca.setMaxAttributeNames(m_MaxAttributeNames);
    result = pca.analyze(input);

    if (result == null)
      m_OutputToken = new Token(new SpreadSheet[]{pca.getLoadings(), pca.getScores()});

    return result;
  }
}
