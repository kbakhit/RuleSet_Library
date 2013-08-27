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

import com.khaledbakhit.api.rslib.LaunchSetup;

/**
 * SetupNotConfiguredException occurs when a required variable 
 * within LaunchSetup Object that is null.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 19/06/2013
 */
public class SetupNotConfiguredException extends NullPointerException 
{
    private static final long serialVersionUID = 2190620133L;
    /**
     * Creates a new instance of
     * <code>SetupNotConfiguredException</code> without detail message.
     */
    public SetupNotConfiguredException() 
    {
        super("An important variable within LaunchSetup Object is not defined.");
    }

    /**
     * Constructs an instance of
     * <code>SetupNotConfiguredException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public SetupNotConfiguredException(String msg) 
    {
        super(msg);
    }
    /**
     * Create a <code>SetupNotConfiguredException</code> error.
     * @param variableName Variable name within LaunchSetup Object that is null.
     * @see LaunchSetup
     */
    public static void occur(String variableName)
    {
        throw new SetupNotConfiguredException(variableName+" within LaunchSetup Object is not defined.");
    }
}
