///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Vector2D.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:35 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.math;

/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.3 $, $Date: 2005/02/17 16:48:35 $
 */
public interface Vector2D
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the x2D.
     */
    double getX2D();

    /**
     * @return Returns the y2D.
     */
    double getY2D();

    /**
     * @param x2d The x2D to set.
     */
    void setX2D(double x2d);

    /**
     * @param y2d The y2D to set.
     */
    void setY2D(double y2d);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
