///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AbstractFreeElectronsCount.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 28, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.5 $
//          $Date: 2005/02/17 16:48:31 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
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
package joelib2.feature.types.atomlabel;

import joelib2.data.BasicElementHolder;

import joelib2.molecule.Atom;
import joelib2.molecule.AtomHelper;
import joelib2.molecule.Bond;

import joelib2.util.iterator.BondIterator;

import org.apache.log4j.Category;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.5 $, $Date: 2005/02/17 16:48:31 $
 */
public class AbstractFreeElectronsCount
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(
            AbstractFreeElectronsCount.class.getName());

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Get the number of free electrons.
     * If the free electrons are not defined the count of the implicit
     * and explicit bonds are used for calculating the number of free
     * electrons.
     *
     * @return    the number of free electrons
     * @see #ELECTRONS_UNDEFINED
     */
    public static int calculate(Atom atom)
    {
        int freeEl = atom.getFreeElectrons();

        if (freeEl == Atom.ELECTRONS_UNDEFINED)
        {
            AtomHelper.correctFormalCharge(atom);

            int sumBO = 0;
            BondIterator bit = atom.bondIterator();
            Bond bond;

            while (bit.hasNext())
            {
                bond = bit.nextBond();
                sumBO += bond.getBondOrder();
            }

            //    // very primitive ! Only usable for the first/second period with strong 8 electron rule
            //    int           usedEl  = 2 * (sumBO + (this.getImplicitValence() - this.getValence()));
            //    freeEl = 8 - usedEl;
            int hatoms = AtomImplicitValence.getImplicitValence(atom) -
                atom.getValence();
            int usedEl = sumBO + hatoms + atom.getFormalCharge();

            //      System.out.println("usedEl:"+usedEl);
            //      System.out.println("extEl:"+JOEElementTable.instance().getExteriorElectrons(this.getAtomicNum()));
            freeEl =
                BasicElementHolder.instance().getExteriorElectrons(atom
                    .getAtomicNumber()) - usedEl;

            if (freeEl < 0)
            {
                logger.warn(atom.getParent().getTitle() + ": Atom #" +
                    atom.getIndex() + "(" +
                    BasicElementHolder.instance().getSymbol(
                        atom.getAtomicNumber()) + ") has " + freeEl +
                    " electrons. Check formal charge and valence." + "Charge:" +
                    atom.getFormalCharge() + " Valence:" + atom.getValence() +
                    " Impl. valence:" +
                    AtomImplicitValence.getImplicitValence(atom));
                freeEl = atom.getFreeElectrons();
            }
            else
            {
                atom.setFreeElectrons(freeEl);
            }
        }

        return freeEl;
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
