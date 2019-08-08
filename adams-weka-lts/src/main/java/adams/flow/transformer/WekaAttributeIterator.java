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
 * WekaAttributeIterator.java
 * Copyright (C) 2011-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.base.BaseRegExp;
import weka.core.Instance;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Iterates through all attributes of a dataset and outputs the names.<br>
 * The attributes can be limited with the range parameter and furthermore with the regular expression applied to the names.<br>
 * Instead of outputting the names, it is also possible to output the 1-based indices.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaAttributeIterator
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the names&#47;indices as an array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-range &lt;adams.core.Range&gt; (property: range)
 * &nbsp;&nbsp;&nbsp;The range of attributes to iterate over; A range is a comma-separated list
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; the following placeholders can be used as well:
 * &nbsp;&nbsp;&nbsp; first, second, third, last_2, last_1, last.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used to further limit the attribute set.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-output-indices &lt;boolean&gt; (property: outputIndices)
 * &nbsp;&nbsp;&nbsp;If set to true, 1-based indices of matches are output instead of names.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaAttributeIterator
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 7689330704841468990L;

  /** the range of attributes to work on. */
  protected Range m_Range;

  /** the regular expression applied to the attribute names. */
  protected BaseRegExp m_RegExp;

  /** whether to output indices instead of the strings. */
  protected boolean m_OutputIndices;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Iterates through all attributes of a dataset and outputs the names.\n"
        + "The attributes can be limited with the range parameter and "
        + "furthermore with the regular expression applied to the names.\n"
        + "Instead of outputting the names, it is also possible to output the "
        + "1-based indices.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "range", "range",
      new Range(Range.ALL));

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "output-indices", "outputIndices",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "range", m_Range);
    value = QuickInfoHelper.toString(this, "regExp", m_RegExp, ", subset: ");
    if (value != null)
      result += value;
    result += QuickInfoHelper.toString(this, "outputIndices", m_OutputIndices, "output indices", ", ");

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, outputs the names/indices as an array rather than one-by-one.";
  }

  /**
   * Sets the range of attributes to operate on.
   *
   * @param value	the range
   */
  public void setRange(Range value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the range of attributes to operate on.
   *
   * @return		the range
   */
  public Range getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The range of attributes to iterate over; " + m_Range.getExample() + ".";
  }

  /**
   * Sets the regular expression for the names.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for the name.
   *
   * @return		the prefix
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used to further limit the attribute set.";
  }

  /**
   * Sets whether to output 1-based indices of matches instead of the names.
   *
   * @param value	true if to output indices
   */
  public void setOutputIndices(boolean value) {
    m_OutputIndices = value;
    reset();
  }

  /**
   * Returns whether to output 1-based indices of matches instead of the names.
   *
   * @return		true if to output indices
   */
  public boolean getOutputIndices() {
    return m_OutputIndices;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputIndicesTipText() {
    return "If set to true, 1-based indices of matches are output instead of names.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, Instance.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instances	data;
    int[]	indices;
    String	name;
    boolean	useRegExp;

    result = null;

    if (m_InputToken.getPayload() instanceof Instance)
      data = ((Instance) m_InputToken.getPayload()).dataset();
    else
      data = ((Instances) m_InputToken.getPayload());

    m_Range.setMax(data.numAttributes());
    indices   = m_Range.getIntIndices();
    useRegExp = !m_RegExp.isEmpty() && !m_RegExp.isMatchAll();
    m_Queue.clear();
    for (int index: indices) {
      name = data.attribute(index).name();
      if (useRegExp)
	if (!m_RegExp.isMatch(name))
	  continue;
      if (m_OutputIndices)
        m_Queue.add("" + (index + 1));
      else
        m_Queue.add(name);
    }

    return result;
  }
}
