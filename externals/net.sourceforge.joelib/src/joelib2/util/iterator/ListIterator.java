/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.util.iterator;

import java.util.Iterator;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ListIterator extends Iterator
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public Object actual();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object clone();

    public void decrementIndex();

    /**
     *  Returns the index number ot the sctual <tt>Object</tt> . Warning: Util now
     *  you should not use setIndex(actualIndex()) because the internal index will
     *  be not set correctly !!!
     *
     * @return    Description of the Return Value
     */
    public int getIndex();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasNext();

    public void incrementIndex();

    /**
     *  Description of the Method
     *
     * @param  newObject  Description of the Parameter
     */
    public void insert(Object newObject);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object next();

    /**
     *  Description of the Method
     */
    public void remove();

    /**
     *  Description of the Method
     */
    public void reset();

    /**
     *  Sets the actual index.
     *
     * @param  index  The new actualIndex value
     */
    public void setIndex(int index);
}
