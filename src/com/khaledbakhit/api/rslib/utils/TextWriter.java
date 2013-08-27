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
package com.khaledbakhit.api.rslib.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TextWriter helps writing text based files.
 * 
 * @author Khaled Bakhit
 * @since 2.0
 * @version 29/08/2011
 */
public class TextWriter implements java.io.Closeable 
{
    
    private FileWriter fw;
    private BufferedWriter bw;
    /**
     * PrintWriter Object that writes messages to output file.
     */
    public PrintWriter out;

    /**
     * TextWriter constructor.
     * @param name Output file name.
     * @throws IOException Unable to locate path. 
     */
    public TextWriter(String name) throws IOException
    {
        this(new File(name));
    }
    /**
     * TextWriter constructor.
     * @param dest Destination File.
     * @throws IOException Unable to locate destination file.
     */
    public TextWriter(File dest) throws IOException
    {
        fw= new FileWriter(dest);
        bw= new BufferedWriter(fw);
        out= new PrintWriter(bw);
    }
    
    @Override
    public void close() throws IOException
    {
        out.flush();
        out.close();
        bw.close();
        fw.close();
    }
    
    
}
