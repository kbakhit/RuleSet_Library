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
package com.khaledbakhit.api.rslib.dataset;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.exceptions.InputParseException;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import java.io.File;
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
 * DataSetFactory assists in extracting DataSet Objects from input DataSet files.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 23/08/2013
 */
public class DataSetFactory 
{
    /**
     * LaunchSetup Object containing input configurations.
     */
    private LaunchSetup sp;
  
    /**
     * DataSetFactory constructor. Uses the default LaunchSetup Object.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup()
     */
    public DataSetFactory()
    {
        this(Program.getInstance().getLaunchSetup());
    }
    /**
     * RuleSetFactory constructor.
     * @param sp LaunchSetup Object containing input configurations.
     */
    public DataSetFactory(LaunchSetup sp)
    {
        this.sp= sp;
    }
    /**
     * Extract all DataSets from the default directory. 
     * @return List Object containing DataSet Objects.
     * @throws FileNotFoundException Input files not found.
     * @throws InputParseException DataSet input is not supported.
     * @throws IOException Unable to read DataSet Files.
     */
    public List<DataSet> extractDataSets() throws FileNotFoundException, InputParseException, IOException
    {
        return extractDataSets(0,1);
    }
    /**
     * Extract all DataSets from given DataSet directory/file.
     * @param input_ruleset_dir Directory containing DataSets.
     * @return List Object containing DataSet Objects.
     * @throws FileNotFoundException Input files not found.
     * @throws InputParseException  DataSet input is not supported.
     * @throws IOException Unable to read DataSet Files.
     */
    public List<DataSet> extractDataSets(File input_ruleset_file)throws FileNotFoundException, InputParseException, IOException
    {
        return extractDataSets(input_ruleset_file, 0, 1);
    }
    
    /**
     * Extract DataSets from the default directory. 
     * @param start starting index of File[] array.
     * @param forward increment value for File[] array traversal.
     * @return List Object containing DataSet Objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException DataSet input is not supported.
     * @throws IOException Unable to read DataSet Files.
     */
    public List<DataSet> extractDataSets(int start, int forward) throws FileNotFoundException, InputParseException, IOException 
    {
        return extractDataSets(new File(sp.input_dataset_dir), start, forward);
    }
     /**
     * Extract DataSets from input default directory.
     * @return List object containing DataSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  DataSet input is not supported.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Error occurred during execution.
     * @throws IOException Unable to read DataSet Files.
     */
    public List<DataSet> fastExtractDataSets() throws InterruptedException, ExecutionException, FileNotFoundException, InputParseException, IOException
    {
        return fastExtractDataSets(new File(sp.input_dataset_dir), null);
    }
   
    /**
     * Extract DataSets from input default directory.
     * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
     * @return List object containing DataSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  DataSet input is not supported.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Error occurred during execution.
     * @throws IOException Unable to read DataSet Files.
     */
    public List<DataSet> fastExtractDataSets(ExecutorService executor) throws InterruptedException, ExecutionException, FileNotFoundException, InputParseException, IOException
    {
        return fastExtractDataSets(new File(sp.input_dataset_dir), executor);
    }
    
    
    /**
     * Extract DataSets from input directory..
     * @param dir File or Directory containing all the DataSets.
     * @param start starting index of File[] array.
     * @param forward increment value for File[] array traversal.
     * @return List Object containing DataSet Objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  DataSet input is not supported.
     * @throws IOException Unable to read DataSet Files.
     */
    public List<DataSet> extractDataSets(File dir, int start, int forward) throws FileNotFoundException, InputParseException, IOException
    {
        if(!dir.exists())
            throw new FileNotFoundException(dir.getName()+" doesn't exist.");
        
        if(sp.dataset_reader==null)
            SetupNotConfiguredException.occur("dataset_reader");
        File[] list;
        if(dir.isDirectory())
            list= dir.listFiles();
        else
            list= new File[]{dir};
       
        LinkedList<DataSet> mainList= new LinkedList<DataSet>();  
        for(int i=start; i<list.length; i+=forward)
            mainList.add(new DataSet(list[i], sp));
          
        return mainList;
    }
    
    /**
     * Extract DataSets from input directory.
     * @param dir Directory containing all the RuleSets.
     * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
     * @return List Object containing DataSet objects.
     * @throws FileNotFoundException Unable to locate Input File.
     * @throws InputParseException  DataSet input is not supported.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Error occurred during execution.
     * @throws IOException Unable to read DataSet Files.
     */
    public List<DataSet> fastExtractDataSets(File dir, ExecutorService executor) throws InterruptedException, ExecutionException, FileNotFoundException, InputParseException, IOException
    {
        if(!dir.exists())
            throw new FileNotFoundException(dir.getName()+" doesn't exist.");
     
        if(!dir.isDirectory())
            return extractDataSets(dir);
           
        final File[] list= dir.listFiles();
        
        
        List<Callable<DataSet>> partitions= new LinkedList<Callable<DataSet>>();
 
         for(int i=0; i<list.length; i++)
         {
             final File dsFile= list[i];
             partitions.add(new Callable<DataSet>()
             {
                 @Override
                 public DataSet call() throws FileNotFoundException, IOException, InputParseException 
                 {
                     return new DataSet(dsFile, sp);
                 } 
             });
         }  
         boolean shutdown= executor==null;
         if(shutdown)
            executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
           
         List<Future<DataSet>> results= executor.invokeAll(partitions);
         if(shutdown)
             executor.shutdown();
         LinkedList<DataSet> mainList= new LinkedList<DataSet>();
         
         for(Future<DataSet> result: results)
            mainList.add(result.get());
    
         return mainList;
    }   
}