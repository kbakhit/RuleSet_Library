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

import java.io.File;

/**
 * InputParseException indicates parse failure of given input.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 19/06/2013
 */
public class InputParseException extends InvalidInputException 
{
    private static final long serialVersionUID = 1190620133L;

    /**
     * Creates a new instance of
     * <code>InputParseException</code> without detail message.
     */
    public InputParseException() 
    {
        super("Failed to parse input file.");
    }

    /**
     * Constructs an instance of
     * <code>InputParseException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InputParseException(String msg) {
        super(msg);
    }
      /**
     * Constructs an instance of <code>InputParseException</code>.
     *
     * @param inputFile input file that caused the exception.
     */
    public InputParseException(File inputFile)      
    {
        super("Failed to parse "+inputFile.getAbsolutePath()+".");
    }
}
