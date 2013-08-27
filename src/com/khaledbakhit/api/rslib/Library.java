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
package com.khaledbakhit.api.rslib;

/**
 * Library holds information about RuleSet library.
 * 
 * @author Khaled Bakhit
 * @since 1.0
 * @version 19/05/2011
 */
public class Library 
{
    /**
     * Name of library. 
     */
    public final static String NAME="RuleSet Library";
    /**
     * Author of the library.
     */
    public final static String AUTHOR="Khaled Bakhit";
    
    /**
     * Email address of Author of the library. 
     */
    public final static String AUTHOR_EMAIL= "kb@khaled-bakhit.com";
    /**
     * Current library's version.
     */
    public final static float VERSION= 4.0f;
    /**
     * Last Update date of the library. 
     */
    public final static String LAST_UPDATE_DATE="August 26, 2013";
    
    /**
     * Enum representing output type required.
     * Indexes match those defined by State Analyzer 2.0
     * @since 2.0
     */
    public enum OUTPUT_TYPE 
    {
        /**
         * Set all output types as text files.
         * @since 2.0
         */
        TEXT,
        /**
         * Set all output types as pure Excel files.
         * @since 3.0
         */
        XLS,
        /**
         * Set all output types as CSV files.
         * @since 2.0
         */
        CSV  
    };
}