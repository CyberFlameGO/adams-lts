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
 * WekaOptionHandlerHelpGenerator.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.help;

import adams.gui.core.ConsolePanel;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.core.Capabilities;
import weka.core.CapabilitiesHandler;
import weka.core.MultiInstanceCapabilitiesHandler;
import weka.core.OptionMetadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Help generator for {@link weka.core.OptionHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaOptionHandlerHelpGenerator
  extends AbstractHelpGenerator {

  /**
   * Returns whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(weka.core.OptionHandler.class, cls);
  }

  /**
   * Returns whether the generated help is HTML or plain text.
   *
   * @param cls		the class to generate the help for
   * @return		true if HTML
   */
  @Override
  public boolean isHtml(Class cls) {
    return true;
  }

  /**
   * returns a comma-separated list of all the capabilities.
   *
   * @param c		the capabilities to get a string representation from
   * @return		the string describing the capabilities
   */
  protected String listCapabilities(Capabilities c) {
    String	result;
    Iterator iter;

    result = "";
    iter   = c.capabilities();
    while (iter.hasNext()) {
      if (result.length() != 0)
	result += ", ";
      result += iter.next().toString();
    }

    return result;
  }

  /**
   * generates a string from the capapbilities, suitable to add to the help
   * text.
   *
   * @param title	the title for the capabilities
   * @param c		the capabilities
   * @return		a string describing the capabilities
   */
  protected String addCapabilities(String title, Capabilities c) {
    String		result;
    String		caps;

    result = title + "<br>";

    // class
    caps = listCapabilities(c.getClassCapabilities());
    if (caps.length() != 0) {
      result += "<i>Class</i> - ";
      result += caps;
      result += "<br><br>";
    }

    // attribute
    caps = listCapabilities(c.getAttributeCapabilities());
    if (caps.length() != 0) {
      result += "<i>Attributes</i> - ";
      result += caps;
      result += "<br><br>";
    }

    // other capabilities
    caps = listCapabilities(c.getOtherCapabilities());
    if (caps.length() != 0) {
      result += "<i>Other</i> - ";
      result += caps;
      result += "<br><br>";
    }

    // additional stuff
    result += "<i>Additional</i><br>";
    result += "min # of instances: " + c.getMinimumNumberInstances() + "<br>";
    result += "<br>";

    return result;
  }

  /**
   * Generates and returns the help for the specified class.
   *
   * @param cls		the class to generate the help for
   * @return		the help, null if failed to produce
   */
  @Override
  public String generate(Class cls) {
    StringBuilder 	result;
    Object		obj;
    BeanInfo 		bi;
    MethodDescriptor[] 	methods;
    Object[] 		args;
    boolean 		firstTip;
    StringBuilder 	options;
    String 		name;
    Method 		meth;
    OptionMetadata 	meta;
    String 		tempTip;
    String 		globalInfo;

    result = null;

    try {
      obj = cls.newInstance();
    }
    catch (Exception ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Failed to instantiate class: " + cls.getName(), ex);
      return null;
    }

    // get methods
    try {
      bi      = Introspector.getBeanInfo(cls);
      methods = bi.getMethodDescriptors();
    }
    catch (IntrospectionException ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Couldn't introspect class: " + cls.getName(), ex);
      return null;
    }

    // Look for a globalInfo method that returns a string
    // describing the target
    args     = new Object[]{};
    firstTip = true;
    options  = new StringBuilder();
    for (MethodDescriptor method : methods) {
      name = method.getDisplayName();
      meth = method.getMethod();
      meta = meth.getAnnotation(OptionMetadata.class);

      if (name.endsWith("TipText") || meta != null) {
	if (meth.getReturnType().equals(String.class) || meta != null) {
	  try {
	    tempTip = meta != null ? meta.description() : (String) (meth.invoke(obj, args));
	    name = meta != null ? meta.displayName() : name;

	    if (firstTip) {
	      options.append("<b>Options</b><br>");
	      firstTip = false;
	    }
	    tempTip = tempTip.replace("<html>", "").replace("</html>", "").replace("<p>", "<br><br>");
	    options.append("<i>").append(name.replace("TipText", "")).append("</i> - ");
	    options.append(tempTip).append("<br><br>");

	  }
	  catch (Exception ex) {
	    // ignored
	  }
	}
      }

      if (name.equals("globalInfo")) {
	if (meth.getReturnType().equals(String.class)) {
	  try {
	    globalInfo = (String) (meth.invoke(obj, args));
	    result = new StringBuilder("<b>Name</b><br>");
	    result.append(cls.getName()).append("<br><br>");
	    result.append("<b>Synopsis</b><br>").append(globalInfo).append("<br><br>");

	    if (obj instanceof CapabilitiesHandler) {
	      result.append(addCapabilities(
		"<b>Capabilities</b>",
		((CapabilitiesHandler) obj).getCapabilities()));
	      if (obj instanceof MultiInstanceCapabilitiesHandler) {
		result.append(addCapabilities(
		  "<b>Multi-instance Capabilities</b>",
		  ((MultiInstanceCapabilitiesHandler) obj).getMultiInstanceCapabilities()));
	      }
	    }
	  }
	  catch (Exception ex) {
	    // ignored
	  }
	}
      }
    }

    if (result != null)
      result.append(options.toString());

    if (result != null)
      return result.toString();
    else
      return null;
  }
}
