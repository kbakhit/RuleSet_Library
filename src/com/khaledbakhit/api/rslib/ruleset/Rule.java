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
package com.khaledbakhit.api.rslib.ruleset;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.StartUp;
import com.khaledbakhit.api.rslib.calc.Mathematics;
import com.khaledbakhit.api.rslib.dataset.DataSet;
import com.khaledbakhit.api.rslib.dataset.DataSetLine;
import com.khaledbakhit.api.rslib.dataset.DataSetReader;
import com.khaledbakhit.api.rslib.exceptions.InvalidInputException;
import com.khaledbakhit.api.rslib.exceptions.UncleanDataSetException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Rule represents a rule inside a RuleSet.
 * 
 * @author Khaled Bakhit
 * @since 1.0
 * @version 24/08/2013
 */
public class Rule implements java.io.Serializable, Comparable<Rule>
{
    private static final long serialVersionUID = 124082013L;
    /**
     * Quality measure of this Rule.
     * @since 3.3
     */
    private double quality;
    /**
     * Previous amount of correct classifications.
     * @since 3.3
     */
    private int old_classified_correct= 0;
    /**
     * Previous amount of wrong classifications.
     * @since 3.3
     */
    private int old_classified_wrong= 0;
    /**
     * Previous amount of failed classifications.
     * @since 3.3
     */
    private int old_classified_failed= 0;
    /**
     * Amount of correct classifications.
     * @since 3.3
     */
    private int classified_correct= 0;
    /**
     * Amount of wrong classifications.
     * @since 3.3
     */
    private int classified_wrong= 0;
    /**
     * Amount of failed classifications.
     * @since 3.3
     */
    private int classified_failed= 0;
    /**
     * Numeric classification of this Rule.
     */
    private int classification;
    /**
     * String classification of this Rule. 
     * @since 3.0
     */
    private String stringclassification;
    /**
     * List Object containing metrics.
     */
    private List<String> metricList;
    /**
     * List Object containing conditions.
     */
    private List<Condition> conditions;
    /**
     * Pointer to the parent RuleSet.
     */
    private RuleSet parent;
    /**
     * LaunchSetup Object containing input configuration.
     * @since 4.0
     */
    private LaunchSetup sp;
    /**
     * Rule constructor.
     * @param stringclassification default classification of this Rule.
     * @param sp LaunchSetup Object containing input configuration.
     * @throws InvalidInputException Metrics input file is invalid.
     */
    public Rule(String stringclassification, LaunchSetup sp) throws InvalidInputException
    {
        this.sp= sp;
        this.stringclassification= stringclassification;
        this.metricList= StartUp.getMetricList(sp);
        conditions= new LinkedList<Condition>();
        try
        {
            classification= Integer.parseInt(stringclassification);
        }
        catch(Exception e)
        {
            classification= -1; 
        }
    }
    
    /**
     * Attach this Rule to the given RuleSet which will become it's new parent.
     * @param rs New RuleSet parent to attach to.
     */
    public void attach(RuleSet rs)
    {
        setParent(rs);
        rs.addRule(this);
    }
    /**
     * Set the given RuleSet as parent of this Rule.
     * @param rs New RuleSet parent.
     */
    public void setParent(RuleSet rs)
    {
        parent= rs;
    }
    /**
     * Get Parent RuleSet of this Rule.  
     * @return Parent RuleSet of this Rule.
     */
    public RuleSet getParent()
    {
        return parent;
    }
    /**
     * Get amount of correct classifications.
     * @return Amount of correct classifications.
     * @since 3.3
     */
    public int getCorrect()
    {
        return classified_correct;
    }
    /**
     * Get amount of wrong classifications.
     * @return Amount of wrong classifications.
     * @since 3.3
     */
    public int getWrong()
    {
        return classified_wrong;
    }
    /**
     * Get amount of failed classifications.
     * @return Amount of failed classifications.
     * @since 3.3
     */
    public int getFailed()
    {
        return classified_failed;            
    }
      /**
     * Get previous amount of correct classifications.
     * When you clear measurements, previous results are still saved.
     * @return Previous amount of correct classifications.
     * @see #clearMeasurments() 
     * @since 3.3
     */
    public int getPreviousCorrect()
    {
        return old_classified_correct;
    }
    /**
     * Get previous amount of wrong classifications.
     * When you clear measurements, previous results are still saved.
     * @return Previous amount of wrong classifications.
     * @see #clearMeasurments() 
     * @since 3.3
     */
    public int getPreviousWrong()
    {
        return old_classified_wrong;
    }
    /**
     * Get previous amount of failed classifications.
     * When you clear measurements, previous results are still saved.
     * @return Previous amount of failed classifications.
     * @see #clearMeasurments() 
     * @since 3.3
     */
    public int getPreviousFailed()
    {
        return old_classified_failed;            
    }
    /**
     * Get accuracy of the Rule based on amount of correct and wrong classifications.
     * @return Accuracy of the Rule.
     * @since 3.3
     */
    public double getAccuracy()
    {
        return Mathematics.accuracy(classified_correct, classified_correct + classified_wrong);
    }
    /**
     * Get coverage of the Rule based on amount of correct, wrong and failed classifications.
     * @return Coverage of the Rule.
     * @since 3.3
     */
    public double getCoverage()
    {
        return Mathematics.coverage(classified_correct, classified_wrong, classified_failed);
    }
     /**
     * Get previous accuracy of the Rule based on amount of correct and wrong classifications.
     * When you clear measurements, previous results are still saved.
     * @return Previous accuracy of the Rule.
     * @see #clearMeasurments() 
     * @since 3.3
     */
    public double getPreviousAccuracy()
    {
        return Mathematics.accuracy(old_classified_correct, old_classified_correct + old_classified_wrong);
    }
    /**
     * Get previous coverage of the Rule based on amount of correct, wrong and failed classifications.
     * When you clear measurements, previous results are still saved.
     * @return Previous coverage of the Rule.
     * @see #clearMeasurments() 
     * @since 3.3
     */
    public double getPreviousCoverage()
    {
        return Mathematics.coverage(old_classified_correct, old_classified_wrong, old_classified_failed);
    }
    /**
     * Set the quality measure of this Rule.
     * @param quality Quality measure of this Rule.
     * @since 3.3
     */
    public void setQuality(double quality)
    {
        this.quality= quality;
    }
    /**
     * Get the quality measure of this Rule.
     * @return Quality measure of this Rule.
     * @since 3.3
     */
    public double getQuality()
    {
        return quality;
    }
    
    /**
     * Clear measurements.
     * @since 3.3
     */
    public void clearMeasurments()
    {
        old_classified_correct= classified_correct;
        old_classified_wrong= classified_wrong;
        old_classified_failed= classified_failed;
        classified_correct = classified_wrong = classified_failed = 0;
        quality = 0.0;
    }
   
    /**
     * Check if classification is numeric. 
     * @return true if numeric, false otherwise.
     * @since 3.0
     */
    public boolean isClassNumeric()
    {
        return this.classification!= -1;
    }
    /**
     * Add a condition to this Rule Object. <b>Cannot be Classification</b>.
     * Use <code>setClassification()</code> instead.
     * @param condition Condition to add to this Rule Object.
     * @see #setClassification(int) 
     * @see #setClassification(java.lang.String) 
     * @throws InvalidInputException Condition is a classification.
     * @since 3.3
     */
    public void addCondition(Condition condition) throws InvalidInputException
    {
        if(condition.isClassification)
            throw new InvalidInputException("Classification Condition is not allowed here.");
        conditions.add(condition);
    }
    /**
     * Add a condition to this Rule Object
     * @param column According to metric name.
     * @param operation The operations are: =, ==, <, <=, >, >=, !=.
     * @param value Value after the operation.
     */
    public void addCondition(String column, String operation, String value)
    {
       conditions.add(new Condition(column.trim(), operation.trim(), value.trim()));
    }
  
    /**
     * Add a condition to this Rule Object
     * @param column According to metric name.
     * @param operation The operations are: =, ==, <, <=, >, >=, !=.
     * @param value Value after the operation.
     */
    public void addCondition(String column, String operation, double value)
    {
        addCondition(column, operation,value+"");
    }

    /**
     * Get the classification related to this Rule.
     * @return int classification.
     */
    public int getClassification()
    {
        return classification;
    }
    
    /**
     * Get the classification related to this Rule.
     * @return String classification.
     * @since 3.0
     */
    public String getStringClassification()
    {
        return stringclassification;
    }
     
    /**
     * Set the classification related to this Rule. 
     * @param value classification value.
     */
    protected void setClassification(int value)
    {
        classification= value;
        this.stringclassification= value+"";
    }
    /**
     * Set the classification related to this Rule. 
     * @param value classification String value.
     * @since 3.0
     */
    protected void setClassification(String value)
    {
        this.stringclassification= value;
        try
        {
            this.classification= Integer.parseInt(stringclassification);
        }
        catch(Exception e)
        {
            this.classification= -1;
        }
    }
    
    /**
     * Get Condition for the given metric.
     * @param metricName Metric name to search Condition for.
     * @return Condition for given metric or null if not found.
     * @since 3.3
     */
    public Condition getCondition(String metricName)
    {
        java.util.Iterator<Condition> it= conditions.iterator();
         Condition cond;
         while(it.hasNext())
         {
             cond= it.next();
             if(cond.metric!=null && cond.metric.equals(metricName))
                 return cond;
         }
         return null;
    }
    /**
     * Get Condition Index for the given metric.
     * @param metricName Metric name to search Condition for.
     * @return Condition index for given metric or -1 if not found.
     * @since 3.3
     */
    private int getConditionIndex(String metricName)
    {
        java.util.Iterator<Condition> it= conditions.iterator();
        Condition cond;
        int index= 0;
         while(it.hasNext())
         {
             cond= it.next();
             if(cond.metric!=null && cond.metric.equals(metricName))
                 return index;
             index++;
         }
         return -1;
    }
     /**
      * Test a DataSet Object.
      * @param dataset DataSet Object to test.
      * @throws UncleanDataSetException Invalid DataSet line detected.
      */
     public void test(DataSet dataset) throws UncleanDataSetException
     {
         test(dataset, null);
       
     }
     /**
      * Test a DataSet Object.
      * @param dataset DataSet Object to test.
      * @param cond Condition to try to see any improvements.
      * @throws UncleanDataSetException Invalid DataSet line detected.
      * @since 3.3
      */
     public void test(DataSet dataset, Condition cond) throws UncleanDataSetException 
     {
        Iterator<DataSetLine> it= dataset.getDataSetLinesIterator();
        DataSetLine line;
        while(it.hasNext())
        {
            line= it.next();
            test(line, cond);
        }
     }
     /**
      * Test a DataSet File.
      * @param dataset DataSet File to test.
      * @throws FileNotFoundException Unable to locate file.
      * @throws UncleanDataSetException DataSet is not clean.
      */
     public void test(File dataset) throws FileNotFoundException, UncleanDataSetException
     {
         test(dataset, null);
       
     }
     /**
      * Test a DataSet File.
      * @param dataset DataSet File to test.
      * @param cond Condition to try to see any improvements.
      * @throws FileNotFoundException Unable to locate file.
      * @throws UncleanDataSetException DataSet is not clean.
      * @since 3.3
      */
     public void test(File dataset, Condition cond) throws FileNotFoundException, UncleanDataSetException
     {
        DataSetReader reader= (DataSetReader) sp.dataset_reader.newInstance();
        DataSetLine line= null;
        while(reader.hasNext())
        {
            line= reader.getNext(line);
            test(line, cond);
        }
        try
        {
            reader.close();
        }
        catch(Exception e)
        {}
     }
     
    /**
     * Test a line of DataSet on this Rule.
     * @param line DataSetLine Object to test.
     * @return true if match, false otherwise.
     * @throws UncleanDataSetException Invalid DataSet line detected.
     * @since 4.0
     */
     public boolean test(DataSetLine line) throws UncleanDataSetException
    {
        return test(line, null);
    }
     
    /**
     * Test a line of DataSet on this Rule.
     * @param line DataSetLine to test.
     * @param cond Condition to try to see any improvements.
     * @return true if match, false otherwise.
     * @throws UncleanDataSetException Invalid DataSet line detected.
     * @since 4.0
     */
     public boolean test(DataSetLine line, Condition cond) throws UncleanDataSetException
    {
        try
        {
            boolean match;
            java.util.Iterator<String> it= metricList.iterator();
            Condition rule_cond;
            String[] metrics= line.getMetrics();
            String val, metricName;
            for(int i=0; i< metrics.length; i++)
            {
                metricName= it.next(); //Name of attribute column at index i
                rule_cond= getCondition( metricName );//Rule's condition for given metric
                if(rule_cond==null)
                    continue;
                
                val= metrics[i]; //Value of metric i in the DataSetLine.
                if(cond!=null && !cond.isClassification && cond.metric.equals(metricName))
                    match= analyze(val, cond.operator, cond.value);
                else
                    match= analyze(val, rule_cond.operator, rule_cond.value);
                if(!match)
                {
                    classified_failed++;//no match, thus Rule failed to classify.
                    return false; //match is false
                }
            }
            
            String compare_classification= 
                    (cond!=null && cond.isClassification)?
                    cond.classification :  stringclassification;
            
            if(line.getClassification().equals(compare_classification))
                    classified_correct++;
            else if(RuleSet.isMatchingWithinRange())    
            {
                int lineClass,c;
                try 
                {
                    lineClass= Integer.parseInt(line.getClassification());  
                    c= Integer.parseInt(compare_classification);    
                }
                    
                catch(Exception e)   
                {
                    throw new RuntimeException("Matching within range cannot be applied on non-numeric classes!");    
                }
                double range= RuleSet.getRange();
                double upper= lineClass + range;
                double lower= lineClass + range;
                    
                if(c>= lower && c <= upper)
                    classified_correct++;
                else
                    classified_wrong++;    
            }
            else
                classified_wrong++;        
           
            return true;//match is true.
        }
        catch(Exception e)
        {
            throw new UncleanDataSetException(line+" is not a valid DataSet line.");
        }
    }
     
     
    /**
     * Analyze an equation.
     * @param dsVal Value at the specific metric in dataset.
     * @param operation String operation: >, < , >=, <=, ==, !=.
     * @param value Value at Rule to compare with.
     * @return true if correct, false otherwise.
     */
    private boolean analyze(String dsVal, String operation, String value)
    {
        try
        {
           double number= Double.parseDouble(dsVal);
           double val= Double.parseDouble(value);
    
           if(operation.equalsIgnoreCase("=") || operation.equalsIgnoreCase("=="))
                return (number==val);
          else  if(operation.equalsIgnoreCase("<"))
            return (number<val);
          else  if(operation.equalsIgnoreCase("<=") || operation.equalsIgnoreCase("=<"))
            return (number<=val);
          else  if(operation.equalsIgnoreCase(">"))
            return (number>val);
          else if(operation.equalsIgnoreCase("!="))
              return (number!=val);
        else
            return (number>=val);
        }
        catch(Exception e)
        {         
           int compared= dsVal.compareTo(value); 
           
           if(operation.equalsIgnoreCase("=") || operation.equalsIgnoreCase("=="))
                return (compared==0);
          else  if(operation.equalsIgnoreCase("<"))
                return compared<0;
          else if(operation.equalsIgnoreCase("<=") || operation.equalsIgnoreCase("=<"))
            return compared<=0;
          else  if(operation.equalsIgnoreCase(">"))
            return compared>0;
          else if(operation.equalsIgnoreCase("!="))
              return compared!=0;
        else
            return compared>=0;
        }
    }

    @Override
     public String toString()
    {
        String msg="Rule: ";
        for(int i=0; i<conditions.size(); i= i+3)
            msg+= "\n"+conditions.get(i)+" "+conditions.get(i+1)+" "+conditions.get(i+2);
        msg+="\n---> class "+stringclassification;
        return msg;
        }
    /**
     * Get the list of Conditions involved in this Rule.
     * @param includeClassification True if classification based Condition should be included, false otherwise.
     * @return List containing conditions conditions.
     * @since 3.3
     */
     public List<Condition> getConditions(boolean includeClassification)
     {
         if(!includeClassification)
            return conditions;
     
         LinkedList<Condition> conds= new LinkedList<Condition>();
         for(Condition cond: conditions)
             conds.add(cond);
        
         Condition class_cond= new Condition(this.stringclassification);
         class_cond.setParent(this);
         conds.add(class_cond);
         return conds;
     }
     
     /**
      * Get the number of conditions in this RuleSet. 
      * @return number of conditions.
      */
     public int getNumberofConditions()
     {
         return conditions.size();
     }
     /**
      * Set the Conditions for this Rule.
      * @param conds List containing Conditions.
      * @since 3.3
      */
     public void setConditions(List<Condition> conds)
     {
         conditions= new LinkedList<Condition>();
         for(Condition cond: conds)
         {
             if(cond.isClassification)
             {
                 this.stringclassification= cond.classification;
                 continue;
             }
             conditions.add(cond);
         }
     }
     /**
      * Update a condition in this Rule.
      * @param cond Condition to update.
      * @since 3.3
      */
     public void updateCondition(Condition cond)
     {
         if(cond.isClassification)
         {
             this.stringclassification= cond.classification;
             try
             {
                 this.classification= Integer.parseInt(this.stringclassification);
             }
             catch(Exception e)
             {
                 this.classification= -1;
             }
         }
         else
         { 
             int index= getConditionIndex(cond.metric);
             conditions.set(index, cond);
         }
     }
    
     /**
      * Check if this Rule implies to given Rule.
      * @param rule Rule to check relation with.
      * @return True if implies relation established, false otherwise.
      * @since 3.3
      */
     public boolean implies(Rule rule)
     {
         if(conditions.size()!= rule.conditions.size())
             return false;
         if(!this.stringclassification.equals(rule.stringclassification))
             return false;
 
         List<Condition> cond1= getConditions(false);
         List<Condition> cond2= rule.getConditions(false);
         
         for(int i=0; i<cond1.size(); i++)
         {
             if(cond1.get(i).implies(cond2.get(i)))
                 return true;
         }
         return false;  
     }
     /**
      * Check if this Rule contradicts given Rule.
      * @param rule Rule to check relation with.
      * @return True if contradicts relation established, false otherwise.
      * @since 3.3
      */
     public boolean contradicts(Rule rule)
     {  
         if(this.conditions.size()!= rule.conditions.size())
             return false;
         if(this.stringclassification.equals(rule.stringclassification))
             return false;
         List<Condition> cond1= this.getConditions(false);
         List<Condition> cond2= rule.getConditions(false);
         
         for(int i=0; i<cond1.size(); i++)
         {
             if(!cond1.get(i).equals(cond2.get(i)))
                 return false;
         }
         return true;    
     }
     
     @Override
     public int compareTo(Rule o)    
     {
         return (int) (quality-o.getQuality());
     }
}