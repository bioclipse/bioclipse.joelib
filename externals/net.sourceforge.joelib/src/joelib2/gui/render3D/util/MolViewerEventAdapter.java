///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolViewerEventAdapter.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.gui.render3D.util;

import joelib2.data.BasicElementHolder;

import joelib2.feature.types.atomlabel.AtomENPauling;
import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomInAromaticSystem;
import joelib2.feature.types.atomlabel.AtomPartialCharge;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;

import org.apache.log4j.Category;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:34 $
 */
public class MolViewerEventAdapter implements MolViewerEventListener
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render3D.util.MolViewerEventAdapter");

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the MolViewerEventAdapter object
     */
    public MolViewerEventAdapter()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void atomPicked(MolViewerEvent event)
    {
        if (logger.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer(100);
            Atom atom = (Atom) event.getParam();
            sb.append("atom idx:" + atom.getIndex());
            sb.append(", atomic number:" + atom.getAtomicNumber());
            sb.append(", element symbol:" +
                BasicElementHolder.instance().getSymbol(atom.getAtomicNumber()));
            sb.append(", aromatic flag:" + AtomInAromaticSystem.isValue(atom));
            sb.append(", atom vector:" + atom.getCoords3D());
            sb.append(", hybridisation:" + AtomHybridisation.getIntValue(atom));
            sb.append(", implicit valence:" +
                AtomImplicitValence.getImplicitValence(atom));
            sb.append(", charge:" + atom.getFormalCharge());
            sb.append(", partial charge:" +
                AtomPartialCharge.getPartialCharge(atom));
            sb.append(", valence:" + atom.getValence());
            sb.append(", ext Electrons:" +
                BasicElementHolder.instance().getExteriorElectrons(
                    atom.getAtomicNumber()));
            sb.append(", pauling electronegativity:" +
                AtomENPauling.getDoubleValue(atom));
            sb.append(", free electrons:" + atom.getFreeElectrons());
            logger.debug(sb.toString());
        }

        //System.out.println("Atom: " + event.getParam());
    }

    public void bondPicked(MolViewerEvent event)
    {
        if (logger.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer(100);
            Bond bond = (Bond) event.getParam();
            sb.append("  atom #");
            sb.append(bond.getBeginIndex());
            sb.append(" is attached to atom #");
            sb.append(bond.getEndIndex());
            sb.append(" with bond of order ");
            sb.append(bond.getBondOrder());
            sb.append(" which is ");

            if (BondInAromaticSystem.isAromatic(bond))
            {
                sb.append("an aromatic bond.");
            }
            else
            {
                sb.append("a non-aromatic bond.");
            }

            logger.debug(sb.toString());
        }
    }

    /**
     * Description of the Method
     *
     * @param event  Description of the Parameter
     */
    public void centralDisplayChange(MolViewerEvent event)
    {
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
