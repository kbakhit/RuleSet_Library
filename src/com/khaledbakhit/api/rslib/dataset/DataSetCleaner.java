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
import com.khaledbakhit.api.rslib.StartUp;
import com.khaledbakhit.api.rslib.exceptions.*;
import com.khaledbakhit.api.rslib.utils.Debugger;
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
 * DataSetCleaner cleans datasets from incorrect data lines.
 * @author Khaled Bakhit
 * @since 1.0
 * @version 22/08/2013
 */
public class DataSetCleaner
{
    /**
     * List object containing classifications as String. 
     * @since 3.1
     */
    private List<String> classList;
   /**
     * Illegal character. 
     * @since 1.0
     */
    private char illegal;
    /**
     * Default illegal character (?).
     * @since 1.0
     */
    private final char DEFAULT_ILLEGAL_CHARACTER= '?';
    /**
     * LaunchSetup Object containing input configuration.
     * @since 4.0
     */
    private LaunchSetup sp;
    /**
     * The correct number of attributes.
     * @since 1.0
     */
    private int nbrofArtt;
    /**
     * Directory containing input DataSet files.
     * @since 1.0
     */
    private File directory;
    /**
     * StringBuilder containing log information.
     */
    private StringBuilder logger;
    
    /**
     * DataSetCleaner Constructor. 
     * Uses input files defined by default LaunchSetup Object.
     * @throws MetricsNotFoundException Unable to locate metrics File.
     * @throws ClassificationsNotFoundException Unable to locate classifications file.
     * @throws InvalidInputException Unable to parse input files.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public DataSetCleaner() throws MetricsNotFoundException, ClassificationsNotFoundException, InvalidInputException
    {
        this( Program.getInstance().getLaunchSetup() );
    }
     /**
     * DataSetCleaner Constructor.
     * @param sp LaunchSetup option containing input configuration.
     * @throws MetricsNotFoundException Unable to locate metrics File.
     * @throws ClassificationsNotFoundException Unable to locate classifications file.
     * @throws InvalidInputException Unable to parse input files.
     * @since 4.0
     */
    public DataSetCleaner(LaunchSetup sp)throws MetricsNotFoundException, ClassificationsNotFoundException, InvalidInputException
    {
        this.sp= sp;
        directory= new File(sp.input_dataset_dir);
        nbrofArtt= StartUp.getMetricList(sp).size();
        illegal= DEFAULT_ILLEGAL_CHARACTER;
        classList= StartUp.getClassList(sp);
        logger= new StringBuilder();
    }
    
    /**
     * Set the illegal character that can be found in dataset line. 
     * @param illegal Illegal character that shouldn't exist in dataset lines.
     */
    public void setIllegalChar(char illegal)
    {
        this.illegal= illegal;
    }
    /**
     * Get the illegal character that can be found in dataset line. 
     * @return Illegal character that can be found in dataset line. 
     */
    public char getIllegelChar()
    {
        return illegal;
    }
     
    /**
     * Start the cleaning operation.
     * @throws FileNotFoundException Unable to locate DataSet file.
     * @throws IOException Unable to product clean version of DataSet file.
     */
    public void clean() throws FileNotFoundException, IOException
    {
        clean(0,1);
    }

    /**
     * Perform a fast clean using Java Concurrent API.
     * @param set List Object containing DataSet Objects to clean.
     * @return Cleaned List Object.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Unable to complete task.
     * @since 4.0
     */
    public List<DataSet> fastClean(List<DataSet> sets) throws InterruptedException, ExecutionException
    {
        return fastClean(sets, null);
    }
    
    /**
     * Perform a fast clean using Java Concurrent API.
     * @param set List Object containing DataSet Objects to clean.
     * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
     * @return Cleaned List Object.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Unable to complete task.
     * @since 4.0
     */
    public List<DataSet> fastClean(List<DataSet> sets, ExecutorService executor) throws InterruptedException, ExecutionException
    {
        final List<DataSet> toReturn= new LinkedList<DataSet>(); 
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
                    DataSet ds= clean(dataset);
                    if(ds!=null)
                        toReturn.add(ds);
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
         return toReturn;
    }
    
     /**
     * Perform a fast clean using Java Concurrent API.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Unable to complete task.
     * @since 3.2
     */
    public void fastClean() throws InterruptedException, ExecutionException
    {
        fastClean((ExecutorService) null);
    }
    
    /**
     * Perform a fast clean using Java Concurrent API.
       * @param executor ExecutorService Object to execute function in parallel. If null, creates a default one.
     * @throws InterruptedException Execution interrupted.
     * @throws ExecutionException Unable to complete task.
     * @since 3.2
     */
    public void fastClean(ExecutorService executor) throws InterruptedException, ExecutionException
    {
         List<Callable<Boolean>> partitions= new LinkedList<Callable<Boolean>>();
         final File[] list= directory.listFiles();
         for(int i=0; i<list.length; i++)
         {
             final File dataset= list[i];
             partitions.add(new Callable<Boolean>()
             {
                 @Override
                 public Boolean call() throws FileNotFoundException, IOException 
                 {
                    clean(dataset);
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
     * Start the cleaning operation of the list of DataSet Files[]. Use in multi-threaded execution.
     * @param start starting index.
     * @param forward value to add to the previous position to get the next position.
     * @throws FileNotFoundException Unable to locate DataSet file.
     * @throws IOException Unable to product clean version of DataSet file.
     * @since 3.0
     */
    public void clean(int start, int forward) throws FileNotFoundException, IOException
    {
        File[] list= directory.listFiles();
        for(int i=start; i<list.length;i+=forward)
            clean(list[i]);
    }
    
    /**
     * Check if the given DataSet line is clean and valid or not.
     * @param line DataSet line to check.
     * @return true if line is clean and valid, false otherwise.
     * @since 4.0
     */
    public boolean isClean(DataSetLine line)
    {
       if(line==null)
           return false; 
       return this.isMetricCountLegal(line) && this.isClassLegal(line) && this.isLineLegal(line);
    }

    /**
     * Clean the given dataset file.
     * @param dataset File to clean.
     * @throws FileNotFoundException Unable to locate DataSet file.
     * @throws IOException Unable to product clean version of DataSet file.
     */
    public void clean(File dataset) throws FileNotFoundException, IOException
    {
        if(sp.dataset_reader == null )
            SetupNotConfiguredException.occur("dataset_reader");
        if(sp.dataset_writer == null )
            SetupNotConfiguredException.occur("dataset_writer");
        String name= dataset.getAbsolutePath();
        Debugger.printlnInfo("cleaning "+ name );
        int kept= 0;
        int deleted= 0;
        DataSetReader reader= (DataSetReader) sp.dataset_reader.newInstance();
        reader.open(new FileInputStream(dataset));
       // Scanner scan= new Scanner(dataset);
        boolean keep;
        
        File backupFile= new File(name+".backup");
        DataSetWriter writer= (DataSetWriter) sp.dataset_writer.newInstance();
        DataSet ds= new DataSet();
        ds.setFile(backupFile);
      
        DataSetLine line= null;
        while(reader.hasNext())
        {
            line= reader.getNext(line);
            if(line==null)
                continue;
            keep= true; 
            if(!isLineLegal(line))
            {
                Debugger.printlnWarning("deleted:\n"+line);
                Debugger.printlnWarning("reason: illegal character '"+illegal+"' found.");
                keep= false;
            }
            else if(!isClassLegal(line))
            {
                Debugger.printlnWarning("deleted:\n"+line);
                Debugger.printlnWarning("reason: illegal classification found.");
                keep= false;
            }
            else if(!isMetricCountLegal(line))
            {
                 Debugger.printlnWarning("deleted:\n"+line);
                 Debugger.printlnWarning("reason: illegal number of metrics found.");
                keep= false;
            }
            
            if(!keep)
            {
                deleted++;
                continue;
            }
            kept++;
            ds.addDataSetLine(line);
        }
        if(logger!=null)
        {
            logger.append(" file: ").append(dataset.getName()).append('\n');
            logger.append(" total lines: ").append(kept+deleted).append('\n');
            logger.append(" kept: ").append(kept).append('\n');
            logger.append(" deleted: ").append(deleted).append('\n');
            logger.append("-------------------------").append('\n');
        }
        reader.close();
        dataset.delete();
        writer.write(ds);
        writer.close();
        if(kept==0)
        {
            if(!backupFile.delete())
                backupFile.deleteOnExit();
            return;
        }
        if(!backupFile.renameTo(dataset))
            Debugger.printlnWarning("Unable to restore "+ dataset.getName());
    }
    
    /**
     * Clean the given DataSet Object.
     * @param sets List containing DataSet Objects to clean.
     * @return Cleaned List Object.
     * @since 4.0
     */
    public List<DataSet> clean(List<DataSet> sets)
    {
        LinkedList<DataSet> toReturn= new LinkedList<DataSet>();
        DataSet ds;
        for(DataSet set: sets)
        {
            ds= clean(set);
            if(ds!=null)
                toReturn.add(ds);
        }
        return toReturn;
    }
    
      /**
     * Clean the given DataSet Object.
     * @param dataset DataSetObject to clean to clean.
     * @throws FileNotFoundException Unable to locate DataSet file.
     * @throws IOException Unable to product clean version of DataSet file.
     * @since 4.0
     */
    public DataSet clean(DataSet dataset) 
    {
        String name= dataset.getFile().getAbsolutePath();
        Debugger.printlnInfo("cleaning "+name);
        int kept= 0;
        int deleted= 0;
        LinkedList<DataSetLine> backup= new LinkedList<DataSetLine>();
        boolean keep;
        Iterator<DataSetLine> it= dataset.getDataSetLinesIterator();
        DataSetLine line;
        while(it.hasNext())
        {
            line= it.next();
            keep= true; 
            if(!isLineLegal(line))
            {
                Debugger.printlnWarning("deleted:\n"+line);
                Debugger.printlnWarning("reason: illegal character '"+illegal+"' found.");
                keep= false;
            }
            else if(!isClassLegal(line))
            {
                Debugger.printlnWarning("deleted:\n"+line);
                Debugger.printlnWarning("reason: illegal classification found.");
                keep= false;
            }
            else if(!isMetricCountLegal(line))
            {
                 Debugger.printlnWarning("deleted:\n"+line);
                 Debugger.printlnWarning("reason: illegal number of metrics found.");
                keep= false;
            }
            
            if(!keep)
            {
                deleted++;
                continue;
            }
            kept++;
            backup.add(line);
        }
        if(logger!=null)
        {
            logger.append(" file: ").append(dataset.getFile().getName()).append('\n');
            logger.append(" total lines: ").append(kept+deleted).append('\n');
            logger.append(" kept: ").append(kept).append('\n');
            logger.append(" deleted: ").append(deleted).append('\n');
            logger.append("-------------------------").append('\n');
        }
        
        if(deleted==0)
            return dataset;
        if(kept==0)
            return null;
        
        dataset.getDataSetLines().clear();
        dataset.getDataSetLines().addAll(backup);
        return dataset;
    }
    
    /**
     * Check if dataset line doesn't contain an illegal character. 
     * @param line dataset line to check.
     * @return true if legal, false otherwise.
     * @since 4.0
     */
    public boolean isLineLegal(DataSetLine line)
    {
        for(String metric: line.getMetrics())
            if(metric.indexOf(illegal)!=-1)
                return false;
        return true;
    }
     /**
     * Check if number of metrics in given dataset line is legal. 
     * @param line dataset line to check.
     * @return true if legal, false otherwise. 
     * @since 4.0
     */
    public boolean isMetricCountLegal(DataSetLine line)
    {
        return nbrofArtt== line.getMetrics().length;   
    }

    /**
     * Check if classification given by dataset line is legal. 
     * @param line dataset line to check.
     * @return true if legal, false otherwise.
     * @since 4.0
     */
    public boolean isClassLegal(DataSetLine line)
    {
        try
        {
            String classification= line.getClassification();
            if(classList!=null)
                return classList.contains(classification);
        }
        catch(Exception e)
        {
        }
        return false;
    }
   
    /**
     * Produce log file telling about each file processed,
     * how many lines where kept and deleted.
     * @throws IOException Unable to produce log File.
     */
    public void produceLog() throws IOException
    {
        if(sp.dataset_output_log_dir==null)
            SetupNotConfiguredException.occur("dataset_output_log_dir");
        TextWriter txt= new TextWriter(sp.dataset_output_log_dir+"/Cleaner_Log_"+System.currentTimeMillis()+".txt");
        txt.out.print(logger.toString());
        txt.close();
        clearLog();
    }
    /**
     * Clear current log.
     */
    public void clearLog()
    {
        logger= new StringBuilder();
    }
}