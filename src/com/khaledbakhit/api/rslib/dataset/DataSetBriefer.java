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
package com.khaledbakhit.api.rslib.dataset;

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.Library;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.calc.Function;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import com.khaledbakhit.api.rslib.utils.Debugger;
import com.khaledbakhit.api.rslib.utils.ExcelWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * DataSetBriefer is responsible of determining the test results of each DataSet.
 * @author Khaled Bakhit
 * @since 2.0
 * @version 21/08/2013
 */
public class DataSetBriefer
{
    /**
     * LaunchSetup Object containing information about directories used.
     * @since 4.0
     */
    private LaunchSetup sp;

    /**
     * DataSetBriefer constructor. Uses default LaunchSetup Object.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public DataSetBriefer()
    {
         this(Program.getInstance().getLaunchSetup());
    }
    /**
     * DataSetBriefer constructor. 
     * @param sp LaunchSetup Object containing input configuration.
     * @since 4.0
     */
    public DataSetBriefer(LaunchSetup sp)
    {
        this.sp= sp;
    }
    
    /**
     * Collect RuleSet individual results and brief all DataSet individual test results.
     * @parm type output type. <b>Must same as RuleSet individual results file format.</b>
     * @throws IOException Unable to complete task.
     * @throws BiffException Unable to read/write Excel files.
     */
    public void brief(Library.OUTPUT_TYPE type) throws IOException, BiffException
    {
        if(sp.input_dataset_dir == null )
            SetupNotConfiguredException.occur("input_dataset_dir");
        if(sp.ruleset_output_individual_results_dir == null )
            SetupNotConfiguredException.occur("ruleset_output_individual_results_dir");
        if(sp.dataset_output_individual_results_dir == null )
            SetupNotConfiguredException.occur("dataset_output_individual_results_dir");
        
        File file= new File(sp.input_dataset_dir);
        String[] datasets= file.list(); // listing all input dataset files
        //getting all RuleSet individual results files
        File[] rules= new File(sp.ruleset_output_individual_results_dir).listFiles();
        
        Scanner scan, subScan;
        if(type== Library.OUTPUT_TYPE.TEXT || type== Library.OUTPUT_TYPE.CSV)
        {
            boolean isText= type==Library.OUTPUT_TYPE.TEXT;
            String sep= (isText)?"\t":",";
            FileWriter fw;
            BufferedWriter bw;
            PrintWriter output;
            for(int i=0; i<datasets.length; i++)
            {
                fw= new FileWriter(sp.dataset_output_individual_results_dir+"/"+
                        datasets[i]+ ((isText)?".txt":".csv"));    
                bw= new BufferedWriter(fw);
                output= new PrintWriter(bw);
                output.print("Summarized Results"+sep);
        
                for(int j=0; j< Function.size(); j++)
                    output.print(sep + Function.getName(j) );
                
                output.println("\n\nRuleSets: ");
                //Iterating through every RuleSet and getting the line
                //related to current Dataset datasets[i]
                for(int j=0; j<rules.length; j++)
                    try 
                    {
                        scan= new Scanner(rules[j]);
                        //printing name of RuleSet file without extension
                        output.print(rules[j].getName().substring(0, rules[j].getName().lastIndexOf('.'))+sep+sep);
                        //skipping blank lines
                        scan.nextLine();
                        scan.nextLine();
                        scan.nextLine();
                        //searching for the line relative to the current dataset.
                        while(scan.hasNext())
                        {
                            subScan= new Scanner(scan.nextLine());
                            subScan.useDelimiter(sep);
                            //check if line starts with datasets[i] name
                            if(datasets[i].equalsIgnoreCase(subScan.next()))
                            {
                                subScan.next();
                                for(int k=0; k<Function.size(); k++)
                                    output.print(subScan.next()+ sep );
                                output.println();
                                break;
                            }
                        }
                }
                catch(Exception e)
                {
                    Debugger.printlnWarning("No record found for "+rules[j].getName());
                    Debugger.printlnError(e);
                    continue;
                }
            
                output.close();
            }
       }
        else
        {
            ExcelWriter output;
            for(int i=0; i<datasets.length; i++)
            {
                //fw points to DataSet individual results file
                output= new ExcelWriter(sp.dataset_output_individual_results_dir+"/"+
                        datasets[i]+".xls");
                output.addSheet(datasets[i]);
                output.print("Summarized Results\t");
                for(int j=0; j<Function.size(); j++)
                    output.print("\t"+Function.getName(j));
                output.println();
                output.println();
                output.println("RuleSets: ");
                //Iterating through every RuleSet and getting the line
                //related to current Dataset datasets[i]
                int row=0;
                String r;
                Cell[] c;
                Workbook work;
                Sheet s;
                
                for(int j=0; j<rules.length; j++)
                    try
                    {
                    
                        work= Workbook.getWorkbook(rules[j]);
                        s= work.getSheet(0);
                        //printing name of RuleSet file without extension
                        output.print(rules[j].getName().substring(0, rules[j].getName().lastIndexOf('.'))+"\t\t");
                        //skipping blank lines
                        row+=3;
                        for(int k=row; k<s.getRows(); k++)
                        {
                            c= s.getRow(k);
                            r= "";

                            for(int m=0; m<c.length; m++)
                                r+= c[m].getContents()+"\t";
                            
                            subScan= new Scanner(r);
                            subScan.useDelimiter("\t");
                            //check if line starts with datasets[i] name
                            if(datasets[i].equalsIgnoreCase(subScan.next()))
                            {
                                subScan.next();
                                for(int m=0; m<Function.size(); m++)
                                    output.print("\t"+subScan.next());
                            
                                output.println();
                                break;
                            }
                    }
                }
                catch(Exception e)
                {
                    Debugger.printlnWarning("No record found for "+rules[j].getName());
                    Debugger.printlnError(e);
                    continue;
                }
                output.flush();
                output.close();
            }
        }
    }
}