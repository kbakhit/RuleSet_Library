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

import com.khaledbakhit.api.rslib.LaunchSetup;
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.exceptions.SetupNotConfiguredException;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Packager zips experiment output results along with the input and moves
 * them into folder containing completed experiments.
 * @author Khaled Bakhit
 * @since 3.0
 * @version 24/08/2013
 */
public class Packager 
{ 
    /**
     * Zip the Input and Output folders and place them in the completed Folder.
     * Uses the default LaunchSetup Object defined.
     * @param outputName Name of output File.
     * @return output zip File.
     * @throws FileNotFoundException Unable to find destination.
     * @throws IOException Unable to complete task.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public static File pack(String outputName) throws FileNotFoundException, IOException
    {
        return pack(outputName, Program.getInstance().getLaunchSetup());
    }
    
    /**
     * Zip the Input and Output folders and place them in the completed Folder. 
     * @param outputName Name of output File.
     * @param sp LaunchSetup Object containing input configuration.
     * @return output zip File.
     * @throws FileNotFoundException Unable to find destination.
     * @throws IOException Unable to complete task.
     * @since 4.0
     */
    public static File pack(String outputName, LaunchSetup sp) throws FileNotFoundException, IOException 
    {
        if(sp.output_completed_dir== null)
            SetupNotConfiguredException.occur("output_completed_dir");
        else if(sp.input_dir == null)
            SetupNotConfiguredException.occur("input_dir");
        else if(sp.output_dir == null)
            SetupNotConfiguredException.occur("output_dir");
        
        String tempDir= sp.output_completed_dir+"/"+outputName;
        
        File outputLocation= new File(tempDir);
        if(!outputLocation.exists())
            outputLocation.mkdir();
        //Move results into completed folder
        move(sp.input_dir, tempDir+"/"+(new File(sp.input_dir)).getName());
        move(sp.output_dir, tempDir+"/"+(new File(sp.output_dir)).getName());
        
        //Performing zip operation
        outputName= tempDir;
        zip(outputName,outputName);
         
        System.gc();
        //Delete original input
        delete(sp.input_dir);
        System.gc();
        delete(sp.output_dir);
         System.gc();
        //Delete moved folder
        delete(outputName);
     
        //Refresh Program
        Program.getFreshInstance(sp);
        return new File(outputName+".zip");
    }
    
    /**
     * Delete a given file or directory. 
     * @param filename name of file or directory to delete.
     */
    public static void delete(String filename)
    {
        File f= new File(filename);
        if(f.isDirectory())
        {
            File[] list= f.listFiles();
            for(int i=0; i<list.length; i++)
                delete(list[i].getAbsolutePath());
        }
        if(!f.delete())
            f.deleteOnExit();
    }
    
    /**
     * Move a file or directory from source to destination. 
     * @param src Source file name (can be directory).
     * @param dest Output file name (can be name of directory).
     * @throws FileNotFoundException Unable to find destination.
     * @throws IOException Unable to complete task.
     */
    public static void move(String src, String dest) throws FileNotFoundException, IOException 
    {
        File srcDir= new File(src);
        File outDir= new File(dest);
        if(!outDir.exists() && srcDir.isDirectory())
                outDir.mkdir();
        
        File[] srcList; 
        if(srcDir.isDirectory())
            srcList= srcDir.listFiles();
        else
            srcList= new File[]{srcDir};
        File srcFile;
        FileOutputStream fos;
        FileChannel channel;
        FileInputStream fis;
        ReadableByteChannel rbc;
        
        for(int i=0; i<srcList.length; i++)
        {
            srcFile= srcList[i];
            if(srcFile.isDirectory())
                move(srcFile.getAbsolutePath(),dest+"/"+srcFile.getName());
            else
            {
                fis= new FileInputStream(srcFile);
                fos= new FileOutputStream(dest+"/"+srcFile.getName());
                channel= fos.getChannel();
                rbc= Channels.newChannel(fis);
                channel.transferFrom(rbc, 0,srcFile.length());
                rbc.close();
                channel.close();
                fos.close();
                fis.close();
                
            }
        }
     }
    
    private static CRC32 crc;
    
    /**
     * Zip a given source file into a zip file. 
     * <br><b>WARNING: empty folders will not be zipped</b></br>
     * @param src name of file or directory to zip.
     * @param filename name of output zip file.
     * @throws FileNotFoundException Unable to find destination.
     * @throws IOException Unable to complete task.
     */
    public static void zip(String src, String filename) throws FileNotFoundException, IOException 
    {
        filename= filename+".zip";
        ZipOutputStream s=  new ZipOutputStream((OutputStream)
                new FileOutputStream(filename));
        s.setLevel(9);
        File srcDir= new File(src);
        File[] list= srcDir.listFiles();
        crc= new CRC32();
        zip("",list, s);
        s.finish();
        s.close();
    }
    
    /**
     * Enter the following list of Files into the ZipOutputStream. 
     * @param path Path of the files, starting from name of source File 
     * {@link #zip(java.lang.String, java.lang.String)}.
     * @param list Files to zip.
     * @param s ZipOutputStream connected to output zip file.
     * @throws IOException Unable to complete task.
     */
    private static void zip(String path,File[] list, ZipOutputStream s) throws IOException
    {
        byte[] buff;
        ZipEntry entry;
        FileInputStream fis;
        File input;
        
        for(int i=0; i<list.length; i++)
        {
            input= list[i];
            if(input.isDirectory())
            {
                zip(path+input.getName()+"/",input.listFiles(), s);
                continue;
            }
            buff= new byte[(int) input.length()];
            fis= new FileInputStream(input);
            fis.read(buff, 0, buff.length);
            fis.close();
            entry= new ZipEntry(path+input.getName());
            entry.setSize(buff.length);
            crc.reset();
            entry.setCrc(crc.getValue());
            s.putNextEntry(entry);
            s.write(buff, 0, buff.length);
            s.flush();    
            s.closeEntry();
        }
    }
    
    
}
