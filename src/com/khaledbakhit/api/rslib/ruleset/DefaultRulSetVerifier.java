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
import com.khaledbakhit.api.rslib.Program;
import com.khaledbakhit.api.rslib.exceptions.InvalidInputException;
import com.khaledbakhit.api.rslib.utils.Debugger;
import java.util.LinkedList;
import java.util.List;

/**
 * DefaultRuleSetVerifier is the default implementation of RuleSetVerifier
 * abstract class.<br/>
 * 
 * <b>Auto-correct mode may lead to incorrect corrections, especially if all
 * metrics/classifications differ by one character.<i>Use with caution.</i></b>
 * @author Khaled Bakhit
 * @since 2.0
 * @version 24/08/2013
 */
public class DefaultRulSetVerifier extends RuleSetVerifier
{
    /**
     * LinkedList Object containing cached corrections.
     */  
    private LinkedList<MemorizedAnswer> dict;
     /**
     * DefaultRulSetVerifier constructor. Uses the default LaunchSetup Object.
     * @param autoCorrect True to activate auto-correct, false otherwise.
     * @throws InvalidInputException Input files are invalid.
     * @see Program#getInstance() 
     * @see Program#getLaunchSetup() 
     */
    public DefaultRulSetVerifier(boolean autoCorrect) throws InvalidInputException
    {
       this(autoCorrect, Program.getInstance().getLaunchSetup());
    }
    /**
     * DefaultRulSetVerifier constructor.
     * @param autoCorrect True to activate auto-correct, false otherwise.
     * @param sp LaunchSetup Object containing input configuration.
     * @throws InvalidInputException InvalidInputException Input files are invalid.
     * @since 4.0
     */
    public DefaultRulSetVerifier(boolean autoCorrect, LaunchSetup sp) throws InvalidInputException
    {
        super(autoCorrect, sp);
        dict= new LinkedList<MemorizedAnswer>();
    }

    @Override
    public void fixClassification(Condition cond) 
    {
        cond.classification= fix(cond.classification, classifications);
    }

    @Override
    public void fixMetric(Condition cond) 
    {
       cond.metric= fix(cond.metric, metrics);
    }
    
    private String fix(String wrong_word, List<String> correct_list)
    {
        String correction= null;
        
        for(MemorizedAnswer answer: dict)
            if(answer.wrong.equals(wrong_word))
            {
                correction= answer.correct;
                break;
            }

        if(correction==null)
        {
            if(autoCorrect)
                correction= autoCorrect(wrong_word, correct_list, 60);
            else
                correction= promptForCorrection(wrong_word, correct_list);
            
            if(correction==null)
            {
                Debugger.printlnError("failed to replace "+ wrong_word+"\nWill exit now."
                        +"\nKindly fix your input.");
                System.exit(0);
            }
        }
        Debugger.printlnSensitive("replaced "+wrong_word+" with "+ correction);
        dict.add(new MemorizedAnswer(wrong_word ,correction));
        return correction;
    }
    
     /**
     * Attempt to auto-correct given wrong word.
     * @param wrong_word Incorrect word to replace.
     * @param correct_list List containing possible replacements.
     * @param percentage percentage match with replacement.
     * @return corrected word or null if failed.
     */
    private String autoCorrect(String wrong_word, List<String> correct_list, int percentage)
    {
        if(percentage< 0)
            return null;
        char[] elements= wrong_word.toCharArray();
        double result;
        LinkedList<String> suggestions= new LinkedList<String>();
        String m;
        for(int i=0; i<correct_list.size(); i++)
        {
            m= correct_list.get(i);
            result= Match(m, elements);
               if(result>=percentage)
                    suggestions.add(m);
        }
        if(suggestions.isEmpty())
            return autoCorrect(wrong_word, correct_list, percentage-10);

        for(String suggestion: suggestions)
        {
            if(suggestion.contains(wrong_word) 
                    || suggestion.startsWith(wrong_word) 
                        || suggestion.endsWith(wrong_word))
                return suggestion;
        }
        return suggestions.get(0);
    }

     /**
      * Get the percentage match between word and char array.
      * @param word Word to check.
      * @param array Reference array.
      * @return percentage match between word and char array.
      */
    private double Match(String word, char[] array)
     {
         double percentage;
         int sum= 0; // Sum of Matches
         for(int i=0; i<array.length; i++)
         {
             if(word.indexOf(array[i])!=-1)
                    sum++;
         }
         percentage= sum*100.0/array.length;
         return percentage;
     }
    
    /**
     * MemorizedAnswer is used to memorize corrections of wrong words.
     */
    private class MemorizedAnswer
    {
        /**
         * Wrong version of the word.
         */
        public String wrong;
        /**
         * Correct version of the word.
         */
        public String correct;

        /**
         * MemorizedAnswer constructor.
         * @param wrong Wrong version of the word.
         * @param correct Correction version of the word.
         */
        public MemorizedAnswer(String wrong, String correct)
        {
                this.wrong= wrong;
                this.correct= correct;
        }
    }

}
