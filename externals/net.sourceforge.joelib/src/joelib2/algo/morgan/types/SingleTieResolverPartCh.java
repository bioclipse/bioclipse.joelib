///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SingleTieResolverPartCh.java,v $
//  Purpose:  Helper class for resolving renumbering ties.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.algo.morgan.types;

import joelib2.algo.morgan.AtomDoubleParent;
import joelib2.algo.morgan.SingleTieResolver;

import joelib2.feature.types.atomlabel.AtomPartialCharge;

import joelib2.molecule.Molecule;


/**
 * Interface for resolving renumbering ties.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:29 $
 */
public class SingleTieResolverPartCh implements SingleTieResolver
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public double getResolvingValue(AtomDoubleParent ap, Molecule mol)
    {
        //              double tmp=0.0;
        //
        //              NbrAtomIterator nait = mol.getAtom(ap.atomIdx).nbrAtomIterator();
        //              Bond bond;
        //              Atom nbrAtom;
        //              while (nait.hasNext())
        //              {
        //                      nbrAtom = nait.nextNbrAtom();
        //                      bond = nait.actualBond();
        //                      tmp+=nbrAtom.getPartialCharge();
        //              }
        //              System.out.println("pChSum:"+tmp);
        //System.out.print("pCh:"+mol.getAtom(ap.atomIdx).getPartialCharge());
        //              double tmp2=0.0;
        //              nait = mol.getAtom(ap.parent).nbrAtomIterator();
        //              while (nait.hasNext())
        //              {
        //                      nbrAtom = nait.nextNbrAtom();
        //                      bond = nait.actualBond();
        //                      tmp2+=nbrAtom.getPartialCharge();
        //              }
        //              System.out.println("pChSumP:"+tmp);
        //System.out.println(" pChP:"+mol.getAtom(ap.parent).getPartialCharge());
        //              return tmp;
        return (double) AtomPartialCharge.getPartialCharge(mol.getAtom(
                    ap.atomIdx));
    }

    public boolean init(Molecule mol)
    {
        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
