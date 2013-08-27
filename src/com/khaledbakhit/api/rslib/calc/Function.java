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
package com.khaledbakhit.api.rslib.calc;

import com.khaledbakhit.api.rslib.utils.Debugger;
import com.khaledbakhit.api.rslib.interfaces.Formula;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;

/**
 * Function provides a quick access to all supported functions.
 * 
 * @author Khaled Bakhit
 * @since 3.1
 * @version 13/09/2013
 */
public class Function 
{
    /**
     * String array containing names of built-in functions. 
     */
    private static final String[] FUNCTIONS
            = new String[]{"Accuracy","Jindex","Precision"
                    ,"Recall","Sensitivity","Specificity"};
    /**
     * Flag for the correctness/accuracy function. 
     */
    public static final int CORRECTNESS= 0;
    /**
     * Flag for the Jindex function. 
     */
    public static final int JINDEX= 1;
    /**
     * Flag for the precision function. 
     */
    public static final int PRECISION= 2;
    /**
     * Flag for the recall function. 
     */
    public static final int RECALL= 3;
    /**
     * Flag for the sensitivity function. 
     */
    public static final int SENSITIVITY= 4;
    /**
     * Flag for the specificity function. 
     */
    public static final int SPECIFICITY= 5;
    /**
     * LinkedList object containing imported Formula Objects. 
     */
    private static LinkedList<Formula> additional= new LinkedList<Formula>();
    
    /**
     * Get the value produced by the given function. 
     * @param type Function type.
     * @param matrix RuleSet confusion matrix.
     * @return Result from executing function over given matrix, or -1 if type invalid.
     */
    public static double getFunction(int type, int[][] matrix)
    {
        switch(type)
        {
            case(Function.CORRECTNESS):
                return Mathematics.correctness(matrix);
            case(Function.JINDEX):
                return Mathematics.jindex(matrix);
            case(Function.PRECISION):
                return Mathematics.precision(matrix);
            case(Function.RECALL):
                return Mathematics.recall(matrix);
            case(Function.SENSITIVITY):
                return Mathematics.sensitivity(matrix);
            case(Function.SPECIFICITY):
                return Mathematics.specificity(matrix);
            default:
                break;
        }
        type= type- FUNCTIONS.length;
       
        if(type<0 || type>= additional.size())
            return -1;
        else
        {
            Formula form= additional.get(type);
            if(form.requiresMatrix())
                return form.performCalculation(matrix);
            else
                return form.performCalculation();
        }
    }
    
    /**
     * Get the name of the Function. 
     * @param type Index of Function.
     * @return Name of Function.
     */
    public static String getName(int type)
    {
        if(type<0)
            return "Unknown function index";
        if(type>=FUNCTIONS.length)
        {
            type= type- FUNCTIONS.length;
            if(type<0 || type>= additional.size())
                return "Unknown function index";
            else
                return additional.get(type).getName();
        }
        else
           return FUNCTIONS[type];
    }
    
    /**
     * Get the number of Functions registered. 
     * @return number of Functions registered.
     */
    public static int size()
    {
        return FUNCTIONS.length+additional.size();
    }

    /**
     * Register a new formula. JarFile name must point to the {@link Formula}
     * implementing class. <br/>
     * <i>It should be: packageName + className + .jar </i>
     * @param jarFile Jar File containing {@link Formula} implementing class.
     * @return registered Formula Object or null if failed.
     */
    public static Formula register(File jarFile) 
    {
        Formula form= loadFromJar(jarFile);
        if(form==null || !isValid(form))
        {
            Debugger.printlnWarning(jarFile.getAbsolutePath()+" is not supported!");
            return  null;
        }
        else
        {
            register(form);
            Debugger.printlnInfo("Registered "+ form.getName());
            return form;
        }
    }
    /**
     * Check if Formula Object is valid.
     * @param form Formula Object to verify.
     * @return True if is valid, false otherwise.
     */
    private static boolean isValid(Formula form)
    {
        try
        {
            Method[] methods= form.getClass().getDeclaredMethods();
            //test double performCalculation()
            Method method= getMethodByName("performCalculation", 0, methods);
            if(method==null)
                return false;
            if(!method.getReturnType().getSimpleName().equals("double"))
                return false;
            
            //test String getName()
            method= getMethodByName("getName", 0, methods);
            if(method==null)
                return false;
            if(!method.getReturnType().getSimpleName().equals("String"))
                return false;
            
            //test boolean requiresMatrix()
            method= getMethodByName("requiresMatrix", 0, methods);
            if(method==null)
                return false;
            if(!method.getReturnType().getSimpleName().equals("boolean"))
                return false;
            
            //test double performCalculation(int[][])
            method= getMethodByName("performCalculation", 1, methods);
            if(method==null)
                return false;
            if(!method.getReturnType().getSimpleName().equals("double"))
                return false;
            if(!method.getParameterTypes()[0].getSimpleName().equals("int[][]"))
                return false;

            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    /**
     * Find method by name and number of parameters.
     * @param name Name of method to find.
     * @param paramCount Number of parameters this method contains.
     * @param methods Search pool.
     * @return Method required or null if not found.
     */
    private static Method getMethodByName(String name, int paramCount, Method[] methods)
    {
        for(Method method: methods)
            if(method.getName().equals(name) && method.getParameterTypes().length == paramCount)
                return method;
        return null;
    }
    
 
    /**
     * Load Formula Object from Jar File.
     * @param jarFile jar File containing the Formula implementing class.
     * @return  Formula Object loaded or null if not found.
     */
    @SuppressWarnings("unchecked")
    private static Formula loadFromJar(File jarFile) 
    {
        //remove .jar extension
        String className= jarFile.getName().substring(0, jarFile.getName().lastIndexOf('.'));
        
        Class<Formula> cls;

        try 
        {
            URL url = new URL("jar:file:" + jarFile.getAbsolutePath() + "!/");
            URL[] urls = {url};
            ClassLoader cl = URLClassLoader.newInstance(urls);
            cls = (Class<Formula>) cl.loadClass(className);
            return cls.newInstance();
        } 
        catch (Exception e) 
        {
            return null;
        }
    }
    
    /**
     * Register a new formula. 
     * @param form Formula Object to register.
     */
    public static void register(Formula form)
    {
        additional.add(form);
    }
    
    /**
     * Add a new formula. Same as {@link #register(formulas.Formula)}
     * @param form Formula Object to register.
     */
    public static void addFormula(Formula form)
    {
        register(form);
    }
    
    /**
     * Unregister a formula. 
     * @param form Formula object to unregister.
     * @return true if formula unregister, false otherwise.
     */
    public static boolean unregister(Formula form)
    {
        return additional.remove(form);
    }
    
    /**
     * Remove a formula. Same as {@link #unregister(formulas.Formula)}
     * @param form Formula object to remove.
     * @return true if formula removed, false otherwise.
     */
    public static boolean removeFormula(Formula form)
    {
        return unregister(form);
    }
}
  