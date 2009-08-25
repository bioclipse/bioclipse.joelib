///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SortedVector.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/01/26 12:07:33 $
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

import joelib2.sort.QuickInsertSort;

import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;


/**
 * Sorted Vector.
 * Can contain equal objects. An TreeSet, HashMap or an AVLTree can only contain
 * different objects.
 *
 * @see java.util.TreeSet
 * @see java.util.HashMap
 */
public class SortedVector extends Vector implements SortedVectorInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    protected Comparator comparator;
    private boolean sorted = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SortedVector(Comparator _comparator)
    {
        super();

        comparator = _comparator;
    }

    /**
     * Constructs a vector containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this
     *       vector.
     * @throws NullPointerException if the specified collection is null.
     * @since   1.2
     */
    public SortedVector(Collection c)
    {
        super(c);
        sorted = false;
    }

    public SortedVector(Comparator _comparator, int initialCapacity)
    {
        super(initialCapacity);

        comparator = _comparator;
    }

    public SortedVector(Comparator _comparator, int initialCapacity,
        int increment)
    {
        super(initialCapacity, increment);

        comparator = _comparator;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Appends the specified element to the end of this Vector.
     *
     * @param o element to be appended to this Vector.
     * @return true (as per the general contract of Collection.add).
     * @since 1.2
     */
    public synchronized boolean add(Object o)
    {
        boolean b = super.add(o);
        sorted = false;

        return b;
    }

    /**
     * Inserts the specified element at the specified position in this Vector.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * @exception ArrayIndexOutOfBoundsException index is out of range
     *                  (index &lt; 0 || index &gt; size()).
     * @since 1.2
     */
    public void add(int index, Object element)
    {
        if (!sorted)
        {
            sort();
        }

        super.add(index, element);
        sorted = false;
    }

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this Vector, in the order that they are returned by the specified
     * Collection's Iterator.  The behavior of this operation is undefined if
     * the specified Collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified Collection is this Vector, and this Vector is nonempty.)
     *
     * @param c elements to be inserted into this Vector.
     * @return <tt>true</tt> if this Vector changed as a result of the call.
     * @exception ArrayIndexOutOfBoundsException index out of range (index
     *                  &lt; 0 || index &gt; size()).
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    public synchronized boolean addAll(Collection c)
    {
        boolean b = super.addAll(c);
        sorted = false;

        return b;
    }

    /**
     * Inserts all of the elements in in the specified Collection into this
     * Vector at the specified position.  Shifts the element currently at
     * that position (if any) and any subsequent elements to the right
     * (increases their indices).  The new elements will appear in the Vector
     * in the order that they are returned by the specified Collection's
     * iterator.
     *
     * @param index index at which to insert first element
     *                    from the specified collection.
     * @param c elements to be inserted into this Vector.
     * @exception ArrayIndexOutOfBoundsException index out of range (index
     *                  &lt; 0 || index &gt; size()).
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    public synchronized boolean addAll(int index, Collection c)
    {
        if (!sorted)
        {
            sort();
        }

        boolean b = super.addAll(index, c);
        sorted = false;

        return b;
    }

    /**
     * Adds the specified component to the end of this vector,
     * increasing its size by one. The capacity of this vector is
     * increased if its size becomes greater than its capacity. <p>
     *
     * This method is identical in functionality to the add(Object) method
     * (which is part of the List interface).
     *
     * @param   obj   the component to be added.
     * @see           #add(Object)
     * @see           List
     */
    public synchronized void addElement(Object obj)
    {
        super.addElement(obj);
        sorted = false;
    }

    /**
     * Returns a clone of this vector. The copy will contain a
     * reference to a clone of the internal data array, not a reference
     * to the original internal data array of this {@link java.util.Vector} object.
     *
     * @return  a clone of this vector.
     */
    public synchronized Object clone()
    {
        if (!sorted)
        {
            sort();
        }

        return super.clone();
    }

    /**
     * Copies the components of this vector into the specified array. The
     * item at index <tt>k</tt> in this vector is copied into component
     * <tt>k</tt> of <tt>anArray</tt>. The array must be big enough to hold
     * all the objects in this vector, else an
     * <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param   anArray   the array into which the components get copied.
     * @throws  NullPointerException if the given array is null.
     */
    public synchronized void copyInto(Object[] anArray)
    {
        super.copyInto(anArray);
        sorted = false;
    }

    /**
    * Returns the component at the specified index.<p>
    *
    * This method is identical in functionality to the get method
    * (which is part of the List interface).
    *
    * @param      index   an index into this vector.
    * @return     the component at the specified index.
    * @exception  ArrayIndexOutOfBoundsException  if the <tt>index</tt>
    *             is negative or not less than the current size of this
    *             {@link java.util.Vector} object.
    *             given.
    * @see           #get(int)
    * @see           List
    */
    public synchronized Object elementAt(int index)
    {
        if (!sorted)
        {
            sort();
        }

        return super.elementAt(index);
    }

    /**
     * Returns an enumeration of the components of this vector. The
     * returned <tt>Enumeration</tt> object will generate all items in
     * this vector. The first item generated is the item at index <tt>0</tt>,
     * then the item at index <tt>1</tt>, and so on.
     *
     * @return  an enumeration of the components of this vector.
     * @see     Enumeration
     * @see     Iterator
     */
    public Enumeration elements()
    {
        if (!sorted)
        {
            sort();
        }

        return super.elements();
    }

    /**
    * Returns the first component (the item at index <tt>0</tt>) of
    * this vector.
    *
    * @return     the first component of this vector.
    * @exception  NoSuchElementException  if this vector has no components.
    */
    public synchronized Object firstElement()
    {
        if (!sorted)
        {
            sort();
        }

        return super.firstElement();
    }

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this Vector.
     *
     * @param index index of element to return.
     * @exception ArrayIndexOutOfBoundsException index is out of range (index
     *                   &lt; 0 || index &gt;= size()).
     * @since 1.2
     */
    public synchronized Object get(int index)
    {
        if (!sorted)
        {
            sort();
        }

        return super.get(index);
    }

    /**
     * Searches for the first occurence of the given argument, beginning
     * the search at <tt>index</tt>, and testing for equality using
     * the <tt>equals</tt> method.
     *
     * @param   elem    an object.
     * @param   index   the non-negative index to start searching from.
     * @return  the index of the first occurrence of the object argument in
     *          this vector at position <tt>index</tt> or later in the
     *          vector, that is, the smallest value <tt>k</tt> such that
     *          <tt>elem.equals(elementData[k]) && (k &gt;= index)</tt> is
     *          <tt>true</tt>; returns <tt>-1</tt> if the object is not
     *          found. (Returns <tt>-1</tt> if <tt>index</tt> &gt;= the
     *          current size of this {@link java.util.Vector}.)
     * @exception  IndexOutOfBoundsException  if <tt>index</tt> is negative.
     * @see     Object#equals(Object)
     */
    public synchronized int indexOf(Object elem, int index)
    {
        if (!sorted)
        {
            sort();
        }

        return super.indexOf(elem, index);
    }

    /**
     * Inserts the specified object as a component in this vector at the
     * specified <tt>index</tt>. Each component in this vector with
     * an index greater or equal to the specified <tt>index</tt> is
     * shifted upward to have an index one greater than the value it had
     * previously. <p>
     *
     * The index must be a value greater than or equal to <tt>0</tt>
     * and less than or equal to the current size of the vector. (If the
     * index is equal to the current size of the vector, the new element
     * is appended to the Vector.)<p>
     *
     * This method is identical in functionality to the add(Object, int) method
     * (which is part of the List interface). Note that the add method reverses
     * the order of the parameters, to more closely match array usage.
     *
     * @param      obj     the component to insert.
     * @param      index   where to insert the new component.
     * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
     * @see        #size()
     * @see           #add(int, Object)
     * @see           List
     */
    public synchronized void insertElementAt(Object obj, int index)
    {
        if (!sorted)
        {
            sort();
        }

        super.insertElementAt(obj, index);
        sorted = false;
    }

    /**
     * Returns the last component of the vector.
     *
     * @return  the last component of the vector, i.e., the component at index
     *          <tt>size()&nbsp;-&nbsp;1</tt>.
     * @exception  NoSuchElementException  if this vector is empty.
     */
    public synchronized Object lastElement()
    {
        if (!sorted)
        {
            sort();
        }

        return super.lastElement();
    }

    /**
     * Searches backwards for the specified object, starting from the
     * specified index, and returns an index to it.
     *
     * @param  elem    the desired component.
     * @param  index   the index to start searching from.
     * @return the index of the last occurrence of the specified object in this
     *          vector at position less than or equal to <tt>index</tt> in
     *          the vector, that is, the largest value <tt>k</tt> such that
     *          <tt>elem.equals(elementData[k]) && (k &lt;= index)</tt> is
     *          <tt>true</tt>; <tt>-1</tt> if the object is not found.
     *          (Returns <tt>-1</tt> if <tt>index</tt> is negative.)
     * @exception  IndexOutOfBoundsException  if <tt>index</tt> is greater
     *             than or equal to the current size of this vector.
     */
    public synchronized int lastIndexOf(Object elem, int index)
    {
        if (!sorted)
        {
            sort();
        }

        return super.lastIndexOf(elem, index);
    }

    /**
     * Removes the element at the specified position in this Vector.
     * shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the Vector.
     *
     * @exception ArrayIndexOutOfBoundsException index out of range (index
     *                   &lt; 0 || index &gt;= size()).
     * @param index the index of the element to removed.
     * @since 1.2
     */
    public synchronized Object remove(int index)
    {
        sorted = false;

        return super.remove(index);
    }

    /**
     * Removes from this Vector all of its elements that are contained in the
     * specified Collection.
     *
     * @return true if this Vector changed as a result of the call.
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    public synchronized boolean removeAll(Collection c)
    {
        return super.removeAll(c);
    }

    /**
     * Deletes the component at the specified index. Each component in
     * this vector with an index greater or equal to the specified
     * <tt>index</tt> is shifted downward to have an index one
     * smaller than the value it had previously. The size of this vector
     * is decreased by <tt>1</tt>.<p>
     *
     * The index must be a value greater than or equal to <tt>0</tt>
     * and less than the current size of the vector. <p>
     *
     * This method is identical in functionality to the remove method
     * (which is part of the List interface).  Note that the remove method
     * returns the old value that was stored at the specified position.
     *
     * @param      index   the index of the object to remove.
     * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
     * @see        #size()
     * @see           #remove(int)
     * @see           List
     */
    public synchronized void removeElementAt(int index)
    {
        if (!sorted)
        {
            sort();
        }

        super.removeElementAt(index);
    }

    /**
     * Retains only the elements in this Vector that are contained in the
     * specified Collection.  In other words, removes from this Vector all
     * of its elements that are not contained in the specified Collection.
     *
     * @return true if this Vector changed as a result of the call.
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    public synchronized boolean retainAll(Collection c)
    {
        return super.retainAll(c);
    }

    /**
     * Replaces the element at the specified position in this Vector with the
     * specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @exception ArrayIndexOutOfBoundsException index out of range
     *                  (index &lt; 0 || index &gt;= size()).
     * @since 1.2
     */
    public synchronized Object set(int index, Object element)
    {
        if (!sorted)
        {
            sort();
        }

        Object o = super.set(index, element);
        sorted = false;

        return o;
    }

    /**
    * Sets the component at the specified <tt>index</tt> of this
    * vector to be the specified object. The previous component at that
    * position is discarded.<p>
    *
    * The index must be a value greater than or equal to <tt>0</tt>
    * and less than the current size of the vector. <p>
    *
    * This method is identical in functionality to the set method
    * (which is part of the List interface). Note that the set method reverses
    * the order of the parameters, to more closely match array usage.  Note
    * also that the set method returns the old value that was stored at the
    * specified position.
    *
    * @param      obj     what the component is to be set to.
    * @param      index   the specified index.
    * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
    * @see        #size()
    * @see        List
    * @see           #set(int, java.lang.Object)
    */
    public synchronized void setElementAt(Object obj, int index)
    {
        super.setElementAt(obj, index);
        sorted = false;
    }

    /**
     * Returns a view of the portion of this List between fromIndex,
     * inclusive, and toIndex, exclusive.  (If fromIndex and ToIndex are
     * equal, the returned List is empty.)  The returned List is backed by this
     * List, so changes in the returned List are reflected in this List, and
     * vice-versa.  The returned List supports all of the optional List
     * operations supported by this List.<p>
     *
     * This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).   Any operation that expects
     * a List can be used as a range operation by operating on a subList view
     * instead of a whole List.  For example, the following idiom
     * removes a range of elements from a List:
     * <pre>
     *            list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for indexOf and lastIndexOf,
     * and all of the algorithms in the Collections class can be applied to
     * a subList.<p>
     *
     * The semantics of the List returned by this method become undefined if
     * the backing list (i.e., this List) is <i>structurally modified</i> in
     * any way other than via the returned List.  (Structural modifications are
     * those that change the size of the List, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @param fromIndex low endpoint (inclusive) of the subList.
     * @param toIndex high endpoint (exclusive) of the subList.
     * @return a view of the specified range within this List.
     * @throws IndexOutOfBoundsException endpoint index value out of range
     *         <tt>(fromIndex &lt; 0 || toIndex &gt; size)</tt>
     * @throws IllegalArgumentException endpoint indices out of order
     *               <tt>(fromIndex &gt; toIndex)</tt>
     */
    public synchronized List subList(int fromIndex, int toIndex)
    {
        if (!sorted)
        {
            sort();
        }

        return super.subList(fromIndex, toIndex);
    }

    /**
     * Returns an array containing all of the elements in this Vector
     * in the correct order.
     *
     * @since 1.2
     */
    public synchronized Object[] toArray()
    {
        if (!sorted)
        {
            sort();
        }

        return super.toArray();
    }

    /**
     * Returns an array containing all of the elements in this Vector in the
     * correct order; the runtime type of the returned array is that of the
     * specified array.  If the Vector fits in the specified array, it is
     * returned therein.  Otherwise, a new array is allocated with the runtime
     * type of the specified array and the size of this Vector.<p>
     *
     * If the Vector fits in the specified array with room to spare
     * (i.e., the array has more elements than the Vector),
     * the element in the array immediately following the end of the
     * Vector is set to null.  This is useful in determining the length
     * of the Vector <em>only</em> if the caller knows that the Vector
     * does not contain any null elements.
     *
     * @param a the array into which the elements of the Vector are to
     *                be stored, if it is big enough; otherwise, a new array of the
     *                 same runtime type is allocated for this purpose.
     * @return an array containing the elements of the Vector.
     * @exception ArrayStoreException the runtime type of a is not a supertype
     * of the runtime type of every element in this Vector.
     * @throws NullPointerException if the given array is null.
     * @since 1.2
     */
    public synchronized Object[] toArray(Object[] a)
    {
        if (!sorted)
        {
            sort();
        }

        return super.toArray(a);
    }

    /**
     * Removes from this List all of the elements whose index is between
     * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
     * elements to the left (reduces their index).
     * This call shortens the ArrayList by (toIndex - fromIndex) elements.  (If
     * toIndex==fromIndex, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed.
     * @param toIndex index after last element to be removed.
     */
    protected void removeRange(int fromIndex, int toIndex)
    {
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * Sorts the elements of this vector.
     */
    private void sort()
    {
        if (this.size() > 1)
        {
            QuickInsertSort sorting = new QuickInsertSort();
            Object[] usedArray = new Object[elementCount];
            System.arraycopy(elementData, 0, usedArray, 0, elementCount);
            sorting.sort(usedArray, comparator);
            System.arraycopy(usedArray, 0, elementData, 0, elementCount);
        }

        sorted = true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
