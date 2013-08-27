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
package com.khaledbakhit.api.rslib.interfaces;

import com.khaledbakhit.api.rslib.exceptions.InputParseException;
import java.io.Closeable;
import java.io.InputStream;


/**
 * Parser Interface defines building blocks for parsers.
 * @author Khaled Bakhit.
 * @since 4.0
 * @version 18/08/2013
 */
public interface Parser<E> extends Closeable
{
    /**
     * Perform the parsing operation.
     * @inputstream InputStream to parse.
     * @throws InputParseException InputParseException Unable to parse input file.
     */
    public void parse(InputStream inputstream) throws InputParseException;
    /**
     * Get the parsed data.
     * @return Parsed data following the parsing operation.
     * @see Parser#parse() 
     */
    public E getParsedData();
    /**
     * Create a new instance of Parser.
     * @return New instance of Parser.
     */
    public Parser<E> newInstance();
}
