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
package com.khaledbakhit.api.rslib.ruleset;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.interfaces.Writer;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.parsers.DefaultRuleSetParser;
import com.khaledbakhit.api.rslib.utils.TextWriter;
import java.io.IOException;

/**
 * DefaultRuleSetWriter is the default implementation of the RuleSetWriter
 * abstract class.<br/>
 * 
 * It outputs RuleSets in the default form that can be parsed by a 
 * {@link DefaultRuleSetParser} class instance.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 23/08/2013
 */
public class DefaultRuleSetWriter extends RuleSetWriter 
{
    /**
     * TextWriter Object to write output file.
     */
    private TextWriter txt;
     /**
     * DefaultRuleSetWriter constructor.
     * @param sp LaunchSetup Object contains input configuration.
     */
    public DefaultRuleSetWriter(LaunchSetup sp)
    {
        super(sp);
    }

    @Override
    public void write(RuleSet rs) throws IOException 
    {
        close();
        if(sp.ruleset_output_definition_dir==null)
            SetupNotConfiguredException.occur("ruleset_output_definition_dir");
        txt= new TextWriter(sp.ruleset_output_definition_dir+"/"+rs.getParent().getName());
    
        txt.out.println("------------------");
        txt.out.println("Processing tree 0");
        txt.out.println("Final rules from tree 0:");

        int i= -1;
        for(Rule r: rs.getRules())
        {
            i++;
            txt.out.println("Rule " + i + ": ");
            for(Condition cond: r.getConditions(false))
                txt.out.println(cond.metric + " " + cond.operator + " " + cond.value);
            
            txt.out.println("->  class " + r.getStringClassification() + "  [100.0%]");
        }
       
        txt.out.println("Default class: " + rs.getDefaultStringCond());
    }

    @Override
    public Writer<RuleSet> newInstance() 
    {
       return new DefaultRuleSetWriter(sp);
    }
    @Override
    public void close() throws IOException 
    {
        if(txt!=null)
            txt.close();
    } 
}