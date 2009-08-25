/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.data;

import joelib2.molecule.Molecule;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia  Valency
 * @.wikipedia Orbital hybridisation
 * @.wikipedia Molecule
 * @.license    GPL
 * @.cvsversion $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 */
public interface ImplicitValenceTyper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    void correctAromaticNitrogens(Molecule mol);

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    void getImplicitValence(Molecule mol, int[] impVal);

    boolean isValidType(String type);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
