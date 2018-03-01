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
 * DefaultWekaExperimentIO.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.io;

import adams.core.Utils;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.runner.AbstractExperimentRunner;
import adams.gui.tools.wekamultiexperimenter.runner.DefaultWekaExperimentRunner;
import weka.experiment.Experiment;
import weka.experiment.ExtExperiment;
import weka.experiment.RemoteExperiment;

import java.io.File;
import java.util.logging.Level;

/**
 * Default IO handler for experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultWekaExperimentIO
  extends AbstractWekaExperimentIO<Experiment> {

  /** for serialization. */
  private static final long serialVersionUID = -7678768486122004558L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Handles native Weka experiments.\n"
        + "Supported file extensions:\n"
        + "- read: " + Utils.flatten(getSupportedFileExtensions(true), ",") + "\n"
        + "- write: " + Utils.flatten(getSupportedFileExtensions(false), ",");
  }

  /**
   * Creates a new experiment.
   * 
   * @return		the generated experiment, null if failed
   */
  @Override
  public Experiment create() {
    return new ExtExperiment();
  }

  /**
   * Loads an experiment.
   * 
   * @param file	the file to load
   * @return		the experiment, null if failed to load
   */
  @Override
  public Experiment load(File file) {
    Experiment	result;
    try {
      result = Experiment.read(file.getAbsolutePath());
      if (result instanceof RemoteExperiment)
	result = ((RemoteExperiment) result).getBaseExperiment();
      else
        result = new ExtExperiment(result);
      return result;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load experiment from " + file + "!", e);
      return null;
    }
  }

  /**
   * Saves an experiment.
   * 
   * @param exp		the experiment to save
   * @param file	the file to save to
   * @return		false if failed to save
   */
  @Override
  public boolean save(Experiment exp, File file) {
    try {
      Experiment.write(file.getAbsolutePath(), exp);
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write experiment to " + file + "!", e);
      return false;
    }
  }
  
  /**
   * Creates an experiment runner thread object.
   * 
   * @param owner	the owning experimenter
   * @return		the runner
   * @throws Exception	if failed to instantiate runner
   */
  @Override
  public AbstractExperimentRunner createRunner(ExperimenterPanel owner) throws Exception {
    return new DefaultWekaExperimentRunner(owner);
  }
}
