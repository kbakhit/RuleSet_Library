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
package com.khaledbakhit.api.rslib.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * ExcelWriter writes documents in form of Microsoft Excel 2003 workbook form.
 * @author Khaled Bakhit
 * @since 3.0
 * @version 05/09/2011
 */
public class ExcelWriter implements java.io.Closeable
{
    /**
     * WritableWorkbook object that writes the required output file. 
     */
    private WritableWorkbook workbook;
    
    /**
     * WritableSheet object representing an Excel sheet. 
     */
    private WritableSheet sheet;
    
    /**
     * Row index to write into. 
     */
    private int row;
    
    /**
     * Column index to write into. 
     */
    private int col;
    
    /**
     * Arial 10 Bold Blue format. 
     */
    public WritableCellFormat BoldBlue;
    /**
     * Arial 10 Bold Red format.
     */
    public WritableCellFormat BoldRed;
    /**
     * Arial 10 Bold Black format. 
     */
    public WritableCellFormat BoldBlack;
    
    /**
     * Arial 10 Black Default format. 
     */
    public WritableCellFormat Default;
    /**
     * ExcelWriter constructor. 
     * @param outputName Name of output Excel 2003 workbook.
     * @throws IOException Unable to work with output location.
     */
    public ExcelWriter(String outputName) throws IOException
    {
        this(new File(outputName));
    }
    
    /**
     * ExcelWriter constructor. 
     * @param outputFile Excel 2003 output workbook.
     * @throws IOException Unable to work with output location.
     */
    public ExcelWriter(File outputFile) throws IOException
    {
        workbook = Workbook.createWorkbook(outputFile);
        WritableFont font1 = new WritableFont(WritableFont.ARIAL, 10); 
        WritableFont font2= new WritableFont(WritableFont.ARIAL, 10);
        WritableFont font3= new WritableFont(WritableFont.ARIAL, 10);
        WritableFont defaultFont= new WritableFont(WritableFont.ARIAL, 10);
        try {
            font1.setColour(Colour.DARK_BLUE);
            font1.setBoldStyle(WritableFont.BOLD);
            font2.setBoldStyle(WritableFont.BOLD);
            font2.setColour(Colour.RED);
            font3.setColour(Colour.BLACK);
            font3.setBoldStyle(WritableFont.BOLD);
            } 
        catch (WriteException ex) 
        {
         
        }
        
        BoldBlue= new WritableCellFormat(font1);
        BoldRed= new WritableCellFormat(font2);
        BoldBlack= new WritableCellFormat(font3);
        Default= new WritableCellFormat(defaultFont);
    }
    
    /**
     * Add a sheet to the beginning of the workbook.  
     * @param name Name of sheet to add/create.
     */
    public void addSheet(String name)
    {
        sheet= workbook.createSheet(name,0);
        row=0;
        col=0;
       
    }
    
    /**
     * Print the following line into the ExcelSheet using default font.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param line String to print.
     */
    public void println(String line)
    {
       println(line,Default);
    }
    
    /**
     * Print an empty line. 
     */
    public void println()
    {
        row++;
        col=0;
    }
    
    /**
     * Print the following line into the ExcelSheet.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param line String to print.
     * @param font Font to use.
     */
    public void println(String line,WritableCellFormat font)
    {
        String[] words= line.split("\t");
        println(words,font);
    }
    
     /**
     * Print the following line into the ExcelSheet.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param words String array.
     */
    public void println(String[] words)
    {
        println(words, Default);
    }
    
    /**
     * Print the following line into the ExcelSheet.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param words String array.
     * @param font Font to use. 
     */
    public void println(String[] words,WritableCellFormat font)
    {
        print(words, font);
        println();
    }
    
     /**
     * Print the following words into the ExcelSheet.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param words String array.
     */
    public void print(String[] words)
    {
        print(words, Default);
    }
    
    /**
     * Print the following words into the ExcelSheet.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param words String array.
     * @param font Font to use. 
     */
    public void print(String[] words,WritableCellFormat font)
    {
        double found;
        for(int i=0;i<words.length; i++)
            try
             {
                 try
                 {
                   found= Double.parseDouble(words[i]);
                   sheet.addCell(new jxl.write.Number(i+col,row,found, font));
                 }
                 catch(Exception e)
                 {
                     sheet.addCell(new Label(i+col,row,words[i],font));
                 }
             }
             catch(Exception e){}
            
        col= col + words.length;
    }
    
    
     /**
     * Print the following line into the ExcelSheet.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param words LinkedList object containing String objects.
     */
    public void println(LinkedList<String> words)
    {
        println(words, Default);
    }
    
    /**
     * Print the following line into the ExcelSheet.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param words LinkedList object containing String objects.
     * @param font Font to use. 
     */
    public void println(LinkedList<String> words,WritableCellFormat font)
    {
       double found;
        for(int i=0;i<words.size(); i++)
             try
             {
                 try
                 {
                   found= Double.parseDouble(words.get(i));
                   sheet.addCell(new jxl.write.Number(i+col,row,found, font));
                 }
                 
                 catch(Exception e)
                 {
                    sheet.addCell(new Label(i+col,row,words.get(i),font));
                 }
             }
             catch(Exception e)
             {
                
             }
       
           println();
    }
    
    /**
     * Print the following line into the ExcelSheet without going into a new line.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param line String to print.
     */
    public void print(String line)
    {
        print(line, Default);
    }
    
    /**
     * Print the following line into the ExcelSheet without going into a new line.
     * <br>Use {@code '\t'} to separate between cells.</br>
     * @param line String to print.
     * @param font Font to use.
     */
    public void print(String line, WritableCellFormat font)
    {
        String[] words= line.split("\t");
       
        double found;
        for(int i=0;i<words.length; i++)
            try
            {
                if(words[i].trim().length()==0)
                    continue;
            
                try
                 {
                   found= Double.parseDouble(words[i]);
                   sheet.addCell(new jxl.write.Number( i + col , row ,found, font));
                 }
                 catch(Exception e)
                 {
                    sheet.addCell(new Label( i + col ,row , words[i] ,font));
                 }
              
            }
            catch(Exception e)
            {
                Debugger.printlnError(e);
            }    
     
        col= col + words.length; //col++      
    }
    
    /**
     * Flush the written content. 
     */
    public void flush()
    {
        try {
            workbook.write();
        } catch (Exception ex) 
        {
        }
    }
    
    
    @Override
    public void close()
    {
        try {
            workbook.close();
        } catch (Exception ex) 
        {
        }
    }
}
