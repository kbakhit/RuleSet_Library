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
package com.khaledbakhit.api.rslib.dataset;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.interfaces.Writer;

/**
 * DataSetWriter writes DataSetLine Objects into DataSet files.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 22/08/2013
 */
public abstract class DataSetWriter implements Writer<DataSet>  
{
    /**
     * LaunchSetup Object contains input configuration.
     */
    protected LaunchSetup sp;
    /**
     * DataSetWriter constructor.
     * @param sp LaunchSetup Object contains input configuration.
     */
    public DataSetWriter(LaunchSetup sp)
    {
        this.sp= sp;
    }
     /**
     * Get the default DataSetWriter Object.
     * @param sp LaunchSetup Object contains input configuration.
     * @return Default DataSetWriter Object.
     */
    public static DataSetWriter getDefaultDataSetWriter(LaunchSetup sp)
    {
        return new DefaultDataSetWriter(sp);    
    }   
}