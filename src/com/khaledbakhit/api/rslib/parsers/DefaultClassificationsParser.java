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
import com.khaledbakhit.api.rslib.exceptions.InputParseException;
import com.khaledbakhit.api.rslib.utils.Debugger;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Default library's implementation of the ClassificationParser abstract class.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 18/08/2013
 */
public class DefaultClassificationsParser extends ClassificationsParser 
{
    /**
     * Scanner Object to read input classification file.
     */
    private Scanner scan;
    /**
     * List containing parsed data.
     */
    private LinkedList<String> parsedData;

    @Override
    public void parse(InputStream inputstream) throws InputParseException {
        try {
            if (parsedData == null) {
                parsedData = new LinkedList<String>();
            } else {
                parsedData.clear();
            }
            scan = new Scanner(inputstream);
            String attribute;

            while (scan.hasNextLine()) {
                attribute = scan.nextLine().trim();
                if (attribute.length() > 0) {
                    parsedData.add(attribute);
                }

            }
        } catch (Exception e) {
            Debugger.printlnError(e);
            throw new InputParseException(e.getMessage());
        }
    }

    @Override
    public List<String> getParsedData() 
    {
        return parsedData;
    }
    @Override
    public Parser<List<String>> newInstance() 
    {
        return new DefaultClassificationsParser();
    }
    
    @Override
    public void close() throws IOException 
    {
       if(scan!=null)
           scan.close();
    }

}
