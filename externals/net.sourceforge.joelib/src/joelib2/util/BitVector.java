///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: BitVector.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.7 $
//          $Date: 2005/02/17 16:48:41 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
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
package joelib2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.BitSet;
import java.util.List;


/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.7 $, $Date: 2005/02/17 16:48:41 $
 */
public interface BitVector extends BitSetInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public BitVector and(BitVector bv);

    /**
     *  Returns the number of bits which are set in this <tt>BitSet14</tt> AND
     *  the <tt>BitSet14</tt> b.
     *
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    public int andCount(BitVector b);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector andSet(BitVector bv);

    /**
     *  Description of the Method
     *
     * @param  bit  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean bitIsOn(int bit);

    //public  boolean Resize(int)
    public boolean bitIsSet(int bit);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int countBits();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean empty();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int endBit();

    /*   OEBitVec &OEBitVec::operator+= (OEBitVec &bv)
     *{
     *int old_size = _size;
     *Resize(_size*SETWORD+bv._size*SETWORD);
     *for (int i = 0;i < bv._size;i++)  _set[i+old_size] = bv._set[i];
     *return(*this);
     *} */
    public int firstBit();

    /*   void OEBitVec::Fold(int nbits)
     *{
     *int nwords = nbits/SETWORD;
     *if (_size < nwords)
     *{
     *_set.resize(nwords);
     *_size = nwords;
     *return;
     *}
     *int i,idx = nwords;
     *for (i = 0,idx=nwords;idx < _size;idx++)
     *{
     *_set[i] |= _set[idx];
     *if (i+1 < nwords) i++;
     *else i = 0;
     *}
     *_set.resize(nwords);
     *_size = nwords;
     *} */
    public void fold(int nbits);

    public void fromBoolArray(boolean[] boolArray);

    /**
     *  Description of the Method
     *
     * @param  intArray  Description of the Parameter
     */
    public void fromIntArray(int[] intArray);

    /**
     * Reads this bit vector from a <tt>String</tt>.
     * e.g. [0 10 15 23]. It's a list of all set bits in this
     * bit vector, which are separated by a space character.
     * The bit vector is enclosed by two brackets.
     *
     * @param  s  the string representation of set bits enclosed by []-brackets
     */
    public void fromString(String s);

    /**
     * @param  v     Description of the Parameter
     */
    public void fromVectorWithIntArray(List v);

    /**
     *  Description of the Method
     *
     * @param  is               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public void in(InputStream is) throws IOException;

    /**
     *  Description of the Method
     */
    public void negate();

    /**
     *  Returns the index of the first bit that is set to <tt>true</tt> that
     *  occurs on or after the specified starting index. If no such bit exists
     *  then -1 is returned. To iterate over the <tt>true</tt> bits in a
     *  <tt>BitSet14</tt>, use the following loop: for(int i=bs.nextSetBit(0);
     *  i>=0; i=bs.nextSetBit(i+1)) { // operate on index i here }
     *
     * @param  last                        the index to start checking from
     *      (inclusive).
     * @return                             the index of the next set bit.
     * @throws  IndexOutOfBoundsException  if the specified index is negative.
     */
    public int nextBit(int last);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector or(BitVector bv);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector orSet(BitVector bv);

    /**
     *  Description of the Method
     *
     * @param  os  Description of the Parameter
     */
    public void out(OutputStream os);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector set(final BitVector bv);

    /**
     *  Clears the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  bitIndex                       The new bitOff value
     */
    public void setBitOff(int bitIndex);

    /**
     *  Sets the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  bitIndex                       The new bitOn value
     */
    public void setBitOn(int bitIndex);

    /**
     *  Clears the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  from                           index of the first bit to be
     *      cleared.
     * @param  to                             index after the last bit to be
     *      cleared.
     */
    public void setRangeOff(int from, int to);

    /**
     *  Sets the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  from                           index of the first bit to be
     *      cleared.
     * @param  to                             index after the last bit to be
     *      cleared.
     */
    public void setRangeOn(int from, int to);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector sub(BitVector bv);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector subSet(BitVector bv);

    /**
     *  Returns the tanimoto similarity coefficient between this <tt>BitSet14</tt>
     *  with the <tt>BitSet14</tt> b.
     *
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    public double tanimoto(BitVector b);

    public boolean[] toBoolArr(int to);

    public boolean[] toBoolArray();

    public int[] toIntArray();

    /**
     * Writes this bit vector to a <tt>String</tt>.
     * e.g. [0 10 15 23]. It's a list of all set bits in this
     * bit vector, which are separated by a space character.
     * The bit vector is enclosed by two brackets.
     *
     * @return    Description of the Return Value
     */
    public String toString();

    /**
     * @param  v     Description of the Parameter
     */
    public void toVectorWithIntArray(List v);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector xor(BitVector bv);

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector xorSet(BitVector bv);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
