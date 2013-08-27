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
import com.khaledbakhit.api.rslib.StartUp;
import com.khaledbakhit.api.rslib.exceptions.InvalidInputException;
import com.khaledbakhit.api.rslib.utils.Debugger;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Default Library implementation of DataSetReader abstract class.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 22/08/2013
 */
public class DefaultDataSetReader extends DataSetReader 
{
    /**
     * Scanner Object to read input dataset files of default format.
     */
    private Scanner scan;
    /**
     * DefaultDataSetReader constructor.
     * @param sp LaunchSetup Object containing input configuration.
     */
    public DefaultDataSetReader(LaunchSetup sp) 
    {
        super(sp);
    }

    @Override
    public void open(InputStream inputStream) 
    {
        close();
        scan = new Scanner(inputStream);
    }

    @Override
    public boolean hasNext() {
        return scan != null && scan.hasNextLine();
    }

    @Override
    public DataSetLine getNext(DataSetLine buffer) {
        if (scan == null) {
            throw new NullPointerException("Did not open an input for this DataSetReader.");
        }

        String line = scan.nextLine();

        if (line.length() == 0)
            return null; //Empty line
        
        if (buffer == null) 
            buffer = new DataSetLine();
        

        line = line.trim();
        String[] metrics = buffer.getMetrics();


        if (metrics == null) {
            try {
                metrics = new String[StartUp.getMetricList(sp).size()];
            } catch (InvalidInputException ex) {
                Debugger.printlnError("Unable to determine number of attributes!");
                Debugger.printlnError(ex);
                throw new RuntimeException(ex.getMessage());
            }
        }
        String[] split= line.split(",");

        try 
        {
            if(split.length != metrics.length + 1)
                throw new Exception();
           
            for(int i=0; i< metrics.length; i++)
                metrics[i]= split[i].trim();
           
            String classification= split[split.length-1].trim();
            
            buffer.setClassification(classification);
            buffer.setMetrics(metrics);
            return buffer;

        } catch (Exception e) 
        {//DataSetLine must be invalid--> return line
            Debugger.printlnWarning(line + " is an invalid DataSet line.");
            return null;
        }
    }

    @Override
    public void close() {
        if (scan != null) {
            scan.close();
        }
    }

    @Override
    public DataSetReader newInstance() {
        return new DefaultDataSetReader(sp);

    }
    /**
     * Get next DataSetLine Object.
     * @return Next DataSetLine Object or null if not found.
     */
    public DataSetLine getNext() 
    {
        return getNext(new DataSetLine());
    }
}
