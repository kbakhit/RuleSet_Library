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
import com.khaledbakhit.api.rslib.engines.DefaultEngine;
import com.khaledbakhit.api.rslib.engines.Engine;
import com.khaledbakhit.api.rslib.engines.EngineListener;
import com.khaledbakhit.api.rslib.schedule.RunSetting;
import com.khaledbakhit.api.rslib.utils.Debugger;

/**
 * EngineDemo class demonstrates how to setup RuleSet Library and use Engine instances.
 * 
 * @author Khaled Bakhit
 * @since 4.0
 * @version 27/08/2013
 */
public class EngineDemo
{
    public static void main(String[] args)
    {
        /**
         * Step 1: Identify your application.
         * If you use default LaunchSetup configuration, the library
         * will create its input/output directories at following path:
         * [On Windows]
         * C:\Users\John\Documents\Host.ProgramName
         */
        Host.HostProgramName= "EngineDemo";
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
         * Step 4: Create and Customize your RunSetting Object.
         */
        RunSetting rn= RunSetting.getHighSpeed();
        rn.name= "Engine Demo RunSetting";
        
        /**
         * Step 5: Create and Run your Engine
         */
        Engine engine= new DefaultEngine(rn, customSP);
        engine.setEngineListner(new EngineListener() 
        {
            @Override
            public void runStarted(Engine engine) {
                Debugger.printlnInfo("Run Started!");
            }

            @Override
            public void runForceStopped(Engine engine) {
                Debugger.printlnInfo("Run Stopped!");
            }

            @Override
            public void runCompleted(Engine engine) {
                Debugger.printlnInfo("Run Completed!");
            }

            @Override
            public void errorOccured(Exception error, Engine engine) {
                Debugger.printlnError(error);
            }

            @Override
            public void updateProgress(double progress, Engine engine) {
                Debugger.printlnInfo(progress + " % completed!");
            }
        });
        
        engine.launch();    
    }

}
