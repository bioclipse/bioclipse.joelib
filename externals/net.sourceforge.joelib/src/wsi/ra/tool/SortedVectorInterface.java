///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: SortedVectorInterface.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:44 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package wsi.ra.tool;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:44 $
 */
public interface SortedVectorInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    boolean add(Object o);

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
    void add(int index, Object element);

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
    boolean addAll(Collection c);

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
    boolean addAll(int index, Collection c);

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
    void addElement(Object obj);

    /**
     * Returns a clone of this vector. The copy will contain a
     * reference to a clone of the internal data array, not a reference
     * to the original internal data array of this {@link java.util.Vector} object.
     *
     * @return  a clone of this vector.
     */
    Object clone();

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
    void copyInto(Object[] anArray);

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
    Object elementAt(int index);

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
    Enumeration elements();

    /**
     * Returns the first component (the item at index <tt>0</tt>) of
     * this vector.
     *
     * @return     the first component of this vector.
     * @exception  NoSuchElementException  if this vector has no components.
     */
    Object firstElement();

    // Positional Access Operations
    Object get(int index);

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
    int indexOf(Object elem, int index);

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
    void insertElementAt(Object obj, int index);

    /**
     * Returns the last component of the vector.
     *
     * @return  the last component of the vector, i.e., the component at index
     *          <tt>size()&nbsp;-&nbsp;1</tt>.
     * @exception  NoSuchElementException  if this vector is empty.
     */
    Object lastElement();

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
    int lastIndexOf(Object elem, int index);

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
    Object remove(int index);

    /**
     * Removes from this Vector all of its elements that are contained in the
     * specified Collection.
     *
     * @return true if this Vector changed as a result of the call.
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    boolean removeAll(Collection c);

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
    void removeElementAt(int index);

    /**
     * Retains only the elements in this Vector that are contained in the
     * specified Collection.  In other words, removes from this Vector all
     * of its elements that are not contained in the specified Collection.
     *
     * @return true if this Vector changed as a result of the call.
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    boolean retainAll(Collection c);

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
    Object set(int index, Object element);

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
    void setElementAt(Object obj, int index);

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
    List subList(int fromIndex, int toIndex);

    /**
     * Returns an array containing all of the elements in this Vector
     * in the correct order.
     *
     * @since 1.2
     */
    Object[] toArray();

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
    Object[] toArray(Object[] a);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
