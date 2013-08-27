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
import com.khaledbakhit.api.rslib.utils.TextWriter;
import java.io.IOException;

/**
 * Default Library implementation of DataSetWriter abstract class.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 22/08/2013
 */
public class DefaultDataSetWriter extends DataSetWriter
{
    /**
     * TextWriter Object to write output into file.
     */
    private TextWriter txt;
    /**
     * DefaultDataSetWriter constructor.
     * @param sp LaunchSetup Object contains input configuration.
     */
    public DefaultDataSetWriter(LaunchSetup sp)
    {
        super(sp);
    }

    @Override
    public void write(DataSet object)throws IOException
    {
        close();
        txt= new TextWriter(object.getFile());
        for(DataSetLine line: object.getDataSetLines())
            txt.out.println(line);
    }

    public Writer<DataSet> newInstance() 
    {
        return new DefaultDataSetWriter(sp);
      
    }

    @Override
    public void close() throws IOException 
    {
      if(txt!=null)   
          txt.close();
    }
    
    
}
