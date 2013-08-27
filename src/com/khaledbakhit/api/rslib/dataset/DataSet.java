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
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * DataSet Object represents a DataSet file containing cases.
 * Dealing with DataSet input as Files has low memory consumption but requires
 * more processing time every time you read from disk.
 * However, dealing with DataSet input as DataSet Object (store in memory) sharply
 * decreases processing time (read from main memory rather than disk) but creates 
 * a much bigger foot print in memory.
 * 
 * @author Khaled Bakhit
 * @since 4.0
 * @version 20/08/2013
 */
public class DataSet implements Serializable
{
    private static final long serialVersionUID = 120082013L;
    /**
     * File containing the DataSet cases.
     */
    private File datasetFile;
    /**
     * LinkedList Object containing DataSetLine Objects extracted from DataSet File.
     */
    private LinkedList<DataSetLine> lines;
    
    /**
     * DataSet constructor.
     */
    public DataSet()
    {
        lines= new LinkedList<DataSetLine>();
    }
     
    /**
     * DataSet constructor. It will extract DataSetLines automatically.
     * @param datasetFile File containing the DataSet cases.
     * @param sp LaunchSetup Object containing input configuration.
     * @throws FileNotFoundException Unable to locate DataSet file.
     * @throws IOException Unable to read DataSet file.
     * @see LaunchSetup#dataset_reader
     */
    public DataSet(File datasetFile, LaunchSetup sp) throws FileNotFoundException, IOException
    {
        this();
        this.datasetFile= datasetFile;
        if(sp.dataset_reader==null)
            SetupNotConfiguredException.occur("dataset_reader");
        extract(datasetFile, (DataSetReader) sp.dataset_reader.newInstance());
    }
    /**
     * Add a DataSetLine into this DataSet Object.
     * @param line DataSetLine Object to add.
     */
    public void addDataSetLine(DataSetLine line)
    {
        lines.add(line);
    }
    /**
     * Get List containing all DataSetLine Objects that make up this DataSet.
     * @return List containing all DataSetLine Objects.
     */
    public List<DataSetLine> getDataSetLines()
    {
        return lines;
    }
    /**
     * Get iterator over all DataSetLine Objects that make up this DataSet.
     * @return Iterator containing all DataSetLine Objects.
     */
    public Iterator<DataSetLine> getDataSetLinesIterator()
    {
        return lines.iterator();
    }
    /**
     * Get the File from which this DataSet is parsed from.
     * @return File from which this DataSet is parsed from.
     */
    public File getFile()
    {
        return datasetFile;
    }
    /**
     * Set the parent DataSet File. Will not extract automatically. 
     * Call {@link #extract(com.khaledbakhit.api.rslib.dataset.DataSetReader)} to
     * extract DataSetLine Objects from the new File.
     * @param datasetFile Parent File for this DataSet Object.
     */
    public void setFile(File datasetFile)
    {
        this.datasetFile= datasetFile;
    }
    /**
     * Extract all DataSetLines from parent DataSet File.
     * @param reader DataSetReader Object to read the DataSet File.
     * @throws FileNotFoundException Unable to locate DataSet File.
     * @throws IOException Unable to read from DataSet File.
     * @see #getFile() 
     */
    public final void extract(DataSetReader reader) throws FileNotFoundException, IOException
    {
        extract(datasetFile, reader);
    }
    /**
     * Extract all DataSetLines from given DataSet File.
     * @param datasetFile File containing DataSet cases.
     * @param reader DataSetReader Object to read the DataSet File.
     * @throws FileNotFoundException Unable to locate DataSet File.
     * @throws IOException Unable to read from DataSet File.
     */
    public final void extract(File datasetFile, DataSetReader reader) throws FileNotFoundException, IOException
    {
        this.datasetFile= datasetFile;
        lines.clear();
        reader.open(new FileInputStream(datasetFile));
        while(reader.hasNext())
            lines.add(reader.getNext());
        reader.close();
    }
}