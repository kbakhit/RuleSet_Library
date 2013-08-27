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

import java.io.*;
import java.util.LinkedList;
import  com.khaledbakhit.api.rslib.*;
import  com.khaledbakhit.api.rslib.calc.Function;
import  com.khaledbakhit.api.rslib.calc.Mathematics;
import com.khaledbakhit.api.rslib.dataset.DataSet;
import com.khaledbakhit.api.rslib.dataset.DataSetLine;
import com.khaledbakhit.api.rslib.dataset.DataSetReader;
import com.khaledbakhit.api.rslib.exceptions.InvalidInputException;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.utils.Debugger;
import  com.khaledbakhit.api.rslib.utils.ExcelWriter;
import com.khaledbakhit.api.rslib.utils.TextWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/**
 * RuleSet Object represents a RuleSet that contains many Rules and a default
 *  classification that help it classify DataSet cases.
 * 
 * @author Khaled Bakhit
 * @since 1.0
 * @version 24/08/2013
 */
public class RuleSet implements java.io.Serializable
{
    private static final long serialVersionUID = 224082013L;
    /**
     * Default numeric classification. 
     */
    private int default_cond;
    /**
     * Default String classification. 
     * @since 3.0
     */
    private String string_default_cond;
    /**
     * Default confusion matrix. 
     */
    private int[][] Matrix;
    /**
     * Individual confusion matrix. 
     * @since 2.0
     */
    private int[][] IndiMatrix;
    /**
     * List object containing Rule objects. 
     */
    private List<Rule> list;
    /**
     * LaunchSetup Object that contains input configuration.
     * @since 4.0
     */
    private LaunchSetup sp;
    /**
     * Currently used output type. 
     * @since 2.0
     */
    private Library.OUTPUT_TYPE output_type= Library.OUTPUT_TYPE.CSV;
    /**
     * TextWriter Object to write output in TXT or CSV format.
     * @since 2.0
     */
    private TextWriter txt;
    /**
     * ExcelWriter Object to write output in XSL format.
     * @since 3.0
     */
    private ExcelWriter ew;
    /**
     * Separator used for output files.
     * @since 2.0
     */
    private String sep= ",";
    /**
     * Output extension currently used.
     * @since 2.0
     */
    private String ext= ".csv";
    /**
     * Tested case number.
     * @since 3.1
     */
    private int caseNumber;
    /**
     * Parent file where this RuleSet was extracted from. 
     */
    private File parent;
    /**
     * ID number of this RuleSet. This is used incase Parent File contains
     * more than one RuleSet.
     */
    private int ID= -1;
    /**
     * RuleSet constructor.
     * Uses the default LaunchSetup Object defined by static instance of Program.
     * @param parent Parent file from which this RuleSet was parsed.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     * @throws InvalidInputException Input classification file is invalid.
     */
    public RuleSet(File parent) throws InvalidInputException
    {
        this(parent, Program.getInstance().getLaunchSetup());
    }
    /**
     * RuleSet constructor.
     * @param parent Parent file from which this RuleSet was parsed.
     * @param sp LaunchSetup Object containing input configuration.
     * @throws InvalidInputException Input classification file is invalid.
     * @since 4.0
     */
    public RuleSet(File parent, LaunchSetup sp) throws InvalidInputException
    {
        this.sp= sp;
        default_cond= -1;
        string_default_cond= default_cond+"";
        
        list= new LinkedList<Rule>();
        int Matrix_Size= StartUp.getClassList(sp).size();
        Matrix= new int[Matrix_Size][Matrix_Size];
        IndiMatrix= new int[Matrix_Size][Matrix_Size];
      
        if(RuleSet.needRuleFiredInfo())
            caseNumber= 0;
        setParent(parent);
    }
    /**
     * Set the parent file from which this RuleSet was parsed.
     * @param parent Parent file from which this RuleSet was parsed.
     */
    public final void setParent(File parent)
    {
        this.parent= parent;
    }
    /**
     * Get the parent file from which this RuleSet was parsed.
     * @return Parent file from which this RuleSet was parsed.
     */
    public final File getParent()
    {
        return parent;
    }  
    
     /**
     * Set an ID for this RuleSet. Important incase parent File contains more
     * than one RuleSet. Ensures outputs are not overwritten by sibling RuleSets.
     * @param ID ID number to set for this RuleSet.
     * @since 4.0
     */
    public void setID(int ID)
    {
        this.ID= ID;
    }
    /**
     * Get the ID for this RuleSet. Important incase parent File contains more
     * than one RuleSet. Ensures outputs are not overwritten by sibling RuleSets.
     * @return ID of this RuleSet.
     * @since 4.0
     */
    public int getID()
    {
        return ID;
    }   
    
    /**
     * Clear all measurements done.
     */
    public void clearMeasurements()
    {
        for(int i=0; i<Matrix.length; i++)
            for(int j=0; j<Matrix[i].length; j++)
            {
                Matrix[i][j]= 0;
                IndiMatrix[i][j]= 0;
            }
    }
    /**
     * Test given DataSet Object. {@link TestingMode#SEQUENTIAL} mode is used.
     * @param dataset DataSet Object to test.
     * @throws InvalidInputException DataSet is not clean.
     * @since 4.0
     */
    public void test(DataSet dataset) throws InvalidInputException
    {
       test(dataset, TestingMode.SEQUENTIAL);
    }
    
    
    /**
     * Test given DataSet File.{@link TestingMode#SEQUENTIAL} mode is used.
     * @param dataset DataSet File to test.
     * @throws FileNotFoundException Unable to locate input DataSet File.
     * @throws InvalidInputException DataSet is not clean.
     */
    public void test(File dataset) throws InvalidInputException, FileNotFoundException
    {
        test(dataset, TestingMode.SEQUENTIAL);
    }
    
    /**
     * Test given DataSet Object using {@link TestingMode#VOTING} mode.
     * @param dataset DataSet Object to test.
     * @throws InvalidInputException DataSet is not clean.
     * @since 4.0
     */
    public void votingTest(DataSet dataset) throws InvalidInputException
    {
        test(dataset, TestingMode.VOTING);
    }
    
        
    /**
     * Test given DataSet File using {@link TestingMode#VOTING} mode.
     * @param dataset DataSet File to test.
     * @throws FileNotFoundException Unable to locate input DataSet File.
     * @throws InvalidInputException DataSet is not clean.
     * @since 2.0
     */
    public void votingTest(File dataset) throws InvalidInputException, FileNotFoundException
    {
        test(dataset, TestingMode.VOTING);
    }
        
    /**
     * Test given DataSet Object.
     * @param dataset DataSet Object to test.
     * @param mode Testing mode to perform.
     * @throws InvalidInputExceptiona DataSet is not clean.
     * @since 4.0
     */
    public void test(DataSet dataset, TestingMode mode) throws InvalidInputException
    {
        Iterator<DataSetLine> it= dataset.getDataSetLinesIterator();
        while(it.hasNext())
            if(mode== TestingMode.SEQUENTIAL)
                sequentialTest(it.next());
            else
                votingTest(it.next());
    }
    
    
    /**
     * Test given DataSet File.
     * @param dataset DataSet File to test.
     * @param mode Testing mode to perform.
     * @throws FileNotFoundException Unable to locate input DataSet File.
     * @throws InvalidInputExceptiona DataSet is not clean.
     * @since 2.0
     */
    public void test(File dataset, TestingMode mode) throws InvalidInputException, FileNotFoundException
    {
        if(sp.dataset_reader == null)
            SetupNotConfiguredException.occur("dataset_reader");
        DataSetReader reader= (DataSetReader) sp.dataset_reader.newInstance();
        reader.open(new FileInputStream(dataset));
        DataSetLine line= null;
        while(reader.hasNext())
        {
            line= reader.getNext(line);
            if(mode== TestingMode.SEQUENTIAL)
                sequentialTest(line);
            else
                votingTest(line);
        }
        try
        {
            reader.close();
        }
        catch(Exception e){}
    }    
    /**
     * Set the output type.
     * @param type New output type. 
     * @since 2.0
     */
    public void setOutputType(Library.OUTPUT_TYPE type)
    {
        output_type= type;
        if(output_type== Library.OUTPUT_TYPE.TEXT)
        {
            ext= ".txt"; sep= "\t";
        }
        else if(output_type== Library.OUTPUT_TYPE.CSV)
        {
            ext= ".csv"; sep=",";
        }
        else
        {
            ext= ".xls";
            sep= "\t";
        }
    }

    /**
     * Start recording calculations per DataSet (for individual results output).
     * @throws IOException Unable to open output stream.
     * @since 2.0
     */
    public void startRecording() throws IOException 
    {
        if(sp.ruleset_output_individual_results_dir==null)
            SetupNotConfiguredException.occur("ruleset_output_individual_results_dir");
          
        String outname= sp.ruleset_output_individual_results_dir+"/"+getParent().getName();
        if(ID!= -1)
            outname+= "("+ID+")";
        outname+= ext;
       
        if(output_type!=Library.OUTPUT_TYPE.XLS)
        {
            txt= new TextWriter(outname);
            txt.out.print("Summarized Results" + sep);
            for(int i=0; i<Function.size(); i++)
                txt.out.print(sep + Function.getName(i));
            txt.out.println();
            txt.out.println();
            txt.out.println("DataSets: ");
        }
        else
        {
            ew= new ExcelWriter(outname);
            ew.addSheet(getParent().getName());
           
            
            ew.print("Summarized Results"+sep,ew.BoldBlue);
            for(int i=0; i<Function.size(); i++)
                ew.print(sep+Function.getName(i), ew.BoldBlue);
        
            ew.println();
            ew.println();
            ew.println("DataSets: ",ew.BoldRed);
        }   
    }
    
    /**
     * Stop recording RuleSetFireInfo.
     * @since 3.1
     */
    public void stopRecordingRuleSetFireInfo()
    {
        if(RuleSet.needRuleFiredInfo())
            this.closeRFI();
    }

    /**
     * Stop recording calculations per DateSet and save.
     * @since 2.0
     */
    public void stopRecording()
    {
        if(output_type!=Library.OUTPUT_TYPE.XLS)
        {
            if(parent!=null)
            {
                txt.out.println();
                txt.out.println("This RuleSet belongs to "+ parent.getName());
            }
            try
            {
              txt.close();  
            }
            catch(Exception e)
            {
                Debugger.printlnSensitive("Unable to close output stream for RuleSet individual results!");
            }
        }
        else
        {
            if(parent!=null)
            {
                ew.println();
                ew.println("This RuleSet belongs to "+ parent.getName(),ew.BoldBlack);
            }
         ew.flush();
         ew.close();
        }
    }

    /**
     * Record results on the following dataSetName.
     * @param datasetName Name of DataSet.
     * @since 2.0
     */
    public void record(String datasetName)
    {
          if(output_type!=Library.OUTPUT_TYPE.XLS)
          {
              txt.out.print(datasetName + sep );
              for(int i=0; i<Function.size(); i++)
                txt.out.print(sep+ Function.getFunction(i,IndiMatrix) ); 
              txt.out.println();
          }
          else
          {
              ew.print(datasetName+sep);
              for(int i=0; i<Function.size(); i++)
                ew.print(sep+""+Function.getFunction(i,this.IndiMatrix)); 
              ew.println();
          }
    }
     /**
      * Reset the individual matrix counter. Important if you're recording.
      * @since 2.0
      */
     public void indiReset()
    {
        for(int i=0; i<IndiMatrix.length; i++)
            for(int j=0; j<IndiMatrix[i].length; j++)             
                IndiMatrix[i][j]= 0;
     }

    /**
     *Set the default condition/classification.
     * @param val int default classification.
     */
     public void setDefaultCond(int val)
    {
        default_cond= val;
    }
     /**
      * Set the default condition/classification. 
      * @param val String default condition.
      * @since 3.0
      */
     public void setDefaultCond(String val)
     {
         val= val.trim();
         string_default_cond= val;
         try
         {
             default_cond= Integer.parseInt(val);
         }
         catch(Exception e)
         {
             default_cond= -1;
         }
     }
     
    /**
     * Check if classification is numeric. 
     * @return true if numeric, false otherwise.
     * @since 3.0
     */
     public boolean isClassNumeric()
     {
         return default_cond!=-1;
     }

    /**
     * Get the default condition/classification assigned to this RuleSet.
     * @return default classification.
     */
     
     public int getDefaultCond()
     {
        return default_cond;
     }
     
      /**
     * Get the default condition/classification assigned to this RuleSet.
     * @return default classification.
     * @since 3.0
     */
     public String getDefaultStringCond()
     {
         return string_default_cond;
     }
    /**
     * Add Rule to this RuleSet.
     * @param rule Rule object.
     */
     public void addRule(Rule rule)
    {
          list.add(rule);
          rule.setParent(this);
    }

    /**
     * Get the Rules involved in this RuleSet.
     * @return List containing Rules.
     */
    public List<Rule> getRules() 
    {
        return list;
    }

    /**
     * Set the Rules of this RuleSet.
     * @param rules List containing Rules.
     */
    public void setRule(List<Rule> rules) 
    {
        list.clear();
        list.addAll(rules);
    }
    /**
     * Get the number of Rules in this RuleSet object.
     * @return number of Rules present.
     */
    public int getNumberofRules() 
    {
        return list.size();
    }

    /**
     * Test a DataSet line using using {@link TestingMode#SEQUENTIAL} mode.
     * @param line DataSet single line to test.
     * @return Classification predicted by this RuleSet or null if line invalid.
     * @throws InvalidInputException Invalid DataSet line detected.
     * @since 4.0
     */
     public String sequentialTest(DataSetLine line) throws InvalidInputException 
     {
        if(line==null)
            return null; 
     
        int classification = this.default_cond;
        String string_classification = this.string_default_cond;
        boolean match;
        int rule_number = -1, counter= -1;
        
        for (Rule r: list) 
        {
            counter++;
            match = r.test(line);
            if (match) 
            {
                classification = r.getClassification();
                string_classification = r.getStringClassification();
                rule_number= counter;
                break;
            }
        }
        try 
        {
            if (classification == -1) 
                throw new Exception();
           
            int lineClass = Integer.parseInt(line.getClassification());
            
            if (RuleSet.isMatchingWithinRange()) 
            {
                double range= RuleSet.getRange();
                double upper = lineClass + range;
                double lower = lineClass - range;
                if (classification != lineClass && classification >= lower && classification <= upper) 
                    classification = lineClass;
            }
            Matrix[lineClass][classification]++;
            IndiMatrix[lineClass][classification]++;

        } 
        catch (Exception e) 
        {
            int lineClass = StartUp.getClassList().indexOf(line.getClassification());
            int thisClass = StartUp.getClassList().indexOf(string_classification);
            Matrix[lineClass][thisClass]++;
            IndiMatrix[lineClass][thisClass]++;
        }

        if (RuleSet.needRuleFiredInfo()) 
        {
            String ruleName;
            if (rule_number == -1) 
                ruleName = "Default";
             
            else 
                ruleName = "Rule " + rule_number;
            
            try 
            {
                printRuleFiredInfo(ruleName, string_classification, line.getClassification());
                this.caseNumber++;
            } 
            catch (IOException e)
            {
                Debugger.printlnWarning("failed to collect information about Rules matching DataSet");
                RuleSet.setNeedRuleFiredInfo(false);
            }
        }
        return string_classification;
    }
     /**
      * Test a DataSet line.
      * @param line DataSet single line to test.
      * @param mode Testing mode to perform.
      * @return  Classification predicted by this RuleSet or null if line is invalid.
      * @throws InvalidInputException Invalid DataSet line detected.
      * @since 4.0
      */
     public String test(DataSetLine line, TestingMode mode)throws InvalidInputException
     {
         if(mode== TestingMode.SEQUENTIAL)
             return sequentialTest(line);
         else
             return votingTest(line);
     }

    /**
     * Test a DataSet line using {@link TestingMode#VOTING} mode.
     * @param line DataSet single line to test.
     * @return Classification predicted by this RuleSet or null if line is invalid.
     * @throws InvalidInputException Invalid DataSet line detected.
     * @since 4.0
     */
    public String votingTest(DataSetLine line) throws InvalidInputException
    {
        if(line == null)
            return null;

        int[] voting = new int[StartUp.getClassList().size()];

        int classification, lineClass;
        try
        {
            lineClass= Integer.parseInt(line.getClassification());
        }
        catch(Exception e)
        {
           lineClass= -1; 
        }
        String string_classification = this.string_default_cond;
        voting[StartUp.getClassList().indexOf(string_classification)]++;
        
        boolean match;
        for (Rule r: list) 
        {
            match = r.test(line);
            if (match) 
            {
                string_classification = r.getStringClassification();
                 
                if (RuleSet.isMatchingWithinRange()) 
                {
                    try
                    {
                        classification= r.getClassification();
                        if(classification==-1 || lineClass== -1)
                        {
                            Debugger.printlnSensitive("Matching within Range does not work with non-numeric classifications!");
                            throw new Exception();
                        }
                        double range= RuleSet.getRange();
                        double upper = lineClass + range;
                        double lower = lineClass - range;
                
                        if (classification != lineClass && classification >= lower && classification <= upper) 
                            string_classification= line.getClassification();
                    }
                    catch(Exception e){}
                }
                voting[StartUp.getClassList().indexOf(string_classification)]++;
            }
        }
        /*
         * Get the winning classification by vote
         */
        int winner_index = -1, winner_val = -1;
        for (int i = 0; i < voting.length; i++) 
        {
            if (winner_index == -1 || (winner_val < voting[i])) {
                winner_index = i;
                winner_val = voting[i];
            }
        }

        classification = winner_index;
        try 
        {
            if (classification == -1)
                throw new Exception();
            Matrix[lineClass][classification]++;
            IndiMatrix[lineClass][classification]++;

        } 
        catch (Exception e) 
        {
            lineClass = StartUp.getClassList().indexOf(line.getClassification());
            int thisClass = StartUp.getClassList().indexOf(string_classification);

            Matrix[lineClass][thisClass]++;
            IndiMatrix[lineClass][thisClass]++;
        }
        return string_classification;

    }
    /**
     * TextWriter Object to write which Rule fired information.
     * @since 3.1
     */
    private TextWriter RFIfw;
    /**
     * Write Rule Fired Information.
     * @param ruleName Name of Rule that fired a match/prediction.
     * @param ruleClass Classification determined by the Rule.
     * @param dsClass Classification set by DataSet line.
     * @throws IOException Unable to write information.
     * @since 3.1
     */
    private void printRuleFiredInfo(String ruleName, String ruleClass, String dsClass) throws IOException
    {
        if(RFIfw== null)
        {
            if(sp.ruleset_output_fireinfo_dir== null)
                SetupNotConfiguredException.occur("ruleset_output_fireinfo_dir");
            File outputDir= new File(sp.ruleset_output_fireinfo_dir+"/"+this.getParent().getName());
            if(!outputDir.exists())
                outputDir.mkdir();
            
            
            RFIfw= new TextWriter(sp.ruleset_output_fireinfo_dir+"/"+this.getParent().getName()
                    +"/"+"RuleSet "+outputDir.listFiles().length+".csv");
           
            RFIfw.out.println("Case Nbr, Rule Name, Predicted, Actual");
         
        }
        
        RFIfw.out.println(caseNumber+","+ruleName+","+ruleClass+","+dsClass);
     
    }
    /**
     * Close stream for writing rule fired information.
     * @since 3.1
     */
    private void closeRFI()
    {
        try
        {
            RFIfw.close();
        }
        catch(Exception e)
        {
            Debugger.printlnSensitive("Unable to close output stream for recording rule firing info");
        }
        RFIfw= null;
        caseNumber= 0;
    }  
     /**
      * Get the main confusion matrix of this RuleSet object. 
      * @return Confusion matrix.
      */
     public int[][] getMatrix()
     {
         return this.Matrix;
     }
     /**
      * Get True Positive (TP) measure. 
      * @return True Positive measure.
      */
     public int getTruePositive()
     {
         return this.Matrix[0][0];
     }
     /**
      * Get True Positive (TP) measure. 
      * @return True Positive measure.
      */
     public int getTP()
     {
         return getTruePositive();
     }
     /**
      * Get True Negative (TN) measure. 
      * @return True Negative measure.
      */
     public int getTrueNegative()
     {
         return this.Matrix[1][1];
     }
     /**
      * Get True Negative (TN) measure. 
      * @return True Negative measure.
      */
     public int getTN()
     {
         return getTrueNegative();
     }
     /**
      * Get False Negative (FN) measure. 
      * @return False Negative measure.
      */
     public int getFN()
     {
         return getFalseNegative();
     }
     /**
      * Get False Negative (FN) measure. 
      * @return False Negative measure.
      */
     public int getFalseNegative()
     {
         return this.Matrix[0][1];
     }
      
     /**
      * Get False Positive (FP) measure. 
      * @return False Positive measure.
      */
     public int getFP()
     {
         return getFalsePositive();
     }
      
     /**
      * Get False Positive (FP) measure. 
      * @return False Positive measure.
      */
     public int getFalsePositive()
     {
         return this.Matrix[1][0];
     }
       
     /**
      * Get True Positive (TP) measure for individual dataset. 
      * @return True Positive measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiTruePositive()
     {
         return this.IndiMatrix[0][0];
     }
      /**
      * Get True Positive (TP) measure for individual dataset. 
      * @return True Positive measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiTP()
     {
         return getIndiTruePositive();
     }
      /**
      * Get True Negative (TN) measure for individual dataset. 
      * @return True Negative measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiTrueNegative()
     {
         return this.IndiMatrix[1][1];
     } 
     /**
      * Get True Negative (TN) measure for individual dataset. 
      * @return True Negative measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiTN()
     {
         return getIndiTrueNegative();
     }
     /**
      * Get False Negative (FN) measure for individual dataset. 
      * @return False Negative measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiFN()
     {
         return getIndiFalseNegative();
     }
     /**
      * Get False Negative (FN) measure for individual dataset. 
      * @return False Negative measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiFalseNegative()
     {
         return this.IndiMatrix[0][1];
     }
     /**
      * Get False Positive (FP) measure for individual dataset. 
      * @return False Positive measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiFP()
     {
         return getIndiFalsePositive();
     } 
     /**
      * Get False Positive (FP) measure for individual dataset. 
      * @return False Positive measure for individual dataset.
      * @since 2.0
      */ 
     public int getIndiFalsePositive()
     {
         return this.IndiMatrix[1][0];
     }
     /**
      * Get the individual confusion matrix of this RuleSet object. 
      * @return Individual confusion matrix.
      * @since 2.0
      */
     public int[][] getIndiMatrix()
     {
         return this.IndiMatrix;
     }
     
    /**
     * Get the correctness of general matrix.
     * @return double correctness.
     */
    public double getCorrectness() 
    {
        return Mathematics.correctness(Matrix);
    }

    /**
     * Get the Jindex of general matrix.
     * @return double Jindex.
     */
    public double getJindex() 
    {
        return Mathematics.jindex(Matrix);
    }

    /**
     * Get the precision of general matrix.
     * @return double precision.
     */
    public double getPrecision() 
    {
        return Mathematics.precision(Matrix);
    }

    /**
     * Get the recall of general matrix.
     * @return double recall.
     */
    public double getRecall() 
    {
        return Mathematics.recall(Matrix);
    }

    /**
     * Get the sensitivity of general matrix.
     * @return double sensitivity.
     */
    public double getSensitivity() 
    {
        return Mathematics.sensitivity(Matrix);
    }

    /**
     * Get the specificity of general matrix.
     * @return double specificity.
     */
    public double getSpecificity() 
    {
        return Mathematics.specificity(Matrix);
    }

    /**
     * Get the correctness of the individual matrix.
     * @return double correctness.
     * @since 2.0
     */
    public double getIndiCorrectness() 
    {
        return Mathematics.correctness(IndiMatrix);
    }

    /**
     * Get the Jindex of individual matrix.
     * @return double Jindex.
     * @since 2.0
     */
    public double getIndiJindex() 
    {
        return Mathematics.jindex(IndiMatrix);
    }

    /**
     * Get the precision of individual matrix.
     * @return double precision.
     * @since 2.0
     */
    public double getIndiPrecision() 
    {
        return Mathematics.precision(IndiMatrix);
    }

    /**
     * Get the recall of individual matrix.
     * @return double recall.
     * @since 2.0
     */
    public double getIndiRecall() 
    {
        return Mathematics.recall(IndiMatrix);
    }

    /**
     * Get the sensitivity of individual matrix.
     * @return double sensitivity.
     * @since 2.0
     */
    public double getIndiSensitivity() 
    {
        return Mathematics.sensitivity(IndiMatrix);
    }

    /**
     * Get the specificity of individual matrix.
     * @return double specificity.
     * @since 2.0
     */
    public double getIndiSpecificity() 
    {
        return Mathematics.specificity(IndiMatrix);
    }

    /**
     * Produce definition of this RuleSet inside a file.
     * @throws Exception Unable to complete task.
     * @since 1.1
     */
    public void produceDefinition() throws Exception 
    {
        if(sp.ruleset_writer==null)
            SetupNotConfiguredException.occur("ruleset_writer");
        
        RuleSetWriter writer= (RuleSetWriter) sp.ruleset_writer.newInstance();
        writer.write(this);
        writer.close();
    }
       
     /**
      * Produce the general confusion matrix.
      * @throws IOException Unable to complete task.
      * @throws InvalidInputException Classifications input file is invalid.
      * @since 1.1
      */
     public void produceMatrix() throws IOException, InvalidInputException
     {
         if(sp.ruleset_output_matrix_dir== null)
             SetupNotConfiguredException.occur("ruleset_output_matrix_dir");
         
        String outname= sp.ruleset_output_matrix_dir+"/"+getParent().getName();
         if(ID!= -1)
            outname+= "("+ID+")";
        outname+= ext;
        outputMatrix(Matrix, outname);
     }
         
     /**
      * Produce the individual confusion matrix.
      * @throws IOException Unable to complete task.
      * @throws InvalidInputException Classifications input file is invalid.
      * @since 2.0
      */
     public void produceIndiMatrix(String datasetName) throws IOException, InvalidInputException
     {
         if(sp.ruleset_output_matrix_dir== null)
             SetupNotConfiguredException.occur("ruleset_output_matrix_dir");
         
        String dir= sp.ruleset_output_matrix_dir+"/"+datasetName+"/";
        File f= new File(dir);
        if(!f.exists())
            f.mkdirs();
        String outname= dir + getParent().getName();
        if(ID!= -1)
            outname+= "("+ID+")";
        outname+= ext;
        outputMatrix(IndiMatrix, outname);
     }
     
     private void outputMatrix(int[][] matrix, String filename) throws IOException, InvalidInputException
     {
         List<String> cls= StartUp.getClassList(sp);
         
         if(output_type!= Library.OUTPUT_TYPE.XLS)
         {
             TextWriter txt= new TextWriter(filename);
             txt.out.println("Horizantal: Predictions by Ruleset");
             txt.out.println("Verticle: Actual Predictions in Dataset");
             txt.out.print(sep);
             
             for(String c: cls)
                 txt.out.print(c +sep);
       
             txt.out.println();
             
             for(int i=0; i<matrix.length; i++)
              {
                  txt.out.print(cls.get(i) +sep);
                  for(int j=0; j<matrix[i].length; j++)
                      txt.out.print(matrix[i][j]+sep);
                  txt.out.println();
              }
                
             if(this.parent!=null)
             {
                  
                 txt.out.println();
                 txt.out.println("This RuleSet belongs to "+this.parent.getName());  
             }
             txt.out.close(); 
         }
         else
         {
             ExcelWriter ex2= new ExcelWriter(filename);
             ex2.addSheet(parent.getName());
             ex2.println("Horizantal: Predictions by Ruleset",ex2.BoldBlue);
             ex2.println("Verticle: Actual Predictions in Dataset",ex2.BoldRed);
             ex2.print(sep+"");
  
             for(String c: cls)
                 ex2.print(c +sep);
              
              ex2.println();
              for(int i=0; i<matrix.length; i++)
              {
                  ex2.print(cls.get(i)+sep);
                  for(int j=0; j<matrix[i].length; j++)
                  {
                      ex2.print(matrix[i][j]+""+sep);
                  }
                  ex2.println();
              }
                
              if(this.parent!=null)
              {
                  ex2.println();
                  ex2.println("This RuleSet belongs to "+parent.getName(),ex2.BoldBlack);
               }
             ex2.flush();
             ex2.close();
         }
     }
    
     /**
      * Get cut off list for the given metric name.
      * @param metric Metric name for which cut off list must be determined.
      * @return Cut off list determined for given metric name.
      * @since 3.3
      */
     protected LinkedList<Double> getCutOffList(String metric)
     {
        LinkedList<Condition> sameMetricConditions= new LinkedList<Condition>();
        Condition cond;
        
        for(Rule rule: this.list)
        {
            cond= rule.getCondition(metric);
            if(cond!=null)
            {
                cond.classification= rule.getStringClassification();
                sameMetricConditions.add(cond);
            }    
        }
       
        Condition[] sameMetricArray= new Condition[sameMetricConditions.size()];
        sameMetricArray= sameMetricConditions.toArray(sameMetricArray);
        Arrays.sort(sameMetricArray);
        
        LinkedList<Double> cutpoints= new LinkedList<Double>();
        Condition cond1, cond2;
        double v1, v2, cp;
        for(int i=0; i<sameMetricArray.length-1; i++)
        {
            cond1= sameMetricArray[i];
            cond2= sameMetricArray[i+1];
            if(cond1.classification.equals(cond2.classification))
                continue; //no shift occured.
            v1= Double.parseDouble(cond1.value);
            v2= Double.parseDouble(cond2.value);
            cp= (v1+v2)/2.0;
            if(!cutpoints.contains(cp))
                cutpoints.add(cp);
        }
        return cutpoints;
     }
      
      /**
      * Get value list for the given metric name.
      * @param metric Metric name for which value list must be determined.
      * @return Value list determined for given metric name.
      * @since 3.3
      */
     public LinkedList<Double> getValueList(String metric)
     {  
        LinkedList<Condition> sameMetricConditions= new LinkedList<Condition>();
        Condition cond;
        for(Rule rule: this.list)
        {
            cond= rule.getCondition(metric);
            if(cond!=null)
            {
                cond.classification= rule.getStringClassification();
                sameMetricConditions.add(cond);
            }    
        }
        
        LinkedList<Double> values= new LinkedList<Double>();
        double value;
        for(int i=0; i<sameMetricConditions.size(); i++)
        {
            value= Double.parseDouble(sameMetricConditions.get(i).value);
            if(!values.contains(value))
                values.add(value);
        }
        return values;
     }
     
     /**
      * Sort Rules based on their quality measure. This will affect future outcomes of
      * {@link TestingMode#SEQUENTIAL}.
      * @since 3.3
      */
     public void sortRulesByQuality()
     {
         Rule[] rules= new Rule[list.size()];
         rules= list.toArray(rules);
         Arrays.sort(rules);
         Rule[] des_rules= new Rule[rules.length];
         for(int i=0, j= des_rules.length-1; i<rules.length; i++, j--)
             des_rules[j]= rules[i];
         list.clear();
         list.addAll(Arrays.asList(des_rules));
     }
     /**
      * Set the optimal default condition for this Rule based on given DataSet file.
      * @param dataset DataSet File for which optimal default classification must be determined. 
      * @throws FileNotFoundException Unable to locate DataSet file.
      * @throws InvalidInputException DataSet file is invalid.
      * @since 3.3
      */
     public void setOptimualDefaultCond(File dataset) throws FileNotFoundException, InvalidInputException
     {
         if(sp.dataset_reader==null)
             SetupNotConfiguredException.occur("dataset_reader");
         
         DataSetReader reader= (DataSetReader) sp.dataset_reader.newInstance();
         reader.open(new FileInputStream(dataset));
         int[] unclassifiedClasses= new int[StartUp.getClassList(sp).size()];
        
         DataSetLine line;
         boolean matched;
         while(reader.hasNext())
         {
             line= reader.getNext();
             if(line==null)
                 continue;
             matched= false;
             for(Rule r: list)
                 if(r.test(line))
                 {
                     matched= true;
                     break;
                 }
             if(matched)
                 continue;
              unclassifiedClasses[StartUp.getClassList().indexOf(line.getClassification())]++;
         }
         try
         {
            reader.close();
         }
         catch(Exception e){}
         
         int max_index= 0;
         for(int i=1; i< unclassifiedClasses.length; i++)
             if(unclassifiedClasses[max_index]< unclassifiedClasses[i])
                 max_index= i;
         setDefaultCond(StartUp.getClassList().get(max_index));
         Debugger.printlnSensitive("Default class set: "+this.string_default_cond);
     }
     
     /**
      * Set the optimal default condition for this Rule based on given DataSet Object.
      * @param dataset DataSet Object for which optimal default classification must be determined.
      * @throws InvalidInputException DataSet file is invalid.
      * @since 3.3
      */
     public void setOptimualDefaultCond(DataSet dataset) throws InvalidInputException
     {
         if(sp.dataset_reader==null)
             SetupNotConfiguredException.occur("dataset_reader");
         
         int[] unclassifiedClasses= new int[StartUp.getClassList(sp).size()];
        
         Iterator<DataSetLine> it= dataset.getDataSetLinesIterator();
         DataSetLine line;
         boolean matched;
         while(it.hasNext())
         {
             line= it.next();
             matched= false;
             for(Rule r: list)
                 if(r.test(line))
                 {
                     matched= true;
                     break;
                 }
             if(matched)
                 continue;
              unclassifiedClasses[StartUp.getClassList().indexOf(line.getClassification())]++;
         }
         int max_index= 0;
         for(int i=1; i< unclassifiedClasses.length; i++)
             if(unclassifiedClasses[max_index]< unclassifiedClasses[i])
                 max_index= i;
         setDefaultCond(StartUp.getClassList().get(max_index));
         Debugger.printlnSensitive("Default class set: "+this.string_default_cond);
     }
     
     
    private static volatile int temp_count = 0;

    private synchronized int getTempID() 
    {
        return temp_count++;
    }

    @Override
    public RuleSet clone() 
    {
        try 
        {
            String name = sp.home_dir + "/" + this.getParent().getName() + list.size() + "" + getTempID();
            File file = new File(name);
            file.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            fos.close();

            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            RuleSet rs = (RuleSet) ois.readObject();
            ois.close();
            fis.close();
            if (!file.delete())
                file.deleteOnExit();
            
            return rs;
        } 
        catch (Exception e) 
        {
            Debugger.printlnError(e);
            Debugger.printlnWarning("RuleSet copying failed!");
            return null;
        }

    }
  
    
    @Override
    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------\n");
        sb.append("Processing tree 0\n");
        sb.append("Final rules from tree 0:\n");

        int i= -1;
        for(Rule r: getRules())
        {
            i++;
            sb.append("Rule ").append(i).append(": \n");
            for(Condition cond: r.getConditions(false))
                sb.append(cond.metric).append(" ").append(cond.operator).append(" ").append(cond.value).append("\n");
     
            sb.append("->  class ").append(list.get(i).getStringClassification()).append("  [100.0%]\n");
        }
        sb.append("Default class: ").append(string_default_cond).append("\n");
        return sb.toString();
    }
    
     /**
     * Testing Mode versions.<br/>
     * {@link TestingMode#SEQUENTIAL} is default mode. Rules are selected sequentially 
     * to decide classification for given DataSet line. The first Rule to have match decides
     * the predicted classification.<br/>
     * {@link TestingMode#VOTING} is used to allow all Rules to cast their prediction.
     * The classification with most amount of votes becomes the predicted classification.
     * @since 2.0
     */
    public enum TestingMode
    {
        SEQUENTIAL, VOTING;
    }
    
 
    /**
     * Testing mode used to run experiments.
     * @since 2.0
     */
    private static TestingMode testMode= TestingMode.SEQUENTIAL; 
     
    /**
     * Flag to indicate whether Rules match classifications within a range { a-Range, a, a+Range }.
     * @see #RANGE
     * @since 3.1
     */
    private static boolean matchWithinRange= false;
    
     // Testing range. Needs matchWitinRange= true to be used.
 
    /**
     * Classification acceptable error range. Requires <code>{@link #matchWithinRange}= true</code>
     * @see #matchWithinRange
     * @since 3.1
     */
    private static double range= 0.0;
       
    /**
     * Get the testing mode required on DataSet files.
     * @return TestingMode required.
     * @since 2.0
     */
    public static TestingMode getTestingMode()
    {
        return testMode;
    }
    /**
     * Set the testing mode required on DataSet files.
     * @param mode testing mode required.
     * @since 2.0
     */
    public static void setTestingMode(TestingMode mode)
    {
        testMode= mode;
        if(testMode== TestingMode.VOTING)
            needRuleFiredInfo= false;
    }
    /**
     * Check if Rules match classifications within a range { a-Range, a, a+Range }.
     * @return True if matching is done within range, false otherwise.
     * @since 3.1
     */
    public static boolean isMatchingWithinRange()
    {
        return matchWithinRange;
    }
    /**
     * Deactivate matching within range.
     * @since 3.1
     */
    public static void deactivateMatchingWithinRange()
    {
        matchWithinRange= false;
    }
    /**
     * Activate matching within range.
     * @param range Classification acceptable error range { a-Range, a, a+Range }.
     */
    public static void activateMatchingWithinRange(double new_range)
    {
        matchWithinRange= true;
        range= new_range;
    }
    /**
     * Get classification acceptable error range.
     * @return Classification acceptable error range.
     */
    public static double getRange()
    {
        return range;
    }

    /**
     * Flag to enable output information about which Rule fired during DataSet testing.
     * @since 3.1
     */
    private static boolean needRuleFiredInfo= false;
    
  
    /**
     * Set if information about which Rule fired during DataSet case testing
     * is needed or not.
     * <br>Will not work if VOTING testing mode is set. In this mode, such info
     * tracking is not available.</br>
     * @param need flag indicating if Rule fired information is needed or not.
     * @since 3.1
     */
    public static void setNeedRuleFiredInfo(boolean need)
    {
        if(testMode== TestingMode.VOTING)  
            need= false;
            
        needRuleFiredInfo= need;
    }
    /**
     * Check whether output information about which Rule fired during DataSet testing
     * is enabled.
     * @return True if is enabled, false otherwise.
     * @since 3.1
     */
    public static boolean needRuleFiredInfo()
    {
        return needRuleFiredInfo;
    }
}
