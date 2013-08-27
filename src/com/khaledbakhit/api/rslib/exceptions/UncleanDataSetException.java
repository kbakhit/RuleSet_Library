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
package com.khaledbakhit.api.rslib.exceptions;

/**
 * UncleanDataSetException indicates given dataset is of invalid/unsupported format.
 * @author Khaled Bakhit
 * @since 3.0
 * @version 5/09/2011
 */
public class UncleanDataSetException extends InvalidInputException 
{
    private static final long serialVersionUID = 4150920113L;
    /**
     * Creates a new instance of <code>UncleanDataSetException</code> without detail message.
     */
    public UncleanDataSetException() 
    {
        super("unclean DataSet detected.");
    }

    /**
     * Constructs an instance of <code>UncleanDataSetException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UncleanDataSetException(String msg) 
    {
        super(msg);
    }
     /**
     * Constructs an instance of <code>UncleanDataSetException</code>.
     * @param dataset The unclean dataset file.
     */
    public UncleanDataSetException(java.io.File dataset)
    {
        super(dataset.getAbsolutePath()+" is unclean.");
    }
}
