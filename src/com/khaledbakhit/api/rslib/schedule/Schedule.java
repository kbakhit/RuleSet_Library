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
package com.khaledbakhit.api.rslib.schedule;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.engines.Engine;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.utils.Packager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Schedule class is responsible for scheduling experiments and collecting results
 * after testing is done.
 * @author Khaled Bakhit
 * @since 3.0
 * @version 25/08/2013
 */
public class Schedule 
{
    /**
     * LaunchSetup Object containing input configuration.
     * @since 4.0
     */
    private LaunchSetup sp;
    /**
     * LinkedList object pointing to Files containing RuleSets.
     */
    private LinkedList<File> Rulesets;
    /**
     * LinkedList object pointing to Files containing DataSets.
     */
    private LinkedList<File> Datasets;
    /**
     * File object containing classifications. 
     */
    private File classification;
    /**
     * File object containing metrics.
     */
    private File metric;
    /**
     * RunSetting of this Schedule object. 
     */
    private RunSetting setting;
    /**
     * Name of this Schedule object.
     */
    private String name;
    /**
     * Start time of this Schedule. 
     */
    private String startTime; 
    /**
     * End time of this Schedule. 
     */
    private String endTime;
  
    /**
     * Run duration of this Schedule. 
     */
    private String duration; 
    /**
     *  Engine object that executes the experiment.
     */
    private Engine engine;
   
    /**
     * Schedule constructor. Uses the default LaunchSetup Object defined by Program.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public Schedule()
    {
         this(System.currentTimeMillis()+"", Program.getInstance().getLaunchSetup());
    }
    
    /**
     * Schedule constructor. Uses the default LaunchSetup Object defined by Program.
     * @param Name Name for the Schedule.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public Schedule(String name)
    { 
        this( name , Program.getInstance().getLaunchSetup());
    }
    
    /**
     * Schedule constructor.
     * @param name Name for this Schedule.
     * @param sp LaunchSetup Object containing input configuration.
     * @since 4.0
     */
    public Schedule(String name, LaunchSetup sp)
    {  
        this.sp= sp;
        Rulesets= new LinkedList<File>();
        Datasets= new LinkedList<File>();
        this.name= name;
        startTime= "Unknown";
        endTime= "Unknown";
        duration= "Unknown";
    }
    
    /**
     * Set the engine of this Schedule. 
     * @param engine Engine object. 
     */
    public void setEngine(Engine engine)
    {
        this.engine= engine;
    }
     
     /**
     * Get the engine of this Schedule. 
     * @return Engine object.
     */
    public Engine getEngine()
    {
        return engine;
    }
    
    /**
     * Check if Schedule has an Engine. 
     * @return true if has an engine, false otherwise. 
     */
    public boolean hasEngine()
    {
        return engine!=null;
    }
    /**
     * Get the name of this Schedule object.
     * @return Name of this Schedule object.
     */
    public String getName()
    {
        return name;
    }
    /**
     * Get the DataSet List. 
     * @return List containing DataSet Files.
     */
    public List<File> getDataSetList()
    {
        return this.Datasets;
    }
    /**
     * Get the RuleSet List. 
     * @return List containing RuleSet Files.
     */
    public LinkedList<File> getRuleSetList()
    {
        return this.Rulesets;
    }
    
    /**
     * Set the name of this Schedule object. 
     * @param name Name of this Schedule object.
     */
    public void setName(String name)
    {
        this.name= name;
    }
    
    /**
     * Add a RuleSet file. 
     * @param ruleset RuleSet file. 
     */
    public void addRuleSet(File ruleset)
    {
        this.Rulesets.add(ruleset);
    }
    
    /**
     * Add a DataSet file. 
     * @param dataset DataSet file. 
     */
    public void addDataSet(File dataset)
    {
        this.Datasets.add(dataset);
    }
    
    /**
     * Set the classification file. 
     * @param classification File containing classifications.
     */
    public void setClassification(File classification)
    {
        this.classification= classification;
    }
    
    /**
     * Set the metric file. 
     * @param metric  File containing metrics.
     */
    public void setMetric(File metric)
    {
        this.metric= metric;
    }
    
    /**
     * Check if Schedule object contains classification file. 
     * @return true if has classification file, false otherwise.
     */
    public boolean hasClassification()
    {
        return this.classification!=null;
    }
    
     /**
     * Check if Schedule object contains metric file. 
     * @return true if has metric file, false otherwise.
     */
    public boolean hasMetric()
    {
        return this.metric!=null;
    }
    /**
     * Record the start time of this Schedule. 
     * @param startTime start time recorded. 
     */
    public void setStartTime(String startTime)
    {
        this.startTime= startTime;
    }
    
     /**
     * Record the end time of this Schedule. 
     * @param endTime end time recorded. 
     */
    public void setEndTime(String endTime)
    {
        this.endTime= endTime;
    }
    
    /**
     * Record moment of calling this method as start time. 
     */
    public void recordStartTime()
    {
        setStartTime(new Date().toString());
    }
    
    /**
     * Record moment of calling this method as end time. 
     */
    public void recordEndTime()
    {
        setEndTime(new Date().toString());
    }
    
     /**
     * Record the duration time of this Schedule. 
     * @param duration duration of the Schedule recorded. 
     */
    public void setDuration(String duration)
    {
        this.duration= duration;
    }
    
    /**
     * Set the RunSetting. 
     * @param rn RunSetting object defining parameters of an experiment.
     */
    public void setRunSetting(RunSetting rn)
    {
        this.setting= rn;
    }
    
    /**
     * Get the RunSetting.
     * @return RunSetting object associated with this Schedule.
     */
    public RunSetting getRunSetting()
    {
        return this.setting;
    }
    
    /**
     * Remove given RuleSet File. 
     * @param ruleset RuleSet file to remove.
     * @return true if File removed, false if it was not found. 
     */
    public boolean removeRuleSet(File ruleset)
    {
        return this.Rulesets.remove(ruleset);
    }
    
    /**
     * Remove given DataSet File. 
     * @param dataset DataSet file to remove.
     * @return true if File removed, false if it was not found. 
     */
    public boolean removeDataSet(File dataset)
    {
        return this.Datasets.remove(dataset);
    }
    
    /**
     * Check if it is needed to clear Input Folder before preparing for a new
     * experiment.
     * @return true if needs clearing, false otherwise.
     */
    public boolean requiresInputClear()
    {
        try
        {
            if(sp.input_ruleset_dir== null)
                SetupNotConfiguredException.occur("input_ruleset_dir");
            if(sp.input_dataset_dir== null)
                SetupNotConfiguredException.occur("input_dataset_dir");
            
            File RuleSets= new File(sp.input_ruleset_dir);
            File DataSets= new File(sp.input_dataset_dir);
        
            return RuleSets.listFiles().length!=0 || DataSets.listFiles().length!=0;
        }
        catch(Exception e)
        {
            return true;
        }
    }
    
    private boolean hasFiles(File dir)
    {
        if(!dir.isDirectory())
            return true; //located a file
        for(File subDir : dir.listFiles())
        {
            if(subDir.isDirectory() && hasFiles(subDir))
                return true;
            else
                return true; //located a file
         }
        return false;
                
    }
    
    /**
     * Check if it is needed to clear Output Folder before preparing for a new
     * experiment.
     * @return true if needs clearing, false otherwise.
     */
    public boolean requiresOutputClear()
    {
       try 
       {
           if(sp.output_dir == null)
               SetupNotConfiguredException.occur("output_dir");
           
           File dir= new File(sp.output_dir);
           return hasFiles(dir);
       }
       catch(Exception e)
       {
           return true;
       }
    }
    
    /**
     * Delete Input files without deleting the Input directories. 
     */
    public void deleteOldInput()
    {
        delete(sp.input_dir);
    }
    
     /**
     * Delete Output files without deleting the Output directories. 
     */
    public void deleteOldOutput()
    {
        delete(sp.output_dir);
    }
    
    /**
     * Delete the given file or directory. 
     * @param filename Name of file or directory to delete.
     */
    private void delete(String filename)
    {
        File f= new File(filename);
        if(f.isDirectory())
        {
            File[] list= f.listFiles();
            for(int i=0; i<list.length; i++)
                delete(list[i].getAbsolutePath());
        }
        else
            f.delete();
    }
    
    /**
     * Move all required input files into the default folders. 
     * @throws Exception Unable to complete task.
     */
    public void prepareExperiment() throws Exception
    {
        FileOutputStream fos;
        FileChannel channel;
        FileInputStream fis;
        ReadableByteChannel rbc;// Channels.newChannel(url.openStream());
        
        //transfering classes.txt
        if(sp.input_class_file == null)
            SetupNotConfiguredException.occur("input_class_file");
        fos= new FileOutputStream(sp.input_class_file);
        channel= fos.getChannel();
        fis= new FileInputStream(this.classification);
        rbc= Channels.newChannel(fis);
        channel.transferFrom(rbc, 0,this.classification.length());
        channel.close();
        fos.close();
        fis.close();
        
        //transferring metrics.txt
        if(sp.input_metric_file == null)
            SetupNotConfiguredException.occur("input_metric_file");
        fos= new FileOutputStream(sp.input_metric_file);
        channel= fos.getChannel();
        fis= new FileInputStream(this.metric);
        rbc= Channels.newChannel(fis);
        channel.transferFrom(rbc, 0,this.metric.length());
        channel.close();
        fos.close();
        fis.close();
        //transfering dataset files
        if(sp.input_dataset_dir == null)
            SetupNotConfiguredException.occur("input_dataset_dir");
        File input;
        for(int i=0; i<this.Datasets.size(); i++)
        {
            input= this.Datasets.get(i);
            fis= new FileInputStream(input);
            rbc= Channels.newChannel(fis);
            
            fos= new FileOutputStream(sp.input_dataset_dir+"/"+input.getName());
            channel= fos.getChannel();
            channel.transferFrom(rbc, 0, input.length());
            channel.close();
            fos.close();
            fis.close();
        }
        
        //transfering ruleset files
         if(sp.input_ruleset_dir == null)
            SetupNotConfiguredException.occur("input_ruleset_dir");
         for(int i=0; i<this.Rulesets.size(); i++)
        {
            input= this.Rulesets.get(i);
            fis= new FileInputStream(input);
            rbc= Channels.newChannel(fis);
            
            fos= new FileOutputStream(sp.input_ruleset_dir+"/"+input.getName());
            channel= fos.getChannel();
            channel.transferFrom(rbc, 0, input.length());
            channel.close();
            fos.close();
            fis.close();
        }
        
    }
    
    /**
     * Produce a log file for this Schedule object. 
     * @throws IOException Unable to complete task.
     */
    public void produceLog() throws IOException
    { 
        if(sp.output_dir == null)
            SetupNotConfiguredException.occur("output_dir");
        
        FileWriter fw= new FileWriter(sp.output_dir+"/"+this.name+"-log.txt");
        BufferedWriter bw= new BufferedWriter(fw);
        PrintWriter output= new PrintWriter(bw);
        
        String msg= " The Schedule "+this.name+" has completed its run."+System.lineSeparator()
                    +" The details are the following: "+System.lineSeparator()
                    +" - Start time: "+this.startTime+System.lineSeparator()
                    +" - End time: "+this.endTime+System.lineSeparator()
                    +" - Duration: "+this.duration+System.lineSeparator()
                    +" - SettingName: "+this.setting.name;
        output.println(msg);
        output.close();
        bw.close();
        fw.close();
    }
    
    /**
     * Produce an error log file for this Schedule object. 
     * @param message Error message.
     * @throws IOException Unable to complete task.
     */
    public void produceErrorLog(String message) throws IOException
    { 
        if(sp.output_dir == null)
            SetupNotConfiguredException.occur("output_dir");
        
        FileWriter fw= new FileWriter(sp.output_dir+"/"+this.name+"-log.txt");
        BufferedWriter bw= new BufferedWriter(fw);
        PrintWriter output= new PrintWriter(bw);
        
        String msg= " The Schedule "+this.name+" has failed to complete its run."+System.lineSeparator()
                    +" The details are the following: "+System.lineSeparator()
                    +" - Start time: "+this.startTime+System.lineSeparator()
                    +" - Error time: "+this.endTime+ System.lineSeparator()
                    +" - Duration: "+this.duration+System.lineSeparator()
                    +" - Error message: "+message+System.lineSeparator()
                    +" - SettingName: "+this.setting.name;
        output.println(msg);
        output.close();
        bw.close();
        fw.close();
    }
    
    /**
     * Pack the results of this Schedule object into a zip file. 
     * @return true if success, false otherwise.
     */
    public boolean pack()
    {
        try
        {
            Packager.pack(this.name);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}
