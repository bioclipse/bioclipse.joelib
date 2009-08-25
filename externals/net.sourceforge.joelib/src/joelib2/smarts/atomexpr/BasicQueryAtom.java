///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicQueryAtom.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
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
package joelib2.smarts.atomexpr;

import cformat.PrintfStream;

import joelib2.smarts.BasicQueryPattern;
import joelib2.smarts.QueryPattern;
import joelib2.smarts.SMARTSParser;

import joelib2.smarts.types.BasicSMARTSElement;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Category;


/**
 * Atom expression in SMARTS substructure search.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:39 $
 */
public class BasicQueryAtom implements java.io.Serializable, QueryAtom
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(BasicQueryAtom.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int type;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomExpr object
     */
    public BasicQueryAtom()
    {
    }

    /**
     *  Constructor for the AtomExpr object
     *
     * @param  dataType  Description of the Parameter
     */
    public BasicQueryAtom(int _type)
    {
        type = _type;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  type  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static BasicQueryAtom allocAtomExpr(int type)
    {
        BasicQueryAtom result = null;

        // = new AtomExpr();
        //    result = allocAtomExpr();
        //    result.type = expr.type;
        //    result = allocAtomExpr(expr.type);
        switch (type)
        {
        case (AE_ANDHI):
        case (AE_ANDLO):
        case (AE_OR):
            result = new BasicQueryAtomBinary(type);

            break;

        case (AE_NOT):
            result = new BasicQueryAtomMono(type);

            break;

        case (AE_RECUR):
            result = new BasicQueryAtomPattern(type);

            break;

        case (AE_LEAF):
            result = new BasicQueryAtomValue(type);

            break;
        }

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  left  Description of the Parameter
     * @param  right  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryAtom andAtomExpr(QueryAtom left, QueryAtom right)
    {
        QueryAtom expr;
        int order;
        BasicQueryAtomValue lAELft = (BasicQueryAtomValue) left;
        BasicQueryAtomValue lAERgt = (BasicQueryAtomValue) right;

        /* Identities  */
        if (equalAtomExpr(left, right))
        {
            freeAtomExpr(right);

            return left;
        }

        if ((left.getType() == AE_LEAF) && (lAELft.label == AL_CONST))
        {
            if (lAELft.value != 0)
            {
                freeAtomExpr(left);

                return right;
            }
            else
            {
                freeAtomExpr(right);

                return left;
            }
        }

        if ((right.getType() == AE_LEAF) && (lAERgt.label == AL_CONST))
        {
            if (lAERgt.value != 0)
            {
                freeAtomExpr(right);

                return left;
            }
            else
            {
                freeAtomExpr(left);

                return right;
            }
        }

        BasicQueryAtomBinary bAELft = (BasicQueryAtomBinary) left;
        BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) right;

        /*  Distributivity   */
        if (left.getType() == AE_OR)
        {
            expr = copyAtomExpr(right);
            expr = orAtomExpr(andAtomExpr(expr, bAELft.left),
                    andAtomExpr(right, bAELft.right));

            bAELft.left = null;
            bAELft.right = null;
            freeAtomExpr(left);

            return expr;
        }

        if (right.getType() == AE_OR)
        {
            expr = copyAtomExpr(left);
            expr = orAtomExpr(andAtomExpr(expr, bAERgt.left),
                    andAtomExpr(left, bAERgt.right));
            bAERgt.left = null;
            bAERgt.right = null;
            freeAtomExpr(right);

            return expr;
        }

        /* Recursion  */
        if ((right.getType() == AE_RECUR) && (left.getType() != AE_RECUR))
        {
            return constrainRecursion(right, left);
        }

        if ((right.getType() != AE_RECUR) && (left.getType() == AE_RECUR))
        {
            return constrainRecursion(left, right);
        }

        order = orderAtomExpr(left, right);

        if (order > 0)
        {
            expr = left;
            left = right;
            right = expr;
        }

        bAELft = (BasicQueryAtomBinary) left;
        bAERgt = (BasicQueryAtomBinary) right;

        if (left.getType() == AE_ANDHI)
        {
            expr = andAtomExpr(bAELft.right, right);
            expr = andAtomExpr(bAELft.left, expr);
            bAELft.left = null;
            bAELft.right = null;
            freeAtomExpr(left);

            return expr;
        }

        if (right.getType() == AE_ANDHI)
        {
            if (orderAtomExpr(left, bAERgt.left) > 0)
            {
                expr = andAtomExpr(left, bAERgt.right);
                expr = andAtomExpr(bAERgt.left, expr);
                bAERgt.left = null;
                bAERgt.right = null;
                freeAtomExpr(right);

                return expr;
            }

            if (equalAtomExpr(left, bAERgt.left))
            {
                freeAtomExpr(left);

                return right;
            }
        }

        return andAtomExprLeaf(left, right);
    }

    /**
     *  Description of the Method
     *
     * @param  left  Description of the Parameter
     * @param  right  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryAtom andAtomExprLeaf(QueryAtom left, QueryAtom right)
    {
        if (atomExprConflict(left, right))
        {
            freeAtomExpr(left);
            freeAtomExpr(right);

            return buildAtomLeaf(AL_CONST, SMARTSParser.IFALSE);
        }

        if (atomExprImplied(left, right))
        {
            freeAtomExpr(left);

            return right;
        }

        right = atomExprImplies(left, right);

        if (right == null)
        {
            return left;
        }

        return buildAtomBin(AE_ANDHI, left, right);
    }

    /**
     *  Description of the Method
     *
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static boolean atomExprConflict(QueryAtom lft, QueryAtom rgt)
    {
        BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) rgt;

        while (rgt.getType() == AE_ANDHI)
        {
            if (atomLeafConflict(lft, bAERgt.left))
            {
                return true;
            }

            rgt = bAERgt.right;
        }

        return atomLeafConflict(lft, rgt);
    }

    /* return EXPR(rgt) => LEAF(lft);  */

    /**
     *  Description of the Method
     *
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static boolean atomExprImplied(QueryAtom lft, QueryAtom rgt)
    {
        BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) rgt;

        while (rgt.getType() == AE_ANDHI)
        {
            if (atomLeafImplies(bAERgt.left, lft))
            {
                return true;
            }

            rgt = bAERgt.right;
        }

        return atomLeafImplies(rgt, lft);
    }

    /* remove implied nodes from EXPR(rgt)  */

    /**
     *  Description of the Method
     *
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryAtom atomExprImplies(QueryAtom lft, QueryAtom rgt)
    {
        QueryAtom tmp;

        //BinAtomExpr  bAELft  = (BinAtomExpr) lft;
        BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) rgt;

        if (rgt.getType() != AE_ANDHI)
        {
            if (atomLeafImplies(lft, rgt))
            {
                freeAtomExpr(rgt);

                return null;
            }

            return rgt;
        }

        tmp = atomExprImplies(lft, bAERgt.right);

        if (tmp != null)
        {
            if (atomLeafImplies(lft, bAERgt.left))
            {
                bAERgt.right = null;
                freeAtomExpr(rgt);

                return tmp;
            }

            bAERgt.right = tmp;

            return rgt;
        }
        else
        {
            bAERgt.right = null;

            if (atomLeafImplies(lft, bAERgt.left))
            {
                freeAtomExpr(rgt);

                return null;
            }

            tmp = bAERgt.left;
            bAERgt.left = null;
            freeAtomExpr(rgt);

            return tmp;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  left  Description of the Parameter
     * @param  right  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static boolean atomLeafConflict(QueryAtom left, QueryAtom right)
    {
        QueryAtom tmp;
        BasicQueryAtomValue lAELft = null;
        BasicQueryAtomValue lAERgt = null;

        if ((left.getType() == AE_LEAF) && (right.getType() == AE_LEAF))
        {
            lAELft = (BasicQueryAtomValue) left;
            lAERgt = (BasicQueryAtomValue) right;

            if (lAELft.label == lAERgt.label)
            {
                if (isNegatingAtomLeaf(left))
                {
                    if (lAELft.value == 0)
                    {
                        return lAERgt.value != 0;
                    }
                    else if (lAELft.value == -1)
                    {
                        return lAERgt.value == 0;
                    }

                    if (lAERgt.value == 0)
                    {
                        return lAELft.value != 0;
                    }
                    else if (lAERgt.value == -1)
                    {
                        return lAELft.value == 0;
                    }
                }

                return lAELft.value != lAERgt.value;
            }

            if (lAELft.label > lAERgt.label)
            {
                tmp = left;
                left = right;
                right = tmp;
                lAELft = (BasicQueryAtomValue) left;
                lAERgt = (BasicQueryAtomValue) right;
            }

            /* Aromaticity . Ring  */
            if ((lAELft.label == AL_AROM) && (lAERgt.label == AL_RINGS))
            {
                return ((lAELft.value != 0) && (lAERgt.value == 0));
            }

            /* Positive charge ~ Negative charge  */
            if ((lAELft.label == AL_NEGATIVE) && (lAERgt.label == AL_POSITIVE))
            {
                return ((lAELft.value != 0) || (lAERgt.value != 0));
            }

            /* Total hcount >= Implicit hcount  */
            if ((lAELft.label == AL_HCOUNT) && (lAERgt.label == AL_IMPLICIT))
            {
                return (lAELft.value < lAERgt.value);
            }
        }

        if ((left.getType() == AE_LEAF) && (right.getType() == AE_NOT))
        {
            right = ((BasicQueryAtomMono) right).next;
            lAERgt = (BasicQueryAtomValue) right;

            if ((lAELft.label == AL_NEGATIVE) && (lAERgt.label == AL_POSITIVE))
            {
                return ((lAELft.value == 0) && (lAERgt.value == 0));
            }

            if ((lAELft.label == AL_POSITIVE) && (lAERgt.label == AL_NEGATIVE))
            {
                return ((lAELft.value == 0) && (lAERgt.value == 0));
            }

            return false;
        }

        if ((left.getType() == AE_NOT) && (right.getType() == AE_LEAF))
        {
            left = ((BasicQueryAtomMono) left).next;
            lAELft = (BasicQueryAtomValue) left;

            if ((lAELft.label == AL_NEGATIVE) && (lAERgt.label == AL_POSITIVE))
            {
                return ((lAELft.value == 0) && (lAERgt.value == 0));
            }

            if ((lAELft.label == AL_POSITIVE) && (lAERgt.label == AL_NEGATIVE))
            {
                return ((lAELft.value == 0) && (lAERgt.value == 0));
            }

            return false;
        }

        return false;
    }

    /* return LEAF(lft) => LEAF(rgt);  */

    /**
     *  Description of the Method
     *
     * @param  left  Description of the Parameter
     * @param  right  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static boolean atomLeafImplies(QueryAtom left, QueryAtom right)
    {
        BasicQueryAtomValue lAELft = (BasicQueryAtomValue) left;
        BasicQueryAtomValue lAERgt = (BasicQueryAtomValue) right;

        if ((left.getType() == AE_LEAF) && (right.getType() == AE_LEAF))
        {
            /* Implied Ring Membership  */
            if ((lAERgt.label == AL_RINGS) && (lAERgt.value == -1))
            {
                if (lAELft.label == AL_AROM)
                {
                    return lAELft.value != 0;
                }

                if (lAELft.label == AL_RINGS)
                {
                    return lAELft.value > 0;
                }

                if (lAELft.label == AL_SIZE)
                {
                    return lAELft.value > 0;
                }
            }

            /* Positive charge ~ Negative charge  */
            if ((lAELft.label == AL_POSITIVE) && (lAERgt.label == AL_NEGATIVE))
            {
                return (lAELft.value == 0) && (lAERgt.value == 0);
            }

            return false;
        }

        if ((left.getType() == AE_LEAF) && (right.getType() == AE_NOT))
        {
            right = ((BasicQueryAtomMono) right).next;
            lAERgt = (BasicQueryAtomValue) right;

            if (lAELft.label == lAERgt.label)
            {
                return lAELft.value != lAERgt.value;
            }

            if ((lAELft.label == AL_POSITIVE) && (lAERgt.label == AL_NEGATIVE))
            {
                return true;
            }

            if ((lAELft.label == AL_NEGATIVE) && (lAERgt.label == AL_POSITIVE))
            {
                return true;
            }

            return false;
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  op   Description of the Parameter
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryAtom buildAtomBin(int op, QueryAtom lft, QueryAtom rgt)
    {
        QueryAtom result;
        result = allocAtomExpr(op);

        BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) result;

        //result.bin.type = op;
        bAE.left = lft;
        bAE.right = rgt;

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  prop  Description of the Parameter
     * @param  val   Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryAtom buildAtomLeaf(int prop, int val)
    {
        QueryAtom result;
        result = allocAtomExpr(AE_LEAF);

        BasicQueryAtomValue lAE = (BasicQueryAtomValue) result;

        //result.leaf.type = AE_LEAF;
        lAE.label = prop;
        lAE.value = val;

        return lAE;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryAtom buildAtomNot(QueryAtom expr)
    {
        QueryAtom result;
        result = allocAtomExpr(AE_NOT);

        BasicQueryAtomMono mAE = (BasicQueryAtomMono) result;

        //result.type = AE_NOT;
        mAE.next = expr;

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  pat  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryAtom buildAtomRecurs(QueryPattern pat)
    {
        BasicQueryAtom result;

        result = allocAtomExpr(AE_RECUR);

        BasicQueryAtomPattern rAE = (BasicQueryAtomPattern) result;

        //result.recur.type = AE_RECUR;
        rAE.recurrent = pat;

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  recur  Description of the Parameter
     * @param  expr   Description of the Parameter
     * @return        Description of the Return Value
     */
    public static QueryAtom constrainRecursion(QueryAtom recur, QueryAtom expr)
    {
        QueryAtom head;
        QueryPattern pattern;
        BasicQueryAtomPattern rAE = (BasicQueryAtomPattern) recur;

        //RecurAtomExpr rAE2=(RecurAtomExpr)rAE1.recur;
        pattern = (BasicQueryPattern) rAE.recurrent;
        head = andAtomExpr(pattern.getAtoms()[0].getAtom(), expr);
        pattern.getAtoms()[0].setAtom(head);

        if (isInvalidAtom(head))
        {
            BasicQueryPattern.freePattern(pattern);

            return buildAtomLeaf(AL_CONST, 0);
        }

        return recur;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryAtom copyAtomExpr(QueryAtom expr)
    {
        BasicQueryAtom result;

        //    result = allocAtomExpr();
        //    result.type = expr.type;
        result = allocAtomExpr(expr.getType());

        switch (expr.getType())
        {
        case (AE_ANDHI):
        case (AE_ANDLO):
        case (AE_OR):

            BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
            BasicQueryAtomBinary binResult = (BasicQueryAtomBinary) result;
            binResult.left = copyAtomExpr(bAE.left);
            binResult.right = copyAtomExpr(bAE.right);

            break;

        case (AE_NOT):

            BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;
            BasicQueryAtomMono monResult = (BasicQueryAtomMono) result;
            monResult.next = copyAtomExpr(mAE.next);

            break;

        case (AE_RECUR):

            //RecurAtomExpr rAE     = (RecurAtomExpr) expr;
            QueryAtomPattern recurResult = (BasicQueryAtomPattern) result;
            recurResult.setRecurrent(BasicQueryPattern.copyPattern(
                    (BasicQueryPattern) recurResult.getRecurrent()));

            break;

        case (AE_LEAF):

            BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;
            BasicQueryAtomValue leafResult = (BasicQueryAtomValue) result;
            leafResult.label = lAE.label;
            leafResult.value = lAE.value;

            break;
        }

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     * @return       Description of the Return Value
     */
    public static boolean displayAndAromElem(QueryAtom expr, OutputStream os)
    {
        PrintfStream fp = new PrintfStream(os);

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        QueryAtom lft;
        QueryAtom rgt;
        BasicSMARTSElement elem = null;

        BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
        lft = bAE.left;

        if ((lft.getType() != AE_LEAF))
        {
            return false;
        }

        BasicQueryAtomValue lAELft = (BasicQueryAtomValue) lft;

        if ((lAELft.label != AL_AROM))
        {
            return false;
        }

        rgt = bAE.right;

        if ((rgt.getType() == AE_ANDHI) || (rgt.getType() == AE_ANDLO))
        {
            BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) rgt;
            rgt = bAERgt.left;
        }

        if ((rgt.getType() != AE_LEAF))
        {
            return false;
        }

        BasicQueryAtomValue lAERgt = (BasicQueryAtomValue) rgt;

        if ((lAERgt.label != AL_ELEM))
        {
            return false;
        }

        //if (UNUSED)
        //{
        //  elem = &Elem[rgt.leaf.value];
        //  /* Should never happen! */
        //  if( !elem.aromflag ) return false;
        //}
        if (elem != null)
        {
            if (lAELft.value != 0)
            {
                fp.print(Character.toLowerCase(elem.symbol.charAt(0)));
            }
            else
            {
                fp.print(elem.symbol.charAt(0));
            }

            if (elem.symbol.charAt(1) != ' ')
            {
                fp.print(elem.symbol.charAt(1));
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void displayAtomExpr(QueryAtom expr, OutputStream os)
    {
        PrintfStream fp = new PrintfStream(os);

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        QueryAtom next;
        BasicQueryAtomBinary bAE = null;

        switch (expr.getType())
        {
        case (AE_LEAF):
            displayAtomLeaf(expr, fp);

            break;

        case (AE_RECUR):

            BasicQueryAtomPattern rAE = (BasicQueryAtomPattern) expr;
            fp.print("$(");
            SMARTSParser.generateSMARTSString((BasicQueryPattern) rAE.recurrent,
                fp);
            fp.print(')');

            break;

        case (AE_NOT):

            BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;
            fp.print('!');
            displayAtomExpr(mAE.next, fp);

            break;

        case (AE_ANDHI):
            bAE = (BasicQueryAtomBinary) expr;

            if (displayAndAromElem(expr, fp))
            {
                next = bAE.right;

                if (next.getType() == AE_ANDHI)
                {
                    BasicQueryAtomBinary bAENext = (BasicQueryAtomBinary) next;

                    if (needExplicitAnd2(expr))
                    {
                        fp.print('&');
                    }

                    displayAtomExpr(bAENext.right, fp);
                }
            }
            else
            {
                displayAtomExpr(bAE.left, fp);

                if (needExplicitAnd(expr))
                {
                    fp.print('&');
                }

                displayAtomExpr(bAE.right, fp);
            }

            break;

        case (AE_OR):
            bAE = (BasicQueryAtomBinary) expr;

            if (displayOrAromElem(expr, fp))
            {
                break;
            }

            displayAtomExpr(bAE.left, fp);
            fp.print(',');
            displayAtomExpr(bAE.right, fp);

            break;

        /* Should never happen!  */
        case (AE_ANDLO):
            bAE = (BasicQueryAtomBinary) expr;
            displayAtomExpr(bAE.left, fp);
            fp.print(';');
            displayAtomExpr(bAE.right, fp);

            break;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void displayAtomLeaf(QueryAtom expr, OutputStream os)
    {
        PrintfStream fp = new PrintfStream(os);

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

        switch (lAE.label)
        {
        case (AL_AROM):

            if (lAE.value != 0)
            {
                fp.print('a');
            }
            else
            {
                fp.print('A');
            }

            break;

        case (AL_CONNECT):
            fp.printf("X%d", lAE.value);

            break;

        case (AL_CONST):

            if (lAE.value != 0)
            {
                fp.print("!*");
            }
            else
            {
                fp.print('*');
            }

            break;

        case (AL_DEGREE):
            fp.printf("D%d", lAE.value);

            break;

        case (AL_ELEM):

            //          if(lAE.value!=1)
            //          {
            //            fp.print( JOEElementTable.instance().getSymbol(lAE.value) );
            //          }
            //          else
            //          {
            //            fp.printf("#%d",lAE.value);
            //          }
            //if(UNUSED)
            //{
            //            if( !Elem[lAE.value].aromflag && (lAE.value!=1) )
            //            {
            //              ptr = Elem[lAE.value].symbol;
            //              fp.print(ptr[ptrIndex]);
            //              if( ptr[ptrIndex+1] != ' ' )
            //              fp.print(ptr[1]);
            //            }
            //            else fp.printf("#%d",lAE.value);
            //}
            break;

        case (AL_HCOUNT):
            fp.print('H');

            if (lAE.value != 1)
            {
                fp.printf("%d", lAE.value);
            }

            break;

        case (AL_HEAVY_CONNECT):
            fp.printf("Q%d", lAE.value);

            break;

        case (AL_IMPLICIT):
            fp.print('h');

            if (lAE.value != 1)
            {
                fp.printf("%d", lAE.value);
            }

            break;

        case (AL_MASS):
            fp.printf("%d", lAE.value);

            break;

        case (AL_NEGATIVE):
            fp.print('-');

            if (lAE.value != 1)
            {
                fp.printf("%d", lAE.value);
            }

            break;

        case (AL_POSITIVE):
            fp.print('+');

            if (lAE.value != 1)
            {
                fp.printf("%d", lAE.value);
            }

            break;

        case (AL_RINGS):
            fp.print('R');

            if (lAE.value != -1)
            {
                fp.printf("%d", lAE.value);
            }

            break;

        case (AL_SIZE):
            fp.print('r');
            fp.printf("%d", lAE.value);

            break;

        case (AL_VALENCE):
            fp.printf("v%d", lAE.value);

            break;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     * @return       Description of the Return Value
     */
    public static boolean displayOrAromElem(QueryAtom expr, OutputStream os)
    {
        PrintfStream fp = new PrintfStream(os);

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        QueryAtom lft;
        QueryAtom rgt;
        QueryAtom arg;

        if (expr.getType() != AE_OR)
        {
            return false;
        }

        BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
        lft = bAE.left;

        if ((lft.getType() != AE_LEAF))
        {
            return false;
        }

        BasicQueryAtomValue lAELft = (BasicQueryAtomValue) lft;

        if ((lAELft.label != AL_AROM))
        {
            return false;
        }

        rgt = bAE.getRight();

        if (rgt.getType() != AE_NOT)
        {
            return false;
        }

        BasicQueryAtomMono mAERgt = (BasicQueryAtomMono) rgt;

        arg = mAERgt.getNext();

        if ((arg.getType() != AE_LEAF))
        {
            return false;
        }

        BasicQueryAtomValue lAEArg = (BasicQueryAtomValue) arg;

        if ((lAEArg.label != AL_ELEM))
        {
            return false;
        }

        //if (UNUSED)
        //{
        //  elem = &Elem[arg.leaf.value];
        //  /* Should never happen! */
        //  if( !elem.aromflag ) return false;
        //  fp.print('!');
        //  if( !lft.leaf.value )
        //  {
        //    fp.print(tolower(elem.symbol[0]));
        //  }
        //  else fp.print(elem.symbol[0]);
        //  if( elem.symbol[1] != ' ' ) fp.print(elem.symbol[1]);
        //}
        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     * @return       Description of the Return Value
     */
    public static boolean displaySimpleAtomExpr(QueryAtom expr, OutputStream os)
    {
        PrintfStream fp = new PrintfStream(os);

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        if (OLDCODE)
        {
            QueryAtom lft;
            QueryAtom rgt;
            BasicSMARTSElement elem = null;

            if ((expr.getType() != AE_ANDHI) && (expr.getType() != AE_ANDLO))
            {
                return false;
            }

            BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
            lft = bAE.left;
            rgt = bAE.right;

            BasicQueryAtomValue lAELft = (BasicQueryAtomValue) lft;
            BasicQueryAtomValue lAERgt = (BasicQueryAtomValue) rgt;

            if ((lft.getType() != AE_LEAF) || (lAELft.label != AL_AROM) ||
                    (rgt.getType() != AE_LEAF) || (lAERgt.label != AL_ELEM))
            {
                return false;
            }

            //if (UNUSED)
            //{
            //  elem = &Elem[lBERgt.value];
            //  if( !elem.aromflag || !elem.organic ) return false;
            //}
            if (elem != null)
            {
                if (lAELft.value != 0)
                {
                    fp.print(Character.toLowerCase(elem.symbol.charAt(0)));
                }
                else
                {
                    fp.print(elem.symbol.charAt(0));
                }
            }

            return true;
        }
        else
        {
            //    throw new Exception(ParseSmart.getName()+".displaySimpleAtomExpr not defined");
            logger.error("ParseSmart.displaySimpleAtomExpr not defined.");
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void displaySMARTSAtom(QueryAtom expr, OutputStream os)
    {
        PrintfStream fp = new PrintfStream(os);

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        //if (UNUSED)
        //{
        //  Element elem;
        //   if( expr.type == AE_LEAF )
        //   {
        //     if( expr.leaf.prop == AL_ELEM )
        //     {
        //       elem = &Elem[expr.leaf.value];
        //       if( elem.organic && !elem.aromflag )
        //       {
        //         fp.print(elem.symbol[0]);
        //         if( elem.symbol[1] != ' ' ) fp.print(elem.symbol[1]);
        //         return;
        //       }
        //     }
        //     else if( expr.leaf.prop == AL_CONST )
        //     {
        //      if( expr.leaf.value )
        //       {
        //         fp.print('*');
        //         return;
        //       }
        //     }
        //   }
        //   else if( displaySimpleAtomExpr(expr,fp) ) return;
        //   fp.print('[');
        //   displayAtomExpr(expr);
        //   fp.print(']');
        // }
    }

    /*=============================== */
    /*  Dump SMARTS Atom Expression   */
    /*=============================== */

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void dumpAtomExpr(QueryAtom expr, OutputStream os)
    {
        PrintfStream fp;
        BasicQueryAtomBinary bAE = null;

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        if (expr != null)
        {
            switch (expr.getType())
            {
            case AE_LEAF:

                BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;
                fp.print("LEAF(");

                switch (lAE.label)
                {
                case AL_CONST:
                    fp.print("CONST");

                    break;

                case AL_MASS:
                    fp.print("MASS");

                    break;

                case AL_AROM:
                    fp.print("AROM");

                    break;

                case AL_ELEM:
                    fp.print("ELEM");

                    break;

                case AL_HCOUNT:
                    fp.print("HCOUNT");

                    break;

                case AL_NEGATIVE:
                    fp.print("NEGATIVE");

                    break;

                case AL_POSITIVE:
                    fp.print("POSITIVE");

                    break;

                case AL_CONNECT:
                    fp.print("CONNECT");

                    break;

                case AL_HEAVY_CONNECT:
                    fp.print("HEAVY_CONNECT");

                    break;

                case AL_GROUP:
                    fp.print("GROUP");

                    break;

                case AL_DEGREE:
                    fp.print("DEGREE");

                    break;

                case AL_IMPLICIT:
                    fp.print("IMPLICIT");

                    break;

                case AL_RINGS:
                    fp.print("RINGS");

                    break;

                case AL_SIZE:
                    fp.print("SIZE");

                    break;

                case AL_VALENCE:
                    fp.print("VALENCE");

                    break;

                case AL_HYB:
                    fp.print("HYBRIDISATION");

                    break;

                case AL_ELECTRONEGATIVE:
                    fp.print("ELECTRONEGATIVE");

                    break;

                default:
                    fp.print("???");

                    break;
                }

                fp.printf(",%d)", lAE.value);

                break;

            case AE_RECUR:

                //            fp.print("RECUR(...)");
                fp.println("RECUR(");

                BasicQueryAtomPattern recur = (BasicQueryAtomPattern) expr;
                fp.print(recur.recurrent.toString("  "));
                fp.print(")");

                break;

            case AE_NOT:

                BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;
                fp.print("NOT(");
                dumpAtomExpr(mAE.next, fp);
                fp.print(')');

                break;

            case AE_ANDHI:
                bAE = (BasicQueryAtomBinary) expr;
                fp.print("ANDHI(");
                dumpAtomExpr(bAE.left, fp);
                fp.print(',');
                dumpAtomExpr(bAE.right, fp);
                fp.print(')');

                break;

            case AE_OR:
                bAE = (BasicQueryAtomBinary) expr;
                fp.print("OR(");
                dumpAtomExpr(bAE.left, fp);
                fp.print(',');
                dumpAtomExpr(bAE.right, fp);
                fp.print(')');

                break;

            case AE_ANDLO:
                bAE = (BasicQueryAtomBinary) expr;
                fp.print("ANDLO(");
                dumpAtomExpr(bAE.left, fp);
                fp.print(',');
                dumpAtomExpr(bAE.right, fp);
                fp.print(')');

                break;

            default:
                fp.print("???(...)");

                break;
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static boolean equalAtomExpr(QueryAtom lft, QueryAtom rgt)
    {
        if (lft.getType() != rgt.getType())
        {
            return false;
        }

        if (lft.getType() == AE_LEAF)
        {
            BasicQueryAtomValue lAELft = (BasicQueryAtomValue) lft;
            BasicQueryAtomValue lAERgt = (BasicQueryAtomValue) rgt;

            return ((lAELft.label == lAERgt.label) &&
                    (lAELft.value == lAERgt.value));
        }
        else if (lft.getType() == AE_NOT)
        {
            BasicQueryAtomMono mAELft = (BasicQueryAtomMono) lft;
            BasicQueryAtomMono mAERgt = (BasicQueryAtomMono) rgt;

            return equalAtomExpr(mAELft.next, mAERgt.next);
        }
        else if (lft.getType() == AE_RECUR)
        {
            return false;
        }

        BasicQueryAtomBinary bAELft = (BasicQueryAtomBinary) lft;
        BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) rgt;

        return equalAtomExpr(bAELft.left, bAERgt.left) &&
            equalAtomExpr(bAELft.right, bAERgt.right);
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     */
    public static void freeAtomExpr(QueryAtom expr)
    {
        if (expr != null)
        {
            switch (expr.getType())
            {
            case (AE_ANDHI):
            case (AE_ANDLO):
            case (AE_OR):

                BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
                freeAtomExpr(bAE.left);
                freeAtomExpr(bAE.right);

                break;

            case (AE_NOT):

                BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;
                freeAtomExpr(mAE.next);

                break;

            case (AE_RECUR):

                BasicQueryAtomPattern rAE = (BasicQueryAtomPattern) expr;
                BasicQueryPattern.freePattern((BasicQueryPattern) rAE.recurrent);

                break;
            }

            if (expr != null)
            {
                expr = null;
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  elem  Description of the Parameter
     * @param  flag  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryAtom generateAromElem(int elem, int flag)
    {
        QueryAtom expr1;
        QueryAtom expr2;

        expr1 = buildAtomLeaf(AL_AROM, flag);
        expr2 = buildAtomLeaf(AL_ELEM, elem);

        return buildAtomBin(AE_ANDHI, expr1, expr2);
    }

    /**
     *  Description of the Method
     *
     * @param  elem  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryAtom generateElement(int elem)
    {
        return buildAtomLeaf(AL_ELEM, elem);
    }

    /**
     *  Gets the booleanAtomLeaf attribute of the ParseSmart class
     *
     * @param  expr  Description of the Parameter
     * @return       The booleanAtomLeaf value
     */
    public static boolean isBooleanAtomLeaf(QueryAtom expr)
    {
        BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

        return (lAE.label == AL_AROM) || (lAE.label == AL_CONST);
    }

    /**
     *  Gets the invalidAtom attribute of the ParseSmart class
     *
     * @param  expr  Description of the Parameter
     * @return       The invalidAtom value
     */
    public static boolean isInvalidAtom(QueryAtom expr)
    {
        if (expr == null)
        {
            return true;
        }

        //  return( (expr.type==AE_LEAF) &&
        //        (expr.leaf.prop==AL_CONST) && expr.leaf.value==0 );
        if (expr.getType() == AE_LEAF)
        {
            BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

            return ((lAE.label == AL_CONST) && (lAE.value == 0));
        }

        return false;
    }

    /**
     *  Gets the negatingAtomLeaf attribute of the ParseSmart class
     *
     * @param  expr  Description of the Parameter
     * @return       The negatingAtomLeaf value
     */
    public static boolean isNegatingAtomLeaf(QueryAtom expr)
    {
        BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

        return (lAE.label == AL_RINGS);
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static boolean needExplicitAnd(QueryAtom expr)
    {
        QueryAtom rgt;
        QueryAtom lft;

        BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
        lft = bAE.left;
        rgt = bAE.right;

        if (rgt.getType() == AE_ANDHI)
        {
            rgt = bAE.left;
        }

        if ((lft.getType() == AE_LEAF) && (rgt.getType() == AE_LEAF))
        {
            BasicQueryAtomValue lAELft = (BasicQueryAtomValue) lft;
            BasicQueryAtomValue lAERgt = (BasicQueryAtomValue) rgt;

            if ((lAELft.label == AL_ELEM) && (lAERgt.label == AL_SIZE))
            {
                return (lAELft.value == 5);
            }
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static boolean needExplicitAnd2(QueryAtom expr)
    {
        QueryAtom arom;
        QueryAtom elem;
        QueryAtom next;

        BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
        arom = bAE.left;
        expr = bAE.right;
        elem = bAE.left;
        next = bAE.right;

        BasicQueryAtomBinary bAENext = (BasicQueryAtomBinary) next;

        if (next.getType() == AE_ANDHI)
        {
            next = bAENext.left;
        }

        if ((next.getType() == AE_LEAF))
        {
            BasicQueryAtomValue lAENext = (BasicQueryAtomValue) next;

            if (lAENext.label == AL_SIZE)
            {
                BasicQueryAtomValue lAEArom = (BasicQueryAtomValue) arom;
                BasicQueryAtomValue lAEElem = (BasicQueryAtomValue) elem;

                if ((lAEArom.value == 0) && (lAEElem.value == 6))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryAtom notAtomExpr(QueryAtom expr)
    {
        QueryAtom result;
        QueryAtom lft;
        QueryAtom rgt;

        if (expr.getType() == AE_LEAF)
        {
            BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

            if (isBooleanAtomLeaf(expr))
            {
                lAE.value = ((lAE.value == 0) ? 1 : 0);

                return expr;
            }
            else if (isNegatingAtomLeaf(expr))
            {
                if (lAE.value == -1)
                {
                    lAE.value = 0;

                    return expr;
                }
                else if (lAE.value == 0)
                {
                    lAE.value = -1;

                    return expr;
                }
            }
        }
        else if (expr.getType() == AE_NOT)
        {
            BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;
            result = mAE.next;
            mAE.next = null;
            freeAtomExpr(expr);

            return result;
        }
        else if ((expr.getType() == AE_ANDHI) || (expr.getType() == AE_ANDLO))
        {
            BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
            lft = notAtomExpr(bAE.left);
            rgt = notAtomExpr(bAE.right);
            bAE.left = null;
            bAE.right = null;
            freeAtomExpr(expr);

            return orAtomExpr(lft, rgt);
        }
        else if (expr.getType() == AE_OR)
        {
            BasicQueryAtomBinary bAE = (BasicQueryAtomBinary) expr;
            lft = notAtomExpr(bAE.left);
            rgt = notAtomExpr(bAE.right);
            bAE.left = null;
            bAE.right = null;
            freeAtomExpr(expr);

            return andAtomExpr(lft, rgt);
        }

        return buildAtomNot(expr);
    }

    /**
     *  Description of the Method
     *
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryAtom orAtomExpr(QueryAtom lft, QueryAtom rgt)
    {
        QueryAtom expr;
        int order;
        BasicQueryAtomValue lAELft = (BasicQueryAtomValue) lft;
        BasicQueryAtomValue lAERgt = (BasicQueryAtomValue) rgt;

        /* Identities  */
        if (equalAtomExpr(lft, rgt))
        {
            freeAtomExpr(rgt);

            return lft;
        }

        if ((lft.getType() == AE_LEAF) && (lAELft.label == AL_CONST))
        {
            if (lAELft.value != 0)
            {
                freeAtomExpr(rgt);

                return lft;
            }
            else
            {
                freeAtomExpr(lft);

                return rgt;
            }
        }

        if ((rgt.getType() == AE_LEAF) && (lAERgt.label == AL_CONST))
        {
            if (lAERgt.value != 0)
            {
                freeAtomExpr(lft);

                return rgt;
            }
            else
            {
                freeAtomExpr(rgt);

                return lft;
            }
        }

        order = orderAtomExpr(lft, rgt);

        if (order > 0)
        {
            expr = lft;
            lft = rgt;
            rgt = expr;
        }

        BasicQueryAtomBinary bAELft = (BasicQueryAtomBinary) lft;
        BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) rgt;

        if (lft.getType() == AE_OR)
        {
            expr = orAtomExpr(bAERgt.right, rgt);
            expr = orAtomExpr(bAELft.left, expr);
            bAELft.left = null;
            bAERgt.right = null;
            freeAtomExpr(lft);

            return expr;
        }

        if (rgt.getType() == AE_OR)
        {
            if (orderAtomExpr(lft, bAERgt.left) > 0)
            {
                expr = orAtomExpr(lft, bAERgt.right);
                expr = orAtomExpr(bAERgt.left, expr);
                bAERgt.left = null;
                bAERgt.right = null;
                freeAtomExpr(rgt);

                return expr;
            }

            if (equalAtomExpr(lft, bAERgt.left))
            {
                freeAtomExpr(lft);

                return rgt;
            }
        }

        return orAtomExprLeaf(lft, rgt);
    }

    /**
     *  Description of the Method
     *
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryAtom orAtomExprLeaf(QueryAtom lft, QueryAtom rgt)
    {
        return buildAtomBin(AE_OR, lft, rgt);
    }

    /**
     *  Description of the Method
     *
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static int orderAtomExpr(QueryAtom lft, QueryAtom rgt)
    {
        QueryAtom larg;
        QueryAtom rarg;
        int stat;

        if (lft.getType() == AE_NOT)
        {
            /* larg.type == AE_LEAF  */
            BasicQueryAtomMono mAELft = (BasicQueryAtomMono) lft;
            larg = mAELft.next;
        }
        else
        {
            larg = lft;
        }

        if (rgt.getType() == AE_NOT)
        {
            /* rarg.type == AE_LEAF  */
            BasicQueryAtomMono mAERgt = (BasicQueryAtomMono) rgt;
            rarg = mAERgt.next;
        }
        else
        {
            rarg = rgt;
        }

        if (larg.getType() > rarg.getType())
        {
            return 1;
        }
        else if (larg.getType() < rarg.getType())
        {
            return -1;
        }

        BasicQueryAtomValue lAELarg = (BasicQueryAtomValue) larg;
        BasicQueryAtomValue lAERarg = (BasicQueryAtomValue) rarg;

        if (larg.getType() == AE_LEAF)
        {
            if (lAELarg.label > lAERarg.label)
            {
                return 1;
            }

            if (lAELarg.label < lAERarg.label)
            {
                return -1;
            }

            return (lAELarg.value - lAERarg.value);
        }

        BasicQueryAtomBinary bAELft = (BasicQueryAtomBinary) lft;
        BasicQueryAtomBinary bAERgt = (BasicQueryAtomBinary) rgt;
        stat = orderAtomExpr(bAELft.left, bAERgt.left);

        if (stat != 0)
        {
            return stat;
        }

        return orderAtomExpr(bAELft.right, bAERgt.right);
    }

    /**
     * @return Returns the type.
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(int type)
    {
        this.type = type;
    }

    public String toString()
    {
        ByteArrayOutputStream bs = new ByteArrayOutputStream(1000);
        PrintStream ps = new PrintStream(bs);

        BasicQueryAtom.dumpAtomExpr(this, ps);

        //    AtomExpr.displayAtomExpr(this, ps);
        return bs.toString();
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
