///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GasteigerMarsili.java,v $
//  Purpose:  Calculates the Gasteiger charge.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
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
package joelib2.molecule.charge;

import joelib2.data.BasicElementHolder;
import joelib2.data.BasicProtonationModel;
import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.atomlabel.AtomFreeOxygenCount;
import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomIsCarboxylOxygen;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.atomlabel.AtomIsNitrogen;
import joelib2.feature.types.atomlabel.AtomIsNonPolarHydrogen;
import joelib2.feature.types.atomlabel.AtomIsOxygen;
import joelib2.feature.types.atomlabel.AtomIsPhosphateOxygen;
import joelib2.feature.types.atomlabel.AtomIsPhosphorus;
import joelib2.feature.types.atomlabel.AtomIsSulfateOxygen;
import joelib2.feature.types.atomlabel.AtomIsSulfur;
import joelib2.feature.types.atomlabel.AtomType;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Partial charge calculation.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:37 $
 * @.cite gm78
 */
public class GasteigerMarsili implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(GasteigerMarsili.class
            .getName());
    private static final long serialVersionUID = 1L;
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.11 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:37 $";

    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            BasicElementHolder.class, BasicProtonationModel.class,
            AtomHeavyValence.class, AtomHybridisation.class,
            AtomImplicitValence.class, AtomIsHydrogen.class,
            AtomIsNitrogen.class, AtomIsNonPolarHydrogen.class,
            AtomIsOxygen.class, AtomIsPhosphorus.class, AtomIsSulfur.class,
            AtomType.class
        };

    private final static double MX_GASTEIGER_DENOM =
        GasteigerMarsiliState.MX_GASTEIGER_DENOM;
    private final static double MX_GASTEIGER_DAMP =
        GasteigerMarsiliState.MX_GASTEIGER_DAMP;
    private final static int MX_GASTEIGER_ITERS =
        GasteigerMarsiliState.MX_GASTEIGER_ITERS;

    //~ Instance fields ////////////////////////////////////////////////////////

    private List<GasteigerMarsiliState> gmStates;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEGastChrg object
     */
    public GasteigerMarsili()
    {
        gmStates = new Vector<GasteigerMarsiliState>();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean assignPartialCharges(Molecule mol, double[] pCharges)
    {
        // primitive model
        //initialPartialCharges(mol,pCharges);

        Atom atom;
        GasteigerMarsiliState gasteigerState = null;
        resizeGMStates(mol.getAtomsSize() + 1);

        double[] a = new double[1];
        double[] b = new double[1];
        double[] c = new double[1];
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (!gasteigerSigmaChi(atom, a, b, c))
            {
                return (false);
            }

            gasteigerState = ((GasteigerMarsiliState) gmStates.get(
                        atom.getIndex()));

            // set a,b and c values and the denominator
            gasteigerState.setValues(a[0], b[0], c[0],
                pCharges[atom.getIndex() - 1]);
        }

        double alpha;
        double charge;
        double denom;
        int j;
        int iter;
        Bond bond;
        Atom src;
        Atom dst;
        alpha = 1.0;

        for (iter = 0; iter < MX_GASTEIGER_ITERS; iter++)
        {
            alpha *= MX_GASTEIGER_DAMP;

            for (j = 1; j < gmStates.size(); j++)
            {
                gasteigerState = gmStates.get(j);
                charge = gasteigerState.q;
                gasteigerState.chi = (((gasteigerState.c * charge) +
                            gasteigerState.b) * charge) + gasteigerState.a;
                //if(logger.isDebugEnabled())logger.debug
                //System.out.println(mol.getTitle()+" init atom "+j+" with "+gasteigerState);
                //                if(j==19){
                //                    Object obj=null;
                //                    obj.toString();
                //                }
            }

            BondIterator bit = mol.bondIterator();

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                src = bond.getBegin();
                dst = bond.getEnd();
                gasteigerState = gmStates.get(src.getIndex());

                GasteigerMarsiliState gasteigerState2 = gmStates.get(dst
                        .getIndex());

                if (gasteigerState.chi >= gasteigerState2.chi)
                {
                    if (AtomIsHydrogen.isHydrogen(dst))
                    {
                        denom = MX_GASTEIGER_DENOM;
                    }
                    else
                    {
                        denom = gasteigerState2.denom;
                    }
                }
                else
                {
                    if (AtomIsHydrogen.isHydrogen(src))
                    {
                        denom = MX_GASTEIGER_DENOM;
                    }
                    else
                    {
                        denom = gasteigerState.denom;
                    }
                }

                charge = (gasteigerState.chi - gasteigerState2.chi) / denom;
                gasteigerState.q -= (alpha * charge);
                gasteigerState2.q += (alpha * charge);
                //System.out.println("GM:"+gasteigerState);
                //System.out.println("GM2:"+gasteigerState2);
            }
        }

        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            gasteigerState = gmStates.get(atom.getIndex());
            pCharges[atom.getIndex() - 1] = gasteigerState.q;
        }

        return (true);
    }

    /**
     *  Description of the Method
     *
     * @param  size  Description of the Parameter
     */
    public void resizeGMStates(int size)
    {
        gmStates.clear();

        for (int j = 0; j < size; j++)
        {
            gmStates.add(new GasteigerMarsiliState());
        }
    }

    /**
     * @param  a     of type <tt>double[0]</tt> .
     * @param  b     of type <tt>double[0]</tt> .
     * @param  c     of type <tt>double[0]</tt> .
     * @param  atom  Description of the Parameter
     * @return       Description of the Return Value
     */
    private boolean gasteigerSigmaChi(Atom atom, double[] a, double[] b,
        double[] c)
    {
        //System.out.println(atom+" "+atom.getAtomicNumber()+" "+AtomHybridisation.getHybridisation(atom)+" "+AtomFreeOxygenCount.freeOcount(atom)+" "+atom.getIndex()+" "+a[0]+" "+b[0]+" "+c[0]+" "+atom.getValence()+" "+atom.getFormalCharge());

        int count;
        double[] val = new double[]{0.0, 0.0, 0.0};

        switch (atom.getAtomicNumber())
        {
        case 1:

            //H
            val[0] = 0.37f;
            val[1] = 7.17f;
            val[2] = 12.85f;

            break;

        case 6:

            //C
            if (AtomHybridisation.getIntValue(atom) == 3)
            {
                val[0] = 0.68f;
                val[1] = 7.98f;
                val[2] = 19.04f;
            }

            if (AtomHybridisation.getIntValue(atom) == 2)
            {
                val[0] = 0.98f;
                val[1] = 8.79f;
                val[2] = 19.62f;
            }

            if (AtomHybridisation.getIntValue(atom) == 1)
            {
                val[0] = 1.67f;
                val[1] = 10.39f;
                val[2] = 20.57f;
            }

            break;

        case 7:

            //N
            if (AtomHybridisation.getIntValue(atom) == 3)
            {
                if ((atom.getValence() == 4) || (atom.getFormalCharge() != 0))
                {
                    val[0] = 0.0f;
                    val[1] = 0.0f;
                    val[2] = 23.72f;
                }
                else
                {
                    val[0] = 2.08f;
                    val[1] = 11.54f;
                    val[2] = 23.72f;
                }
            }

            if (AtomHybridisation.getIntValue(atom) == 2)
            {
                if (atom.getType().equals("Npl") ||
                        atom.getType().equals("Nam"))
                {
                    val[0] = 2.46f;
                    val[1] = 12.32f;
                    val[2] = 24.86f;
                }
                else
                {
                    val[0] = 2.57f;
                    val[1] = 12.87f;
                    val[2] = 24.87f;
                }
            }

            if (AtomHybridisation.getIntValue(atom) == 1)
            {
                val[0] = 3.71f;
                val[1] = 15.68f;
                val[2] = 27.11f;
            }

            break;

        case 8:

            //O
            if (AtomHybridisation.getIntValue(atom) == 3)
            {
                val[0] = 2.65f;
                val[1] = 14.18f;
                val[2] = 28.49f;
            }

            if (AtomHybridisation.getIntValue(atom) == 2)
            {
                val[0] = 3.75f;
                val[1] = 17.07f;
                val[2] = 31.33f;
            }

            break;

        case 9:

            //F
            val[0] = 3.12f;
            val[1] = 14.66f;
            val[2] = 30.82f;

            break;

        case 15:

            //P
            val[0] = 1.62f;
            val[1] = 8.90f;
            val[2] = 18.10f;

            break;

        case 16:

            //S
            count = AtomFreeOxygenCount.getIntValue(atom);

            if ((count == 0) || (count == 1))
            {
                val[0] = 2.39f;
                val[1] = 10.14f;
                val[2] = 20.65f;
            }

            if (count > 1)
            {
                val[0] = 2.39f;
                val[1] = 12.00f;
                val[2] = 24.00f;
            }

            /*S2? if (count == 0) {val[0] = 2.72;val[1] = 10.88;val[2] = 21.69;} */
            break;

        case 17:

            //Cl
            val[0] = 2.66f;
            val[1] = 11.00f;
            val[2] = 22.04f;

            break;

        case 35:

            //Br
            val[0] = 2.77f;
            val[1] = 10.08f;
            val[2] = 19.71f;

            break;

        case 53:

            //I
            val[0] = 2.90f;
            val[1] = 9.90f;
            val[2] = 18.82f;

            break;

        case 13:

            //Al
            val[0] = 1.06f;
            val[1] = 5.47f;
            val[2] = 11.65f;

            break;
        }

        if (val[2] != 0.0)
        {
            a[0] = val[1];
            b[0] = (val[2] - val[0]) / 2;
            c[0] = ((val[2] + val[0]) / 2) - val[1];
        }
        else
        {
            return (false);
        }

        return (true);
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    private void initialPartialCharges(Molecule mol, double[] pCharges)
    {
        Atom atom;
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (AtomIsCarboxylOxygen.isCarboxylOxygen(atom))
            {
                pCharges[atom.getIndex() - 1] = -0.500f;
            }
            else if (AtomIsPhosphateOxygen.isPhosphateOxygen(atom) &&
                    (AtomHeavyValence.valence(atom) == 1))
            {
                pCharges[atom.getIndex() - 1] = -0.666f;
            }
            else if (AtomIsSulfateOxygen.isSulfateOxygen(atom))
            {
                pCharges[atom.getIndex() - 1] = -0.500f;
            }
            else
            {
                pCharges[atom.getIndex() - 1] = (double) atom.getFormalCharge();
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
