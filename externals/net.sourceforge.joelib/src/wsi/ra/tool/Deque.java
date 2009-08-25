///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Deque.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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

import org.apache.log4j.Category;


/**
 * Deque implementation.
 * Allows a First-In-First-Out- and a Last-In-First-Out-Deque.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:44 $
 */
public class Deque extends java.lang.Object implements DequeInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance("wsi.ra.tool.Deque");

    //~ Instance fields ////////////////////////////////////////////////////////

    public DequeNode head;
    public DequeNode tail;
    private int size;

    //~ Constructors ///////////////////////////////////////////////////////////

    public Deque()
    {
        // generate empty Deque
        this(null);
    }

    public Deque(DequeNode head)
    {
        size = 0;
        this.head = head;
        tail = head;

        if (tail != null)
        {
            while (tail.next != null)
            {
                tail = tail.next;
                size++;
            }
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns the back <tt>DequeNode</tt> of this stack.
     */
    public DequeNode getBack()
    {
        return tail;
    }

    /**
     * Returns an <tt>DequeIterator</tt> for this stack, which starts
     * with the front <tt>DequeNode</tt>.
     */
    public DequeIterator getDequeIterator()
    {
        return new DequeIterator(this);
    }

    /**
     * Returns the front <tt>DequeNode</tt> of this stack.
     */
    public DequeNode getFront()
    {
        return head;
    }

    public DequeNode insertAfter(DequeNode a, Object o)
    {
        if (a == tail)
        {
            return pushBack(o);
        }

        size++;

        return new DequeNode(a, o, a.next);
    }

    public DequeNode insertBefore(DequeNode a, Object o)
    {
        if (a == head)
        {
            return pushFront(o);
        }

        size++;

        return new DequeNode(a.previous, o, a);
    }

    public boolean isEmpty()
    {
        return (head == null);
    }

    /**
     * Returns the back <tt>DequeNode</tt> and removes it from this stack.
     */
    public Object popBack()
    {
        if (tail != null)
        {
            size--;

            Object o = tail.key;

            if (head == tail)
            {
                head = tail = null;

                return o;
            }

            tail.previous.next = null;
            tail = tail.previous;

            return o;
        }

        return null;
    }

    /**
     * Returns the front <tt>DequeNode</tt> and removes it from this stack.
     */
    public Object popFront()
    {
        if (head != null)
        {
            size--;

            Object o = head.key;
            head = head.next;

            if (head != null)
            {
                head.previous = null;
            }

            if (head == null)
            {
                tail = null;
            }

            return o;
        }

        return null;
    }

    /**
     * Adds the <tt>DequeNode</tt> to the back of this stack.
     */
    public DequeNode pushBack(Object o)
    {
        size++;

        DequeNode n = new DequeNode(tail, o, null);

        if (tail == null)
        {
            head = tail = n;

            return tail;
        }

        tail.next = n;
        tail = n;

        return tail;
    }

    /**
     * Adds the <tt>DequeNode</tt> to the front of this stack.
     */
    public DequeNode pushFront(Object o)
    {
        size++;

        DequeNode n = new DequeNode(null, o, head);

        if (head == null)
        {
            head = tail = n;

            return head;
        }

        n.next = head;
        head = n;

        return head;
    }

    public DequeNode pushFront(Deque l)
    {
        if ((l == null) || (l.head == null))
        {
            return null;
        }

        size += l.size;

        l.tail.next = head;
        head.previous = l.tail;
        head = l.head;

        return head;
    }

    public void remove(DequeNode n)
    {
        if (n == null)
        {
            return;
        }

        if (head == n)
        {
            popFront();

            return;
        }

        if (tail == n)
        {
            popBack();

            return;
        }

        if (head == null)
        {
            logger.error("Cannot remove from empty list");
        }

        size--;
        n.previous.next = n.next;
        n.next.previous = n.previous;
    }

    public void removeAll()
    {
        head = tail = null;
        size = 0;
    }

    /**
     * Gets the size of this stack.
     */
    public int size()
    {
        return size;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
