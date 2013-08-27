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

import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.StartUp;
import java.util.LinkedList;

/**
 * Condition class can either represent a classification or a metrics formula (Rule condition).
 * @author Khaled Bakhit
 * @since 3.3
 * @version 23/08/2013
 */
public class Condition implements java.io.Serializable, Comparable<Condition> 
{
    private static final long serialVersionUID = 115102013L;
    /**
     * Rule Object holding this Condition.
     */
    private Rule parent;
    /**
     * Condition quality.
     */
    public double quality;
    /**
     * Metric of this Condition.
     */
    public String metric;
    /**
     * Operator of this Condition.
     */
    public String operator;
    /**
     * Value of this Condition.
     */
    public String value;
    /**
     * Numeric value of this Condition.
     * @since 4.0
     */
    public double numeric_value= -1;
    /**
     * Classification of this Condition.
     */
    public String classification;
    /**
     * Flag indicating whether this Condition is a classification or equation.
     */
    public final boolean isClassification;
    /**
     * Flag indicating if value is numeric or not.
     * @since 4.0
     */
    public boolean isNumeric;

    /**
     * Condition constructor.
     *
     * @param metric Metric of this Condition.
     * @param operator Operator of this Condition.
     * @param value Value of this Condition.
     */
    public Condition(String metric, String operator, String value) 
    {
        this.isClassification = false;
        this.metric = metric;
        this.operator = operator;
        this.value = value;
        try
        {
            numeric_value= Double.parseDouble(value);
            isNumeric= true;
        }
        catch(Exception e)
        {
            isNumeric= false;
            numeric_value= -1.0;
        }
    }

    /**
     * Condition constructor.
     *
     * @param classification Classification of this Condition.
     */
    public Condition(String classification) 
    {
        this.classification = classification;
        this.isClassification = true;
        this.isNumeric= false;
    }
    
     /**
     * Set the given Rule as parent of this Condition.
     * @param r New Rule parent.
     */
    public void setParent(Rule r)
    {
        parent= r;
    }
    /**
     * Get Parent Rule of this Condition.  
     * @return Parent Rule of this Condition.
     */
    public Rule getParent()
    {
        return parent;
    }
    
    @Override
    public int compareTo(Condition o) 
    {
        if (isClassification && o.isClassification) 
            return classification.compareTo(o.classification);
        
        if (isClassification)
            return -1;
        
        if (o.isClassification) 
            return 1;
        
        if (!metric.equals(o.metric)) 
            return metric.compareTo(o.metric);
        
        if(isNumeric && o.isNumeric )
             return (int) (numeric_value - o.numeric_value);

        return value.compareTo(o.value);
    }

    /**
     * Check if condition operator is continuous.
     *
     * @return true if is continuous, false otherwise.
     */
    public boolean isContinuous() 
    {
        if (isClassification()) 
            return false;
        //checks if a certain attribute is continuous or discrete
        return !(operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("==") || operator.equalsIgnoreCase("!=")); 
    }

    /**
     * Check if condition operator is discrete.
     *
     * @return true if is discrete, false otherwise.
     */
    public boolean isDiscrete() 
    {
        if (isClassification())
            return false;
        return !isContinuous();
    }

    /**
     * Check if this Condition is a classification.
     *
     * @return true if is a classification, false otherwise.
     */
    public boolean isClassification() {
        return isClassification;
    }

    /**
     * Check if this Condition is an equation.
     *
     * @return true if is an equation, false otherwise.
     */
    public boolean isEquation() {
        return !isClassification();
    }
    /**
     * Check if value is numeric.
     * @return true if value is numeric, false otherwise.
     * @since 4.0
     */
    public boolean isNumricValue()
    {
        return isNumeric;
    }

    @Override
    public Condition clone() {
        if (isClassification()) {
            return new Condition(this.classification);
        }
        return new Condition(metric, operator, value);
    }

    /**
     * Perturb this Condition.
     */
    public void perturb() 
    {
        if (isClassification) 
        {
            String c = classification;
            while (c.equals(this.classification))
                this.classification = StartUp.getRandomClass();
        } 
        else 
        {
            if (operator.equals("=") || operator.equals("==")) 
                operator = "!=";
            else if (operator.equals("!=")) 
                operator = "=";
            else if (operator.equals("<")) 
                operator = ">=";
            else if (operator.equals(">=") || operator.equals("=>")) 
                operator = "<";
            else if (operator.equals(">")) 
                operator = "<=";
            else if (operator.equals("<=") || operator.equals("=<")) 
                operator = ">";    
        
            if (isContinuous()) 
            {
                LinkedList<Double> cutpoints = parent.getParent().getCutOffList(metric);
                if (cutpoints.size() <= 1) 
                {
                    //keep current value.
                } 
                else 
                {
                    String new_value = value;
                    while (value.equals(new_value))
                        new_value = "" + cutpoints.get(Program.rand.nextInt(cutpoints.size()));
                    value = new_value;
                }
            } 
            else //Discrete
            {
                LinkedList<Double> values = parent.getParent().getValueList(metric);
                if (values.size() <= 1) 
                {
                    //keep current value.
                } 
                else 
                {
                    String new_value = value;
                    while (value.equals(new_value)) 
                        new_value = "" + values.get(Program.rand.nextInt(values.size()));
                    value = new_value;
                }
            }
        }

    }

    /**
     * Check if Condition contradicts with this Condition.
     * @param cond Condition to check with.
     * @return True if contradicts, false otherwise.
     */
    public boolean contradicts(Condition cond) {
        if (this.isClassification || cond.isClassification) {
            return false;
        }
        if (!this.metric.equals(cond.metric)) {
            return false;
        }

        double v1 = Double.parseDouble(value);
        double v2 = Double.parseDouble(cond.value);

        if (operator.equals("<") || operator.equals("<=") || operator.equals("=<")) {
            if (cond.operator.equals(">") || cond.operator.equals(">=") || cond.operator.equals("=>")) {
                return !(v1 > v2);
            }
            return false;
        } else if (operator.equals(">") || operator.equals(">=") || operator.equals("=>")) {
            if (cond.operator.equals("<") || cond.operator.equals("<=") || cond.operator.equals("=<")) {
                return !(v1 < v2);
            }
            return false;
        } else {
            return false;
        }

    }
    /**
     * Check if Condition implies this Condition.
     * @param cond Condition to check with.
     * @return True if implies, false otherwise.
     */
    public boolean implies(Condition cond) {
        if (this.isClassification || cond.isClassification) {
            return false;
        }
        if (!this.metric.equals(cond.metric)) {
            return false;
        }

        double v1 = Double.parseDouble(value);
        double v2 = Double.parseDouble(cond.value);

        if (operator.equals(">") || operator.equals(">=") || operator.equals("=>")) {
            if (cond.operator.equals(">") || cond.operator.equals(">=") || cond.operator.equals("=>")) {
                return (v1 >= v2);
            }
            return false;
        } else if (operator.equals("<") || operator.equals("<=") || operator.equals("=<")) {
            if (cond.operator.equals("<") || cond.operator.equals("<=") || cond.operator.equals("=<")) {
                return (v1 <= v2);
            }
            return false;
        } else if (operator.equals("=") || operator.equals("==")) {
            if (cond.operator.equals("=") || cond.operator.equals("==")) {
                return v1 == v2;
            }
            return false;
        } else if (operator.equals("!=")) {
            if (cond.operator.equals("!=")) {
                return v1 != v2;
            }
            return false;
        } else {
            return false;
        }
    }   
    /**
     * Check if Condition equals this Condition.
     * @param cond Condition to check with.
     * @return True if equal, false otherwise.
     */
    public boolean equals(Condition cond) {
        if (this.isClassification && cond.isClassification) {
            return this.classification.equals(cond.classification);
        } else if (this.isClassification || cond.isClassification) {
            return false;
        }
        return metric.equals(cond.metric)
                && operator.equals(cond.operator)
                && value.equals(cond.value);
    }
}