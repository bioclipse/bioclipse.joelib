/*
 * Created on Jan 15, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.rotor;

import joelib2.smarts.SMARTSPatternMatcher;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface RotorRule
{
    //~ Methods ////////////////////////////////////////////////////////////////

    double getDelta();

    void getReferenceAtoms(int[] ref);

    SMARTSPatternMatcher getSmartsPattern();

    String getSmartsString();

    double[] getTorsionValues();

    boolean isValid();

    void setDelta(float delta);
}
