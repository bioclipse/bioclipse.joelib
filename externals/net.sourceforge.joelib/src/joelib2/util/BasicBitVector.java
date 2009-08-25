///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicBitVector.java,v $
//  Purpose:  BitSet14 extensions.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.4 $
//            $Date: 2005/02/17 16:48:41 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * BitSet14 extensions.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/02/17 16:48:41 $
 */
public class BasicBitVector extends BitSet implements java.io.Serializable,
    BitVector
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(BasicBitVector.class
            .getName());

    //~ Constructors ///////////////////////////////////////////////////////////

    //private int _size;

    /**
     *  {@link java.util.Vector} of <tt>int[1]</tt>
     */

    //private Vector _set;
    public BasicBitVector()
    {
        super();

        //_set.resize(STARTWORDS);
        //_size=_set.size();
        //clear();
    }

    /**
     *  Constructor for the JOEBitVec object
     *
     * @param  bits  Description of the Parameter
     */
    public BasicBitVector(int bits)
    {
        super(bits);

        //_set.resize(bits/SETWORD);
        //_size=_set.size();
        //clear();
    }

    /**
     *  Constructor for the JOEBitVec object
     *
     * @param  bv  Description of the Parameter
     */
    public BasicBitVector(final BitVector bv)
    {
        clear();
        super.or((BitSet) bv);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  bv1  Description of the Parameter
     * @param  bv2  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static BitVector and(BitVector bv1, BitVector bv2)
    {
        BasicBitVector tmp = (BasicBitVector) bv1.clone();

        tmp.andSet(bv2);

        return tmp;
    }

    /**
     *  Description of the Method
     *
     * @param  bv1  Description of the Parameter
     * @param  bv2  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static BitVector or(BitVector bv1, BitVector bv2)
    {
        BasicBitVector tmp = (BasicBitVector) bv1.clone();

        tmp.orSet(bv2);

        return tmp;
    }

    /**
     *  Description of the Method
     *
     * @param  bv1  Description of the Parameter
     * @param  bv2  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static BitVector sub(BitVector bv1, BitVector bv2)
    {
        BasicBitVector tmp = (BasicBitVector) bv1.clone();

        tmp.subSet(bv2);

        return tmp;
    }

    /**
     *  Description of the Method
     *
     * @param  bv1  Description of the Parameter
     * @param  bv2  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static BitVector xor(BitVector bv1, BitVector bv2)
    {
        BasicBitVector tmp = (BasicBitVector) bv1.clone();

        tmp.xorSet(bv2);

        return tmp;
    }

    //   public  JOEBitVec addSet (JOEBitVec bv)
    //   {
    //
    //   }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector and(BitVector bv)
    {
        BasicBitVector tmp = (BasicBitVector) bv.clone();

        tmp.andSet(this);

        return tmp;
    }

    /**
     *  Returns the number of bits which are set in this <tt>BitSet14</tt> AND
     *  the <tt>BitSet14</tt> b.
     *
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    public int andCount(BitVector b)
    {
        BitSet a = (BitSet) this.clone();
        a.and((BitSet) b);

        return a.cardinality();
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BasicBitVector andSet(BitVector bv)
    {
        super.and((BitSet) bv);

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  bit  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean bitIsOn(int bit)
    {
        return get(bit);
    }

    //public  boolean Resize(int)
    //{
    //}

    /**
     *  Description of the Method
     *
     * @param  bit  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean bitIsSet(int bit)
    {
        return get(bit);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int countBits()
    {
        return super.cardinality();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final boolean empty()
    {
        return (isEmpty());
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final int endBit()
    {
        return (-1);
    }

    /*   OEBitVec &OEBitVec::operator+= (OEBitVec &bv)
     *{
     *int old_size = _size;
     *Resize(_size*SETWORD+bv._size*SETWORD);
     *for (int i = 0;i < bv._size;i++)  _set[i+old_size] = bv._set[i];
     *return(*this);
     *} */

    /**
     *  Returns the index of the first bit that is set to <tt>true</tt>. If no
     *  such bit exists then -1 is returned.
     *
     * @return                             the index of the next set bit.
     * @throws  IndexOutOfBoundsException  if the specified index is negative.
     */
    public int firstBit()
    {
        return nextBit(-1);

        //(get(0) ? 0  : nextBit(-1));
    }

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

    /**
     *  Description of the Method
     *
     * @param  nbits  Description of the Parameter
     */
    public void fold(int nbits)
    {
        // logger.error("Don't know what to do ...");
        System.out.println("Don't know what to do ...");

        //      int nwords = nbits/SETWORD;
        //
        //      if (_size < nwords)
        //        {
        //          _set.resize(nwords);
        //          _size = nwords;
        //          return;
        //        }
        //
        //      int i,idx = nwords;
        //      for (i = 0,idx=nwords;idx < _size;idx++)
        //        {
        //          _set[i] |= _set[idx];
        //          if (i+1 < nwords) i++;
        //          else i = 0;
        //        }
        //      _set.resize(nwords);
        //      _size = nwords;
    }

    public void fromBoolArray(boolean[] boolArray)
    {
        for (int i = 0; i < boolArray.length; i++)
        {
            if (boolArray[i])
            {
                super.set(i);
            }
            else
            {
                super.clear(i);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  intArray  Description of the Parameter
     */
    public void fromIntArray(int[] intArray)
    {
        //    System.out.println("fromIntArr:");
        for (int i = 0; i < intArray.length; i++)
        {
            setBitOn(intArray[i]);

            //      System.out.print(""+intArray[i]+" ");
        }

        //    System.out.println("");
    }

    /**
     * Reads this bit vector from a <tt>String</tt>.
     * e.g. [0 10 15 23]. It's a list of all set bits in this
     * bit vector, which are separated by a space character.
     * The bit vector is enclosed by two brackets.
     *
     * @param  s  the string representation of set bits enclosed by []-brackets
     */
    public void fromString(String s)
    {
        clear();

        StringTokenizer st = new StringTokenizer(s, " \t\n");
        String stmp;

        while (st.hasMoreTokens())
        {
            stmp = st.nextToken();

            if (stmp.equals("["))
            {
                continue;
            }
            else if (stmp.equals("]"))
            {
                break;
            }

            setBitOn(Integer.parseInt(stmp));
        }
    }

    /**
     * @param  v     Description of the Parameter
     */
    public void fromVectorWithIntArray(List v)
    {
        int[] itmp;

        for (int i = 0; i < v.size(); i++)
        {
            itmp = (int[]) v.get(i);
            setBitOn(itmp[0]);
        }

        if (logger.isDebugEnabled())
        {
            //StringBuffer sb = new StringBuffer();
            //sb.append("fromVecInt");
            for (int i = 0; i < v.size(); i++)
            {
                itmp = (int[]) v.get(i);

                //sb.append(itmp[0]);
                //sb.append(' ');
            }

            //logger.debug(sb.toString());
        }
    }

    /**
     *  Description of the Method
     *
     * @param  is               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public void in(InputStream is) throws IOException
    {
        LineNumberReader ln = new LineNumberReader(new InputStreamReader(is));
        String line;

        for (;;)
        {
            line = ln.readLine();

            if (line == null)
            {
                break;
            }

            fromString(line);
        }
    }

    /**
     *  Description of the Method
     */
    public void negate()
    {
        flip(0, size());
    }

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
    public final int nextBit(int last)
    {
        return nextSetBit(last + 1);
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector or(BitVector bv)
    {
        BasicBitVector tmp = (BasicBitVector) bv.clone();

        tmp.orSet(this);

        return tmp;
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector orSet(BitVector bv)
    {
        super.or((BitSet) bv);

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  os  Description of the Parameter
     */
    public void out(OutputStream os)
    {
        PrintStream ps = new PrintStream(os);

        ps.println(this.toString());
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector set(final BitVector bv)
    {
        clear();
        super.or((BitSet) bv);

        return this;
    }

    /**
     *  Clears the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  bitIndex                       The new bitOff value
     */
    public void setBitOff(int bitIndex)
    {
        super.clear(bitIndex);
    }

    /**
     *  Sets the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  bitIndex                       The new bitOn value
     */
    public void setBitOn(int bitIndex)
    {
        super.set(bitIndex);
    }

    /**
     *  Clears the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  from                           index of the first bit to be
     *      cleared.
     * @param  to                             index after the last bit to be
     *      cleared.
     */
    public void setRangeOff(int from, int to)
    {
        super.clear(from, to + 1);
    }

    /**
     *  Sets the bits from the specified fromIndex(inclusive) to the specified
     *  toIndex(inclusive) to <tt>false</tt>.
     *
     * @param  from                           index of the first bit to be
     *      cleared.
     * @param  to                             index after the last bit to be
     *      cleared.
     */
    public void setRangeOn(int from, int to)
    {
        super.set(from, to + 1);
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector sub(BitVector bv)
    {
        BasicBitVector tmp = (BasicBitVector) bv.clone();

        tmp.subSet(this);

        return tmp;
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector subSet(BitVector bv)
    {
        BasicBitVector tmp = (BasicBitVector) bv.clone();

        tmp.negate();
        super.and(tmp);

        return this;
    }

    /**
     *  Returns the tanimoto similarity coefficient between this <tt>BitSet14</tt>
     *  with the <tt>BitSet14</tt> b.
     *
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    public double tanimoto(BitVector b)
    {
        int ab = andCount(b);
        double tanimoto = (double) ab /
            (double) ((this.cardinality() + b.cardinality()) - ab);

        return tanimoto;
    }

    public boolean[] toBoolArr(int to)
    {
        boolean[] boolArray;
        boolArray = new boolean[to];

        for (int i = 0; i < to; i++)
        {
            boolArray[i] = get(i);
        }

        return boolArray;
    }

    public boolean[] toBoolArray()
    {
        return toBoolArr(this.size());
    }

    public int[] toIntArray()
    {
        int[] array;
        array = new int[countBits()];

        //      System.out.println("bits "+countBits());
        int index = 0;

        for (int i = nextBit(-1); i != -1; i = nextSetBit(i + 1))
        {
            array[index] = i;
            index++;

            //        System.out.print(" "+i);
        }

        //      System.out.println("");
        return array;
    }

    /**
     * Writes this bit vector to a <tt>String</tt>.
     * e.g. [0 10 15 23]. It's a list of all set bits in this
     * bit vector, which are separated by a space character.
     * The bit vector is enclosed by two brackets.
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer("[ ");

        for (int i = nextBit(-1); i != -1; i = nextSetBit(i + 1))
        {
            sb.append(i);
            sb.append(' ');
        }

        sb.append("]");

        return sb.toString();
    }

    /**
     * @param  v     Description of the Parameter
     */
    public void toVectorWithIntArray(List v)
    {
        v.clear();

        if (v instanceof Vector)
        {
            ((Vector) v).ensureCapacity(countBits());
        }

        //      System.out.println("bits "+countBits());
        for (int i = nextBit(-1); i != -1; i = nextSetBit(i + 1))
        {
            int[] itmp = new int[]{i};
            v.add(itmp);

            //        System.out.print(" "+i);
        }

        //      System.out.println("");
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector xor(BitVector bv)
    {
        BasicBitVector tmp = (BasicBitVector) bv.clone();

        tmp.xorSet(this);

        return tmp;
    }

    /**
     *  Description of the Method
     *
     * @param  bv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public BitVector xorSet(BitVector bv)
    {
        super.xor((BitSet) bv);

        return this;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
