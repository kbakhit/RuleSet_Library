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

import com.khaledbakhit.api.rslib.calc.Function;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import java.io.*;
import java.util.LinkedList;
/**
 * FormulaImporter imports custom Functions into built-in Function set.
 * @author Khaled Bakhit
 * @since 3.1
 * @version 26/08/2013
 */
public class FormulaImporter 
{
    /**
     * LinkedList object containing names of imported Formulas. 
     */
    private static LinkedList<String> importedFormulas= new LinkedList<String>();
    /**
     * Import custom Formula Objects into built-in Function set.
     */
    public static void importFormulas()
    {
        importFormulas(Program.getInstance().getLaunchSetup());
    }
    /**
     * Import custom Formula Objects into built-in Function set.
     * @param sp LaunchSetup Object containing input configuration.
     * @since 4.0
     */
    public static void importFormulas(LaunchSetup sp)
    {
        if(sp.home_dir == null)
            SetupNotConfiguredException.occur("home_dir");
        if(sp.input_formulas_dir == null)
            SetupNotConfiguredException.occur("input_formulas_dir");
        
        File formulaDir= new File(sp.input_formulas_dir);
        File[] formulaJarFiles= formulaDir.listFiles();
        for(File jarFile: formulaJarFiles)
            if(!isAcceptable(jarFile))
                continue;
            else if(Function.register(jarFile)!= null)
                importedFormulas.add(jarFile.getName());
    }
    /**
     * Check if File is acceptable to import. 
     * @param formula File to import. Must be a jar File.
     * @return true if acceptable, false otherwise.
     * @since 3.1
     */
    private static boolean isAcceptable(File formula)
    {
        String extension= formula.getName().substring(formula.getName().lastIndexOf('.'));
        if(!extension.equalsIgnoreCase(".jar"))
            return false; 
        return !importedFormulas.contains(formula.getName());
    }
}
