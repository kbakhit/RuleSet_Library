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

/**
 * Engine interface defines building blocks for library related Engines.
 * @author Khaled Bakhit
 * @since 3.0
 * @verion 15/09/2011
 */
public interface Engine extends Runnable
{
    /**
     * Launch the engine. 
     */
    public void launch();
    
    /**
     * Check if engine is done running/executing.
     * @return true if done, false otherwise.
     */
    public boolean done();
    /**
     * Get the progress of the Engine's task. 
     * @return Amount of total task completed.
     */
    public double getProgress();
    
    /**
     * Stop the execution. 
     */
    public void stop();
    
    /**
     * Attach an EngineListener to this Engine.
     * @param listener Listener to various Engine related events.
     */
    public void setEngineListner(EngineListener listener);
    /**
     * Get the EngineListener attached to this Engine. Can be null.
     * @return EngineListener listening to Engine related events.
     */
    public EngineListener getEngineListener();
}
