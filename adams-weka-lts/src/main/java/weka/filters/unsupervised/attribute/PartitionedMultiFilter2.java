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
 * PartitionedMultiFilter2.java
 * Copyright (C) 2006-2018 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.filters.unsupervised.attribute;

import adams.core.base.BaseString;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * A filter that applies filters on subsets of
 * attributes and assembles the output into a new dataset. Attributes that are
 * not covered by any of the ranges can be either retained or removed from the
 * output.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start --> Valid options are:
 * <p/>
 *
 * <pre>
 * -D
 *  Turns on output of debugging information.
 * </pre>
 *
 * <pre>
 * -F &lt;classname [options]&gt;
 *  A filter to apply (can be specified multiple times).
 * </pre>
 *
 * <pre>
 * -R &lt;range&gt;
 *  An attribute range (can be specified multiple times).
 *  For each filter a range must be supplied. 'first' and 'last'
 *  are valid indices. 'inv(...)' around the range denotes an
 *  inverted range.
 * </pre>
 *
 * <pre>
 * -U
 *  Flag for leaving unused attributes out of the output, by default
 *  these are included in the filter output.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see weka.filters.StreamableFilter
 */
public class PartitionedMultiFilter2
  extends SimpleBatchFilter {

  /** for serialization. */
  private static final long serialVersionUID = -6293720886005713120L;

  /** The filters. */
  protected Filter m_Filters[] = {
    new AllFilter()
  };

  /** The attribute ranges. */
  protected Range m_Ranges[] = {
    new Range("first-last")
  };

  /** The prefixes. */
  protected BaseString m_Prefixes[] = {
    new BaseString("filtered")
  };

  /** Whether unused attributes are left out of the output. */
  protected boolean m_RemoveUnused = false;

  /** the indices of the unused attributes. */
  protected int[] m_IndicesUnused = new int[0];

  /** temporary filter results when determining output format to avoid
   * duplicate processing of data. */
  protected Instances[] m_Processed;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "A filter that applies filters on subsets of attributes and "
      + "assembles the output into a new dataset. Attributes that are "
      + "not covered by any of the ranges can be either retained or removed "
      + "from the output.\n"
      + "Custom attribute name prefixes can be supplied, by default 'filtered-' is used.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    result.addElement(new Option(
      "\tA filter to apply (can be specified multiple times).", "F", 1,
      "-F <classname [options]>"));

    result.addElement(new Option(
      "\tAn attribute range (can be specified multiple times).\n"
	+ "\tFor each filter a range must be supplied. 'first' and 'last'\n"
	+ "\tare valid indices. 'inv(...)' around the range denotes an\n"
	+ "\tinverted range.", "R", 1, "-R <range>"));

    result.addElement(new Option(
      "\tA prefix for the filtered attributes (can be specified multiple times)."
      + "\t(default: 'filtered')",
      "P", 1, "-P <prefix>"));

    result.addElement(new Option(
      "\tFlag for leaving unused attributes out of the output, by default\n"
	+ "\tthese are included in the filter output.", "U", 0, "-U"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String 		tmpStr;
    String 		classname;
    String[] 		options2;
    List<Filter> 	filters;
    List<Range> 	ranges;
    List<BaseString>	prefixes;
    Range 		range;

    setRemoveUnused(Utils.getFlag("U", options));

    filters = new ArrayList<>();
    while ((tmpStr = Utils.getOption("F", options)).length() != 0) {
      options2 = Utils.splitOptions(tmpStr);
      classname = options2[0];
      options2[0] = "";
      filters.add((Filter) Utils.forName(Filter.class, classname, options2));
    }

    // at least one filter
    if (filters.size() == 0)
      filters.add(new AllFilter());

    setFilters(filters.toArray(new Filter[filters.size()]));

    ranges = new ArrayList<>();
    while ((tmpStr = Utils.getOption("R", options)).length() != 0) {
      if (tmpStr.startsWith("inv(") && tmpStr.endsWith(")")) {
	range = new Range(tmpStr.substring(4, tmpStr.length() - 1));
	range.setInvert(true);
      }
      else {
	range = new Range(tmpStr);
      }
      ranges.add(range);
    }

    // adjust to filters
    if (ranges.size() == 0) {
      for (Object filter: filters)
        ranges.add(new Range("first-last"));
    }

    setRanges(ranges.toArray(new Range[filters.size()]));

    prefixes = new ArrayList<>();
    while ((tmpStr = Utils.getOption("P", options)).length() != 0)
      prefixes.add(new BaseString(tmpStr));

    // adjust to filters
    if (prefixes.size() == 0) {
      for (Object filter: filters)
        prefixes.add(new BaseString("filtered"));
    }

    setPrefixes(prefixes.toArray(new BaseString[filters.size()]));

    // is number of filters the same as ranges?
    checkDimensions();

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    List<String> 	result;
    String 		tmpStr;
    int			i;

    result = new ArrayList<>();

    if (getRemoveUnused()) {
      result.add("-U");
    }

    for (i = 0; i < getFilters().length; i++) {
      result.add("-F");
      result.add(getFilterSpec(getFilter(i)));
    }

    for (i = 0; i < getRanges().length; i++) {
      tmpStr = getRange(i).getRanges();
      if (getRange(i).getInvert()) {
	tmpStr = "inv(" + tmpStr + ")";
      }
      result.add("-R");
      result.add(tmpStr);
    }

    for (i = 0; i < getFilters().length; i++) {
      result.add("-P");
      result.add(getPrefixes()[i].getValue());
    }

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * checks whether the dimensions of filters and ranges fit together.
   *
   * @throws Exception if dimensions differ
   */
  protected void checkDimensions() throws Exception {
    if (getFilters().length != getRanges().length) {
      throw new IllegalArgumentException(
	"Number of filters (= " + getFilters().length + ") "
	  + "and ranges (= " + getRanges().length + ") don't match!");
    }
    if (getFilters().length != getPrefixes().length) {
      throw new IllegalArgumentException(
	"Number of filters (= " + getFilters().length + ") "
	  + "and prefixes (= " + getPrefixes().length + ") don't match!");
    }
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = super.getCapabilities();
    result.enable(Capability.NO_CLASS);

    return result;
  }

  /**
   * tests the data whether the filter can actually handle it.
   *
   * @param instanceInfo the data to test
   * @throws Exception if the test fails
   */
  @Override
  protected void testInputFormat(Instances instanceInfo) throws Exception {
    int		i;
    Instances	inst;
    Range 	range;
    Instances 	subset;

    for (i = 0; i < getRanges().length; i++) {
      inst = new Instances(instanceInfo, 0);
      if (instanceInfo.size() > 0)
	inst.add((Instance) instanceInfo.get(0).copy());
      range = getRanges()[i];
      range.setUpper(instanceInfo.numAttributes() - 1);
      subset = generateSubset(inst, range);
      getFilters()[i].setInputFormat(subset);
    }
  }

  /**
   * Sets whether unused attributes (ones that are not covered by any of the
   * ranges) are removed from the output.
   *
   * @param value if true then the unused attributes get removed
   */
  public void setRemoveUnused(boolean value) {
    m_RemoveUnused = value;
  }

  /**
   * Gets whether unused attributes (ones that are not covered by any of the
   * ranges) are removed from the output.
   *
   * @return true if unused attributes are removed
   */
  public boolean getRemoveUnused() {
    return m_RemoveUnused;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String removeUnusedTipText() {
    return "If true then unused attributes (ones that are not covered by any "
      + "of the ranges) will be removed from the output.";
  }

  /**
   * Sets the list of possible filters to choose from. Also resets the state of
   * the filter (this reset doesn't affect the options).
   *
   * @param filters an array of filters with all options set.
   * @see #reset()
   */
  public void setFilters(Filter[] filters) {
    m_Filters  = filters;
    m_Ranges   = (Range[]) adams.core.Utils.adjustArray(m_Ranges, m_Filters.length, new Range());
    m_Prefixes = (BaseString[]) adams.core.Utils.adjustArray(m_Prefixes, m_Filters.length, new BaseString("filtered"));
    reset();
  }

  /**
   * Gets the list of possible filters to choose from.
   *
   * @return the array of Filters
   */
  public Filter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String filtersTipText() {
    return "The base filters to be used.";
  }

  /**
   * Gets a single filter from the set of available filters.
   *
   * @param index the index of the filter wanted
   * @return the Filter
   */
  public Filter getFilter(int index) {
    return m_Filters[index];
  }

  /**
   * returns the filter classname and the options as one string.
   *
   * @param filter the filter to get the specs for
   * @return the classname plus options
   */
  protected String getFilterSpec(Filter filter) {
    String 	result;

    if (filter == null) {
      result = "";
    }
    else {
      result = filter.getClass().getName()
	+ " "
	+ Utils.joinOptions(filter.getOptions());
    }

    return result;
  }

  /**
   * Sets the list of possible Ranges to choose from. Also resets the state of
   * the Range (this reset doesn't affect the options).
   *
   * @param Ranges an array of Ranges with all options set.
   * @see #reset()
   */
  public void setRanges(Range[] Ranges) {
    m_Ranges   = Ranges;
    m_Prefixes = (BaseString[]) adams.core.Utils.adjustArray(m_Prefixes, m_Ranges.length, new BaseString("filtered"));
    m_Filters  = (Filter[]) adams.core.Utils.adjustArray(m_Filters, m_Filters.length, new AllFilter());
    reset();
  }

  /**
   * Gets the list of possible Ranges to choose from.
   *
   * @return the array of Ranges
   */
  public Range[] getRanges() {
    return m_Ranges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String rangesTipText() {
    return "The attribute ranges to be used; 'inv(...)' denotes an inverted range.";
  }

  /**
   * Sets the list of prefixes to use.
   *
   * @param prefixes an array of prefixes
   * @see #reset()
   */
  public void setPrefixes(BaseString[] prefixes) {
    m_Prefixes = prefixes;
    m_Ranges   = (Range[]) adams.core.Utils.adjustArray(m_Ranges, m_Prefixes.length, new Range());
    m_Filters  = (Filter[]) adams.core.Utils.adjustArray(m_Filters, m_Prefixes.length, new AllFilter());
    reset();
  }

  /**
   * Gets the list of prefixes to use.
   *
   * @return the array of prefixes
   */
  public BaseString[] getPrefixes() {
    return m_Prefixes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String prefixesTipText() {
    return "The prefixes to use; uses 'filtered' if empty.";
  }

  /**
   * Gets a single Range from the set of available Ranges.
   *
   * @param index the index of the Range wanted
   * @return the Range
   */
  public Range getRange(int index) {
    return m_Ranges[index];
  }

  /**
   * Returns whether to allow the determineOutputFormat(Instances) method access
   * to the full dataset rather than just the header.
   *
   * @return whether determineOutputFormat has access to the full input dataset
   */
  public boolean allowAccessToFullInputFormat() {
    return true;
  }

  /**
   * determines the indices of unused attributes (ones that are not covered by
   * any of the range).
   *
   * @param data the data to base the determination on
   * @see #m_IndicesUnused
   */
  protected void determineUnusedIndices(Instances data) {
    TIntList 	indices;
    int 	i;
    int 	n;
    boolean 	covered;

    // traverse all ranges
    indices = new TIntArrayList();
    for (i = 0; i < data.numAttributes(); i++) {
      if (i == data.classIndex())
	continue;

      covered = false;
      for (n = 0; n < getRanges().length; n++) {
	if (getRanges()[n].isInRange(i)) {
	  covered = true;
	  break;
	}
      }

      if (!covered)
	indices.add(i);
    }

    // create array
    m_IndicesUnused = indices.toArray();

    if (getDebug())
      System.out.println("Unused indices: " + Utils.arrayToString(m_IndicesUnused));
  }

  /**
   * generates a subset of the dataset with only the attributes from the range
   * (class is always added if present).
   *
   * @param data the data to work on
   * @param range the range of attribute to use
   * @return the generated subset
   * @throws Exception if creation fails
   */
  protected Instances generateSubset(Instances data, Range range) throws Exception {
    Remove 		filter;
    StringBuilder 	atts;
    Instances 		result;
    int[] 		indices;
    int 		i;

    // determine attributes
    indices = range.getSelection();
    atts    = new StringBuilder();
    for (i = 0; i < indices.length; i++) {
      if (i > 0)
	atts.append(",");
      atts.append("" + (indices[i] + 1));
    }
    if ((data.classIndex() > -1) && (!range.isInRange(data.classIndex())))
      atts.append("," + (data.classIndex() + 1));

    // setup filter
    filter = new Remove();
    filter.setAttributeIndices(atts.toString());
    filter.setInvertSelection(true);
    filter.setInputFormat(data);

    // generate output
    result = Filter.useFilter(data, filter);

    return result;
  }

  /**
   * renames all the attributes in the dataset (excluding the class if present)
   * by adding the prefix to the name.
   *
   * @param data the data to work on
   * @param prefix the prefix for the attributes
   * @return a copy of the data with the attributes renamed
   * @throws Exception if renaming fails
   */
  protected Instances renameAttributes(Instances data, String prefix) throws Exception {
    Instances 			result;
    int 			i;
    ArrayList<Attribute> 	atts;

    // rename attributes
    atts = new ArrayList<>();
    for (i = 0; i < data.numAttributes(); i++) {
      if (i == data.classIndex())
	atts.add((Attribute) data.attribute(i).copy());
      else
	atts.add(data.attribute(i).copy(prefix + data.attribute(i).name()));
    }

    // create new dataset
    result = new Instances(data.relationName(), atts, data.numInstances());
    for (i = 0; i < data.numInstances(); i++)
      result.add((Instance) data.instance(i).copy());

    // set class if present
    if (data.classIndex() > -1)
      result.setClassIndex(data.classIndex());

    return result;
  }

  /**
   * Determines the output format based only on the full input dataset and
   * returns this otherwise null is returned. In case the output format cannot
   * be returned immediately, i.e., immediateOutputFormat() returns false, then
   * this method will be called from batchFinished().
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances 			result;
    int 			i;
    int 			n;
    ArrayList<Attribute> 	atts;
    Attribute 			att;
    String			prefix;

    if (!isFirstBatchDone()) {
      checkDimensions();

      // determine unused indices
      determineUnusedIndices(inputFormat);
      for (i = 0; i < m_Ranges.length; i++)
	m_Ranges[i].setUpper(inputFormat.numAttributes() - 1);

      atts      = new ArrayList<>();
      m_Processed = new Instances[getFilters().length];
      for (i = 0; i < getFilters().length; i++) {
	// generate subset
	m_Processed[i] = generateSubset(inputFormat, getRange(i));
	// set input format
	getFilter(i).setInputFormat(m_Processed[i]);
	m_Processed[i] = Filter.useFilter(m_Processed[i], getFilter(i));

	// rename attributes
	prefix = m_Prefixes[i].getValue();
	if (prefix.trim().isEmpty())
	  prefix = "filtered";
	m_Processed[i] = renameAttributes(m_Processed[i], prefix + "-" + i + "-");

	// add attributes
	for (n = 0; n < m_Processed[i].numAttributes(); n++) {
	  if (n == m_Processed[i].classIndex())
	    continue;
	  atts.add((Attribute) m_Processed[i].attribute(n).copy());
	}
      }

      // add unused attributes
      if (!getRemoveUnused()) {
	for (i = 0; i < m_IndicesUnused.length; i++) {
	  att = inputFormat.attribute(m_IndicesUnused[i]);
	  atts.add(att.copy("unfiltered-" + att.name()));
	}
      }

      // add class if present
      if (inputFormat.classIndex() > -1)
	atts.add((Attribute) inputFormat.classAttribute().copy());

      // generate new dataset
      result = new Instances(inputFormat.relationName(), atts, 0);
      if (inputFormat.classIndex() > -1)
	result.setClassIndex(result.numAttributes() - 1);
    }
    else {
      result = getOutputFormat();
    }

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   * @see #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances 		result;
    int 		i;
    int 		n;
    int 		m;
    int 		index;
    Instances[] 	processed;
    Instance 		inst;
    Instance 		newInst;
    double[] 		values;
    TIntList 		errors;

    // pass data through all filters
    if (m_Processed != null) {
      processed   = m_Processed;
      m_Processed = null;
    }
    else {
      processed = new Instances[getFilters().length];
      for (i = 0; i < getFilters().length; i++) {
	processed[i] = generateSubset(instances, getRange(i));
	if (!isFirstBatchDone()) {
	  getFilter(i).setInputFormat(processed[i]);
	}
	processed[i] = Filter.useFilter(processed[i], getFilter(i));
      }
    }

    result = getOutputFormat();

    // check whether all filters didn't change the number of instances
    errors = new TIntArrayList();
    for (i = 0; i < processed.length; i++) {
      if (processed[i].numInstances() != instances.numInstances())
	errors.add(i);
    }
    if (errors.size() > 0) {
      throw new IllegalStateException(
	"The following filter(s) changed the number of instances: " + errors);
    }

    // assemble data
    for (i = 0; i < instances.numInstances(); i++) {
      inst   = instances.instance(i);
      values = new double[result.numAttributes()];

      // filtered data
      index = 0;
      for (n = 0; n < processed.length; n++) {
	for (m = 0; m < processed[n].numAttributes(); m++) {
	  if (m == processed[n].classIndex())
	    continue;
	  if (processed[n].instance(i).isMissing(m))
	    values[index] = Utils.missingValue();
	  else if (result.attribute(index).isString())
	    values[index] = result.attribute(index).addStringValue(processed[n].instance(i).stringValue(m));
	  else if (result.attribute(index).isRelationValued())
	    values[index] = result.attribute(index).addRelation(processed[n].instance(i).relationalValue(m));
	  else
	    values[index] = processed[n].instance(i).value(m);
	  index++;
	}
      }

      // unused attributes
      if (!getRemoveUnused()) {
	for (n = 0; n < m_IndicesUnused.length; n++) {
	  if (inst.isMissing(m_IndicesUnused[n]))
	    values[index] = Utils.missingValue();
	  else if (result.attribute(index).isString())
	    values[index] = result.attribute(index).addStringValue(inst.stringValue(m_IndicesUnused[n]));
	  else if (result.attribute(index).isRelationValued())
	    values[index] = result.attribute(index).addRelation(inst.relationalValue(m_IndicesUnused[n]));
	  else
	    values[index] = inst.value(m_IndicesUnused[n]);
	  index++;
	}
      }

      // class
      if (instances.classIndex() > -1) {
	index = values.length - 1;
	if (inst.classIsMissing())
	  values[index] = Utils.missingValue();
	else if (result.attribute(index).isString())
	  values[index] = result.attribute(index).addStringValue(inst.stringValue(instances.classIndex()));
	else if (result.attribute(index).isRelationValued())
	  values[index] = result.attribute(index).addRelation(inst.relationalValue(instances.classIndex()));
	else
	  values[index] = inst.value(instances.classIndex());
      }

      // generate and add instance
      if (inst instanceof SparseInstance)
	newInst = new SparseInstance(instances.instance(i).weight(), values);
      else
	newInst = new DenseInstance(instances.instance(i).weight(), values);
      result.add(newInst);
    }

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10215 $");
  }

  /**
   * Main method for executing this class.
   *
   * @param args should contain arguments for the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new PartitionedMultiFilter2(), args);
  }
}
