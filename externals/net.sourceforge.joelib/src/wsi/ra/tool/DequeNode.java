///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DequeNode.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:44 $
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
package wsi.ra.tool;

/**
 * Deque node representation.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:44 $
 */
public class DequeNode extends java.lang.Object implements DequeNodeInterface
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public Object key;

    public DequeNode next;
    public DequeNode previous;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DequeNode(Object key)
    {
        this(null, key, null);
    }

    public DequeNode(DequeNode p, Object key)
    {
        this(p, key, null);
    }

    public DequeNode(DequeNode p, Object key, DequeNode suc)
    {
        this.key = key;
        next = suc;

        if (next != null)
        {
            next.previous = this;
        }

        previous = p;

        if (previous != null)
        {
            previous.next = this;
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the key.
     */
    protected Object getKey()
    {
        return key;
    }

    /**
     * @return Returns the next.
     */
    protected DequeNode getNext()
    {
        return next;
    }

    /**
     * @return Returns the previous.
     */
    protected DequeNode getPrevious()
    {
        return previous;
    }

    /**
     * @param key The key to set.
     */
    protected void setKey(Object key)
    {
        this.key = key;
    }

    /**
     * @param next The next to set.
     */
    protected void setNext(DequeNode next)
    {
        this.next = next;
    }

    /**
     * @param previous The previous to set.
     */
    protected void setPrevious(DequeNode previous)
    {
        this.previous = previous;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
