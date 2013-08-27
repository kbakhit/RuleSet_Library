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
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.StartUp;
import com.khaledbakhit.api.rslib.exceptions.InvalidInputException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;

/**
 * RuleSetVerifier is made to verify that all classifications and metrics used
 * inside a RuleSet file are defined and available in {@link LaunchSetup#input_class_file}
 * and {@link LaunchSetup#input_metric_file}.
 * 
 * @author Khaled Bakhit
 * @since 2.0
 * @version 24/08/2013
 */
public abstract class RuleSetVerifier
{
    /**
     * LaunchSetup Object containing input configurations. 
     */
   protected LaunchSetup sp;
    /**
     * List object containing name of metrics involved.
     */
   protected List<String> metrics;
    /**
     * List object containing classifications.
     */
   protected List<String> classifications;
    /**
     * boolean that decides whether to allow autoCorrecting or not.
     */
   protected boolean autoCorrect;
   
    /**
     * RuleSetVerifier constructor. Uses the default LaunchSetup Object.
     * @param autoCorrect True to activate auto-correct, false otherwise.
     * @throws InvalidInputException Input files are invalid.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public RuleSetVerifier(boolean autoCorrect) throws InvalidInputException
    {
       this(autoCorrect, Program.getInstance().getLaunchSetup());
    }
    /**
     * RuleSetVerifier constructor.
     * @param autoCorrect True to activate auto-correct, false otherwise.
     * @param sp LaunchSetup Object containing input configuration.
     * @throws InvalidInputException InvalidInputException Input files are invalid.
     * @since 4.0
     */
    public RuleSetVerifier(boolean autoCorrect, LaunchSetup sp) throws InvalidInputException
    {
        this.sp= sp;
        this.autoCorrect= autoCorrect;
        metrics= StartUp.getMetricList(sp);
        classifications= StartUp.getClassList(sp);
    }
    /**
     * Get List containing metrics.
     * @return List containing metrics.
     */
    public List<String> getMetrics()
    {
        return metrics;
    }
    /**
     * Get List containing classifications.
     * @return List containing classifications.
     */
    public List<String> getClassifications()
    {
        return classifications;
    }

    /**
     * Enable/Disable the autoCorrect function.
     * @param autoCorrect true to enable, false disable.
     */
    public void setAutoCorrect(boolean autoCorrect)
    {
        this.autoCorrect= autoCorrect;
    }

    /**
     * check if autoCorrect mode is turned on.
     * @return ture if on, false otherwise.
     */
    public boolean isAutoCorrect()
    {
        return this.autoCorrect;
    }
    
     /**
     * Verify List of RuleSets using multi-threading.
     * @param rs List of RuleSets to verify and fix.
     * @since 3.1
     */
    public void fastVerify(List<RuleSet> rs) throws InterruptedException, ExecutionException
    {
        fastVerify(rs, null);
    }
    /**
     * Verify List of RuleSets using multi-threading.
     * @param rs List of RuleSets to verify and fix.
     * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
     * @since 3.1
     */
    public void fastVerify(List<RuleSet> rs, ExecutorService executor) throws InterruptedException, ExecutionException
    {
        List<Callable<Boolean>> partitions= new LinkedList<Callable<Boolean>>(); 
        Iterator<RuleSet> it= rs.iterator();
        while(it.hasNext()) 
        {
            final RuleSet ruleset= it.next();
             partitions.add(new Callable<Boolean>()
             {
                 @Override
                 public Boolean call() throws FileNotFoundException, IOException 
                 {
                    verify(ruleset);
                    return true;
                 } 
             });
         }  
         
         boolean shutdown= executor==null;
         if(shutdown)
             executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
         List<Future<Boolean>> results= executor.invokeAll(partitions);
         if(shutdown)
            executor.shutdown();
         for(Future<Boolean> result: results)
            result.get(); 
    }
      
    /**
     * Verify List of RuleSets.
     * @param rs List of RuleSets to verify.
     */
    public void verify(List<RuleSet> rs)
    {
       verify(rs, 0, 1);
    }

    /**
     * Verify List of RuleSets.
     * @param RuleSet List of RuleSets to verify.
     * @param start starting index of RuleSet to test.
     * @param offset offset from previous RuleSet in the List.
     * @since 3.0
     */
    public void verify(List<RuleSet> rs, int start, int offset)
    {
        for(int i=start; i<rs.size(); i+= offset)
            verify(rs.get(i));
    }
    /**
     * Prompt user for manually entry of correction.
     * @param wrong_word Incorrect word to replace.
     * @param correct_list List containing possible replacements.
     * @return Corrected version of the wrong word supplied by end user.
     */
    public String promptForCorrection(String wrong_word, List<String> correct_list)
    {
          
        String message= wrong_word+" is not a defined.\nPlease suggest a replacement: ";
        String replacement= wrong_word;
        
        while(!correct_list.contains(replacement))
        {
            replacement= JOptionPane.showInputDialog(null,message,"Invalid param detected", JOptionPane.INFORMATION_MESSAGE);
            if(replacement==null)
            {
                JOptionPane.showMessageDialog(null,"The program will exit now.\nPlease fix your input.","Error",JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }   
        }
        return replacement;
    }
    /**
     * Fix wrong classification detected within given Condition Object.
     * @param cond Condition Object containing wrong classification.
     */
    public abstract void fixClassification(Condition cond);
    /**
     * Fix wrong metric detected within given Condition Object.
     * @param cond Condition Object containing wrong metric.
     */
    public abstract void fixMetric(Condition cond);
    /**
     * Verify given RuleSet.
     * @param rs RuleSet to verify.
     */
    public void verify(RuleSet rs)
    {
        List<Rule> rules= rs.getRules();
        List<Condition> conditions;    
        for(Rule r: rules)    
        {
            conditions= r.getConditions(true);
            for(Condition cond: conditions)
                verify(cond);
        }
    }
    /**
     * Verify given Condition Object.
     * @param cond Condition Object to verify.
     * @since 3.3
     */
    public void verify(Condition cond)
    {
        if(cond.isClassification && !classifications.contains(cond.classification))    
            fixClassification( cond );
        
        else if(!cond.isClassification && !metrics.contains(cond.metric))
            fixMetric( cond );
        
    }
}
