///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SymmetryElement.java,v $
//  Purpose:  Brute force symmetry analyzer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Original author: (C) 1996, 2003 S. Patchkovskii, Serguei.Patchkovskii@sympatico.ca
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:36 $
//            $Author: wegner $
//
// Copyright Symmetry:       S. Patchkovskii, 1996,2000,2003
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.math.symmetry;

/**
 * SymmetryElement.
 *
 * @.author Serguei Patchkovskii
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.10 $, $Date: 2005/02/17 16:48:36 $
 */
public class SymmetryElement
{
    //~ Instance fields ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////

    public double[] direction = new double[SymCoordinates.DIMENSION];

    public double distance;

    /**
     * Larges error associated with the element.
     */
    public double maxdev;

    public double[] normal = new double[SymCoordinates.DIMENSION];

    /**
     * 4 for inversion and planes, 7 for axes.
     */
    public int nparam;

    /**
     * Applying transformation this many times is identity.
     */
    public int order;

    /**
     * Correspondence table for the transformation.
     */
    public int[] transform;

    public TransformationAtom transformAtomMethod;

    //~ Constructors ///////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////

    /**
     * Constructor for the AtomIntInt object
     *
     * @param _a
     *            Description of the Parameter
     * @param _ii
     *            Description of the Parameter
     */
    public SymmetryElement(int transformSize)
    {
        transform = new int[transformSize];

        //Initialize with an impossible value
        for (int i = 0; i < transformSize; i++)
        {
            transform[i] = transformSize + 1;
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean sameTransform(int atomsCount, SymmetryElement other)
        throws SymmetryException
    {
        return sameTransform(atomsCount, this, other);
    }

    public boolean sameTransform(int atomsCount, SymmetryElement a,
        SymmetryElement b) throws SymmetryException
    {
        boolean isEqual = false;

        if ((a == null) || (b == null))
        {
            throw new SymmetryException("Symmetry element is not defined.");
        }

        if ((a.order == b.order) && (a.nparam == b.nparam) &&
                (a.transformAtomMethod.equals(b.transformAtomMethod)))
        {
            isEqual = true;

            for (int i = 0; i < atomsCount; i++)
            {
                if (a.transform[i] != b.transform[i])
                {
                    isEqual = false;

                    break;
                }
            }

            //b can also be a reverse transformation for a
            if (!isEqual && (a.order > 2))
            {
                isEqual = true;

                for (int i = 0; i < atomsCount; i++)
                {
                    if (b.transform[a.transform[i]] != i)
                    {
                        isEqual = false;

                        break;
                    }
                }
            }
        }

        return isEqual;
    }

    // ////////////////////////////////////////////////////////////////

    public void transformAtom(Symmetry invoker, SymmetryElement _el,
        SymAtom _from, SymAtom _to) throws SymmetryException
    {
        transformAtomMethod.callTransformation(invoker, _el, _from, _to);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
