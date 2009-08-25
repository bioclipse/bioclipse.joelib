///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicQueryBond.java,v $
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
package joelib2.smarts.bondexpr;

import cformat.PrintfStream;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Category;


/**
 * Bond expression for SMARTS.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:39 $
 */
public class BasicQueryBond implements java.io.Serializable, QueryBond
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(BasicQueryBond.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int type;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the BondExpr object
     */
    public BasicQueryBond()
    {
    }

    /**
     *  Constructor for the BondExpr object
     *
     * @param  dataType  Description of the Parameter
     */
    public BasicQueryBond(int _type)
    {
        type = _type;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    //public static BondExpr allocBondExpr( )
    //{
    //    BondExpr result = new BondExpr();
    //    //result = (BondExpr)malloc(sizeof(BondExpr));
    //
    //  /*
    //    if( !FreeBEList )
    //    {   result = (BondExpr)malloc(sizeof(BondExpr)BONDEXPRPOOL);
    //        for( i=1; i<BONDEXPRPOOL; i++ )
    //        {   result.mon.arg = freeBEList;
    //            //MonAtomExpr mAE=(MonAtomExpr)expr;
    //            freeBEList = result++;
    //        }
    //    } else
    //    {   result = freeBEList;
    //        freeBEList = result.mon.arg;
    //    }
    //  */
    //
    //    return result;
    //}

    /**
     *  Description of the Method
     *
     * @param  type  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static BasicQueryBond allocBondExpr(int type)
    {
        BasicQueryBond result = null;

        // = new BondExpr();
        //    result = allocAtomExpr();
        //    result.type = expr.type;
        //    result = allocAtomExpr(expr.type);
        switch (type)
        {
        case (BE_ANDHI):
        case (BE_ANDLO):
        case (BE_OR):
            result = new BasicQueryBondBinary(type);

            break;

        case (BE_NOT):
            result = new BasicQueryBondMono(type);

            break;

        case (BE_LEAF):
            result = new BasicQueryBondValue(type);

            break;
        }

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  op   Description of the Parameter
     * @param  lft  Description of the Parameter
     * @param  rgt  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryBond buildBondBin(int op, QueryBond lft, QueryBond rgt)
    {
        QueryBond result;
        result = allocBondExpr(op);

        QueryBondBinary bBE = (QueryBondBinary) result;

        //result.bin.type = op;
        bBE.setLeft(lft);
        bBE.setRight(rgt);

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  prop  Description of the Parameter
     * @param  val   Description of the Parameter
     * @return       Description of the Return Value
     */
    public static BasicQueryBond buildBondLeaf(int prop, int val)
    {
        BasicQueryBond result;

        result = allocBondExpr(BE_LEAF);

        BasicQueryBondValue lBE = (BasicQueryBondValue) result;

        //result.leaf.type = BE_LEAF;
        lBE.label = prop;
        lBE.value = val;

        return lBE;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryBond buildBondNot(QueryBond expr)
    {
        BasicQueryBond result;
        result = allocBondExpr(BE_NOT);

        BasicQueryBondMono mBE = (BasicQueryBondMono) result;

        //result.mon.type = BE_NOT;
        mBE.next = expr;

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryBond canonicaliseBond(QueryBond expr)
    {
        if (FOO)
        {
            //            if (!ORIG)
            //            {
            //                //      int index;
            //                //
            //                //      index = getBondExprIndex(expr);
            //                //      freeBondExpr(expr);
            //                //
            //                //      lexPtr = canBondExpr[index];
            //                //      if( lexPtrIndex<=theEnd )
            //                //      {
            //                //        expr = parseBondExpr(0);
            //                //      }
            //                //      else expr = generateDefaultBond();
            //            }
            return transformBondExpr(expr);
        }
        else
        {
            //    throw Exception(ParseSmart.getName()+".canonicaliseBond not defined.");
            logger.error("ParseSmart.canonicaliseBond not defined.");
        }

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryBond copyBondExpr(QueryBond expr)
    {
        QueryBond result;

        //    result = allocAtomExpr();
        //    result.type = expr.type;
        result = allocBondExpr(expr.getType());

        switch (expr.getType())
        {
        case (BE_ANDHI):
        case (BE_ANDLO):
        case (BE_OR):

            BasicQueryBondBinary bBE = (BasicQueryBondBinary) expr;
            QueryBondBinary binResult = (BasicQueryBondBinary) result;
            binResult.setLeft(copyBondExpr(bBE.getLeft()));
            binResult.setRight(copyBondExpr(bBE.getRight()));

            break;

        case (BE_NOT):

            BasicQueryBondMono mBE = (BasicQueryBondMono) expr;
            QueryBondMono monResult = (BasicQueryBondMono) result;
            monResult.setNext(copyBondExpr(mBE.getNext()));

            break;

        case (BE_LEAF):

            BasicQueryBondValue lBE = (BasicQueryBondValue) expr;
            BasicQueryBondValue leafResult = (BasicQueryBondValue) result;
            leafResult.label = lBE.label;
            leafResult.value = lBE.value;

            break;
        }

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void displayBondExpr(QueryBond expr, OutputStream os)
    {
        PrintfStream fp;

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        BasicQueryBondBinary bBE = null;

        switch (expr.getType())
        {
        case (BE_LEAF):
            displayBondLeaf(expr, fp);

            break;

        case (BE_NOT):

            BasicQueryBondMono mBE = (BasicQueryBondMono) expr;
            fp.print('!');
            displayBondExpr(mBE.getNext(), fp);

            break;

        case (BE_ANDHI):
            bBE = (BasicQueryBondBinary) expr;
            displayBondExpr(bBE.left, fp);

            /* fp.print('&');   */
            displayBondExpr(bBE.right, fp);

            break;

        case (BE_ANDLO):
            bBE = (BasicQueryBondBinary) expr;
            displayBondExpr(bBE.left, fp);
            fp.print(';');
            displayBondExpr(bBE.right, fp);

            break;

        case (BE_OR):
            bBE = (BasicQueryBondBinary) expr;
            displayBondExpr(bBE.left, fp);
            fp.print(',');
            displayBondExpr(bBE.right, fp);

            break;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void displayBondLeaf(QueryBond expr, OutputStream os)
    {
        PrintfStream fp;

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        BasicQueryBondValue lBE = (BasicQueryBondValue) expr;

        if (lBE.label == BL_CONST)
        {
            if (lBE.value != 0)
            {
                fp.print("!~");
            }
            else
            {
                fp.print('~');
            }
        }
        else
        {
            /* expr.leaf.prop == BL_TYPE   */
            switch (lBE.value)
            {
            case (BT_SINGLE):
                fp.print('-');

                break;

            case (BT_DOUBLE):
                fp.print('=');

                break;

            case (BT_TRIPLE):
                fp.print('#');

                break;

            case (BT_AROM):
                fp.print(':');

                break;

            case (BT_UP):
                fp.print('\\');

                break;

            case (BT_DOWN):
                fp.print('/');

                break;

            case (BT_UPUNSPEC):
                fp.print("\\?");

                break;

            case (BT_DOWNUNSPEC):
                fp.print("/?");

                break;

            case (BT_RING):
                fp.print('@');

                break;
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void displaySMARTSBond(QueryBond expr, OutputStream os)
    {
        PrintfStream fp;

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        QueryBond lft;
        QueryBond rgt;

        if (expr.getType() == BE_OR)
        {
            BasicQueryBondBinary bBE = (BasicQueryBondBinary) expr;
            lft = bBE.left;
            rgt = bBE.right;

            if ((lft.getType() == BE_LEAF) && (rgt.getType() == BE_LEAF))
            {
                BasicQueryBondValue lBELft = (BasicQueryBondValue) lft;
                BasicQueryBondValue lBERgt = (BasicQueryBondValue) rgt;

                if ((lBELft.label == BL_TYPE) && (lBERgt.label == BL_TYPE))
                {
                    if ((lBELft.value == BT_SINGLE) &&
                            (lBERgt.value == BT_AROM))
                    {
                        return;
                    }

                    if ((lBERgt.value == BT_SINGLE) &&
                            (lBELft.value == BT_AROM))
                    {
                        return;
                    }
                }
            }
        }

        displayBondExpr(expr, fp);
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  os    Description of the Parameter
     */
    public static void dumpBondExpr(QueryBond expr, OutputStream os)
    {
        PrintfStream fp;

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        BasicQueryBondBinary bBE = null;

        switch (expr.getType())
        {
        case (BE_LEAF):
            dumpBondLeaf(expr, fp);

            break;

        case (BE_NOT):

            BasicQueryBondMono mBE = (BasicQueryBondMono) expr;
            fp.print("NOT(");
            dumpBondExpr(mBE.next, fp);
            fp.print(')');

            break;

        case (BE_ANDHI):
            bBE = (BasicQueryBondBinary) expr;
            fp.print("ANDHI(");
            dumpBondExpr(bBE.left, fp);
            fp.print(',');
            dumpBondExpr(bBE.right, fp);
            fp.print(')');

            break;

        case (BE_ANDLO):
            bBE = (BasicQueryBondBinary) expr;
            dumpBondExpr(bBE.left, fp);
            fp.print("ANDLO");
            fp.print(',');
            dumpBondExpr(bBE.right, fp);
            fp.print(')');

            break;

        case (BE_OR):
            bBE = (BasicQueryBondBinary) expr;
            fp.print("OR(");
            dumpBondExpr(bBE.left, fp);
            fp.print(',');
            dumpBondExpr(bBE.right, fp);
            fp.print(')');

            break;
        }
    }

    /**
    *  Description of the Method
    *
    * @param  expr  Description of the Parameter
    * @param  os    Description of the Parameter
    */
    public static void dumpBondLeaf(QueryBond expr, OutputStream os)
    {
        PrintfStream fp;

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        BasicQueryBondValue lBE = (BasicQueryBondValue) expr;

        if (lBE.label == BL_CONST)
        {
            if (lBE.value != 0)
            {
                fp.print("!~");
            }
            else
            {
                fp.print('~');
            }
        }
        else
        {
            /* expr.leaf.prop == BL_TYPE   */
            switch (lBE.value)
            {
            case (BT_SINGLE):
                fp.print("SINGLE");

                break;

            case (BT_DOUBLE):
                fp.print("DOUBLE");

                break;

            case (BT_TRIPLE):
                fp.print("TRIPLE");

                break;

            case (BT_AROM):
                fp.print("AROM");

                break;

            case (BT_UP):
                fp.print("UP");

                break;

            case (BT_DOWN):
                fp.print("DOWN");

                break;

            case (BT_UPUNSPEC):
                fp.print("UNSPEC_UP");

                break;

            case (BT_DOWNUNSPEC):
                fp.print("UNSPEC_DOWN");

                break;

            case (BT_RING):
                fp.print("RING");

                break;
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     */
    public static void freeBondExpr(QueryBond expr)
    {
        if (expr != null)
        {
            switch (expr.getType())
            {
            case (BE_ANDHI):
            case (BE_ANDLO):
            case (BE_OR):

                BasicQueryBondBinary bBE = (BasicQueryBondBinary) expr;
                freeBondExpr(bBE.left);
                freeBondExpr(bBE.right);

                break;

            case (BE_NOT):

                BasicQueryBondMono mBE = (BasicQueryBondMono) expr;
                freeBondExpr(mBE.next);

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
     * @return    Description of the Return Value
     */
    public static QueryBond generateDefaultBond()
    {
        BasicQueryBond expr1;
        BasicQueryBond expr2;

        expr1 = buildBondLeaf(BL_TYPE, BT_SINGLE);
        expr2 = buildBondLeaf(BL_TYPE, BT_AROM);

        return buildBondBin(BE_OR, expr1, expr2);
    }

    /**
     *  Gets the bondExprIndex attribute of the ParseSmart class
     *
     * @param  expr  Description of the Parameter
     * @return       The bondExprIndex value
     */
    public static int getBondExprIndex(QueryBond expr)
    {
        int lft;
        int rgt;
        int arg;
        BasicQueryBondBinary bBE = null;
        BasicQueryBondMono mBE = null;

        switch (expr.getType())
        {
        case (BE_LEAF):
            return getBondLeafIndex(expr);

        case (BE_NOT):
            mBE = (BasicQueryBondMono) expr;
            arg = getBondExprIndex(mBE.next);

            return (arg ^ BS_ALL);

        case (BE_ANDHI):
        case (BE_ANDLO):
            bBE = (BasicQueryBondBinary) expr;
            lft = getBondExprIndex(bBE.left);
            rgt = getBondExprIndex(bBE.right);

            return (lft & rgt);

        case (BE_OR):
            bBE = (BasicQueryBondBinary) expr;
            lft = getBondExprIndex(bBE.left);
            rgt = getBondExprIndex(bBE.right);

            return (lft | rgt);
        }

        /* Avoid Compiler Warning   */
        return 0;
    }

    /*==============================  */
    /*  Canonical Bond Expressions    */
    /*==============================  */

    /**
     *  Gets the bondLeafIndex attribute of the ParseSmart class
     *
     * @param  expr  Description of the Parameter
     * @return       The bondLeafIndex value
     */
    public static int getBondLeafIndex(QueryBond expr)
    {
        BasicQueryBondValue lBE = (BasicQueryBondValue) expr;

        if (lBE.label == BL_CONST)
        {
            if (lBE.value != 0)
            {
                return (BS_ALL);
            }
            else
            {
                return (0);
            }
        }
        else
        {
            /* expr.leaf.prop == BL_TYPE   */
            switch (lBE.value)
            {
            case (BT_SINGLE):
                return (BS_SINGLE);

            case (BT_DOUBLE):
                return (BS_DOUBLE);

            case (BT_TRIPLE):
                return (BS_TRIPLE);

            case (BT_AROM):
                return (BS_AROM);

            case (BT_UP):
                return (BS_UP);

            case (BT_DOWN):
                return (BS_DOWN);

            case (BT_UPUNSPEC):
                return (BS_UPUNSPEC);

            case (BT_DOWNUNSPEC):
                return (BS_DOWNUNSPEC);

            case (BT_RING):
                return (BS_RING);
            }
        }

        return 0;
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryBond notBondExpr(QueryBond expr)
    {
        QueryBond result;

        if (expr.getType() == BE_LEAF)
        {
            BasicQueryBondValue lBE = (BasicQueryBondValue) expr;

            if (lBE.label == BL_CONST)
            {
                lBE.value = ((lBE.value == 0) ? 1 : 0);

                return expr;
            }
        }
        else if (expr.getType() == BE_NOT)
        {
            BasicQueryBondMono mBE = (BasicQueryBondMono) expr;
            result = mBE.getNext();
            mBE.next = null;
            freeBondExpr(expr);

            return result;
        }

        return buildBondNot(expr);
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static QueryBond transformBondExpr(QueryBond expr)
    {
        QueryBond lft;
        QueryBond rgt;
        QueryBond arg;

        if (expr.getType() == BE_LEAF)
        {
            return expr;
        }
        else if (expr.getType() == BE_NOT)
        {
            BasicQueryBondMono mBE = (BasicQueryBondMono) expr;
            arg = mBE.next;
            arg = transformBondExpr(arg);
            mBE.next = null;
            freeBondExpr(expr);

            return notBondExpr(arg);
        }
        else if (expr.getType() == BE_ANDHI)
        {
            BasicQueryBondBinary bBE = (BasicQueryBondBinary) expr;
            lft = bBE.left;
            rgt = bBE.right;
            lft = transformBondExpr(lft);
            rgt = transformBondExpr(rgt);
            bBE.left = lft;
            bBE.right = rgt;

            return expr;
        }
        else if (expr.getType() == BE_ANDLO)
        {
            BasicQueryBondBinary bBE = (BasicQueryBondBinary) expr;
            lft = bBE.left;
            rgt = bBE.right;
            lft = transformBondExpr(lft);
            rgt = transformBondExpr(rgt);
            bBE.left = lft;
            bBE.right = rgt;

            return expr;
        }
        else if (expr.getType() == BE_OR)
        {
            BasicQueryBondBinary bBE = (BasicQueryBondBinary) expr;
            lft = bBE.left;
            rgt = bBE.right;
            lft = transformBondExpr(lft);
            rgt = transformBondExpr(rgt);
            bBE.left = lft;
            bBE.right = rgt;

            return expr;
        }

        return expr;
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

        displayBondExpr(this, ps);

        return bs.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
