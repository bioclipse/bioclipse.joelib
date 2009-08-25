///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicListIterator.java,v $
//  Purpose:  Iterator for the standard Vector.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:42 $
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
package joelib2.util.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 *  Iterator for the standard {@link List}.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 * @TODO Check if we can combine with the java.util.ListIterator
 */
public class BasicListIterator implements Iterator, Cloneable, ListIterator
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    private int index = 0;

    /**
     *  Description of the Field
     */
    private List list = null;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the VectorIterator object
     *
     * @param  vector  Description of the Parameter
     */
    public BasicListIterator(List list)
    {
        this.list = list;
        this.index = 0;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Returns the actual <tt>Object</tt> .
     *
     * @return    Description of the Return Value
     */
    public Object actual()
    {
        if (index > 0)
        {
            return list.get(index - 1);
        }
        else
        {
            return list.get(index);
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object clone()
    {
        BasicListIterator vIter = new BasicListIterator(list);
        vIter.setIndex(getIndex());

        return vIter;
    }

    final public void decrementIndex()
    {
        this.setIndex(this.getIndex() - 1);
    }

    /**
     *  Returns the index number ot the sctual <tt>Object</tt> . Warning: Util now
     *  you should not use setIndex(actualIndex()) because the internal index will
     *  be not set correctly !!!
     *
     * @return    Description of the Return Value
     */
    final public int getIndex()
    {
        if (index > 0)
        {
            return index - 1;
        }
        else
        {
            return 0;
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasNext()
    {
        if (list.size() == 0)
        {
            return false;
        }

        return (((index >= 0) && (index < list.size())) ? true : false);
    }

    final public void incrementIndex()
    {
        this.setIndex(this.getIndex() + 1);
    }

    /**
     *  Description of the Method
     *
     * @param  newObject  Description of the Parameter
     */
    public void insert(Object newObject)
    {
        list.add(index, newObject);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object next()
    {
        if (list == null)
        {
            throw new NoSuchElementException();
        }

        if (hasNext())
        {
            return list.get(index++);
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    /**
     *  Description of the Method
     */
    public void remove()
    {
        //throw new UnsupportedOperationException();
        index--;

        if (index < 0)
        {
            throw new IllegalStateException();
        }

        //if ( hasNext() )
        //{
        try
        {
            list.remove(index);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalStateException();
        }

        //}
        //else
        //{
        //  throw new IllegalStateException();
        //}
    }

    /**
     *  Description of the Method
     */
    public void reset()
    {
        index = 0;
    }

    /**
     *  Sets the actual index.
     *
     * @param  index  The new actualIndex value
     */
    final public void setIndex(int index)
    {
        if (index == 0)
        {
            this.index = 0;
        }
        else
        {
            this.index = index + 1;
        }
    }

    protected final List getList()
    {
        return list;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
