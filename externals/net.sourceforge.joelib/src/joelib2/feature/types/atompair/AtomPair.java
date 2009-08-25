///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomPair.java,v $
//  Purpose:  Atom pair descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:32 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
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
package joelib2.feature.types.atompair;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.molecule.Atom;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.HelperMethods;

import java.io.LineNumberReader;


/**
 * Single atom pair (depends on atom properties used).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:32 $
 */
public class AtomPair
{
    //~ Instance fields ////////////////////////////////////////////////////////

    AtomPairAtomType atomPair1;
    AtomPairAtomType atomPair2;
    AtomPairTypeComparator comparator = new AtomPairTypeComparator();
    double distance;
    private int hash = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    public AtomPair(AtomProperties[] nominal, AtomProperties[] numeric,
        Atom atom1, Atom atom2, double _distance)
    {
        calculateAtomPair(nominal, numeric, atom1, atom2, _distance);
    }

    protected AtomPair()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static AtomPair fromString(LineNumberReader lnr, int numNominalAP,
        int numNumericAP)
    {
        AtomPair ap = new AtomPair();
        AtomPairAtomType tmp;
        tmp = AtomPairAtomType.fromString(lnr, numNominalAP, numNumericAP);

        if (tmp == null)
        {
            return null;
        }
        else
        {
            ap.atomPair1 = AtomPairTypeHolder.instance().addReturn(tmp);
        }

        try
        {
            ap.distance = Double.parseDouble(lnr.readLine());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }

        tmp = AtomPairAtomType.fromString(lnr, numNominalAP, numNumericAP);

        if (tmp == null)
        {
            return null;
        }
        else
        {
            ap.atomPair2 = AtomPairTypeHolder.instance().addReturn(tmp);
        }

        //System.out.println("PARSED AP \""+ap+"\"");
        return ap;
    }

    public void calculateAtomPair(AtomProperties[] nominal,
        AtomProperties[] numeric, Atom atom1, Atom atom2, double _distance)
    {
        AtomPairTypeHolder types = AtomPairTypeHolder.instance();

        AtomPairAtomType ap1 = types.getAPType(nominal, numeric, atom1);
        AtomPairAtomType ap2 = types.getAPType(nominal, numeric, atom2);

        // ensure to store smaller atom pair atom type
        // at position one
        int result = comparator.compare(ap1, ap2);

        if (result < 0)
        {
            atomPair1 = ap1;
            atomPair2 = ap2;
        }
        else
        {
            atomPair1 = ap2;
            atomPair2 = ap1;
        }

        // store distance
        distance = _distance;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof AtomPair)
        {
            return equals((AtomPair) obj);
        }
        else
        {
            return false;
        }
    }

    public boolean equals(AtomPair ap)
    {
        if (distance != ap.distance)
        {
            return false;
        }

        if (atomPair1.equals(ap.atomPair1))
        {
            if (atomPair2.equals(ap.atomPair2))
            {
                return true;
            }
        }

        return false;
    }

    public AtomPairAtomType getAtomType1()
    {
        return atomPair1;
    }

    public AtomPairAtomType getAtomType2()
    {
        return atomPair2;
    }

    public double getDistance()
    {
        return distance;
    }

    public synchronized int hashCode()
    {
        if (hash != 0)
        {
            return hash;
        }

        int hashCode = 1;
        Double dist = new Double(distance);

        hashCode = (31 * hashCode) + atomPair1.hashCode();
        hashCode = (31 * hashCode) + dist.hashCode();
        hashCode = (31 * hashCode) + atomPair2.hashCode();

        hash = hashCode;

        return hashCode;
    }

    public synchronized int reHash()
    {
        hash = 0;

        return hashCode();
    }

    public String toString()
    {
        return toString(BasicIOTypeHolder.instance().getIOType("UNDEFINED"));
    }

    public String toString(IOType ioType)
    {
        StringBuffer sb = new StringBuffer(100);

        //              if (ioType.equals(IOTypeHolder.instance().getIOType("SDF")))
        //              {
        sb.append(atomPair1.toString(ioType));
        sb.append(HelperMethods.eol);
        sb.append(distance);
        sb.append(HelperMethods.eol);
        sb.append(atomPair2.toString(ioType));

        //              }
        //              else
        //              {
        //                      sb.append(atomPair1.toString(ioType));
        //                      sb.append("--");
        //                      sb.append(distance);
        //                      sb.append("--");
        //                      sb.append(atomPair2.toString(ioType));
        //              }
        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
