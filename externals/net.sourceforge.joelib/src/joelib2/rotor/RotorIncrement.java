/*
 * Created on Jan 15, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.rotor;

/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface RotorIncrement
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the delta.
     */
    double getDelta();

    /**
     * @return Returns the values.
     */
    double[] getValues();

    /**
     * @param delta The delta to set.
     */
    void setDelta(double delta);

    /**
     * @param values The values to set.
     */
    void setValues(double[] values);
}
