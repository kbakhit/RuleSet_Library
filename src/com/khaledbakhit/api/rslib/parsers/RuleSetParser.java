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
package com.khaledbakhit.api.rslib.parsers;

import com.khaledbakhit.api.rslib.interfaces.Parser;
import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.ruleset.RuleSet;
import java.util.LinkedList;

/**
 * RuleSetParser parses input RuleSet files.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 18/08/2013
 */
public abstract class RuleSetParser implements Parser<LinkedList<RuleSet>> 
{
    /**
     * LaunchSetup Object contains input configuration.
     */
    protected LaunchSetup sp;
    /**
     * The default RuleSet parser defined by library.
     */
    private static RuleSetParser DEFAULT_PARSER;
    /**
     * RuleSetParser constructor.
     * @param sp LaunchSetup Object containing input configuration.
     */
    public RuleSetParser(LaunchSetup sp)
    {
        this.sp= sp;
    }
    
    /**
     * Get the default RuleSetParser defined by the library.
     * @param sp LaunchSetup Object containing input configuration.
     * @return Default RuleSetParser Object defined by library.
     */
    public static RuleSetParser getDefaultRuleSetParser(LaunchSetup sp)
    {
        if(DEFAULT_PARSER!=null)
            return DEFAULT_PARSER;
        
        DEFAULT_PARSER= new DefaultRuleSetParser(sp);
        return DEFAULT_PARSER;
    }
    /**
     * Set the default RuleSetParser Object.
     * @param parser RuleSetParser Object to consider default.
     */
    public static void setDefaultMetricsParser(RuleSetParser parser)
    {
        DEFAULT_PARSER= parser;
    }
}
