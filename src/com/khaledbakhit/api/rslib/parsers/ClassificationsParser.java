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
import java.util.List;

/**
 * ClassificationsParser parses input classifications file.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 18/08/2013
 */
public abstract class ClassificationsParser implements Parser<List<String>>
{
    /**
     * The default classification parser defined by library.
     */
    private static ClassificationsParser DEFAULT_PARSER;
    /**
     * Get the default ClassificationsParse defined by the library.
     * @return Default ClassificationsParser Object defined by library.
     */
    public static ClassificationsParser getDefaultClassificationsParser()
    {
        if(DEFAULT_PARSER!=null)
            return DEFAULT_PARSER;
        
        DEFAULT_PARSER= new DefaultClassificationsParser(); 
        return DEFAULT_PARSER;
    }
    /**
     * Set the default ClassificationsParser Object.
     * @param parser ClassicationsParser Object to consider default.
     */
    public static void setDefaultClassificationsParser(ClassificationsParser parser)
    {
        DEFAULT_PARSER= parser;
    }
}