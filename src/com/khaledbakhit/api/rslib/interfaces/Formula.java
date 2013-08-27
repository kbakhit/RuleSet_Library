/**
 * RuleSet Library
 * Copyright (C) 2013  Khaled Bakhit
 * 
 * This file is part of RuleSet Library.
 * 
 * RuleSet Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * RuleSet Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with RuleSet Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.khaledbakhit.api.rslib.interfaces;

import com.khaledbakhit.api.rslib.LaunchSetup;

/**
 * Formula interface provides a foundation for integrating user defined functions
 * with the system.
 * Instructions to implementing this interface and deploying your custom Formula:
 * - Your class must implement the Formula interface.
 * - Pack your class into a proper JAR file
 * - Rename the JAR file to path pointing to your class.
 *   Name should look like: Package Name + Class Name + .jar
 * - Place your JAR file into the formulas Folder {@link LaunchSetup#input_formulas_dir}. 
 * 
 * @author Khaled Bakhit
 * @since 3.1
 * @version 18/10/2011
 */
public interface Formula
{   
    /**
     * Get the name of this Formula. 
     * @return Name of this Formula.
     */
    public String getName();
    /**
     * Perform the calculations involved with this Formula. 
     * @return result of calculations performed.
     */
    public double performCalculation();
    
    /**
     * Perform the calculations involved with this Formula. 
     * @param matrix confusion matrix provided by RuleSet Object.
     * @return result of calculations performed.
     */
    public double performCalculation(int[][] matrix);
    /**
     * Check if calculations require a confusion matrix. 
     * @return true if requires matrix, false otherwise.
     */
    public boolean requiresMatrix();
}
