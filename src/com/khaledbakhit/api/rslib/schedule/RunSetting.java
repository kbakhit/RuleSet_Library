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

import com.khaledbakhit.api.rslib.Host;
import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.Library;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.ruleset.RuleSet;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

/**
 * RunSetting contains parameters definition for advanced experiment runs.
 * @author Khaled Bakhit
 * @since 3.0
 * @verion 25/08/2013
 */
public class RunSetting implements java.io.Serializable 
{  
  private static final long serialVersionUID = 115092011L;
  /**
   * Name of this RunSetting Object.
   */
  public String name= System.currentTimeMillis()+""; 
  /**
   * Description of this RunSetting Object.
   */
  public String description="";
  /**
   * Testing Mode of the run.
   */
  public RuleSet.TestingMode testingMode;
  /**
   * Flag indicating if run is matching within range.
   */
  public boolean is_matching_within_range;
  /**
   * Matching within range value.
   */
  public double matching_range;
  /**
   * Flag indicating whether to store DataSet in main memory or not.
   */
  public boolean store_dataset_in_memory;
  //DataSet
  /**
   * Flag indicating whether cleaning of DataSets is required or not.
   */
  public boolean dataset_clean;
  /**
   * Flag indicating whether DataSet cleaning log must be produced or not.
   * Requires {@link #dataset_clean}<code> = true</code>
   */
  public boolean dataset_log;
  /**
   * Flag indicating whether organizing of DataSets is required or not.
   */
  public boolean dataset_organize;
  /**
   * Output type for organizing DataSets.
   * Requires {@link #dataset_organize}<code> = true</code>
   */
  public Library.OUTPUT_TYPE dataset_organizeType;
  
  //RuleSet
  /**
   * Flag indicating whether RuleSet tracking is required or not.
   */
  public boolean rule_track;
   /**
   * Flag indicating whether RuleSet definition output is required or not.
   */
  public boolean ruleset_definition;
  /**
   * Flag indicating whether RuleSet confusion matrix output is required or not.
   */
  public boolean ruleset_matrix;
   /**
   * Flag indicating whether RuleSet individual confusion matrix output is required or not.
   */
  public boolean ruleset_indimatrix;
   /**
   * Output type for RuleSet confusion matrix.
   * Requires {@link #ruleset_matrix}<code> = true</code> or {@link #ruleset_indimatrix}<code> = true</code>
   */
  public Library.OUTPUT_TYPE ruleset_matrixType;
   /**
   * Flag indicating whether RuleSet individual results output is required or not.
   */
  public boolean ruleset_indiresult;
   /**
   * Output type for RuleSet individual results.
   * Requires {@link #ruleset_indiresult}<code> = true</code> 
   */
  public Library.OUTPUT_TYPE ruleset_resultType;
  /**
   * Flag indicating whether RuleSet classification/metrics correction is required or not.
   */
  public boolean ruleset_verify;
  /**
   * Flag indicating whether auto-correction should be used or not.
   * Requires {@link #ruleset_verify}<code> = true</code> 
   */
  public boolean ruleset_autoCorrect;
  
  //State Analyzer 2.0
  /**
   * Flag indicating whether State Analyzer output required or not.
   */
  public boolean sa_output_enable;   
  /**
   * Output type for State Analyzer.
   * Requires {@link #sa_output_enable}<code> = true</code> 
   */
  public Library.OUTPUT_TYPE sa_outputType;
  
  //CPU setting
  /**
   * Flag indicating whether number of threads must be equal to number of processors or not.
   */
  public boolean auto_detectCPU;
  /**
   * Number of threads allowed for multi-threaded operations.
   * Requires {@link #auto_detectCPU}<code> = false</code> 
   */
  public int number_of_threads;
    
  /**
   * Produce a description of this RunSetting Object into a file.
   * Uses the default LaunchSetup Object.
   * @throws IOException Unable to complete task.
   * @see Program#getInstance()
   * @see Program#getLaunchSetup()
   */
  public void produceDescription() throws IOException
  {
      produceDescription(Program.getInstance().getLaunchSetup());
  }
  
 /**
  * Produce a description of this RunSetting Object into a file.
  * @param sp LaunchSetup Object containing input configuration.
  * @throws IOException Unable to complete task.
  * @since 4.0
  */
  public void produceDescription(LaunchSetup sp) throws IOException
  {
      if(sp.output_dir ==  null)
          SetupNotConfiguredException.occur("output_dir");
      FileWriter fw= new FileWriter(sp.output_dir+"/RunSetting.txt");
      BufferedWriter bw= new BufferedWriter(fw);
      PrintWriter output= new PrintWriter(bw);
      output.println("---------------RunSetting---------------");
      output.println("Name: "+this.name);
      output.println("Description: "+this.description);
    
      output.println();
      output.println("The setting are the following:");
      output.println("store DataSet in memory: "+store_dataset_in_memory);
      output.println("dataset cleaning on: "+this.dataset_clean);
      output.println("dataset log on: "+this.dataset_log);
      output.println("dataset re-organization on: "+this.dataset_organize);
      output.println("dataset re-organization output type: "+this.dataset_organizeType);
      
      output.println("rule testing mode: "+ testingMode);
      output.println("is matching within range: "+ is_matching_within_range);
      output.println("range value: "+ matching_range);
      output.println("rule tracking on: "+this.rule_track);
      output.println("ruleset verification on: "+this.ruleset_verify);
      output.println("ruleset verification auto-correct: "+this.ruleset_autoCorrect);
      output.println("ruleset defintion production on: "+this.ruleset_definition);
      output.println("ruleset confusion matrix production on: "+this.ruleset_matrix);
      output.println("ruleset individual confusion matrix production on: "+this.ruleset_indimatrix);
      output.println("ruleset confusion matrix output type: "+ this.ruleset_matrixType);
      output.println("rulset indi. results output production on: "+this.ruleset_indiresult);
      output.println("ruleset indi. results output type: "+ this.ruleset_resultType);
      
   
      output.println("State Analyzer 2.0 output production on: "+this.sa_output_enable);
      output.println("State Analyzer 2.0 output type: "+this.sa_outputType);
   
      output.println();
      output.println("Number of Threads used: "+this.number_of_threads);
      output.println("Host program running "+Library.NAME+" "+Library.VERSION+": ");
      output.println("Name: "+Host.HostProgramName);
      output.println("Version: "+Host.HostProgramVersion);
      output.println("Author: "+Host.Author);
      output.println();
      output.println(Library.NAME+" is created by "+Library.AUTHOR);
  }
  
  /**
   * ExtremeSpeed setting skips DataSet cleaning, organizing and RuleSet matrix
   * production and definition production. It also sets all outputs types as
   * CSV and State Analyzer 2.0 output type as TXT. Disables debugging.
   * @return ExtremeSpeed RunSetting object.
   */
  public static RunSetting getExtremeSpeed()
  {
      RunSetting rn= new RunSetting();
      rn.name= "ExtremeSpeed";
      rn.description=" ExtremeSpeed setting skips DataSet cleaning, organizing and RuleSet matrix "
              +"production and definition production. It also sets all outputs types as "+
    "CVS and State Analyzer 2.0 output type as text. Disables debugging.";
      
      rn.testingMode= RuleSet.TestingMode.SEQUENTIAL;
      rn.is_matching_within_range= false;
      rn.matching_range= 0;      
      rn.store_dataset_in_memory= true;
      rn.dataset_clean= false;
      rn.dataset_log= false;
      rn.dataset_organize= false;
      rn.dataset_organizeType= Library.OUTPUT_TYPE.CSV;
      rn.ruleset_definition= false;
      rn.ruleset_matrix= false;
      rn.ruleset_matrixType= Library.OUTPUT_TYPE.CSV;
      rn.ruleset_resultType= Library.OUTPUT_TYPE.CSV;
     
      rn.auto_detectCPU= true;
      rn.number_of_threads= Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS"));
      rn.sa_output_enable= true;
      rn.sa_outputType= Library.OUTPUT_TYPE.TEXT; 
      rn.ruleset_indimatrix= true;
      rn.ruleset_indiresult= true;
      rn.ruleset_verify= false;
      rn.ruleset_autoCorrect= false;
      rn.rule_track= false;
      return rn;
  }
  
  /**
   * HighSpeed setting enables all features but produces output in less
   * professional way as CSV format. State Analyzer 2.0 output type is XLS.
   * Disables debugging.
   * @return HighSpeed RunSetting object.
   */
  public static RunSetting getHighSpeed()
  {
      RunSetting rn= new RunSetting();
      rn.name= "HighSpeed";
      rn.description=" HighSpeed setting enables all features but produces output in less "+
      "professional way as CVS format. State Analyzer 2.0 output type is xls. "+
      "Disables debugging.";
      rn.testingMode= RuleSet.TestingMode.SEQUENTIAL;
      rn.is_matching_within_range= false;
      rn.matching_range= 0;   
      rn.store_dataset_in_memory= true;
      rn.dataset_clean= true;
      rn.dataset_log= true;
      rn.dataset_organize= true;
      rn.dataset_organizeType= Library.OUTPUT_TYPE.CSV;
      
      rn.ruleset_definition= true;
      rn.ruleset_matrix= true;
      rn.ruleset_matrixType= Library.OUTPUT_TYPE.CSV;
      rn.ruleset_resultType= Library.OUTPUT_TYPE.CSV;
      
      rn.auto_detectCPU= true;
      rn.number_of_threads= Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS"));
      rn.sa_output_enable= true;
      rn.sa_outputType= Library.OUTPUT_TYPE.XLS; 
      rn.ruleset_indimatrix= true;
      rn.ruleset_indiresult= true;
      rn.ruleset_verify= true;
      rn.ruleset_autoCorrect= true;
      rn.rule_track= true;
      return rn;
  }
  
  /**
   * ProfessionalSetting setting enables all features and produces output in a
   * professional way as XLS format. State Analyzer 2.0 output type is XLS.
   * Enables debugging.
   * @return HighSpeed RunSetting object.
   */
  public static RunSetting getProfessionalSetting()
  {
      RunSetting rn= new RunSetting();
      rn.name="Professional";
      rn.description=" ProfessionalSetting setting enables all features and produces output in a "
      +"professional way as XLS format. State Analyzer 2.0 output type is xls. "
      +"Enables debugging.";
      rn.testingMode= RuleSet.TestingMode.SEQUENTIAL;
      rn.is_matching_within_range= false;
      rn.matching_range= 0;   
      rn.store_dataset_in_memory= true;
      rn.dataset_clean= true;
      rn.dataset_log= true;
      rn.dataset_organize= true;
      rn.dataset_organizeType= Library.OUTPUT_TYPE.XLS;
      
      rn.ruleset_definition= true;
      rn.ruleset_matrix= true;
      rn.ruleset_matrixType= Library.OUTPUT_TYPE.XLS;
      rn.ruleset_resultType= Library.OUTPUT_TYPE.XLS;

      rn.sa_output_enable= true;
      rn.sa_outputType= Library.OUTPUT_TYPE.XLS; 
      rn.auto_detectCPU= true;
      rn.number_of_threads= Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS"));
      rn.ruleset_indimatrix= true;
      rn.ruleset_indiresult= true;
      rn.ruleset_verify= true;
      rn.ruleset_autoCorrect= true;
      rn.rule_track= true;
      return rn;
  }
  
  /**
   * Save this RunSetting object into the following directory. 
   * @param dir Directory to save into.
   * @throws IOException Unable to save object.
   */
  public void save(String dir) throws IOException
  {
      FileOutputStream fos= new FileOutputStream(dir+"/"+name+".rsetting");
      ObjectOutputStream oos= new ObjectOutputStream(fos);
      oos.writeObject(this);
      oos.flush();
      oos.close();
  }
  
  /**
   * Save this RunSetting object into the default location. 
   * Uses the default LaunchSetup Object.
   * @throws IOException Unable to complete task.
   * @see Program#getInstance()
   * @see Program#getLaunchSetup()
   */
  public void save() throws IOException
  {
      save(Program.getInstance().getLaunchSetup());
  }
  /**
   * Save this RunSetting object into the default location. 
   * @param sp LaunchSetup Object containing input configuration.
   * @throws IOException Unable to complete task.
   * @since 4.0
   */
  public void save(LaunchSetup sp) throws IOException 
  {
      if(sp.input_runsettings_dir== null)
          SetupNotConfiguredException.occur("input_runsettings_dir");
      save(sp.input_runsettings_dir);
  }
  
  /**
   * Load RunSetting object from given file.
   * @param filename Name of file containing RunSetting object.
   * @throws IOException Unable to load object.
   * @throws ClassNotFoundException Loaded File is not instance of RunSetting.
   */
  public void load(String filename) throws IOException, ClassNotFoundException
  {
      FileInputStream fis= new FileInputStream(filename);
      ObjectInputStream ois= new ObjectInputStream(fis);
      RunSetting temp= (RunSetting)ois.readObject();
      this.copy(temp);
  }
  /**
   * Copy RunSetiings Object.
   * @param rn RunSettings Object to copy from.
   */
  public void copy(RunSetting rn)
  {
      this.name= rn.name;
      this.description= rn.description;
      this.testingMode= rn.testingMode;
      this.is_matching_within_range= rn.is_matching_within_range;
      this.matching_range= rn.matching_range;   
      this.store_dataset_in_memory= rn.store_dataset_in_memory;
      this.dataset_clean=rn.dataset_clean;
      this.dataset_log= rn.dataset_log;
      this.dataset_organize=rn.dataset_organize;
      this.dataset_organizeType=rn.dataset_organizeType;
      
      this.ruleset_definition=rn.ruleset_definition;
      this.ruleset_matrix=rn.ruleset_matrix;
      this.ruleset_matrixType=rn.ruleset_matrixType;
  
      this.sa_output_enable= rn.sa_output_enable;
      this.sa_outputType= rn.sa_outputType; 
      this.ruleset_resultType= rn.ruleset_resultType;
      
      this.auto_detectCPU= rn.auto_detectCPU;
      this.number_of_threads= rn.number_of_threads;
      this.ruleset_indimatrix= rn.ruleset_indimatrix;
      this.ruleset_indiresult= rn.ruleset_indiresult;
      this.ruleset_verify=rn.ruleset_verify;
      this.ruleset_autoCorrect= rn.ruleset_autoCorrect;
      this.rule_track= rn.rule_track;
  }
}