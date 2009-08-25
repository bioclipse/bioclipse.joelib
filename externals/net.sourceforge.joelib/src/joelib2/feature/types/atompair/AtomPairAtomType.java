///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomPairAtomType.java,v $
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

import joelib2.data.BasicElementHolder;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.util.HelperMethods;

import java.io.IOException;
import java.io.LineNumberReader;


/**
 * Atom type for pair descriptor (depends on atom properties used).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:32 $
 */
public class AtomPairAtomType
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * The element represented by the atom number
     */
    public int atomicNumber;

    /**
     * The values of the nominal atom properties
     */
    public String[] nominal;

    /**
     * The values of the numeric atom properties
     */
    public double[] numeric;
    private int hash = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    public AtomPairAtomType(int _atomicNumber, String[] _nominal,
        double[] _numeric)
    {
        atomicNumber = _atomicNumber;

        nominal = new String[_nominal.length];
        System.arraycopy(_nominal, 0, nominal, 0, _nominal.length);
        numeric = new double[_numeric.length];
        System.arraycopy(_numeric, 0, numeric, 0, _numeric.length);
    }

    protected AtomPairAtomType()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static AtomPairAtomType fromString(LineNumberReader lnr,
        int numNominal, int numNumeric)
    {
        AtomPairAtomType type = new AtomPairAtomType();

        try
        {
            type.atomicNumber = BasicElementHolder.instance().getAtomicNum(lnr
                    .readLine());
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return null;
        }

        type.nominal = new String[numNominal];

        if (numNominal > 0)
        {
            try
            {
                for (int i = 0; i < numNominal; i++)
                {
                    type.nominal[i] = lnr.readLine().trim();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                return null;
            }
        }

        type.numeric = new double[numNumeric];

        if (numNumeric > 0)
        {
            try
            {
                for (int i = 0; i < numNumeric; i++)
                {
                    type.numeric[i] = Double.parseDouble(lnr.readLine());
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                return null;
            }
        }

        //System.out.println("PARSED TYPE \""+type+"\"");
        return type;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof AtomPairAtomType)
        {
            return equals((AtomPairAtomType) obj);
        }
        else
        {
            return false;
        }
    }

    public boolean equals(AtomPairAtomType type)
    {
        if (atomicNumber != type.atomicNumber)
        {
            return false;
        }

        if (nominal.length != type.nominal.length)
        {
            return false;
        }

        if (numeric.length != type.numeric.length)
        {
            return false;
        }

        for (int i = 0; i < nominal.length; i++)
        {
            if (!nominal[i].equals(type.nominal[i]))
            {
                return false;
            }
        }

        for (int i = 0; i < numeric.length; i++)
        {
            if (numeric[i] != type.numeric[i])
            {
                return false;
            }
        }

        return true;
    }

    public synchronized int hashCode()
    {
        if (hash != 0)
        {
            return hash;
        }

        int hashCode = atomicNumber;

        for (int i = 0; i < nominal.length; i++)
        {
            hashCode = (31 * hashCode) + nominal[i].hashCode();
        }

        long bits;
        int tmpI;

        for (int i = 0; i < numeric.length; i++)
        {
            // similar code of Double.hashcode() !!!
            // without need to get a Double instance
            bits = Double.doubleToLongBits(numeric[i]);

            // unsigned right shift operator
            tmpI = (int) (bits ^ (bits >>> 32));

            hashCode = (31 * hashCode) + tmpI;
        }

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
        sb.append(BasicElementHolder.instance().getSymbol(atomicNumber));
        sb.append(HelperMethods.eol);

        for (int i = 0; i < nominal.length; i++)
        {
            sb.append(nominal[i]);

            if (i < (nominal.length - 1))
            {
                sb.append(HelperMethods.eol);
            }
        }

        if (numeric.length != 0)
        {
            sb.append(HelperMethods.eol);
        }

        for (int i = 0; i < numeric.length; i++)
        {
            sb.append(numeric[i]);

            if (i < (numeric.length - 1))
            {
                sb.append(HelperMethods.eol);
            }
        }

        //              }
        //              else
        //              {
        //                      sb.append(JOEElementTable.instance().getSymbol(atomicNumber));
        //                      sb.append('_');
        //                      for (int i = 0; i < len; i++)
        //                      {
        //                              sb.append(atomProperties[i]);
        //                              if (i < len_1)
        //                                      sb.append('_');
        //                      }
        //              }
        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
