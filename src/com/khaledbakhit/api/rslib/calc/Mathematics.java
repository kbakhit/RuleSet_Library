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
import java.util.Arrays;
import java.util.List;

/**
 * Mathematics contains some important functions to measure performance.
 * @author Khaled Bakhit
 * @since 3.1
 * @version 15/11/2012
 */
public class Mathematics 
{    
   /**
     * Calculate coverage.
     * @param correct Number of cases classified correctly.
     * @param wrong Number of cases classified incorrectly.
     * @param failed Number of cases failed to classify.
     * @return Computed coverage.
     * @since 3.2
     */
    public static double coverage(double correct, double wrong, double failed)
    {
        double classifications = correct+ wrong;
                       
        return classifications/(classifications+failed);
    }
    /**
     * Calculate accuracy.
     * @param correct Number of cases classified correctly.
     * @param matched Number of cases that didn't fail to classify.
     * @return Computed accuracy.
     */
    public static double accuracy(double correct, double matched)
    {
        if(matched==0)
            matched =1;
        
        return correct/matched;
    }
   
     /**
     * Calculate the correctness of 2D array.
     * @param Matrix int[][] array.
     * @return double correctness.
     */
     public static double correctness(int[][] Matrix)
    {
              double sum1=0, sum2=0;
              for(int i=0; i<Matrix.length; i++)
                  sum1 += Matrix[i][i];

              for(int i=0; i<Matrix.length; i++)
                  for(int j=0; j<Matrix[i].length; j++)
                      sum2+= Matrix[i][j];
             if(sum1==0)
                 return 0;
             if(sum2==0)
             {
                Debugger.printlnWarning("dividing by 0 in Correctness computation");
                return -1;
             }
            return sum1/sum2;

          }

    /**
     * Calculate the jindex of 2D array.
     * @param Matrix int[][] array.
     * @return double jindex.
     */
     public static double jindex(int[][] Matrix)
    {
              double denomenator= 0.0;
              double k= Matrix.length;
              double sum=0.0;

            
              for(int i=0; i<Matrix.length; i++)
              {
                  for(int j=0; j<Matrix.length; j++)
                    denomenator+= Matrix[i][j];
                  if(Matrix[i][i]==0 && denomenator==0)
                      sum+= 0;
                  else
                     sum+= Matrix[i][i]/denomenator;
                denomenator=0;
              }
              if(sum==0)
                  return 0;
              if(k==0)
              {
                Debugger.printlnWarning("no classification lables in class file. Jindex= -1");
                return -1;
                }
              return sum/k;

          }

    /**
     * Calculate the precision of 2D array.
     * @param Matrix int[][] array.
     * @return double precision.
     */
     public static double precision(int[][] Matrix)
    {
        if(Matrix[0].length==2)
        {
            double sum= ((Matrix[0][0]+Matrix[0][1])*1.0);
            if(Matrix[0][0]==0)
                return 0;
            if(sum==0)
            {
                Debugger.printlnWarning("dividing by 0 in Precision computation");
                return -1;
            }
            return Matrix[0][0]/sum;
        }
    else
        {
           Debugger.printlnWarning("number of classification labels > 2, Precision= -1");
            return -1;
        }
    }

    /**
     * Calculate the recall of 2D array.
     * @param Matrix int[][] array.
     * @return double recall.
     */
     public static double recall(int[][] Matrix)
    {
        if(Matrix[0].length==2)
        {
            double sum= ((Matrix[0][0]+Matrix[1][0])*1.0);
            if(Matrix[0][0]==0)
                return 0;
            if(sum==0)
            {
                Debugger.printlnWarning("dividing by 0 in Recall computation");
                return -1;
            }
            return Matrix[0][0]/sum;
        }
    
        else
        {
         Debugger.printlnWarning("number of classification labels > 2, Recall= -1"); 
         return -1;
        }
    }

     
    /**
     * Calculate the sensitivity of 2D array.
     * @param Matrix int[][] array.
     * @return double sensitivity.
     */
     public static double sensitivity(int[][] Matrix)
    {
        return recall(Matrix);
    }
     
     /**
     * Calculate the specificity of 2D array.
     * @param Matrix int[][] array.
     * @return double specificity.
     */
     public static double specificity(int[][] Matrix)
    {
        if(Matrix[0].length==2)
        {
            double sum= ((Matrix[0][1]+Matrix[1][1])*1.0);
            if(Matrix[1][1]==0)
                return 0;
            if(sum==0)
            {
                Debugger.printlnWarning("dividing by 0 in Precision computation");
                return -1;
            }
            return Matrix[1][1]/sum;
        }
    else
        {
            Debugger.printlnWarning("number of classification labels > 2, Precision= -1");
            return -1;
        }
    }

      /**
     * Determine the maximum value inside a single column in 2D array.
     * @param array 2D array to search in.
     * @param column column number to search in.
     * @return maximum value found inside given column.
     */
    public static double max(double[][] array, int column)
    {
        double Max= array[0][column];
        for(int i=1; i<array.length; i++)
            Max= Math.max(array[i][column], Max);
        return Max;
    }
    
    /**
     * Determine the maximum value inside array. 
     * @param array Array to search in.
     * @return maximum value found inside array.
     */
    public static double max(double[] array)
    {
      double Max= array[0];
      for(int i=1; i<array.length; i++)
          Max= Math.max(array[i], Max);
      return Max;
    }
    
    /**
     * Determine the maximum value inside AbstractList. 
     * @param list List to search in.
     * @return maximum value found inside the list.
     */
    public static double max(List<Double> list)
    {
        double Max= list.get(0);
        for(int i=1; i<list.size(); i++)
            Max= Math.max(list.get(i), Max);
        return Max;
    }
    
    /**
     * Determine the minimum value inside a single column in 2D array.
     * @param array 2D array to search in.
     * @param column column number to search in.
     * @return minimum value found inside given column.
     */
    public static double min(double[][] array, int column)
    {
        double Min= array[0][column];
        for(int i=1; i<array.length; i++)
            Min= Math.min(array[i][column], Min);
        return Min;
    }
    
     /**
     * Determine the minimum value inside array. 
     * @param array Array to search in.
     * @return minimum value found inside array.
     */
    public static double min(double[] array)
    {
      double Min=array[0];
      for(int i=1; i<array.length; i++)
          Min= Math.min(array[i], Min);
      return Min;
  }
    /**
     * Determine the minimum value inside AbstractList. 
     * @param list List to search in.
     * @return minimum value found inside the list.
     */
    public static double min(List<Double> list)
    {
        double Min= list.get(0);
        for(int i=1; i<list.size(); i++)
            Min= Math.min(list.get(i), Min);
        return Min;
    }

    /**
     * Determine the average value inside a single column in 2D array.
     * @param array Array to extract values from.
     * @param column column index to look in.
     * @return average of values found in given column of array.
     */
    public static double avg(double[][] array, int column)
    {
        double sum=0.0;
        for(int i=0; i<array.length; i++)
            sum+= array[i][column];
        return sum/array.length;
    }
    
    /**
     * Determine the average value inside array. 
     * @param array Array to extract values from.
     * @return average of values found in array.
     */
    public static double avg(double[] array)
    {
        double sum=0.0;
        for(int i=0; i<array.length; i++)
            sum+= array[i];
        return sum/array.length;
    }
    
    /**
     * Determine the average value inside array. 
     * @param array AbstractList to extract values from.
     * @return average of values found in list.
     */
    public static double avg(List<Double> array)
    {
        double sum=0.0;
        for(int i=0; i<array.size(); i++)
            sum+= array.get(i);
        return sum/array.size();
    }
    
    /**
     * Determine the median value inside the array. 
     * @param array Array to extract values from.
     * @return median of values found in array.
     */
    public static double median(double[] array)
    {
        int length= array.length;
        int middle= length/2;
        if(length==0)
            return 0;
        if(length==1)
            return array[0];
        
        Arrays.sort(array);
        if(length%2!=0)
            return array[middle];
        else
            return (array[middle]+array[middle-1])/2;
            
    }
    
     /**
     * Determine the median value inside the array. 
     * @param array AbstractList to extract values from.
     * @return median of values found in list.
     */
    public static double median(List<Double> array)
    {
       double[] arr= new double[array.size()];
        for(int i=0; i<arr.length; i++)
            arr[i]= array.get(i);
        return median(arr);
    }
    
    /**
     * Determine the median value inside a single column in 2D array.
     * @param array Array to extract values from.
     * @param column column index to look in.
     * @return median of values found in given column of array.
     */
    public static double median(double[][] array, int column)
    {
        double[] arr= new double[array.length];
        for(int i=0; i<arr.length; i++)
            arr[i]= array[i][column];
        return median(arr);
    }

     /**
     * Determine the standard deviation value inside a single column in 2D array.
     * @param array Array to extract values from.
     * @param column column index to look in.
     * @return standard deviation of values found in given column of array.
     */
    public static double stDev(double[][] array, int column)
    {
        if (array.length==1)
            return array[0][column];
         else
         {
            double avg= avg(array,column);
            double X= 0.0;
            double a;
            for(int i=0; i<array.length; i++)
            {
                a= avg- array[i][column];
                X+= Math.pow(a, 2);
            }

            X= X/(array.length-1.0);
            return Math.sqrt(X);
        } 
    }
   
    /**
     * Determine the standard deviation value inside array. 
     * @param array Array to extract values from.
     * @return standard deviation of values found in array.
     */
    public static double stDev(double[] array)
    {
         if (array.length==1)
            return array[0];
         else
         {
            double avg= avg(array);
            double X= 0.0;
            double a;
            for(int i=0; i<array.length; i++)
            {
                a= avg- array[i];
                X+= Math.pow(a, 2);
            }

            X= X/(array.length-1.0);
            return Math.sqrt(X);
        }
    }
    
     /**
     * Determine the standard deviation value inside AbstractList. 
     * @param array AbstractList to extract values from.
     * @return standard deviation of values found in AbstractList.
     */
    public static double stDev(List<Double> array)
    {
         if (array.size()==1)
            return array.get(0);
         else
         {
            double avg= avg(array);
            double X= 0.0;
            double a;
            for(int i=0; i<array.size(); i++)
            {
                a= avg- array.get(i);
                X+= Math.pow(a, 2);
            }
            X= X/(array.size()-1.0);
            return Math.sqrt(X);
        }
    }


    /**
     * print the content of the given array.
     *
     * @param array 2D array to print.
     */
    public static void print(double[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Get the index of maximum value of given array.
     *
     * @param array Array to search in.
     * @return index of maximum value.
     */
    public static int indexofMax(double[] array) {
        if (array == null || array.length == 0) {
            return -1;
        }
        int index = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[index]) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Get the index of minimum value of given array.
     *
     * @param array Array to search in.
     * @return index of minimum value.
     */
    public static int indexofMin(double[] array) {
        if (array == null || array.length == 0) {
            return -1;
        }
        int index = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[index]) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Get the index of maximum value of given array.
     *
     * @param array Array to search in.
     * @return index of maximum value.
     */
    public static int indexofMax(int[] array) {
        if (array == null || array.length == 0) {
            return -1;
        }
        int index = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[index]) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Get the index of minimum value of given array.
     *
     * @param array Array to search in.
     * @return index of minimum value.
     */
    public static int indexofMin(int[] array) {
        if (array == null || array.length == 0) {
            return -1;
        }
        int index = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[index]) {
                index = i;
            }
        }
        return index;
    }
}