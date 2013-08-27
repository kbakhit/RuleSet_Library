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
import java.io.InputStream;

/**
 * Reader interface defines building blocks for library related readers.
 * 
 * @author Khaled Bakhit
 * @since 4.0
 * @version 22/08/2013
 */
public interface Reader<E> extends Closeable
{    
    /**
     * Set the reader's InputStream.
     * @param inputStream InputStream to open and read from.
     */
    public void open(InputStream inputStream);
    /**
     * Check if the reader has more to read.
     * @return true if has more, false otherwise.
     */
    public boolean hasNext();
    /**
     * Read the next E from input.
     * @return E read or null if input was invalid.
     */
    public abstract E getNext();
    /**
     * Create a new instance of Reader.
     * @return New instance of Reader.
     */
    public Reader<E> newInstance();
    
}
