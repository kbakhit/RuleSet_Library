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
package com.khaledbakhit.api.rslib.analyzer;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.Library;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.utils.Debugger;
import java.io.File;
import java.io.IOException;

/**
 * StateAnalyzer2 helps summarizing your results by calculating minimum, maximum,
 * average, and standard deviation.
 * 
 * StateAnalyzer2 requires StateAnalyzer v2 to be installed in the system in same
 * parent directory as the RuleSet library implementing program.
 * 
 * @author Khaled Bakhit
 * @since 1.0
 * @version 25/08/2013
 */
public class StateAnalyzer2 
{
    /**
     * LaunchSetup Object containing directory information.
     */
     private LaunchSetup sp;
     /**
      * Name of the output directory for State Analyzer v 2.0
      */
     private String outputDir= "StateAnalyzer2";

    /**
     * StateAnalyzer2 constructor. Uses the default LaunchSetup Object.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
     public StateAnalyzer2()
     {
         this(Program.getInstance().getLaunchSetup());
     }  
     /**
     * StateAnalyzer2 constructor.
     * @param sp LaunchSetup Object containing input configuration.
     */
     public StateAnalyzer2(LaunchSetup sp)
     {
        this.sp= sp;
        if(sp.output_dir == null)
            SetupNotConfiguredException.occur("output_dir");
        outputDir= sp.output_dir+"/"+outputDir;
        File check= new File(outputDir);
        if(!check.exists())
            check.mkdir();
     }

     /**
      * Analyze and produce results related to the RuleSet library outputs.
      * <br> This is a blocking call. The execution will wait until State Analyzer
      * 2 is terminated. </br>
      * @param type Output type.
      * @param prefix String prefix.
      */
     public void produce(Library.OUTPUT_TYPE type, String prefix)
    {
        try {
            File output= new File(outputDir);
            File input;
            if(prefix.trim().length()>0)
                prefix= prefix+"_";
            
            File f= new File("virtual.m");
            String url= f.getAbsolutePath();
            url= url.substring(0, url.length()- f.getName().length()-1);  
            url= url.substring(0,url.lastIndexOf('\\'));
            
            File hp= new File(url+"/State Analyzer/SA2.jar");
            if(!hp.exists())
            {
               
                Debugger.printlnWarning("State Analyzer 2 wasn't found.");
                Debugger.printlnWarning("The path "+hp.getAbsolutePath()+" should contain SA2.jar.");
                Debugger.printlnWarning("Aborting generating result summaries with SA2");
                return;
            }
            hp= new File(url+"/State Analyzer");
            
            String command= "java -jar SA2.jar -o \""+output.getAbsolutePath()+"\" -t "+type;
            
            String subcommand; 
            input= new File(sp.ruleset_output_individual_results_dir);
            subcommand= command+" -i \""+input.getAbsolutePath()+"\" -p "+prefix+"RuleSets";
          
          
            Runtime.getRuntime().exec(subcommand, null, hp);
       
            input= new File(sp.dataset_output_individual_results_dir);
            subcommand= command+" -i \""+input.getAbsolutePath()+"\" -p "+prefix+"DataSets";
          
            Process p= Runtime.getRuntime().exec(subcommand, null, hp);
            try
            {
                p.waitFor();
            }
            catch(Exception e)
            {
                Debugger.printlnWarning("Failed to lunch SA 2!");
            }
        } 
        catch (IOException ex) 
        {
            Debugger.printlnError(ex);
        }
     }

     /**
      * Analyze and produce results related to the RuleSet library outputs. 
      */
     public void produce()
     {
         produce(Library.OUTPUT_TYPE.XLS,"");
     }
     /**
      * Analyze and produce results related to the RuleSet library outputs. 
      * @param type Output type.
      */
     public void produce(Library.OUTPUT_TYPE type)
     {
         produce(type, "");
     }
    
}
