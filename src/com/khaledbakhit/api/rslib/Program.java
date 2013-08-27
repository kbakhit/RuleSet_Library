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

import java.util.Random;

/**
 * Program class that contains all library's global variables used.
 * Only one instance at a time exists from this Class.
 * 
 * @author Khaled Bakhit
 * @since 1.0
 * @version 25/08/2013
 */
public class Program implements java.io.Serializable
{    
    
    private static final long serialVersionUID = 119052011L;
    
    /**
     * Global static Random Object to be used across the library.
     */
    public static Random rand= new Random();
    
    /**
     * LaunchSetup Object defining directories information.
     * @since 4.0
     */
    private LaunchSetup sp;
    /**
     * Default Program instance.
     */
    private static Program prog; 
    /**
     * Program Constructor.
     */
    public Program(LaunchSetup sp)
    {
        this.sp= sp;
        this.sp.init();
        this.sp.createDirectories();
   }
    /**
     * Get LaunchSetup Object used for directories definition. 
     * @return LaunchSetup Object used for directories definition.
     */
    public LaunchSetup getLaunchSetup()
    {
        return sp;
    }
    /**
    * Get the current instance of Program Object. <b>Uses the default LaunchSetup
    * Object defined by LaunchSetup Class.</b>
    * Only one instance at a time exists from this Class.
    * @return Instance of Program Class.
    * @see LaunchSetup#getDefaultLaunchSetup() 
    * @since 4.0
    */
    public synchronized static Program getInstance()
    {
        return getInstance(LaunchSetup.getDefaultLaunchSetup());
    }
   /**
    * Get the current instance of Program Object.
    * Only one instance at a time exists from this Class.
    * @param sp LaunchSetup Object containing input configuration.
    * @return Instance of Program Class.
    * @since 4.0
    */
    public synchronized static Program getInstance(LaunchSetup sp)
    {
        if(prog==null || prog.sp!= sp)
        {
            prog= new Program(sp);
            LaunchSetup.setDefaultLaunchSetup(sp);
        }
        return prog;
    }
    /**
     * Get a fresh instance of Program Class.
     * Only one instance at a time exists from this Class.
     * @param sp LaunchSetup Object containing input configuration.
     * @return Fresh instance of Program Class.
     * @since 4.0
     */
    public synchronized static Program getFreshInstance(LaunchSetup sp)
    {
        clearInstance();
        return getInstance(sp);
    }
    /**
     * Clear the current default instance of Program Class.
     */
    public synchronized static void clearInstance()
    {
        prog= null;
    }
}