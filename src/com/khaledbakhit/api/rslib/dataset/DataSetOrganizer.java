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
import com.khaledbakhit.api.rslib.Library;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.StartUp;
import com.khaledbakhit.api.rslib.exceptions.*;
import com.khaledbakhit.api.rslib.utils.ExcelWriter;
import com.khaledbakhit.api.rslib.utils.TextWriter;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * DataSetOrganizer rewrites dataset files into more readable form.
 * @author Khaled Bakhit
 * @since 2.0
 * @version 23/08/2013
 */
public class DataSetOrganizer
{
    /**
     * LaunchSetup Object containing input configuration.
     */
    private LaunchSetup sp;
    /**
     * List containing metrics in order.
     */
    private List<String> metricList;
    /**
     * Columns header names of the output files.
     */
    private LinkedList<String> columns;
    /**
     * DataSetOrganizer constructor.
     * Uses the default LaunchSetup Object.
     * @see Program#getInstance()
     * @see Program#getLaunchSetup() 
     * @throws InvalidInputException Unable to parse input metric file.
     */
    public DataSetOrganizer() throws InvalidInputException
    {
        this(Program.getInstance().getLaunchSetup());
    }
    /**
     * DataSetOrganizer constructor.
     * @param sp LaunchSetup Object containing input configuration.
     * @throws InvalidInputException Unable to parse input metric file.
     * @since 4.0
     */
    public DataSetOrganizer(LaunchSetup sp) throws InvalidInputException
    {
        this.sp= sp;
        metricList= StartUp.getMetricList(sp);
        columns= new LinkedList<String>();
        columns.addAll(metricList);
        columns.addLast("class");
    }
    
     /**
    * Organize input DataSet Files using Multi-threading.
    * @param type Output type of converted DataSet files.
    * @throws IOException Unable to complete task.
    * @throws InvalidTypeIndexException
    * @throws InterruptedException
    * @throws ExecutionException 
    * @since 3.0
    */
    public void fastOrganize(final Library.OUTPUT_TYPE type)throws IOException, InvalidTypeIndexException, InterruptedException, ExecutionException
    {  
        fastOrganize(type, (ExecutorService) null);
    }
    
   /**
    * Organize input DataSet Files using Multi-threading.
    * @param type Output type of converted DataSet files.
    * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
    * @throws IOException Unable to complete task.
    * @throws InvalidTypeIndexException
    * @throws InterruptedException
    * @throws ExecutionException 
    * @since 3.0
    */
    public void fastOrganize(final Library.OUTPUT_TYPE type,  ExecutorService executor)throws IOException, InvalidTypeIndexException, InterruptedException, ExecutionException
    {  
        if(sp.input_dataset_dir== null)
            SetupNotConfiguredException.occur("input_dataset_dir");
        List<Callable<Boolean>> partitions= new LinkedList<Callable<Boolean>>();
        File dir= new File(sp.input_dataset_dir);
        final File[] list= dir.listFiles();
         
        for(int i=0; i<list.length; i++)
        {
            final File dataset= list[i]; 
            partitions.add(new Callable<Boolean>() 
            {
                @Override
                public Boolean call() throws FileNotFoundException, IOException 
                {
                    produce(type, dataset);   
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
     * Organize input DataSet Objects.
     * @param type Output type of converted DataSet files.
     * @param sets Output type of converted DataSet files.
     * @throws IOException Unable to complete task.
     */
    public void organize(final Library.OUTPUT_TYPE type, List<DataSet> sets) throws IOException
    {
        for(DataSet set: sets)
            produce(type, set);
    }
    
        /**
    * Organize input DataSet Objects using Multi-threading.
    * @param type Output type of converted DataSet files.
    * @param sets Output type of converted DataSet files.
    * @throws IOException Unable to complete task.
    * @throws InvalidTypeIndexException
    * @throws InterruptedException
    * @throws ExecutionException 
    * @since 3.0
    */
    public void fastOrganize(final Library.OUTPUT_TYPE type, List<DataSet> sets)throws IOException, InvalidTypeIndexException, InterruptedException, ExecutionException
    {  
        fastOrganize(type, sets, null);
    }
    
      /**
    * Organize input DataSet Objects using Multi-threading.
    * @param type Output type of converted DataSet files.
    * @param sets Output type of converted DataSet files.
    * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
    * @throws IOException Unable to complete task.
    * @throws InvalidTypeIndexException
    * @throws InterruptedException
    * @throws ExecutionException 
    * @since 3.0
    */
    public void fastOrganize(final Library.OUTPUT_TYPE type, List<DataSet> sets,  ExecutorService executor)throws IOException, InvalidTypeIndexException, InterruptedException, ExecutionException
    {  
        List<Callable<Boolean>> partitions= new LinkedList<Callable<Boolean>>();
        Iterator<DataSet> it= sets.iterator();
        
        while(it.hasNext())
        {
            final DataSet dataset= it.next(); 
            partitions.add(new Callable<Boolean>() 
            {
                @Override
                public Boolean call() throws FileNotFoundException, IOException 
                {
                    produce(type, dataset);   
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
     * Convert DataSet files into required type.
     * @param type output type index.
     * @throws IOException Unable to complete task.
     * @throws InvalidTypeIndexException output type index is not defined.
     * @since 1.0
     */
    public void organize(Library.OUTPUT_TYPE type) throws IOException, InvalidTypeIndexException
    {
        organize(type,0,1);
    }
    
    
    /**
     * Convert DataSet files into required type.
     * @param type output type index.
     * @param start starting position.
     * @param forward value to add to previous position to determine next file.
     * @throws IOException Unable to complete task.
     * @throws InvalidTypeIndexException output type index is not defined.
     * @since 3.1
     */
    public void organize(Library.OUTPUT_TYPE type, int start, int forward) throws IOException, InvalidTypeIndexException
    {
        if(type==Library.OUTPUT_TYPE.TEXT)
            produceText(start, forward);
        else if(type== Library.OUTPUT_TYPE.XLS)
            produceExcel(start, forward);
        else if(type== Library.OUTPUT_TYPE.CSV)
            produceCSV(start, forward);
        else
            throw new InvalidTypeIndexException(type+" is not a defined type in "+this.getClass().getName());
            
    }

    /**
     * Convert input DataSet file into given output format.
     * Input dataset files defined by LaunchSetup are used.
     * @param type DataSet Output format.
     * @param start starting position.
     * @param forward value to add to previous position to determine next dataset file.
     * @throws IOException Unable to complete task.
     * @since 3.1
     */
    public void produce( Library.OUTPUT_TYPE type, int start, int forward) throws IOException
    {
        if(sp.input_dataset_dir== null)
            SetupNotConfiguredException.occur("input_dataset_dir");
        File dir= new File(sp.input_dataset_dir);
        File[] datasets= dir.listFiles();
           
        for(int i=start; i<datasets.length; i+=forward)
            if(type== Library.OUTPUT_TYPE.XLS)
                produceExcel(datasets[i]);
            else
                produce(type, datasets[i]);
    }
    
    /**
     * Convert input DataSet file into given output format.
     * @param type DataSet Output format.
     * @param datasetFile DataSet file to convert.
     * @throws IOException Unable to complete task.
     */ 
    public void produce(Library.OUTPUT_TYPE type, File datasetFile) throws IOException
    {
        if(type== Library.OUTPUT_TYPE.XLS)
        {
            produceExcel(datasetFile);
            return;
        }
        if(sp.dataset_output_data_dir == null)
            SetupNotConfiguredException.occur("dataset_output_data_dir");
        if(sp.dataset_reader == null )
            SetupNotConfiguredException.occur("dataset_reader");
        String path= sp.dataset_output_data_dir;
        DataSetReader reader= (DataSetReader) sp.dataset_reader.newInstance();
        DataSetLine line= null;
        String[] metrics;
        String ext, sep;
        if(type== Library.OUTPUT_TYPE.CSV)
        {
            ext= ".csv";
            sep= ",";
        }
        else
        {
            ext= ".txt";
            sep=",";
        }
          String name= datasetFile.getName().substring(0, datasetFile.getName().lastIndexOf('.'));
        TextWriter txt= new TextWriter(path+"/"+ name + ext);
        //Output column names
        for(int j=0; j<columns.size(); j++)    
            txt.out.print(columns.get(j)+sep);    
        txt.out.println();
                
        reader.open(new FileInputStream(datasetFile));
        
        while(reader.hasNext())    
        {
            line= reader.getNext(line);
            metrics= line.getMetrics();
            for(int k=0; k< metrics.length; k++)    
                txt.out.print(metrics[k]+sep);
            txt.out.println(line.getClassification());    
        }
            
        reader.close();    
        txt.close();
    }
    
    /**
     * Convert DataSet Object into given output format.
     * @param type DataSet Output format.
     * @param dataset DataSet Object to convert.
     * @throws IOException Unable to complete task.
     * @since 4.0
     */ 
    public void produce(Library.OUTPUT_TYPE type, DataSet dataset) throws IOException
    {
        if(type== Library.OUTPUT_TYPE.XLS)
        {
            produceExcel(dataset);
            return;
        }
        if(sp.dataset_output_data_dir == null)
            SetupNotConfiguredException.occur("dataset_output_data_dir");
        String path= sp.dataset_output_data_dir;
        String[] metrics;
        String ext, sep;
        if(type== Library.OUTPUT_TYPE.CSV)
        {
            ext= ".csv";
            sep= ",";
        }
        else
        {
            ext= ".txt";
            sep="\t";
        }
        String dsName= dataset.getFile().getName();
        dsName= dsName.substring(0, dsName.lastIndexOf('.'));
        TextWriter txt= new TextWriter(path+"/"+ dsName + ext);
        //Output column names
        for(int j=0; j<columns.size(); j++)    
            txt.out.print(columns.get(j)+sep);    
        txt.out.println();
                
        for(DataSetLine line: dataset.getDataSetLines()) 
        {
            metrics= line.getMetrics();
            for(int k=0; k< metrics.length; k++)    
                txt.out.print(metrics[k]+sep);
            txt.out.println(line.getClassification());    
        }
        txt.close();
    }
    
      /**
     * Convert DataSet files into XLS form.
     * @param dataset Dataset Object to convert.
     * @throws IOException Unable to produce output.
     * @since 4.0
     */
     public void produceExcel(DataSet dataset) throws IOException
     {
        if(sp.dataset_output_data_dir == null)
            SetupNotConfiguredException.occur("dataset_output_data_dir");
        String path= sp.dataset_output_data_dir; 
        String[] metrics; 
        String dsName= dataset.getFile().getName();
        dsName= dsName.substring(0, dsName.lastIndexOf('.'));
        ExcelWriter output= new ExcelWriter(path+"/"+ dsName +".xls");
        output.addSheet(dataset.getFile().getName());
        output.println(columns, output.BoldBlue);
          
       
        for(DataSetLine line: dataset.getDataSetLines())
        {
            metrics= line.getMetrics();
            output.print(metrics);
            output.println(line.getClassification());
        }
          
         output.flush();
         output.close();
    }
    
    /**
     * Convert DataSet Object into TXT form.
     * @param dataset Input DataSet Object to convert.
     * @throws IOException Unable to produce output.
     * @since 4.0
     */
     public void produceText(DataSet dataset) throws IOException
     {
         produce(Library.OUTPUT_TYPE.TEXT, dataset);
     }
     /**
     * Convert DataSet Object into CVS form.
     * @param dataset Input DataSet Object to convert.
     * @throws IOException Unable to produce output.
     * @since 4.0
     */
     public void produceCSV(DataSet dataset) throws IOException
     {
         produce(Library.OUTPUT_TYPE.CSV, dataset);
     }
  
    /**
     * Convert DataSet file into XLS form.
     * @param datasetFile dataset file to convert.
     * @throws IOException Unable to produce output.
     * @since 3.0
     */
     public void produceExcel(File datasetFile) throws IOException
     {
        if(sp.dataset_output_data_dir == null)
            SetupNotConfiguredException.occur("dataset_output_data_dir");
        if(sp.dataset_reader == null )
            SetupNotConfiguredException.occur("dataset_reader");
        
        String path= sp.dataset_output_data_dir;
        DataSetReader reader= (DataSetReader) sp.dataset_reader.newInstance();
        DataSetLine line= null;
        String[] metrics; 
        String name= datasetFile.getName().substring(0, datasetFile.getName().lastIndexOf('.'));
        ExcelWriter output= new ExcelWriter(path+"/"+ name +".xls");
        output.addSheet(datasetFile.getName());
        output.println(columns, output.BoldBlue);
          
        reader.open(new FileInputStream(datasetFile));
        while(reader.hasNext())
        {
            line= reader.getNext(line);
            metrics= line.getMetrics();
            output.print(metrics);
            output.println(line.getClassification());
        }
          
         output.flush();
         output.close();
        
         reader.close();
    }
      
     /**
     * Convert DataSet file into TXT form.
     * @param datasetFile Input dataset file to convert.
     * @throws IOException Unable to produce output.
     */
     public void produceText(File datasetFile) throws IOException
     {
         produce(Library.OUTPUT_TYPE.TEXT, datasetFile);
     }
     /**
     * Convert DataSet file into CVS form.
     * @param datasetFile Input dataset file to convert.
     * @throws IOException Unable to produce output.
     */
     public void produceCSV(File datasetFile) throws IOException
     {
         produce(Library.OUTPUT_TYPE.CSV, datasetFile);
     }
    
     /**
      * Convert DataSet files into CSV format. 
      * Input dataset files defined by LaunchSetup are used.
      * @param start starting position.
      * @param forward value to add to previous position to determine next dataset file.
      * @throws IOException Unable to complete task.
      * @since 3.1
      */
    public void produceCSV(int start, int forward) throws IOException
    {
        produce(Library.OUTPUT_TYPE.CSV, start, forward);
    }
    
     /**
      * Convert DataSet files into TXT format. 
      * Input dataset files defined by LaunchSetup are used.
      * @param start starting position.
      * @param forward value to add to previous position to determine next dataset file.
      * @throws IOException Unable to complete task.
      * @since 3.1
      */
    public void produceText(int start, int forward) throws IOException
    {
        produce(Library.OUTPUT_TYPE.TEXT, start, forward);
    }
         
    /**
      * Convert DataSet files into Excel format. 
      * Input dataset files defined by LaunchSetup are used.
      * @param start starting position.
      * @param forward value to add to previous position to determine next dataset file.
      * @throws IOException Unable to complete task.
      * @since 3.1
      */
    public void produceExcel(int start, int forward) throws IOException
    {
        produce(Library.OUTPUT_TYPE.XLS, start, forward);
    }

    /**
     * Convert DataSet files into CSV.
     * @throws IOException Unable to complete task.
     */
    public void produceCSV() throws IOException
    {
        produceCSV(0,1);
    }
    
     /**
      * Convert DataSet files into Excel files.
      * @throws IOException Unable to complete task.
      * @since 3.0
      */
    public void produceExcel() throws IOException
    {
        produceExcel(0,1);
    }

    /**
     * Convert DataSet files into TXT form.
     * @throws IOException Unable to complete task.
     */
    public void produceText() throws IOException
    {
        produceText(0,1);
    }
    
}