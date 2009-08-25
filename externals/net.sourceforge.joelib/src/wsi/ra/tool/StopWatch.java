///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: StopWatch.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:44 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation version 2 of the License.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package wsi.ra.tool;

/**
 * Implements a very simple 'stop watch' which returns the passed time since last reset.
 *
 * @.author     rapp
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:44 $
 */
public class StopWatch implements StopWatchInterface
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private long stopWatchTime;
    private long suspendTime = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    public StopWatch()
    {
        // save current system time
        resetTime();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns the passed time since last 'reset stop watch' call in millis.
     */
    public int getPassedTime()
    {
        return (int) (System.currentTimeMillis() - stopWatchTime);
    }

    /**
     * Prints the passed time since last 'reset stop watch' call in millis to 'stdout'.
     */
    public void printPassedTime(String text)
    {
        System.out.println("Passed time for '" + text + "': " +
            getPassedTime());
    }

    /**
     * Resets the time to the time when the stop watch was suspended.
     */
    public void proceed()
    {
        stopWatchTime = System.currentTimeMillis() - suspendTime;
    }

    /**
     * Saves the current system time in a local variable.
     */
    public void resetTime()
    {
        stopWatchTime = System.currentTimeMillis();
    }

    /**
     * Freezes the time when the stop watch is suspended.
     */
    public void suspend()
    {
        suspendTime = System.currentTimeMillis() - stopWatchTime;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
