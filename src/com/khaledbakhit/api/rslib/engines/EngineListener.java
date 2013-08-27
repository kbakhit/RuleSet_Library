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

import java.util.EventListener;

/**
 * EngineListener listens to Engine related events.
 * @author Khaled Bakhit
 * @since 3.0
 * @version 15/09/2011
 */
public interface EngineListener extends EventListener
{ 
    /**
     * Notify Engine started running.
     * @param engine Engine that created this event.
     */
    public void runStarted(Engine engine);
    /**
     * Notify Engine was forced to stop running.
     * @param engine Engine that created this event.
     */
    public void runForceStopped(Engine engine);
    /**
     * Notify Engine finished running.
     * @param engine Engine that created this event.
     */
    public void runCompleted(Engine engine);
    /**
     * Notify an error has occurred.
     * @param error Exception that created the error.
     * @param engine Engine that created this event.
     */
    public void errorOccured(Exception error, Engine engine);
    /**
     * Update Engine's progress.
     * @param progress Amount of work completed.
     * @param engine Engine that created this event.
     */
    public void updateProgress(double progress, Engine engine);
    
}
