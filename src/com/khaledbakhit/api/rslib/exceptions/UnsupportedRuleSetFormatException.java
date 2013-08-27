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
 * UnsupportedRuleSetFormatException indicates parse error of input RuleSet.
 * @author Khaled Bakhit
 * @since 3.0
 * @version 15/09/2011
 */
public class UnsupportedRuleSetFormatException extends InvalidInputException 
{
    private static final long serialVersionUID = 5150920113L;

    /**
     * Creates a new instance of <code>UnsupportedRuleSetFormatException</code> without detail message.
     */
    public UnsupportedRuleSetFormatException() 
    {
        super("Detected RuleSets of unsupported format.");
    }

    /**
     * Constructs an instance of <code>UnsupportedRuleSetFormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnsupportedRuleSetFormatException(String msg) 
    {
        super(msg);
    }
}
