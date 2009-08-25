/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.util.iterator;

import joelib2.feature.NativeValue;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface NativeValueIterator
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public String actualName();

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean hasNext();

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public Object next();

    public double nextDouble();

    public int nextInt();

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public NativeValue nextNativeValue();

    public String nextString();

    /**
     * Description of the Method
     */
    public void remove();
}
