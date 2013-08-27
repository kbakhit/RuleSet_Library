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

/**
 * Debugger controls debugging information sent to the console.
 *
 * @author Khaled Bakhit
 * @since 1.0
 * @version 19/05/2011
 */
public class Debugger {

    /**
     * DebugLevel represents various debug levels.
     *
     * @author Khaled Bakhit
     *
     *
     * @since 1.0
     * @version 1.0
     */
    public enum DebugLevel {

        NONE, INFO, DEBUG, WARNING, SENSITIVE, ERROR, ALL
    };
    /**
     * Flag indicating if info messages are allowed or not.
     */
    public static boolean INFO = false;
    /**
     * Flag indicating if debug messages are allowed or not.
     */
    public static boolean DEBUG = false;
    /**
     * Flag indicating if warning messages are allowed or not.
     */
    public static boolean WARNING = false;
    /**
     * Flag indicating if sensitive messages are allowed or not.
     */
    public static boolean SENSITIVE = false;
    /**
     * Flag indicating if error messages are allowed or not.
     */
    public static boolean ERROR = false;
    /**
     * Flag indicating if stack traces are allowed or not.
     */
    public static boolean STACKTRACE = false;

    /**
     * Set the Debug level.
     * @param level Debug level required.
     */
    public static void setDebugLevel(DebugLevel level) {
        INFO = DEBUG = WARNING = SENSITIVE = ERROR = STACKTRACE = false;
        if (level == DebugLevel.NONE) {
            return;
        }
        INFO = true;
        if (level == DebugLevel.INFO) {
            return;
        }
        DEBUG = true;
        if (level == DebugLevel.DEBUG) {
            return;
        }
        WARNING = true;
        if (level == DebugLevel.WARNING) {
            return;
        }
        SENSITIVE = true;
        if (level == DebugLevel.SENSITIVE) {
            return;
        }
        ERROR = true;
        if (level == DebugLevel.ERROR) {
            return;
        }
        STACKTRACE = true;
        if (level == DebugLevel.ALL) {
            return;
        }
        //An invalid/unsupported level presented.
        //Will turn everything off in this case.
        setDebugLevel(DebugLevel.NONE);
    }

    /**
     * Print a warning line.
     *
     * @param message Message to print.
     */
    public static void printlnWarning(String message) {
        if (WARNING) {
            System.out.println(message);
        }
    }

    /**
     * Print a sensitive line.
     *
     * @param message Message to print.
     */
    public static void printlnSensitive(String message) {
        if (SENSITIVE) {
            System.out.println(message);
        }
    }

    /**
     * Print an error line.
     *
     * @param message Message to print.
     */
    public static void printlnError(String message) {
        if (ERROR) {
            System.err.println(message);
        }
    }

    /**
     * Print an error line and stack trace.
     *
     * @param e Exception Object to print.
     */
    public static void printlnError(Exception e) {
        printlnError(e.getMessage());
        if (STACKTRACE) {
            e.printStackTrace();
        }
    }

    /**
     * Print a debug line.
     * <p>
     * Similar to calling {@link #printlnDebug(String)} method.
     *
     * @param message Message to print.
     */
    public static void println(String message) {
        printlnDebug(message);
    }

    /**
     * Print a debug line.
     * <p>
     * Similar to calling {@link #println(String)} method.
     *
     * @param message Message to print.
     */
    public static void printlnDebug(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }

    /**
     * Print an info line.
     *
     * @param message Message to print.
     */
    public static void printlnInfo(String message) {
        if (INFO) {
            System.out.println(message);
        }
    }
}
