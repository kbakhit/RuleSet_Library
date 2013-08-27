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

import com.khaledbakhit.api.rslib.dataset.DataSetReader;
import com.khaledbakhit.api.rslib.dataset.DataSetWriter;
import com.khaledbakhit.api.rslib.parsers.ClassificationsParser;
import com.khaledbakhit.api.rslib.parsers.MetricsParser;
import com.khaledbakhit.api.rslib.parsers.RuleSetParser;
import com.khaledbakhit.api.rslib.ruleset.DefaultRulSetVerifier;
import com.khaledbakhit.api.rslib.ruleset.RuleSetVerifier;
import com.khaledbakhit.api.rslib.ruleset.RuleSetWriter;
import com.khaledbakhit.api.rslib.utils.Debugger;
import java.io.File;

/**
 * LaunchSetup defines important input configuration and parameters.
 * 
 * @author Khaled Bakhit
 * @since 4.0
 * @version 25/08/2013
 */
public abstract class LaunchSetup implements java.io.Serializable 
{
    /**
     * Default LaunchSetup Object defining input configuration.
     */
    private static LaunchSetup DEFAULT_LAUNCH_SETUP= null;
    /**
     * Default parser to parse classifications.
     */
    public ClassificationsParser classification_parser;
    /**
     * Default parser to parse metrics.
     */
    public MetricsParser metrics_parser;
    /**
     * Default parser to parse RuleSets.
     */
    public RuleSetParser ruleset_parser;
    /**
     * Default reader to read DataSets.
     */
    public DataSetReader dataset_reader;
    /**
     * Default writer to write DataSets.
     */
    public DataSetWriter dataset_writer;
    /**
     * Default writer to write RuleSets.
     */
    public RuleSetWriter ruleset_writer;
    /**
     * Default verifier for RuleSets.
     */
    public RuleSetVerifier ruleset_verifier;
    /**
     * Main Home Directory. 
     */
    public String home_dir;
    /**
     * Program Data Directory defined by OS.
     */
    public String program_data_dir;
    
     //--List of Input Folders
     /**
     * Name of input directory. 
     */
    public String input_dir;
    /**
     * Name of input class file. 
     */
    public String input_class_file;
    /**
     * Name of input metric file. 
     */
    public String input_metric_file;
    /**
     * Name of RuleSet input directory.
     */
    public String input_ruleset_dir;
    /**
     * Name of DataSet input directory.
     */
    public String input_dataset_dir;
    /**
     * Name of Run Settings directory.
     */
    public String input_runsettings_dir;
    /**
     * Name of Formulas input directory.
     */
    public String input_formulas_dir;
    
    //--List of Output Folders
    /**
     * Name of output directory. 
     */
    public String output_dir;
    /**
     * Name of output completed directory. All completed experiments are saved
     * here in zip format.
     */
    public String output_completed_dir;
    /**
     * Main directory containing RuleSet outputs.
     */
    public String ruleset_output_dir;
    /**
     * Directory containing RuleSet confusion matrices.
     */
    public String ruleset_output_matrix_dir;
    /**
     * Directory containing RuleSet test results against DataSets.
     */
    public String ruleset_output_results_dir;
    /**
     * Directory containing RuleSet definitions.
     */
    public String ruleset_output_definition_dir;
    /**
     * Directory containing RuleSet test results against individual DataSets.
     */
    public String ruleset_output_individual_results_dir;
    /**
     * Directory containing information about which Rule within each RuleSet
     * classified given DataSet line.
     */
    public String ruleset_output_fireinfo_dir;
    /**
     * Main directory for DataSet output.
     */
    public String dataset_output_dir;
    /**
     * Directory containing organized DataSet files.
     */
    public String dataset_output_data_dir;
    /**
     * Directory containing DataSet cleaning log files.
     */
    public String dataset_output_log_dir;
    /**
     * Directory containing DataSet test results against individual RuleSets.
     */
    public String dataset_output_individual_results_dir;
   
    /**
     * Initialize variables that point to directories on disk and reader/writer Objects.
     */
    public abstract void init();

    /**
     * Create all the involved directories.
     */
    public final void createDirectories()
    {
        // <editor-fold defaultstate="collapsed" desc="creating directories">
        checkDir(home_dir);
        checkDir(program_data_dir);
        checkDir(input_dir);
        checkDir(output_dir);
 
        checkDir(input_dataset_dir);
        checkDir(input_ruleset_dir);
        checkDir(dataset_output_dir);
        checkDir(ruleset_output_dir);
        
        
        checkDir(input_runsettings_dir);
        checkDir(input_formulas_dir);
        checkDir(input_runsettings_dir);
        checkDir(output_completed_dir);
        
        checkDir(dataset_output_data_dir);
        checkDir(dataset_output_log_dir);
        checkDir(dataset_output_individual_results_dir);
        
        checkDir(ruleset_output_matrix_dir);
        checkDir(ruleset_output_results_dir);
        checkDir(ruleset_output_fireinfo_dir);
        checkDir(ruleset_output_definition_dir);
        checkDir(ruleset_output_individual_results_dir);
        
        Debugger.printlnInfo(Library.NAME+" HomeDir= "+ home_dir);
       // </editor-fold>  
    }
    /**
     * Get the default LaunchSetup Object.
     * @return Default LaunchSetup Object.
     */
    public static LaunchSetup getDefaultLaunchSetup()
    {
        if(DEFAULT_LAUNCH_SETUP!=null)
            return DEFAULT_LAUNCH_SETUP;
        
        LaunchSetup sp= new LaunchSetup() 
        {
            private static final long serialVersionUID = 125082013L;

            @Override
            public void init() 
            {
                home_dir= getDocumentsHome();
                program_data_dir= getAppDir()+"/"+Host.HostProgramName;

                input_dir= home_dir+"/Input";
                output_dir= home_dir+"/Output";

                input_dataset_dir= input_dir+"/DataSet";
                input_ruleset_dir= input_dir+"/RuleSet";
                input_class_file= input_dir+"/classes.txt";
                input_metric_file= input_dir+"/metrics.txt";
                

                input_runsettings_dir= home_dir+"/RunSettings";
                input_formulas_dir= home_dir+"/Formulas";
                output_completed_dir= home_dir+"/Completed";
                ruleset_output_dir= output_dir+"/RuleSet_Output";

                ruleset_output_matrix_dir= ruleset_output_dir+"/Matrix";
                ruleset_output_results_dir= ruleset_output_dir+"/Results";
                ruleset_output_definition_dir= ruleset_output_dir+"/Definition";
                ruleset_output_individual_results_dir= ruleset_output_dir+"/IndiResults";
                ruleset_output_fireinfo_dir= ruleset_output_dir+"/RuleFireInfo";

                dataset_output_dir= output_dir+"/DataSet_Output";
                dataset_output_data_dir= dataset_output_dir+"/Data";
                dataset_output_log_dir= dataset_output_dir+"/Log";
                dataset_output_individual_results_dir= dataset_output_dir+"/IndiResults";
                
                classification_parser = ClassificationsParser.getDefaultClassificationsParser();
                metrics_parser= MetricsParser.getDefaultMetricsParser();
                ruleset_parser= RuleSetParser.getDefaultRuleSetParser(this);
                dataset_reader= DataSetReader.getDefaultDataSetReader(this);
                
                dataset_writer= DataSetWriter.getDefaultDataSetWriter(this);
                ruleset_writer= RuleSetWriter.getDefaultRuleSetWriter(this);
                try
                {
                    ruleset_verifier= new DefaultRulSetVerifier(true, this);
                }
                catch(Exception e)
                {
                    Debugger.printlnError(e);
                }
            }
        };
           
        DEFAULT_LAUNCH_SETUP= sp;
        return DEFAULT_LAUNCH_SETUP;
    }
    /**
     * Set the default LaunchSetup Object.
     * @param sp LaunchSetup Object that should be the default one.
     */
    public static void setDefaultLaunchSetup(LaunchSetup sp)
    {
        DEFAULT_LAUNCH_SETUP= sp;
    }
    /**
     * Check if Directory exists. If it does not, create it.
     * @param dirName Directory to check.
     */
    private void checkDir(String dirName)
    {
      File file= new File(dirName);
      if(!file.exists())
           file.mkdirs();
    }
    /**
     * Get the documents home directory according to OS. 
     * @return Path to Home directory.
     */
    protected String getDocumentsHome()
    {
        String home=System.getProperty("user.home");
        String softName= Host.HostProgramName;
        if(softName==null)
            softName= Library.NAME;
        File f= new File(home+"/Documents");
        if(f.exists())
        {
            f= new File(home+"/Documents/"+softName);
            if(!f.exists())
                f.mkdir();
            return f.getAbsolutePath();
        }
        f= new File(home+"/My Documents");
        if(f.exists())
        {
             f= new File(home+"/My Documents/"+softName);
            if(!f.exists())
                f.mkdir();
            return f.getAbsolutePath();
        }
        f= new File(softName);
        if(!f.exists())
            f.mkdir();
        return softName;
    }
    /**
     * Get App Data default directory as defined by OS.
     * @return App Data directory.
     */
    protected String getAppDir()
    {
        String hidden= System.getenv("ProgramData"); //Windows Vista+
        if(hidden!=null)
            return hidden;
        hidden= System.getenv("APPDATA");// Windows XP and below
        if(hidden!=null)
            return hidden;
        hidden= System.getenv("appdata");// Windows XP and below
        if(hidden!=null)
            return hidden;
        hidden= System.getenv("HOME");
        if(hidden!=null)
            return hidden;
        return System.getenv("home");
    }   
}