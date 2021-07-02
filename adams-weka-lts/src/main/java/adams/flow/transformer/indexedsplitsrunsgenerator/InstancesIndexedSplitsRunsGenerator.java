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
 * InstancesIndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

/**
 * Indicator interface for generators that process Instances objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface InstancesIndexedSplitsRunsGenerator
  extends IndexedSplitsRunsGenerator {

  public static final String DATASET_NUMATTRIBUTES = "dataset.num_attributes";

  public static final String DATASET_NUMINSTANCES = "dataset.num_instances";

  public static final String DATASET_NAME = "dataset.name";

  public static final String PREFIX_DATASET_ATTRIBUTE = "dataset.attribute.";

  public static final String SUFFIX_NAME = ".name";

  public static final String SUFFIX_TYPE = ".type";
}
