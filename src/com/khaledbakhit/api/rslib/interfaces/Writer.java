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

import java.io.Closeable;
import java.io.IOException;

/**
 * Writer interface defines building blocks for library related writers.
 * 
 * @author Khaled Bakhit
 * @since 4.0
 * @version 22/08/2013
 */
public interface Writer<E> extends Closeable 
{
    /**
     * Write the given E Object.
     * @param object E to write.
     * @throws IOException Unable to write Object.
     */
    public void write(E object) throws IOException;    
    /**
     * Create a new instance of Writer.
     * @return New instance of Writer.
     */
    public Writer<E> newInstance(); 
}