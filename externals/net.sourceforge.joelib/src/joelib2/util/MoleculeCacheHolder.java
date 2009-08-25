/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.util;

import joelib2.feature.data.MoleculeCache;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface MoleculeCacheHolder
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean contains(String _file);

    public MoleculeCache get(String _file);

    public Object put(String file, MoleculeCache obj);
}
