///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicPose.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:37 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.molecule.types;

import joelib2.math.CoordinateTransformation;


/**
 * Atom tree.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.2 $, $Date: 2005/02/17 16:48:37 $
 */
public class BasicPose implements Pose
{
    //~ Instance fields ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////
    private int conformer;

    private CoordinateTransformation coordinateTransformation;

    //~ Constructors ///////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////
    BasicPose()
    {
        conformer = 0;
    }

    BasicPose(final BasicPose cp)
    {
        this.conformer = cp.conformer;
        this.coordinateTransformation = cp.coordinateTransformation;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void clear()
    {
        coordinateTransformation.clear();
        conformer = 0;
    }

    public int getConformer()
    {
        return conformer;
    }

    public CoordinateTransformation getCoordinateTransformation()
    {
        return coordinateTransformation;
    }

    public BasicPose set(BasicPose source)
    {
        coordinateTransformation = source.coordinateTransformation;
        conformer = source.conformer;

        return this;
    }

    public void setConformer(int conf)
    {
        conformer = conf;
    }

    public void setCoordinateTransformation(CoordinateTransformation ct)
    {
        coordinateTransformation = ct;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
