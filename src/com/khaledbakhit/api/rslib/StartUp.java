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

import com.khaledbakhit.api.rslib.exceptions.*;
import com.khaledbakhit.api.rslib.parsers.ClassificationsParser;
import com.khaledbakhit.api.rslib.parsers.MetricsParser;
import com.khaledbakhit.api.rslib.utils.Debugger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Startup Class contains global metrics and classifications used by the library.
 *
 * @author Khaled Bakhit
 * @since 1.0
 * @version 19/08/2013
 */
public class StartUp 
{
    /**
     * List containing String representation of classifications.
     * @since 3.0
     */
    private static List<String> classList;
    /**
     * LinkedList containing String representation of metrics.
     * @since 3.0
     */
    private static List<String> metricList;
    /**
     * Integer array containing classifications.
     */
    private static int[] classifications;
    /**
     * String array containing metrics.
     */
    private static String[] metrics;
    /**
     * File pointing to classifications input.
     */
    private static File classFile;
    /**
     * File pointing to metrics input.
     */
    private static File metricFile;

    /**
     * Clear data contained in the static StartUp arrays. This is required if
     * you want the class to re-determine the data from different parameters.
     *
     * @since 3.0
     */
    public synchronized static void clearMemory() 
    {
        clearClassMemory();
        clearMetricMemory();
    }

    /**
     * Clear data contained in the static StartUp classification arrays. This is
     * required if you want the class to re-determine the data from different
     * parameters.
     *
     * @since 3.0
     */
    public synchronized static void clearClassMemory() {
        classList = null;
        classifications = null;
        classFile = null;
    }

    /**
     * Clear data contained in the static StartUp metrics arrays. This is
     * required if you want the class to re-determine the data from different
     * parameters.
     *
     * @since 3.0
     */
    public synchronized static void clearMetricMemory() {
        metricList = null;
        metrics = null;
        metricFile = null;
    }
    /**
     * Get LinkedList Object containing classifications from classifications input
     * file defined by default LaunchSetup Object. ClassificationParser used is also
     * defined by same LaunchSetup Object.
     * 
     * @return List Object containing classifications.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     * @throws InvalidInputException classification input is invalid.
     */
    public synchronized static List<String> getClassList() throws InvalidInputException
    {
        return getClassList(Program.getInstance().getLaunchSetup());
    }
    
    /**
     * Get LinkedList Object containing classifications from classifications
     * file defined by LaunchSetup Object. ClassificationParser used is also
     * defined by same LaunchSetup Object.
     * 
     * @param sp LaunchSetup Object containing input configurations.
     * @return List Object containing classifications.
     * @throws InvalidInputException classification input is invalid.
     * @since 4.0
     */
    public synchronized static List<String> getClassList(LaunchSetup sp) throws InvalidInputException
    {
        if(sp.input_class_file==null)
            SetupNotConfiguredException.occur("input_class_file");
        if(sp.classification_parser==null)
            SetupNotConfiguredException.occur("classification_parser");
        
        ClassificationsParser parser= (ClassificationsParser) sp.classification_parser.newInstance();
        return getClassList( new File(sp.input_class_file), parser); 
    }
    

    /**
     * Get a LinkedList object containing classifications from the provided
     * classFile.
     *
     * @param classFile File containing classifications. 
     * @param parser ClassificationParser Object to use to parse given input classification file.
     * @return List containing String representation of classifications.
     * @throws InvalidInputException classification input is invalid.
     * @since 4.0
     */
    public synchronized static List<String> getClassList(File classFile, ClassificationsParser parser) throws InvalidInputException {
        if (StartUp.classFile != null
                && StartUp.classFile.getAbsolutePath().equalsIgnoreCase(classFile.getAbsolutePath())
                && classList != null) 
            return classList;
        
        try 
        {
            parser.parse(new FileInputStream(classFile));
            classList = parser.getParsedData();
            StartUp.classFile = classFile;
            parser.close();
        } 
        catch (FileNotFoundException e) 
        {
            Debugger.printlnError(e);
            classList= null;
            throw new ClassificationsNotFoundException(classFile.getName() + " is not found.");
        } 
        catch (NoSuchElementException e2) 
        {
            Debugger.printlnError(e2);
            classList= null;
            throw new InvalidInputException(classFile.getName() + " is of invalid format.");
        }
        catch(IOException e3)
        {
            Debugger.printlnWarning("Unable to close parser: "+e3.getMessage());
        }
        return classList;
    }

    /**
     * Get an integer array containing classifications from the provided
     * classFile.
     *
     * @param classFile File containing classifications.
     * @param parser ClassificationParser Object to use to parse given input classification file.
     * @return Integer array containing classifications.
     * @throws ClassificationNotFoundException classification cannot be located.
     * @throws InvalidInputException Classifications are not numeric.
     * @since 4.0
     */
    public synchronized static int[] getClassifications(File classFile, ClassificationsParser parser) throws ClassificationsNotFoundException, InvalidInputException {
        try {
            if (StartUp.classFile != null
                    && StartUp.classFile.getAbsolutePath().equalsIgnoreCase(classFile.getAbsolutePath())
                    && classList != null) {
                if (classifications != null) {
                    return classifications;
                }
            }
            StartUp.getClassList(classFile, parser);
            classifications = new int[classList.size()];
            Iterator<String> it = classList.iterator();
            for (int i = 0; i < classifications.length; i++) {
                classifications[i] = Integer.parseInt(it.next());
            }

            return classifications;
        } catch (NumberFormatException error) {
            Debugger.printlnError(error);
            throw new InvalidInputException(classFile.getName() + " doesn't have numeric classifications.");
        }
    }

    /**
     * Get a random valid classification.
     *
     * @return Random valid classification or null if classList is not built.
     * @since 3.0
     */
    public static String getRandomClass() 
    {
        if (classList == null)
            return null;
        
        return classList.get(Program.rand.nextInt(classList.size()));
    }
    
    /**
     * Get LinkedList Object containing metrics from metrics
     * file defined by default LaunchSetup Object. MetricsParser used is also
     * defined by same LaunchSetup Object.
     * @return List Object containing metrics.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     * @throws InvalidInputException metrics input is invalid.
     */
    public synchronized static List<String> getMetricList() throws InvalidInputException
    {
        return getMetricList(Program.getInstance().getLaunchSetup());
    }
    
    /**
     * Get LinkedList Object containing metrics from metrics
     * file defined by LaunchSetup Object. MetricsParser used is also
     * defined by same LaunchSetup Object.
     * @param sp LaunchSetup Object containing input configuration.
     * @return List Object containing metrics.
     * @throws InvalidInputException metrics input is invalid.
     * @since 4.0
     */
    public synchronized static List<String> getMetricList(LaunchSetup sp) throws InvalidInputException
    {
        if(sp.input_metric_file==null)
            SetupNotConfiguredException.occur("input_metric_file");
        if(sp.metrics_parser==null)
            SetupNotConfiguredException.occur("metrics_parser");
        
        MetricsParser parser= (MetricsParser) sp.metrics_parser.newInstance();
        return getMetricList(new File(sp.input_metric_file), parser); 
    }
    

    /**
     * Get a LinkedList Object containing metrics from the provided metricFile.
     *
     * @param metricFile File containing metrics.
     * @param parser MetricsParser Object to use to parse given input metrics file. 
     * @return List containing String representation of metrics.
     * @throws InvalidInputException Input file is invalid.
     * @since 4.0
     */
    public synchronized static List<String> getMetricList(File metricFile, MetricsParser parser) throws InvalidInputException{
        if (StartUp.metricFile != null
                && StartUp.metricFile.getAbsolutePath().equalsIgnoreCase(metricFile.getAbsolutePath())
                && metricList != null) {
            return metricList;
        }

        try 
        { 
            parser.parse(new FileInputStream(metricFile));
            metricList = parser.getParsedData();
            StartUp.metricFile = metricFile;
            parser.close();
           
        } catch (FileNotFoundException e) {
            Debugger.printlnError(e);
            throw new MetricsNotFoundException(metricFile.getName() + " is not found.");
        } catch (NoSuchElementException e2) {
            Debugger.printlnError(e2);
            throw new InvalidInputException(metricFile.getName() + " is of invalid format.");
        } 
        catch (IOException e3) 
        {
           Debugger.printlnWarning("Unable to close parser: "+e3.getMessage());
        }
        return metricList;
    }

    /**
     * Get an String array containing metrics from the provided metricFile.
     *
     * @param metricFile File containing metrics.
     * @param parser MetricsParser Object to use to parse given input metrics file. 
     * @return String array containing metrics.
     * @throws MetricsNotFoundException Metrics file cannot be located.
     * @since 4.0
     */
    public synchronized static String[] getMetric(File metricFile, MetricsParser parser) throws InvalidInputException, MetricsNotFoundException {

        if (StartUp.metricFile != null
                && StartUp.metricFile.getAbsolutePath().equalsIgnoreCase(metricFile.getAbsolutePath())
                && metricList != null) {
            if (metrics != null) {
                return metrics;
            }
        }
        StartUp.getMetricList(metricFile, parser);
        metrics = new String[metricList.size()];
        metricList.toArray(metrics);
        return metrics;
    }

    /**
     * Get a random valid metric.
     *
     * @return Random valid metric or null if metricList is not built.
     * @since 3.0
     */
    public static String getRandomMetric() 
    {
        if (metricList == null) 
            return null;
        
        return metricList.get(Program.rand.nextInt(metricList.size()));
    }
}