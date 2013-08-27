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
package com.khaledbakhit.api.rslib.demo;

import com.khaledbakhit.api.rslib.Host;
import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.dataset.DataSet;
import com.khaledbakhit.api.rslib.dataset.DataSetCleaner;
import com.khaledbakhit.api.rslib.dataset.DataSetFactory;
import com.khaledbakhit.api.rslib.ruleset.RuleSet;
import com.khaledbakhit.api.rslib.ruleset.RuleSetFactory;
import com.khaledbakhit.api.rslib.utils.Debugger;
import java.util.List;

/**
 * SimpleDemo demonstrates main components of RuleSet Library.
 * @author Khaled Bakhit
 * @since 27/08/2013
 */
public class SimpleDemo 
{
    
    public static void main(String[] args) throws Exception
    {
        /**
         * Step 1: Identify your application.
         * If you use default LaunchSetup configuration, the library
         * will create its input/output directories at following path:
         * [On Windows]
         * C:\Users\John\Documents\Host.ProgramName
         */
        Host.HostProgramName= "SimpleDemo";
        Host.HostProgramVersion= 1.0f;
        Host.Author= "Khaled Bakhit";
        
        Debugger.setDebugLevel(Debugger.DebugLevel.ALL);
        /**
         * Step 2: Create and Customize your default LaunchSetup Object.
         */
        LaunchSetup customSP= LaunchSetup.getDefaultLaunchSetup();
        //Customize your LaunchSetup Object as required
        
        /**
         * Step 3: Initialize instance of Program class 
         */
        Program.getInstance(customSP);//Now all library classes will refer to your LaunchSetup Object
    
        /**
         * Step 4: Parse and clean your DataSet Files
         */
        List<DataSet> datasets= new DataSetFactory().extractDataSets();
        DataSetCleaner janitor= new DataSetCleaner();
        datasets= janitor.clean(datasets);
        janitor.produceLog();
        
        /**
         * Step 5: Parse your RuleSet Files
         */
        List<RuleSet> rulesets= new RuleSetFactory().extractRuleSets();
        
        /**
         * Step 6: Test Your RuleSets
         */
        RuleSet.setNeedRuleFiredInfo(true);//Produce information about RuleSet classifications
        for(RuleSet ruleset: rulesets)
        {
            ruleset.startRecording(); //start recording results per DataSet
            for(DataSet dataset: datasets)
            {
                ruleset.test(dataset, RuleSet.TestingMode.SEQUENTIAL); 
                ruleset.record(dataset.getFile().getName()); //record DataSet test results
                ruleset.produceIndiMatrix(dataset.getFile().getName()); //produce confusion matrix related to DataSet
                ruleset.indiReset();//Reset results per DataSet
            }
            ruleset.stopRecording();
            ruleset.stopRecordingRuleSetFireInfo();
            ruleset.produceMatrix(); //Output confusion matrix 
        } 
    }
}