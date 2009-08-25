/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.util.iterator;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface NbrAtomIterator extends ListIterator
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public Object actual();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Bond actualBond();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object next();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Atom nextNbrAtom();
}
