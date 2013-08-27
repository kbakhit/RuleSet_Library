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
package com.khaledbakhit.api.rslib.engines;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.analyzer.StateAnalyzer2;
import com.khaledbakhit.api.rslib.dataset.DataSet;
import com.khaledbakhit.api.rslib.dataset.DataSetCleaner;
import com.khaledbakhit.api.rslib.dataset.DataSetFactory;
import com.khaledbakhit.api.rslib.dataset.DataSetOrganizer;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.ruleset.RuleSet;
import com.khaledbakhit.api.rslib.ruleset.RuleSetFactory;
import com.khaledbakhit.api.rslib.schedule.RunSetting;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * DefaultEngine runs experiments using Multi-threading approach maximizing
 * speed and CPU utilization.
 * @author Khaled Bakhit
 * @since 4.0
 * @version 25/08/2013
 */
public class DefaultEngine implements Engine 
{
    /**
     * LaunchSetup Object containing input configuration.
     */
    private LaunchSetup sp;
    /**
     * RunSetting Object containing experiment configuration.
     */
    private RunSetting rn;
    /**
     * ExecutorService Object to run tasks in parallel.
     */
    private ExecutorService executor;
    /**
     * EnginerListener Object listening to Engine's events.
     */
    private EngineListener listener;
    /**
     * Flag indicating whether Engine is done.
     */
    private volatile boolean done;
    /**
     * Flag indicating whether Engine has been forced to stop.
     */
    private volatile boolean stop;
    /**
     * Current progress of work.
     */
    private volatile double progress;
    /**
     * Maximum progress value that can be reached.
     */
    private volatile double max_progress_val;
    
    /**
     * DefaultEngine constructor.
     * @param rn RunSetting Object containing experiment configuration.
     * @param sp LaunchSetup Object containing input configuration.
     */
    public DefaultEngine(RunSetting rn, LaunchSetup sp)
    {
        this.sp= sp;
        this.rn= rn;
        stop= false;
        done= false;
        progress= 0;
        max_progress_val= 100;
    }
    
    @Override
    public void launch()
    {
        if(executor!=null)
            executor.shutdownNow();
        
        executor= Executors.newFixedThreadPool(
                (rn.auto_detectCPU || rn.number_of_threads<=0)?
                        Runtime.getRuntime().availableProcessors():
                                rn.number_of_threads);
        new Thread(this).start();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void run() 
    {
        try
        {
            if(listener!=null)
                listener.runStarted(this);
            progress= 0;
            if(stop)
            {
                terminate();
                return;
            }
            List<DataSet> ds= null; //store in memory, faster but more memory usage.
            if(rn.store_dataset_in_memory)
                ds= new DataSetFactory().fastExtractDataSets(executor);
               
            if(stop)
            {
                terminate();
                return;
            }
            
            if(rn.dataset_clean)
            {
                DataSetCleaner janitor= new DataSetCleaner(sp);
                if(ds!=null)
                    ds= janitor.fastClean(ds, executor);
                else
                    janitor.fastClean(executor);
                if(rn.dataset_log)
                    janitor.produceLog();
            }
            
            if(stop)
            {
                terminate();
                return;
            }
            
            if(rn.dataset_organize)
            {
                DataSetOrganizer org= new DataSetOrganizer(sp);
                if(ds!=null)
                    org.fastOrganize(rn.dataset_organizeType, ds, executor);
                else
                    org.fastOrganize(rn.dataset_organizeType, executor);
            }
            
            if(stop)
            {
                terminate();
                return;
            }
            
            List<RuleSet> rs= new RuleSetFactory(sp).fastExtractRuleSets(executor);
            
            if(stop)
            {
                terminate();
                return;
            }
            
            if(rn.ruleset_verify)
            {
                if(sp.ruleset_verifier == null)
                    SetupNotConfiguredException.occur("ruleset_verifier");
                sp.ruleset_verifier.setAutoCorrect(rn.ruleset_autoCorrect);
                sp.ruleset_verifier.fastVerify(rs, executor);
            }
               
            RuleSet.setTestingMode(rn.testingMode);
            if(rn.is_matching_within_range)
                RuleSet.activateMatchingWithinRange(rn.matching_range);
            else
                RuleSet.deactivateMatchingWithinRange();
            RuleSet.setNeedRuleFiredInfo(rn.rule_track);
           

            if(stop)
            {
                terminate();
                return;
            }
            List<Callable<Void>> partitions= new LinkedList<Callable<Void>>();
       
            final List generics= new LinkedList();
            if(ds!=null)
                generics.addAll(ds);
            else
                generics.addAll(Arrays.asList(new File(sp.input_dataset_dir).listFiles()));
            
            max_progress_val= rs.size() * generics.size();
      
            for(RuleSet rule_set: rs)
            {
                final RuleSet RULESET = rule_set;
          
                partitions.add(new Callable<Void>() 
                {
                    
                    @Override              
                    public Void call() throws Exception 
                    { 
                        if(rn.ruleset_indiresult)
                            RULESET.startRecording();
                        String name;
                        for(Object o : generics)
                        {
                            if(stop)
                                return null;
                            RULESET.setOutputType(rn.ruleset_resultType);
                            if(rn.store_dataset_in_memory)
                            {
                                RULESET.test((DataSet) o);
                                name= ((DataSet)o).getFile().getName();
                            }
                            else
                            {
                                RULESET.test((File) o);
                                name= ((File)o).getName();
                            }
                            
                            RULESET.record(name);
                            RULESET.setOutputType(rn.ruleset_matrixType);
                            if(rn.ruleset_indimatrix)
                                RULESET.produceIndiMatrix( name ); 
                      
                            RULESET.indiReset();
                            updateProgress();
                        }
                        if(stop)    
                            return null;
                        
                        if(rn.ruleset_indiresult)
                            RULESET.stopRecording();
                  
                        RULESET.stopRecordingRuleSetFireInfo();
                        RULESET.setOutputType(rn.ruleset_matrixType);
                  
                        if(rn.ruleset_matrix)
                            RULESET.produceMatrix();
                        if(rn.ruleset_definition)
                            RULESET.produceDefinition();
                  
                        return null;
                    }
                });
            }
            List<Future<Void>> results= executor.invokeAll(partitions);
            executor.shutdown();
            for(Future<Void> re: results)
                re.get();
            if(stop)
            {
                   if(listener!= null)
                        listener.runForceStopped(this);
                   return;
            }
            if(rn.sa_output_enable)
                new StateAnalyzer2(sp).produce(rn.sa_outputType);
            if(listener!=null)
                listener.runCompleted(this);
            
        }
        catch(Exception e)
        {
            if(listener!=null)
                listener.errorOccured(e, this);
        }
        finally
        {
            stop();
        }
    }

    public boolean done() 
    {
        return done;
    }

    @Override
    public double getProgress() 
    {
        
        return (progress*100)/max_progress_val;
    }

    @Override
    public void stop() 
    {
        done= stop= true;
    }
    
    private synchronized void updateProgress()
    {
        progress++;
        if(listener!= null)
            listener.updateProgress(getProgress(), this);
    }
    
   
    
    private void terminate()
    {
        executor.shutdownNow();
        if(listener!= null)
            listener.runForceStopped(this);
    }

    @Override
    public void setEngineListner(EngineListener listener) 
    {
        this.listener= listener;
    }

    @Override
    public EngineListener getEngineListener() 
    {
        return listener;
    }

    
}
