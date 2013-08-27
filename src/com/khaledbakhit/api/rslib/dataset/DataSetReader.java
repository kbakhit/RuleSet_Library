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

import com.khaledbakhit.api.rslib.interfaces.Reader;
import com.khaledbakhit.api.rslib.LaunchSetup;
/**
 * DataSetReader reads DataSetLine Objects from DataSet files.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 22/08/2013
 */
public abstract class DataSetReader implements Reader<DataSetLine> 
{
    /**
     * LaunchSetup Object containing input configuration.
     */
    protected LaunchSetup sp;
    
    /**
     * DataSetReader constructor.
     * @param sp LaunchSetup Object containing input configuration.
     */
    public DataSetReader(LaunchSetup sp)
    {
        this.sp= sp;
    }
  
    /**
     * Read the next DataSetLine from input.
     * @param buffer DataSetLine to store new data.
     * @return DataSetLine read or null if input was invalid.
     */
    public abstract DataSetLine getNext(DataSetLine buffer);
    
    /**
     * Get the default DataSetReader Object.
     * @param sp LaunchSetup Object containing input configuration.
     * @return Default DataSetReader Object.
     */
    public static DataSetReader getDefaultDataSetReader(LaunchSetup sp)
    {
        return new DefaultDataSetReader(sp);   
    }
}
