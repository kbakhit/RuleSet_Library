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

import java.io.Serializable;

/**
 * DataSetLine represents a case line that makes up a DataSet file.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 20/08/2013
 */
public class DataSetLine implements Serializable
{   
    private static final long serialVersionUID = 220082013L;
    /**
     * Classification present at this DataSetLine Object.
     */
    private String classification;
    /**
     * Metric values present at this DataSetLine Object.
     */
    private String[] metrics;
 
    /**
     * DataSetLine constructor.
     */
    public DataSetLine()
    {}
    /**
     * DataSetLine constructor.
     * @param classification Classification value.
     * @param metrics Metric values.
     */
    public DataSetLine(String classification, String[] metrics)
    {
        this.classification= classification;
        this.metrics= metrics;
    }
    /**
     * Get Classification present at this DataSetLine Object.
     * @return Classification present at this DataSetLine Object. 
     */
    public String getClassification()
    {
        return classification;
    }
    /**
     * Get Metric values present at this DataSetLine Object.
     * @return Metric values present at this DataSetLine Object.
     */
    public String[] getMetrics()
    {
        return metrics;
    }
    /**
     * Set Classification present at this DataSetLine Object.
     * @param classification New Classification present at this DataSetLine Object.
     */
    public void setClassification(String classification)
    {
        this.classification= classification;
    }
    /**
     * Set Metric values present at this DataSetLine Object.
     * @param metrics New Metric values present at this DataSetLine Object.
     */
    public void setMetrics(String[] metrics)
    {
        this.metrics= metrics;
    }
    @Override
    public String toString()
    {
        StringBuilder sn= new StringBuilder();
        for(String metric: metrics)
            sn.append(metric).append(',');
        sn.append(classification);
        return sn.toString();
    }
}