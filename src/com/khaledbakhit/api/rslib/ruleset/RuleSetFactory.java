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
import com.khaledbakhit.api.rslib.exceptions.InputParseException;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.parsers.RuleSetParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * RuleSetFactory handles importing RuleSets.
 * 
 * @author Khaled Bakhit
 * @since 1.0
 * @version 24/08/2013
 */
public class RuleSetFactory
{
    /**
     * LaunchSetup Object containing input configurations.
     * @since 4.0
     */
    private LaunchSetup sp;
  
    /**
     * RuleSetFactory constructor. Uses the default configurations.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public RuleSetFactory()
    {
        this(Program.getInstance().getLaunchSetup());
    }
    /**
     * RuleSetFactory constructor.
     * @param sp LaunchSetup Object containing input configurations.
     * @since 4.0
     */
    public RuleSetFactory(LaunchSetup sp)
    {
        this.sp= sp;
    }
    
    /**
     * Extract all RuleSets from the default directory. 
     * @return List Object containing RuleSet Objects.
     * @throws FileNotFoundException Input files not found.
     * @throws InputParseException  RuleSet input is not supported.
     */
    public List<RuleSet> extractRuleSets() throws FileNotFoundException, InputParseException
    {
        return extractRuleSets(0,1);
    }
    /**
     * Extract all RuleSets from given RuleSet directory.
     * @param input_ruleset_dir Directory containing RuleSets.
     * @return List Object containing RuleSet Objects.
     * @throws FileNotFoundException Input files not found.
     * @throws InputParseException  RuleSet input is not supported.
     */
    public List<RuleSet> extractRuleSets(File input_ruleset_dir)throws FileNotFoundException, InputParseException
    {
        return extractRuleSets(input_ruleset_dir, 0, 1);
    }
    
    /**
     * Extract RuleSets from the default directory. 
     * @param start starting index of File[] array.
     * @param forward increment value for File[] array traversal.
     * @return List Object containing extracted RuleSets.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException RuleSet input is not supported.
     * @since 3.0
     */
    public List<RuleSet> extractRuleSets(int start, int forward) throws FileNotFoundException, InputParseException 
    {
        return extractRuleSets(new File(sp.input_ruleset_dir), start, forward);
    }
     /**
     * Extract RuleSets from input default directory.
     * @return List Object containing RuleSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  RuleSet input is not supported.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Error occurred during execution.
     * @since 3.2
     */
    public List<RuleSet> fastExtractRuleSets() throws InterruptedException, ExecutionException, FileNotFoundException, InputParseException
    {
        return fastExtractRuleSets(new File(sp.input_ruleset_dir));
    }
        
    /**
     * Extract RuleSets from input default directory.
     * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
     * @return List Object containing RuleSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  RuleSet input is not supported.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Error occurred during execution.
     * @since 3.2
     */
    public List<RuleSet> fastExtractRuleSets(ExecutorService executor) throws InterruptedException, ExecutionException, FileNotFoundException, InputParseException
    {
        return fastExtractRuleSets(new File(sp.input_ruleset_dir), executor);
    }
    
    
    /**
     * Extract RuleSets from input directory.
     * @param dir File or Directory containing all the RuleSets.
     * @param start starting index of File[] array.
     * @param forward increment value for File[] array traversal.
     * @return LinkedList object containing RuleSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  RuleSet input is not supported.
     * @since 3.0
     */
    public List<RuleSet> extractRuleSets(File dir, int start, int forward) throws FileNotFoundException, InputParseException
    {
        if(!dir.exists())
            throw new FileNotFoundException(dir.getName()+" doesn't exist.");
        
        if(sp.ruleset_parser==null)
            SetupNotConfiguredException.occur("ruleset_parser");
        File[] list;
        if(dir.isDirectory())
            list= dir.listFiles();
        else
            list= new File[]{dir};
       
        LinkedList<RuleSet> mainList= new LinkedList<RuleSet>();
        LinkedList<RuleSet> parsedList;
        RuleSetParser parser= (RuleSetParser) sp.ruleset_parser.newInstance();
        int id;
        for(int i=start; i<list.length; i+=forward)
        {
            parser.parse(new FileInputStream(list[i]));
            parsedList= parser.getParsedData();
            if(parsedList.size()== 1)
                id= -1;
            else
                id= 0;
            for(RuleSet rs: parsedList)
            {
                rs.setParent(list[i]);
                rs.setID(id);
                id++;
                mainList.add(rs);
            }
            try
            {
                parser.close();
            }
            catch(Exception e)
            {}
        }
        return mainList;
    }
    
     /**
     * Extract RuleSets from input Directory.
     * @param dir Directory containing all the RuleSets.
     * @return List Object containing RuleSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  RuleSet input is not supported.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Error occurred during execution.
     * @since 3.2
     */
    public List<RuleSet> fastExtractRuleSets(File dir) throws InterruptedException, ExecutionException, FileNotFoundException, InputParseException
    {
        return fastExtractRuleSets(dir, null);
    }
    
    
    /**
     * Extract RuleSets from input Directory.
     * @param dir Directory containing all the RuleSets.
     * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
     * @return List Object containing RuleSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  RuleSet input is not supported.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Error occurred during execution.
     * @since 3.2
     */
    public List<RuleSet> fastExtractRuleSets(File dir, ExecutorService executor) throws InterruptedException, ExecutionException, FileNotFoundException, InputParseException
    {
        if(!dir.exists())
            throw new FileNotFoundException(dir.getName()+" doesn't exist.");
     
        if(!dir.isDirectory())
            return extractRuleSets(dir);
           
        final File[] list= dir.listFiles();
        
        
        List<Callable<List<RuleSet>>> partitions= new LinkedList<Callable<List<RuleSet>>>();
 
         for(int i=0; i<list.length; i++)
         {
             final File rsFile= list[i];
             partitions.add(new Callable<List<RuleSet>>()
             {
                 @Override
                 public List<RuleSet> call() throws FileNotFoundException, IOException, InputParseException 
                 {
                     return extractRuleSets(rsFile);
                 } 
             });
         }  
         boolean shutdown= executor==null;
         if(shutdown)
            executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
         List<Future<List<RuleSet>>> results= executor.invokeAll(partitions);
         if(shutdown)
            executor.shutdown();
         LinkedList<RuleSet> mainList= new LinkedList<RuleSet>();
         
         for(Future<List<RuleSet>> result: results)
            mainList.addAll(result.get());
    
         return mainList;
    }  
}