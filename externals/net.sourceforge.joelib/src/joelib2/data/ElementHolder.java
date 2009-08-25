/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.data;

import java.awt.Color;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia Chemical element
 * @.wikipedia Atom
 * @.wikipedia Molecule
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public interface ElementHolder
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @return           Description of the Return Value
     */
    double correctedBondRad(int atomicnum);

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @param hyb        Description of the Parameter
     * @return           Description of the Return Value
     */
    double correctedBondRad(int atomicnum, int hyb);

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @return           Description of the Return Value
     */
    double correctedVdwRad(int atomicnum);

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @param hyb        Description of the Parameter
     * @return           Description of the Return Value
     */
    double correctedVdwRad(int atomicnum, int hyb);

    /**
     * Gets the atom electronegativity after Allred and Rochow.
     *
     * @return   The allredRochowEN value
     */
    double getAllredRochowEN(int atomicnum);

    /**
     *  Gets the atomicNum attribute of the JOEElementTable object
     *
     * @param sym  Description of the Parameter
     * @return     The atomicNum value
     */
    int getAtomicNum(final String sym);

    /**
     *  Gets the bORad attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The bORad value
     */
    double getBORad(int atomicnum);

    /**
     * Gets the color attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The color value
     */
    Color getColor(int atomicnum);

    /**
     *  Gets the covalentRad attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The covalentRad value
     */
    double getCovalentRad(int atomicnum);

    /**
     * Gets the electronAffinity attribute of the JOEElement object
     *
     * @return   The electronAffinity value
     */
    double getElectronAffinity(int atomicnum);

    /**
     * Gets the exteriorElectrons attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The exteriorElectrons value
     */
    int getExteriorElectrons(int atomicnum);

    /**
     *  Gets the mass attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The mass value
     */
    double getMass(int atomicnum);

    /**
     *  Gets the maxBonds attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The maxBonds value
     */
    int getMaxBonds(int atomicnum);

    /**
     * Gets the atom electronegativity after Pauling.
     *
     * @return   The paulingEN value
     */
    double getPaulingEN(int atomicnum);

    /**
     * Gets the period of the JOEElement object.
     *
     * @return   The electronAffinity value
     */
    int getPeriod(int atomicnum);

    double getSandersonEN(int atomicnum);

    /**
     *  Gets the symbol attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The symbol value
     */
    String getSymbol(int atomicnum);

    /**
     *  Gets the vdwRad attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The vdwRad value
     */
    double getVdwRad(int atomicnum);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
