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

import com.khaledbakhit.api.rslib.Program;

/**
 * ClassificationsNotFoundException is thrown when classes file is not found.
 * @author Khaled Bakhit
 * @since 3.0
 * @version 15/09/2011
 */
public class ClassificationsNotFoundException extends InvalidInputException 
{
    private static final long serialVersionUID = 1150920113L;
    /**
     * Creates a new instance of <code>ClassificationsNotFoundException</code> without detail message.
     */
    public ClassificationsNotFoundException() 
    {
        super(Program.getInstance().getLaunchSetup().input_class_file+" is not found.");
    }

    /**
     * Constructs an instance of <code>ClassificationssNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ClassificationsNotFoundException(String msg) 
    {
        super(msg);
    }
}
