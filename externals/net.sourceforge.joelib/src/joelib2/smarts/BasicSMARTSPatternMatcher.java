///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicSMARTSPatternMatcher.java,v $
//  Purpose:  Class to parse SMART pattern and store the search expressions.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:39 $
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
package joelib2.smarts;

import joelib2.data.IdentifierExpertSystem;

import joelib2.molecule.Molecule;

import joelib2.smarts.atomexpr.BasicQueryAtom;
import joelib2.smarts.atomexpr.BasicQueryAtomBinary;
import joelib2.smarts.atomexpr.BasicQueryAtomValue;
import joelib2.smarts.atomexpr.QueryAtom;

import joelib2.util.BasicBitVector;

import joelib2.util.types.BasicIntInt;
import joelib2.util.types.BasicIntIntInt;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Class to parse SMART pattern and store the search expressions.
 *
 * <blockquote><pre>
 * String smartsPattern = "c1ccccc1";
 * JOESmartsPattern smarts = new JOESmartsPattern();
 *
 * // parse, initialize and generate SMARTS pattern
 * // to allow fast pattern matching
 * if(!smarts.init(smartsPattern)
 * {
 *   System.err.println("Invalid SMARTS pattern.");
 * }
 *
 * // find substructures
 * smarts.match(mol);
 * Vector         matchList  = smarts.getMatchesUnique();
 * </pre></blockquote>
 *
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:39 $
 * @.cite smarts
 */
public class BasicSMARTSPatternMatcher implements java.io.Serializable,
    SMARTSPatternMatcher
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.6 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:39 $";

    private static Category logger = Category.getInstance(SMARTSParser.class
            .getName());
    private static final Class[] DEPENDENCIES = new Class[]{SMARTSParser.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    protected List<int[]> matches;

    /**
     *  Description of the Field
     */
    protected QueryPattern queryPattern;

    /**
     *  Description of the Field
     */
    protected String smarts;
    private SMARTSParser smartsParser;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOESmartsPattern object
     */
    public BasicSMARTSPatternMatcher()
    {
        matches = new Vector<int[]>();
        queryPattern = null;
        smartsParser = new SMARTSParser();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    /**
     * Get list of matching atoms.
     *
     * @return    {@link java.util.Vector} of <tt>int[]</tt>
     */
    public List<int[]> getMatches()
    {
        return matches;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int getMatchesSize()
    {
        return matches.size();
    }

    /**
     * Get unique list of matching atoms.
     *
     * @return    {@link java.util.Vector} of <tt>int[]</tt>
     */
    public List<int[]> getMatchesUnique()
    {
        if ((matches.size() == 0) || (matches.size() == 1))
        {
            return matches;
        }

        boolean ok;
        BasicBitVector bv = new BasicBitVector();
        List<BasicBitVector> vbv = new Vector<BasicBitVector>();
        List<int[]> mlist = new Vector<int[]>();
        int[] itmp;

        //    System.out.println("mlist hits before:"+_mlist.size());
        for (int i = 0; i < matches.size(); i++)
        {
            ok = true;
            bv.clear();

            //vtmp = _mlist.get(i);
            itmp = (int[]) matches.get(i);
            bv.fromIntArray(itmp);

            for (int j = 0; (j < vbv.size()) && ok; j++)
            {
                if (((BasicBitVector) vbv.get(j)).equals(bv))
                {
                    ok = false;
                }
            }

            if (ok)
            {
                //          System.out.println("add ");
                //mlist.put(vtmp);
                mlist.add(itmp);
                vbv.add((BasicBitVector) bv.clone());
            }
        }

        matches = mlist;

        return matches;
    }

    /**
     *  Gets the charge attribute of the JOESmartsPattern object
     *
     * @param  idx  Description of the Parameter
     * @return      The charge value
     */
    public int getQueryAtomCharge(int idx)
    {
        QueryAtom expr = queryPattern.getAtoms()[idx].getAtom();
        int size = 0;
        QueryAtom[] stack = new BasicQueryAtom[15];
        boolean lftest = true;
        size = 0;
        stack[size] = expr;

        for (; size >= 0; expr = stack[size])
        {
            switch (expr.getType())
            {
            case BasicQueryAtom.AE_LEAF:

                BasicQueryAtomValue leafAtomExpr = (BasicQueryAtomValue) expr;

                switch (leafAtomExpr.label)
                {
                case BasicQueryAtom.AL_NEGATIVE:
                    return (-1 * (int) leafAtomExpr.value);

                case BasicQueryAtom.AL_POSITIVE:
                    return ((int) leafAtomExpr.value);

                default:
                    lftest = true;
                }

                size--;

                break;

            case BasicQueryAtom.AE_OR:
            case BasicQueryAtom.AE_ANDHI:
            case BasicQueryAtom.AE_ANDLO:

                BasicQueryAtomBinary binAtomExpr = (BasicQueryAtomBinary) expr;

                if (stack[size + 1] == binAtomExpr.right)
                {
                    size--;
                }
                else if (stack[size + 1] == binAtomExpr.left)
                {
                    if (lftest)
                    {
                        size++;
                        stack[size] = binAtomExpr.right;
                    }
                    else
                    {
                        size--;
                    }
                }
                else
                {
                    size++;
                    stack[size] = binAtomExpr.left;
                }

                break;

            case BasicQueryAtom.AE_NOT:
                return (0);

            case BasicQueryAtom.AE_RECUR:
                return (0);
            }

            if (size < 0)
            {
                break;
            }
        }

        return (0);
    }

    /**
     *  Gets the atomicNum attribute of the JOESmartsPattern object
     *
     * @param  idx  Description of the Parameter
     * @return      The atomicNum value
     */
    public int getQueryAtomIndex(int idx)
    {
        QueryAtom expr = queryPattern.getAtoms()[idx].getAtom();
        int size = 0;
        QueryAtom[] stack = new BasicQueryAtom[15];
        boolean lftest = true;

        for (size = 0, stack[size] = expr; size >= 0; expr = stack[size])
        {
            switch (expr.getType())
            {
            case BasicQueryAtom.AE_LEAF:

                BasicQueryAtomValue leafAtomExpr = (BasicQueryAtomValue) expr;

                if (leafAtomExpr.label == BasicQueryAtom.AL_ELEM)
                {
                    return (leafAtomExpr.value);
                }

                lftest = true;
                size--;

                break;

            case BasicQueryAtom.AE_OR:
            case BasicQueryAtom.AE_ANDHI:
            case BasicQueryAtom.AE_ANDLO:

                BasicQueryAtomBinary binAtomExpr = (BasicQueryAtomBinary) expr;

                if (stack[size + 1] == binAtomExpr.right)
                {
                    size--;
                }
                else if (stack[size + 1] == binAtomExpr.left)
                {
                    if (lftest)
                    {
                        size++;
                        stack[size] = binAtomExpr.right;
                    }
                    else
                    {
                        size--;
                    }
                }
                else
                {
                    size++;
                    stack[size] = binAtomExpr.left;
                }

                break;

            case BasicQueryAtom.AE_NOT:
                return (0);

            case BasicQueryAtom.AE_RECUR:
                return (0);
            }
        }

        return (0);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int getQueryAtomsSize()
    {
        return ((queryPattern != null) ? queryPattern.getAtomsSize() : 0);
    }

    /**
     *  Gets the bond attribute of the JOESmartsPattern object
     *
     * @param  iii  Description of the Parameter
     * @param  idx  Description of the Parameter
     */
    public void getQueryBond(BasicIntIntInt iii, int idx)
    {
        // source
        iii.intPair.intValue1 = queryPattern.getBonds()[idx].getSource();

        // dest
        iii.intPair.intValue2 = queryPattern.getBonds()[idx].getDestination();

        // ord
        iii.intValue = SMARTSParser.getExprOrder(queryPattern.getBonds()[idx]
                .getBond());
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int getQueryBondsSize()
    {
        return ((queryPattern != null) ? queryPattern.getBondsSize() : 0);
    }

    /**
     *  Gets the sMARTS attribute of the JOESmartsPattern object
     *
     * @return    The sMARTS value
     */
    public String getSmarts()
    {
        return smarts;
    }

    /**
     *  Gets the vector binding of this smarts pattern for SMARTS atom with <tt>idx</tt>.
     * E.g.: O=CO[#1:1] where :1 means that the atom #1 (H atom) has the vector binding number 1.
     * This example can be used in <tt>JOEChemTransformation</tt> to delete this H atoms:<br>
     * TRANSFORM O=CO[#1:1] >> O=CO
     *
     * @param  idx  the SMARTS atom <tt>idx</tt>
     * @return     0 if no vector binding is was defined
     */
    public int getVectorBinding(int idx)
    {
        return (queryPattern.getAtoms()[idx].getVectorBinding());
    }

    /**
     *  Description of the Method
     *
     * @param  s  Description of the Parameter
     * @return    <tt>true</tt> if the initialisation was succesfull
     */
    public boolean init(String s)
    {
        if (s.length() == 0)
        {
            return true;
        }

        char[] _buffer = s.toCharArray();
        smartsParser.buffer = new char[_buffer.length + 1];
        System.arraycopy(_buffer, 0, smartsParser.buffer, 0, _buffer.length);
        queryPattern = smartsParser.parseSMARTSRecord(smartsParser.buffer, 0,
                s.length() - 1);
        smarts = s;

        // dump atom expression
        if (logger.isDebugEnabled())
        {
            logger.debug("Pattern:" + s + "\n" + this.toString());
        }

        return (queryPattern != null);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean isEmpty()
    {
        return (queryPattern == null);
    }

    /**
     *  Gets the valid attribute of the JOESmartsPattern object
     *
     * @return    The valid value
     */
    public boolean isValid()
    {
        return (queryPattern != null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return         <tt>true</tt> SMARTS matching was successfull and pattern occur
     */
    public boolean match(Molecule mol)
    {
        return match(mol, false);
    }

    /**
     *  Description of the Method
     *
     * @param  mol     Description of the Parameter
     * @param  single  Description of the Parameter
     * @return         <tt>true</tt> SMARTS matching was successfull and pattern occur
     */
    public boolean match(Molecule mol, boolean single)
    {
        smartsParser.rsCache.clear();

        return (smartsParser.match(mol, queryPattern, matches, single));
    }

    /**
     * @param  pr   of type <tt>IntInt</tt> -{@link java.util.Vector}
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean restrictedMatch(Molecule mol, List pr)
    {
        return restrictedMatch(mol, pr, false);
    }

    /**
     *  Description of the Method
     *
     * @param  mol   Description of the Parameter
     * @param  vres  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean restrictedMatch(Molecule mol, BasicBitVector vres)
    {
        return restrictedMatch(mol, vres, false);
    }

    /**
     * @param  pr      of type <tt>IntInt</tt> -{@link java.util.Vector}
     * @param  mol     Description of the Parameter
     * @param  single  Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean restrictedMatch(Molecule mol, List pr, boolean single)
    {
        boolean ok;
        List<int[]> mlist = new Vector<int[]>();

        // of type int[]
        smartsParser.rsCache.clear();
        smartsParser.match(mol, queryPattern, mlist);
        matches.clear();

        if (mlist.size() == 0)
        {
            return (false);
        }

        //Vector vtmp;
        int[] itmp;
        BasicIntInt ii;

        for (int i = 0; i < mlist.size(); i++)
        {
            //vtmp = (Vector) mlist.get(i);
            itmp = (int[]) mlist.get(i);
            ok = true;

            for (int j = 0; (j < pr.size()) && ok; j++)
            {
                ii = (BasicIntInt) pr.get(j);

                //itmp = (int[]) vtmp.get(ii.i1);
                //if ( itmp[0] != ii.i2) ok = false;
                if (itmp[ii.intValue1] != ii.intValue2)
                {
                    ok = false;
                }
            }

            if (ok)
            {
                matches.add(itmp);
            }

            if (single && (matches.size() != 0))
            {
                return (true);
            }
        }

        return ((matches.size() == 0) ? false : true);
    }

    /**
     *  Description of the Method
     *
     * @param  mol     Description of the Parameter
     * @param  vres    Description of the Parameter
     * @param  single  Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean restrictedMatch(Molecule mol, BasicBitVector vres,
        boolean single)
    {
        boolean ok;
        List<int[]> mlist = new Vector<int[]>();

        // of type int[]
        smartsParser.rsCache.clear();
        smartsParser.match(mol, queryPattern, mlist);

        matches.clear();

        if (mlist.size() == 0)
        {
            return (false);
        }

        //Vector vtmp;
        int[] itmp;

        for (int i = 0; i < mlist.size(); i++)
        {
            ok = true;

            //vtmp = (Vector)mlist.get(i);
            itmp = (int[]) mlist.get(i);

            for (int j = 0; j < itmp.length; j++)
            {
                //itmp = (int[]) vtmp.get(j);
                if (!vres.get(itmp[j]))
                {
                    ok = false;

                    break;
                }
            }

            if (!ok)
            {
                continue;
            }

            //_mlist.put( vtmp);
            matches.add(itmp);

            if (single && (matches.size() != 0))
            {
                return (true);
            }
        }

        return ((matches.size() == 0) ? false : true);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if (queryPattern != null)
        {
            sb.append(queryPattern.toString());
        }

        return sb.toString();
    }

    /**
     *  Description of the Method
     *
     * @param  ofs  Description of the Parameter
     */
    public void writeMatches(OutputStream ofs)
    {
        PrintStream ps = new PrintStream(ofs);

        int[] itmp;

        for (int i = 0; i < matches.size(); i++)
        {
            itmp = matches.get(i);

            for (int j = 0; j < itmp.length; j++)
            {
                //itmp = (int[]) vtmp.get(j);
                ps.print(itmp[j]);
                ps.print(' ');
            }

            ps.println("");
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
