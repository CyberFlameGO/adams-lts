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
 * WekaInstancesExporter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectexport;

import adams.core.Utils;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSink;

import java.io.File;

/**
 * Exports Weka Instances/Instance objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesExporter
  extends AbstractObjectExporter {

  private static final long serialVersionUID = 4899389310274830738L;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public String getFormatDescription() {
    return "ARFF files";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public String[] getFormatExtensions() {
    return new ArffSaver().getFileExtensions();
  }

  /**
   * Checks whether the exporter can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the exporter can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return (ClassLocator.isSubclass(Instances.class, cls))
      || (ClassLocator.hasInterface(Instance.class, cls));
  }

  /**
   * Performs the actual export.
   *
   * @param obj		the object to export
   * @param file	the file to export to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExport(Object obj, File file) {
    Instances		data;
    Instance		inst;

    try {
      if (obj instanceof Instances) {
	DataSink.write(file.getAbsolutePath(), (Instances) obj);
	return null;
      }
      else {
	inst = (Instance) obj;
	if (inst.dataset() != null) {
	  data = new Instances(inst.dataset());
	  data.add((Instance) inst.copy());
	  DataSink.write(file.getAbsolutePath(), data);
	  return null;
	}
	else {
	  return "Instance has no dataset associated, cannot export as ARFF!";
	}
      }
    }
    catch (Exception e) {
      return "Failed to write Instances to '" + file + "'!\n" + Utils.throwableToString(e);
    }
  }
}
