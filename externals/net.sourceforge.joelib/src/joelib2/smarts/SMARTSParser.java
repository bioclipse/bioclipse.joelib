///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMARTSParser.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.16 $
//            $Date: 2005/03/03 07:13:51 $
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

import cformat.PrintfStream;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.atomlabel.AtomBondOrderSum;
import joelib2.feature.types.atomlabel.AtomExplicitHydrogenCount;
import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomImplicitHydrogenCount;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomInAromaticSystem;
import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.atomlabel.AtomInRingsCount;
import joelib2.feature.types.atomlabel.AtomIsElectronegative;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.atomlabel.AtomKekuleBondOrderSum;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondInRing;

import joelib2.math.BasicVector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.AtomHelper;
import joelib2.molecule.Bond;
import joelib2.molecule.BondHelper;
import joelib2.molecule.Molecule;

import joelib2.smarts.atomexpr.BasicQueryAtom;
import joelib2.smarts.atomexpr.BasicQueryAtomBinary;
import joelib2.smarts.atomexpr.BasicQueryAtomMono;
import joelib2.smarts.atomexpr.BasicQueryAtomPattern;
import joelib2.smarts.atomexpr.BasicQueryAtomValue;
import joelib2.smarts.atomexpr.QueryAtom;
import joelib2.smarts.atomexpr.QueryAtomBinary;

import joelib2.smarts.bondexpr.BasicQueryBond;
import joelib2.smarts.bondexpr.BasicQueryBondBinary;
import joelib2.smarts.bondexpr.BasicQueryBondMono;
import joelib2.smarts.bondexpr.BasicQueryBondValue;
import joelib2.smarts.bondexpr.QueryBond;
import joelib2.smarts.bondexpr.QueryBondBinary;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BasicNbrAtomIterator;
import joelib2.util.iterator.NbrAtomIterator;

import joelib2.util.types.StringString;

import wsi.ra.tool.BasicPropertyHolder;

import java.io.OutputStream;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Contains methods for SMARTS parsing.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.16 $, $Date: 2005/03/03 07:13:51 $
 * @.cite smarts
 */
public class SMARTSParser
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.16 $";
    private static final String RELEASE_DATE = "$Date: 2005/03/03 07:13:51 $";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomBondOrderSum.class, AtomExplicitHydrogenCount.class,
            AtomHeavyValence.class, AtomHybridisation.class,
            AtomImplicitHydrogenCount.class, AtomImplicitValence.class,
            AtomInAromaticSystem.class, AtomInRing.class,
            AtomInRingsCount.class, AtomIsElectronegative.class,
            AtomIsHydrogen.class, AtomKekuleBondOrderSum.class,
            BondInAromaticSystem.class, BondInRing.class
        };
    private static Category logger = Category.getInstance(SMARTSParser.class
            .getName());

    /**
     *  Description of the Field
     */
    private final static boolean STRICT = false;

    /**
     *  Description of the Field
     */
    private final static boolean RECURSIVE = true;

    /**
     *  Description of the Field
     */
    public final static int ELEMMAX = 104;

    /**
     *  Description of the Field
     */
    public final static int ATOMEXPRPOOL = 1;

    /**
     *  Description of the Field
     */
    public final static int BONDEXPRPOOL = 1;

    /**
     *  Description of the Field
     */
    public final static int IFALSE = 0;

    /**
     *  Description of the Field
     */
    public final static int ITRUE = 1;

    public final static int STACKSIZE = 40;
    private static boolean recognizeExpH = false;

    static
    {
        String value = BasicPropertyHolder.instance().getProperties()
                                          .getProperty(
                SMARTSParser.class.getName() +
                ".anyRecognizesExpliciteHydrogens");

        //System.out.println("name:"+ParseSmart.class.getName()+".anyRecognizesExpliciteHydrogens");
        //System.out.println("value:"+value);
        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            recognizeExpH = true;
        }
        else
        {
            recognizeExpH = false;
        }
    }

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public char[] buffer;

    /**
     *  Description of the Field
     */
    public char[] lexPtr;

    /**
     *  Description of the Field
     */
    public int lexPtrIndex;

    /**
     *  Description of the Field
     */
    public char[] mainPtr;

    /**
     *  Description of the Field
     */
    public int mainPtrIndex;

    public List<QueryCache> rsCache = new Vector<QueryCache>();
    private int theEnd;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SMARTSParser()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  pat  Description of the Parameter
     * @param  os   Description of the Parameter
     */
    public static void generateSMARTSString(BasicQueryPattern pat,
        OutputStream os)
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

        ParserState stat = new ParserState();
        int part;
        int dot;
        int i;

        if (pat == null)
        {
            fp.print("[!*]");

            return;
        }

        for (i = 0; i < pat.queryBondsSize; i++)
        {
            pat.queryBonds[i].setVisit(-1);
        }

        for (i = 0; i < pat.queryAtomsSize; i++)
        {
            pat.queryAtoms[i].setVisit(IFALSE);
        }

        /* Determine Closures    */
        for (i = 0; i < pat.queryAtomsSize; i++)
        {
            if (pat.queryAtoms[i].getVisit() == 0)
            {
                traverseSMARTS(pat, i);
            }
        }

        stat.closindex = 1;

        for (i = 0; i < 100; i++)
        {
            stat.closure[i] = -1;
        }

        for (i = 0; i < pat.queryAtomsSize; i++)
        {
            pat.queryAtoms[i].setVisit(IFALSE);
        }

        /* Output SMARTS String    */
        for (part = 1; part < pat.parts; part++)
        {
            if (part != 1)
            {
                fp.print('.');
            }

            dot = 0;
            fp.print('(');

            /* stat.closindex = 1;    */
            while ((i = findSMARTSRoot(pat, part)) != -1)
            {
                if (dot != 0)
                {
                    fp.print('.');
                }
                else
                {
                    dot = 1;
                }

                displaySMARTSPart(pat, stat, i, fp);
            }

            fp.print(')');
        }

        dot = (pat.parts > 1) ? ITRUE : IFALSE;

        /* stat.closindex = 1;    */
        while ((i = findSMARTSRoot(pat, 0)) != -1)
        {
            if (dot != 0)
            {
                fp.print('.');
            }
            else
            {
                dot = 1;
            }

            displaySMARTSPart(pat, stat, i, fp);
        }
    }

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
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  bond  Description of the Parameter
     * @return       Description of the Return Value
     */
    protected static boolean evalBondExpr(QueryBond expr, Bond bond)
    {
        QueryBondBinary bBE = null;

        if (RECURSIVE)
        {
            while (true)
            {
                switch (expr.getType())
                {
                case BasicQueryBond.BE_LEAF:

                    BasicQueryBondValue lBE = (BasicQueryBondValue) expr;

                    if (lBE.label == BasicQueryBond.BL_CONST)
                    {
                        return ((lBE.value != 0) ? true : false);
                    }
                    else
                    {
                        switch (lBE.value)
                        {
                        case BasicQueryBond.BT_SINGLE:

                            if (bond.getParent().hasData(
                                        BondInAromaticSystem.getName()))
                            {
                                return ((bond.getBondOrder() == 1) &&
                                        !BondInAromaticSystem.isAromatic(bond));
                            }
                            else
                            {
                                return ((bond.getBondOrder() == 1) &&
                                        !bond.isBondOrderAromatic());
                            }

                        case BasicQueryBond.BT_AROM:

                            if (bond.getParent().hasData(
                                        BondInAromaticSystem.getName()))
                            {
                                return BondInAromaticSystem.isAromatic(bond);
                            }
                            else
                            {
                                return bond.isBondOrderAromatic();
                            }

                        case BasicQueryBond.BT_DOUBLE:

                            if (bond.getParent().hasData(
                                        BondInAromaticSystem.getName()))
                            {
                                return ((bond.getBondOrder() == 2) &&
                                        !BondInAromaticSystem.isAromatic(bond));
                            }
                            else
                            {
                                return ((bond.getBondOrder() == 2) &&
                                        !bond.isBondOrderAromatic());
                            }

                        case BasicQueryBond.BT_TRIPLE:
                            return (bond.getBondOrder() == 3);

                        case BasicQueryBond.BT_RING:
                            return BondInRing.isInRing(bond);

                        case BasicQueryBond.BT_UP:

                            //System.out.println("SMARTS up ? "+bond.isUp());
                            return bond.isUp();

                        case BasicQueryBond.BT_DOWN:

                            //System.out.println("SMARTS down ? "+bond.isDown());
                            return bond.isDown();

                        case BasicQueryBond.BT_UPUNSPEC:

                            //System.out.println(bond.getParent().getTitle()+" SMARTS up unspecific ? up="+bond.isUp()+" down="+bond.isDown());
                            return !bond.isDown();

                        case BasicQueryBond.BT_DOWNUNSPEC:

                            //System.out.println("SMARTS down pecific ? "+bond.isDown());
                            return !bond.isUp();

                        default:
                            return (false);
                        }
                    }

                case BasicQueryBond.BE_NOT:

                    BasicQueryBondMono mBE = (BasicQueryBondMono) expr;

                    return (!evalBondExpr(mBE.getNext(), bond));

                case BasicQueryBond.BE_ANDHI:
                case BasicQueryBond.BE_ANDLO:
                    bBE = (BasicQueryBondBinary) expr;

                    if (!evalBondExpr(bBE.getLeft(), bond))
                    {
                        return false;
                    }

                    expr = bBE.getRight();

                    break;

                case BasicQueryBond.BE_OR:
                    bBE = (BasicQueryBondBinary) expr;

                    if (evalBondExpr(bBE.getLeft(), bond))
                    {
                        return true;
                    }

                    expr = bBE.getRight();

                    break;
                }
            }

            //    return false;
        }
        else
        {
            int size = 0;
            QueryBond[] stack = new BasicQueryBond[STACKSIZE];
            boolean lftest = true;

            for (size = 0, stack[size] = expr; size >= 0; expr = stack[size])
            {
                switch (expr.getType())
                {
                case (BasicQueryBond.BE_LEAF):

                    BasicQueryBondValue lBE = (BasicQueryBondValue) expr;

                    if (lBE.label == BasicQueryBond.BL_CONST)
                    {
                        lftest = (lBE.value != 0) ? true : false;
                    }
                    else
                    {
                        /* expr.leaf.prop == BondExpr.BL_TYPE    */
                        switch (lBE.value)
                        {
                        case (BasicQueryBond.BT_SINGLE):

                            if (bond.getParent().hasData(
                                        BondInAromaticSystem.getName()))
                            {
                                lftest = ((bond.getBondOrder() == 1) &&
                                        !BondInAromaticSystem.isAromatic(bond));
                            }
                            else
                            {
                                lftest = ((bond.getBondOrder() == 1) &&
                                        !bond.isBondOrderAromatic());
                            }

                            break;

                        case (BasicQueryBond.BT_DOUBLE):

                            if (bond.getParent().hasData(
                                        BondInAromaticSystem.getName()))
                            {
                                lftest = ((bond.getBondOrder() == 2) &&
                                        !BondInAromaticSystem.isAromatic(bond));
                            }
                            else
                            {
                                lftest = ((bond.getBondOrder() == 2) &&
                                        !bond.isBondOrderAromatic());
                            }

                            break;

                        case (BasicQueryBond.BT_TRIPLE):
                            lftest = (bond.getBondOrder() == 3);

                            break;

                        case (BasicQueryBond.BT_AROM):

                            if (bond.getParent().hasData(
                                        BondInAromaticSystem.getName()))
                            {
                                lftest = BondInAromaticSystem.isAromatic(bond);
                            }
                            else
                            {
                                lftest = bond.isBondOrderAromatic();
                            }

                            break;

                        case (BasicQueryBond.BT_RING):
                            lftest = BondInRing.isInRing(bond);

                            break;

                        case (BasicQueryBond.BT_UP):

                            //System.out.println("SMARTS up ? "+bond.isUp());
                            lftest = bond.isUp();

                            break;

                        case (BasicQueryBond.BT_DOWN):

                            //System.out.println("SMARTS down ? "+bond.isDown());
                            lftest = bond.isDown();

                            break;

                        case (BasicQueryBond.BT_UPUNSPEC):
                            break;

                        case (BasicQueryBond.BT_DOWNUNSPEC):
                            break;
                        }
                    }

                    size--;

                    break;

                case (BasicQueryBond.BE_NOT):

                    BasicQueryBondMono mBE = (BasicQueryBondMono) expr;

                    if (stack[size + 1] != mBE.next)
                    {
                        size++;
                        stack[size] = mBE.next;
                    }
                    else
                    {
                        lftest = !lftest;
                        size--;
                    }

                    break;

                case (BasicQueryBond.BE_ANDHI):
                    bBE = (BasicQueryBondBinary) expr;

                    if (stack[size + 1] == bBE.getRight())
                    {
                        size--;
                    }
                    else if (stack[size + 1] == bBE.getLeft())
                    {
                        if (lftest)
                        {
                            size++;
                            stack[size] = bBE.getRight();
                        }
                        else
                        {
                            size--;
                        }
                    }
                    else
                    {
                        size++;
                        stack[size] = bBE.getLeft();
                    }

                    break;

                case (BasicQueryBond.BE_ANDLO):
                    bBE = (BasicQueryBondBinary) expr;

                    if (stack[size + 1] == bBE.getRight())
                    {
                        size--;
                    }
                    else if (stack[size + 1] == bBE.getLeft())
                    {
                        if (lftest)
                        {
                            size++;
                            stack[size] = bBE.getRight();
                        }
                        else
                        {
                            size--;
                        }
                    }
                    else
                    {
                        size++;
                        stack[size] = bBE.getLeft();
                    }

                    break;

                case (BasicQueryBond.BE_OR):
                    bBE = (BasicQueryBondBinary) expr;

                    if (stack[size + 1] == bBE.getRight())
                    {
                        size--;
                    }
                    else if (stack[size + 1] == bBE.getLeft())
                    {
                        if (!lftest)
                        {
                            size++;
                            stack[size] = bBE.getRight();
                        }
                        else
                        {
                            size--;
                        }
                    }
                    else
                    {
                        size++;
                        stack[size] = bBE.getLeft();
                    }

                    break;
                }
            }

            return (lftest);
        }
    }

    /**
     *  Gets the exprOrder attribute of the ParseSmart class
     *
     * @param  expr  Description of the Parameter
     * @return       The exprOrder value
     */
    protected static int getExprOrder(QueryBond expr)
    {
        int size = 0;
        QueryBond[] stack = new BasicQueryBond[15];
        boolean lftest = true;

        for (size = 0, stack[size] = expr; size >= 0; expr = stack[size])
        {
            switch (expr.getType())
            {
            case (BasicQueryBond.BE_LEAF):

                BasicQueryBondValue lBE = (BasicQueryBondValue) expr;

                if (lBE.label == BasicQueryBond.BL_CONST)
                {
                    lftest = true;
                }
                else
                {
                    /* expr.leaf.prop == BondExpr.BL_TYPE    */
                    switch (lBE.value)
                    {
                    case (BasicQueryBond.BT_SINGLE):
                        return (1);

                    case (BasicQueryBond.BT_DOUBLE):
                        return (2);

                    case (BasicQueryBond.BT_TRIPLE):
                        return (3);

                    case (BasicQueryBond.BT_AROM):
                        return (BondHelper.AROMATIC_BO);

                    default:
                        lftest = true;
                    }

                    size--;

                    break;
                }

            case (BasicQueryBond.BE_NOT):
                return (0);

            case (BasicQueryBond.BE_ANDHI):
            case (BasicQueryBond.BE_ANDLO):
            case (BasicQueryBond.BE_OR):

                BasicQueryBondBinary bBE = (BasicQueryBondBinary) expr;

                if (stack[size + 1] == bBE.right)
                {
                    size--;
                }
                else if (stack[size + 1] == bBE.left)
                {
                    if (lftest)
                    {
                        size++;
                        stack[size] = bBE.getRight();
                    }
                    else
                    {
                        size--;
                    }
                }
                else
                {
                    size++;
                    stack[size] = bBE.getLeft();
                }

                break;
            }
        }

        return (0);
    }

    /**
     *  Description of the Method
     *
     * @param  expr  Description of the Parameter
     * @param  atom  Description of the Parameter
     * @return       Description of the Return Value
     */
    protected boolean evalAtomExpr(QueryAtom expr, Atom atom)
    {
        BasicQueryAtomBinary bAE = null;

        if (RECURSIVE)
        {
            while (true)
            {
                switch (expr.getType())
                {
                case BasicQueryAtom.AE_LEAF:

                    BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

                    switch (lAE.label)
                    {
                    case BasicQueryAtom.AL_ELEM:

                        // special hydrogen case
                        // this means user is requesting: #1, so this should match ALWAYS !
                        //if (atom.getAtomicNum()==1)
                        //{
                        //    if(recognizeExpH && lAE.value==atom.getAtomicNum())       return true;
                        //    else return false;
                        //}
                        return (lAE.value == atom.getAtomicNumber());

                    case BasicQueryAtom.AL_AROM:

                        if (atom.getParent().hasData(
                                    AtomInAromaticSystem.getName()))
                        {
                            //System.out.println("lAE.value="+lAE.value+" atom "+atom.getIndex()+" arom:"+AtomInAromaticSystem.isAromatic(atom));
                            if ((lAE.value != 0) &&
                                    AtomInAromaticSystem.isValue(atom))
                            {
                                return true;
                            }
                            else if ((lAE.value == 0) &&
                                    !AtomInAromaticSystem.isValue(atom))
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                        else
                        {
                            //System.out.println("lAE.value="+lAE.value+" atom "+atom.getIndex()+" arom:"+atom.hasBondOfOrder(BondHelper.AROMATIC_BO));
                            if ((lAE.value != 0) &&
                                    atom.hasBondOfOrder(BondHelper.AROMATIC_BO))
                            {
                                return true;
                            }
                            else if ((lAE.value == 0) &&
                                    !atom.hasBondOfOrder(
                                        BondHelper.AROMATIC_BO))
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }

                    case BasicQueryAtom.AL_HCOUNT:

                        //                                System.out.println("atom:"+atom.getIdx()+" exH:"+atom.explicitHydrogenCount()+" imH:"+atom.implicitHydrogenCount());
                        if (AtomExplicitHydrogenCount.getIntValue(atom) >
                                AtomImplicitHydrogenCount.getIntValue(atom))
                        {
                            return (lAE.value ==
                                    AtomExplicitHydrogenCount.getIntValue(
                                        atom));
                        }
                        else
                        {
                            return (lAE.value ==
                                    AtomImplicitHydrogenCount.getIntValue(
                                        atom));
                        }

                    case BasicQueryAtom.AL_DEGREE:
                        return (lAE.value == atom.getValence());

                    case BasicQueryAtom.AL_VALENCE:
                        return (lAE.value ==
                                AtomKekuleBondOrderSum.getIntValue(atom));

                    case BasicQueryAtom.AL_CONNECT:
                        return (lAE.value ==
                                AtomImplicitValence.getImplicitValence(atom));

                    case BasicQueryAtom.AL_HEAVY_CONNECT:
                        return (lAE.value == AtomHeavyValence.valence(atom));

                    case BasicQueryAtom.AL_GROUP:
                        return AtomHelper.isElementOfGroup(atom, lAE.value);

                    case BasicQueryAtom.AL_ELECTRONEGATIVE:

                        //                                return atom.isElectronegative(lAE.value);
                        return AtomIsElectronegative.isElectronegative(atom);

                    case BasicQueryAtom.AL_NEGATIVE:
                        return (lAE.value == (-1 * (atom.getFormalCharge())));

                    case BasicQueryAtom.AL_POSITIVE:
                        return (lAE.value == atom.getFormalCharge());

                    case BasicQueryAtom.AL_HYB:
                        return (lAE.value ==
                                AtomHybridisation.getIntValue(atom));

                    case BasicQueryAtom.AL_RINGS:

                        if (lAE.value == -1)
                        {
                            return AtomInRing.isInRing(atom);
                        }
                        else if (lAE.value == 0)
                        {
                            return (!AtomInRing.isInRing(atom));
                        }
                        else
                        {
                            return (AtomInRingsCount.getIntValue(atom) ==
                                    lAE.value);
                        }

                    case BasicQueryAtom.AL_SIZE:

                        if (lAE.value == 0)
                        {
                            return (!AtomInRing.isInRing(atom));
                        }
                        else
                        {
                            return (AtomHelper.isInRingSize(atom, lAE.value));
                        }

                    case BasicQueryAtom.AL_IMPLICIT:
                        return (lAE.value ==
                                AtomImplicitHydrogenCount.getIntValue(atom));

                    case BasicQueryAtom.AL_CONST:

                        if (recognizeExpH)
                        {
                            return true;
                        }
                        else
                        {
                            return (!AtomIsHydrogen.isHydrogen(atom));
                        }

                    default:
                        return (false);
                    }

                case BasicQueryAtom.AE_NOT:

                    BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;

                    return (!evalAtomExpr(mAE.next, atom));

                case BasicQueryAtom.AE_ANDHI:

                /* Same as AE_ANDLO    */
                case BasicQueryAtom.AE_ANDLO:
                    bAE = (BasicQueryAtomBinary) expr;

                    if (!evalAtomExpr(bAE.left, atom))
                    {
                        return (false);
                    }

                    expr = bAE.right;

                    break;

                case BasicQueryAtom.AE_OR:
                    bAE = (BasicQueryAtomBinary) expr;

                    if (evalAtomExpr(bAE.left, atom))
                    {
                        return (true);
                    }

                    expr = bAE.right;

                    break;

                case BasicQueryAtom.AE_RECUR:

                    //see if pattern has been matched
                    BasicQueryAtomPattern rAE = (BasicQueryAtomPattern) expr;
                    QueryCache rsCE;

                    for (int i = 0; i < rsCache.size(); i++)
                    {
                        rsCE = (QueryCache) rsCache.get(i);

                        if (rsCE.pattern == (BasicQueryPattern) rAE.recurrent)
                        {
                            //System.out.println("AE_RECUR: cache:"+rsCache.size()+" idx: "+atom.getIdx()+" l:"+rsCE.booleans.length);
                            if (atom.getIndex() > rsCE.booleans.length)
                            {
                                logger.error(
                                    "Unsolved 'molecule+SMARTS'-bug in substructure search.");
                                logger.error("Please send message: " +
                                    rsCE.pattern);
                                logger.error("and molecule: " +
                                    atom.getParent().getTitle());
                                logger.error("molecule: " + atom.getParent());
                                logger.error("to the developer mailing list.");

                                return true;
                            }

                            return rsCE.booleans[atom.getIndex()];
                        }
                    }

                    //perceive and match pattern
                    boolean[] vb =
                        new boolean[atom.getParent().getAtomsSize() + 1];
                    List<int[]> mlist = new Vector<int[]>();

                    // of type int[]
                    int[] itmp;

                    if (match(atom.getParent(),
                                (BasicQueryPattern) rAE.recurrent, mlist))
                    {
                        for (int j = 0; j < mlist.size(); j++)
                        {
                            itmp = (int[]) mlist.get(j);
                            vb[itmp[0]] = true;
                        }
                    }

                    rsCache.add(new QueryCache(
                            (BasicQueryPattern) rAE.recurrent, vb));

                    return (vb[atom.getIndex()]);
                }
            }

            //    return false;
        }
        else
        {
            // !RECURSIVE
            int size = 0;
            QueryAtom[] stack = new BasicQueryAtom[STACKSIZE];
            boolean lftest = true;

            for (size = 0, stack[size] = expr; size >= 0; expr = stack[size])
            {
                switch (expr.getType())
                {
                case BasicQueryAtom.AE_LEAF:

                    BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

                    switch (lAE.label)
                    {
                    //expr.leaf.value
                    case BasicQueryAtom.AL_ELEM:

                        // special hydrogen case
                        // this means user is requesting: #1, so this should match ALWAYS !
                        //if (atom.getAtomicNum()==1)
                        //{
                        //    if(recognizeExpH && lAE.value==atom.getAtomicNum())       lftest=true;
                        //    else lftest=false;
                        //}
                        lftest = (lAE.value == atom.getAtomicNumber());

                        break;

                    case BasicQueryAtom.AL_AROM:

                        //lftest = (expr.leaf.value == atom.isAromatic());
                        if (atom.getParent().hasData(
                                    AtomInAromaticSystem.getName()))
                        {
                            if ((lAE.value != 0) &&
                                    AtomInAromaticSystem.isValue(atom))
                            {
                                lftest = true;
                            }
                            else if ((lAE.value == 0) &&
                                    !AtomInAromaticSystem.isValue(atom))
                            {
                                lftest = true;
                            }
                            else
                            {
                                lftest = false;
                            }
                        }
                        else
                        {
                            if ((lAE.value != 0) &&
                                    atom.hasBondOfOrder(BondHelper.AROMATIC_BO))
                            {
                                lftest = true;
                            }
                            else if ((lAE.value == 0) &&
                                    !atom.hasBondOfOrder(
                                        BondHelper.AROMATIC_BO))
                            {
                                lftest = true;
                            }
                            else
                            {
                                lftest = false;
                            }
                        }

                        break;

                    case BasicQueryAtom.AL_HCOUNT:

                        if (AtomExplicitHydrogenCount.getIntValue(atom) >
                                AtomImplicitHydrogenCount.getIntValue(atom))
                        {
                            lftest = (lAE.value ==
                                    AtomExplicitHydrogenCount.getIntValue(
                                        atom));
                        }
                        else
                        {
                            lftest = (lAE.value ==
                                    AtomImplicitHydrogenCount.getIntValue(
                                        atom));
                        }

                        break;

                    case BasicQueryAtom.AL_DEGREE:
                        lftest = (lAE.value == atom.getValence());

                        break;

                    case BasicQueryAtom.AL_VALENCE:
                        lftest = (lAE.value ==
                                AtomBondOrderSum.getIntValue(atom));

                        break;

                    case BasicQueryAtom.AL_CONNECT:

                        //X
                        lftest = (lAE.value ==
                                AtomImplicitValence.getImplicitValence(atom));

                        break;

                    case BasicQueryAtom.AL_HEAVY_CONNECT:

                        //Q
                        lftest = (lAE.value == AtomHeavyValence.valence(atom));

                        break;

                    case BasicQueryAtom.AL_NEGATIVE:
                        lftest = (lAE.value == (-1 * (atom.getFormalCharge())));

                        break;

                    case BasicQueryAtom.AL_POSITIVE:
                        lftest = (lAE.value == atom.getFormalCharge());

                        break;

                    case BasicQueryAtom.AL_HYB:
                        lftest = (lAE.value ==
                                AtomHybridisation.getIntValue(atom));

                        break;

                    case BasicQueryAtom.AL_RINGS:

                        if (lAE.value == -1)
                        {
                            lftest = (AtomInRing.isInRing(atom));
                        }
                        else if (lAE.value == 0)
                        {
                            lftest = !(AtomInRing.isInRing(atom));
                        }
                        else
                        {
                            lftest = (AtomInRingsCount.getIntValue(atom) ==
                                    lAE.value);
                        }

                        break;

                    case BasicQueryAtom.AL_SIZE:

                        if (lAE.value == 0)
                        {
                            lftest = !AtomInRing.isInRing(atom);
                        }
                        else
                        {
                            lftest = AtomHelper.isInRingSize(atom, lAE.value);
                        }

                        break;

                    case BasicQueryAtom.AL_IMPLICIT:
                        lftest = (lAE.value ==
                                AtomImplicitHydrogenCount.getIntValue(atom));

                        break;

                    case BasicQueryAtom.AL_CONST:

                        if (recognizeExpH)
                        {
                            lftest = true;
                        }
                        else
                        {
                            lftest = !AtomIsHydrogen.isHydrogen(atom);
                        }

                        break;

                    case BasicQueryAtom.AL_MASS:
                        break;

                    default:
                        break;
                    }

                    size--;

                    break;

                case BasicQueryAtom.AE_ANDHI:
                    bAE = (BasicQueryAtomBinary) expr;

                    if (stack[size + 1] == bAE.right)
                    {
                        size--;
                    }
                    else if (stack[size + 1] == bAE.left)
                    {
                        if (lftest)
                        {
                            size++;
                            stack[size] = bAE.right;
                        }
                        else
                        {
                            size--;
                        }
                    }
                    else
                    {
                        size++;
                        stack[size] = bAE.left;
                    }

                    break;

                case BasicQueryAtom.AE_OR:
                    bAE = (BasicQueryAtomBinary) expr;

                    if (stack[size + 1] == bAE.right)
                    {
                        size--;
                    }
                    else if (stack[size + 1] == bAE.left)
                    {
                        if (!lftest)
                        {
                            size++;
                            stack[size] = bAE.right;
                        }
                        else
                        {
                            size--;
                        }
                    }
                    else
                    {
                        size++;
                        stack[size] = bAE.left;
                    }

                    break;

                case BasicQueryAtom.AE_ANDLO:
                    bAE = (BasicQueryAtomBinary) expr;

                    if (stack[size + 1] == bAE.right)
                    {
                        size--;
                    }
                    else if (stack[size + 1] == bAE.left)
                    {
                        if (lftest)
                        {
                            size++;
                            stack[size] = bAE.right;
                        }
                        else
                        {
                            size--;
                        }
                    }
                    else
                    {
                        size++;
                        stack[size] = bAE.left;
                    }

                    break;

                case BasicQueryAtom.AE_NOT:

                    BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;

                    if (stack[size + 1] != mAE.next)
                    {
                        size++;
                        stack[size] = mAE.next;
                    }
                    else
                    {
                        lftest = !lftest;
                        size--;
                    }

                    break;

                case BasicQueryAtom.AE_RECUR:

                    //see if pattern has been matched
                    BasicQueryAtomPattern rAE = (BasicQueryAtomPattern) expr;
                    boolean matched = false;

                    QueryCache rsCE;

                    for (int i = 0; i < rsCache.size(); i++)
                    {
                        rsCE = (QueryCache) rsCache.get(i);

                        if (rsCE.pattern == (BasicQueryPattern) rAE.recurrent)
                        {
                            lftest = rsCE.booleans[atom.getIndex()];
                            matched = true;

                            break;
                        }
                    }

                    if (!matched)
                    {
                        boolean[] vb =
                            new boolean[atom.getParent().getAtomsSize() + 1];
                        List<int[]> mlist = new Vector<int[]>();

                        // of type int[]
                        int[] itmp;
                        lftest = false;

                        if (match(atom.getParent(),
                                    (BasicQueryPattern) rAE.recurrent, mlist))
                        {
                            for (int i = 0; i < mlist.size(); i++)
                            {
                                itmp = (int[]) mlist.get(i);

                                if (itmp[0] == atom.getIndex())
                                {
                                    lftest = true;
                                }

                                vb[itmp[0]] = true;
                            }
                        }

                        rsCache.add(new QueryCache(
                                (BasicQueryPattern) rAE.recurrent, vb));
                    }

                    size--;

                    break;
                }
            }

            return lftest;
        }
    }

    /**
     * @param  mlist  {@link java.util.Vector} of an <tt>int[]</tt>
     * @param  mol    Description of the Parameter
     * @param  pat    Description of the Parameter
     * @return         <tt>true</tt> SMARTS matching was successfull and pattern occur
     */
    protected boolean match(Molecule mol, QueryPattern pat, List<int[]> mlist)
    {
        if (mol.isEmpty())
        {
            logger.error("Empty molecule '" + mol.getTitle() + "'");

            return false;
        }

        return match(mol, pat, mlist, false);
    }

    /**
     * @param  mlist   {@link java.util.Vector} of an <tt>int[]</tt>
     * @param  mol     Description of the Parameter
     * @param  pat     Description of the Parameter
     * @param  single  Description of the Parameter
     * @return         <tt>true</tt> SMARTS matching was successfull and pattern occur
     */
    protected boolean match(Molecule mol, QueryPattern pat, List<int[]> mlist,
        boolean single)
    {
        /*            if(mol.empty())
                    {
                            logger.error("Empty molecule '"+mol.getTitle()+"'");
                            return false;
                    }
        */
        mlist.clear();

        if ((pat == null) || (pat.getAtomsSize() == 0))
        {
            logger.error("No pattern available.");

            //shouldn't ever happen
            return false;
        }

        if (single && !pat.isChiral())
        {
            fastSingleMatch(mol, pat, mlist);
        }
        else
        {
            SMARTSMatcher ssm = new SMARTSMatcher(this, mol, pat);
            ssm.match(mlist);
        }

        if (logger.isDebugEnabled())
        {
            if (mlist.size() != 0)
            {
                for (int mm = 0; mm < mlist.size(); mm++)
                {
                    int[] ia = (int[]) mlist.get(mm);

                    for (int i = 0; i < ia.length; i++)
                    {
                        System.out.print(" t" + ia[i]);
                    }
                }

                System.out.println("");
            }
        }

        if (pat.isChiral() && mol.has3D())
        {
            int j;
            int k;
            int r1;
            int r2;
            int r3;
            int r4;
            Atom ra1;
            Atom ra2;
            Atom ra3;
            Atom ra4;

            List<int[]> tmpmlist = new Vector<int[]>();
            int[] itmp;

            for (j = 0; j < pat.getAtomsSize(); j++)
            {
                if (pat.getAtom(j).getChiral() != 0)
                {
                    r1 = r2 = r3 = r4 = -1;
                    r2 = j;

                    for (k = 0; k < pat.getBondsSize(); k++)
                    {
                        if (pat.getBond(k).getDestination() == r2)
                        {
                            if (r1 == -1)
                            {
                                r1 = pat.getBond(k).getSource();
                            }
                            else if (r3 == -1)
                            {
                                r3 = pat.getBond(k).getSource();
                            }
                            else if (r4 == -1)
                            {
                                r4 = pat.getBond(k).getSource();
                            }
                        }
                    }

                    for (k = 0; k < pat.getBondsSize(); k++)
                    {
                        if (pat.getBond(k).getSource() == r2)
                        {
                            if (r1 == -1)
                            {
                                r1 = pat.getBond(k).getDestination();
                            }
                            else if (r3 == -1)
                            {
                                r3 = pat.getBond(k).getDestination();
                            }
                            else if (r4 == -1)
                            {
                                r4 = pat.getBond(k).getDestination();
                            }
                        }
                    }

                    if ((r1 == -1) || (r2 == -1) || (r3 == -1) || (r4 == -1))
                    {
                        continue;
                    }

                    tmpmlist.clear();

                    for (int m = 0; m < mlist.size(); m++)
                    {
                        //vtmp = (Vector)mlist.get(m);
                        //itmp = (int[])vtmp.get(r1);   ra1 = mol.getAtom( itmp[0] );
                        //itmp = (int[])vtmp.get(r2);   ra2 = mol.getAtom( itmp[0] );
                        //itmp = (int[])vtmp.get(r3);   ra3 = mol.getAtom( itmp[0] );
                        //itmp = (int[])vtmp.get(r4);   ra4 = mol.getAtom( itmp[0] );
                        itmp = (int[]) mlist.get(m);
                        ra1 = mol.getAtom(itmp[r1]);
                        ra2 = mol.getAtom(itmp[r2]);
                        ra3 = mol.getAtom(itmp[r3]);
                        ra4 = mol.getAtom(itmp[r4]);

                        double sign = BasicVector3D.calcTorsionAngle(ra1
                                .getCoords3D(), ra2.getCoords3D(),
                                ra3.getCoords3D(), ra4.getCoords3D());

                        if ((sign > 0.0) &&
                                (pat.getAtom(j).getChiral() ==
                                    BasicQueryAtom.AL_ANTICLOCKWISE))
                        {
                            continue;
                        }

                        if ((sign < 0.0) &&
                                (pat.getAtom(j).getChiral() ==
                                    BasicQueryAtom.AL_CLOCKWISE))
                        {
                            continue;
                        }

                        //ok - go ahead and save it
                        //tmpmlist.put( vtmp );
                        tmpmlist.add(itmp);
                    }

                    mlist = tmpmlist;
                }
            }
        }

        //if(mlist.size()==0)logger.info("No matching found.");
        return (mlist.size() != 0);
    }

    //public static Pattern parseSMARTSRecord( String smartsp)
    //{
    //  return parseSMARTSRecord( smartsp.toCharArray() );
    //}
    //
    //public static Pattern parseSMARTSRecord( char ptr[])
    //{
    //  return parseSMARTSRecord(ptr, 0, ptr.length);
    //}

    /**
     *  Description of the Method
     *
     * @param  ptr       Description of the Parameter
     * @param  ptrIndex  Description of the Parameter
     * @param  _theEnd   Description of the Parameter
     * @return           Description of the Return Value
     */
    protected QueryPattern parseSMARTSRecord(char[] ptr, int ptrIndex,
        int _theEnd)
    {
        char[] src;
        int srcIndex = 0;
        theEnd = _theEnd;

        src = ptr;
        srcIndex = ptrIndex;

        while ((srcIndex <= theEnd) && !Character.isSpaceChar(src[srcIndex]))
        {
            //      System.out.print(" "+srcIndex);
            srcIndex++;
        }

        if (srcIndex < ptr.length)
        {
            if (Character.isSpaceChar(src[srcIndex]))
            {
                theEnd = (srcIndex++);

                //lexPtr[srcIndex++] = '\0';
                while (Character.isSpaceChar(src[srcIndex]))
                {
                    srcIndex++;
                }
            }
        }

        //    dst = descr;
        //    int descrIndex = 0;
        //    while( srcIndex<=theEnd && (dstIndex<descrIndex+78) )
        //    {
        //      if( Character.isSpaceChar(src[srcIndex]) )
        //      {
        //        dst[dstIndex++] = ' ';
        //        while( Character.isSpaceChar(src[srcIndex]) ) srcIndex++;
        //      }
        //      else dst[dstIndex++] = src[srcIndex++];
        //    }
        //    //theEndDescr=dstIndex;//lexPtr[dstIndex] = '\0';
        return parseSMARTSString(buffer, 0, theEnd);
    }

    /**
     *  Description of the Method
     *
     * @param  pat   Description of the Parameter
     * @param  part  Description of the Parameter
     * @return       Description of the Return Value
     */
    static int findSMARTSRoot(BasicQueryPattern pat, int part)
    {
        int i;

        for (i = 0; i < pat.queryAtomsSize; i++)
        {
            if (pat.queryAtoms[i].getPart() == part)
            {
                if (pat.queryAtoms[i].getVisit() == 0)
                {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     *  Description of the Method
     *
     * @param  ptr       Description of the Parameter
     * @param  ptrIndex  Description of the Parameter
     */
    void fatalAllocationError(char[] ptr, int ptrIndex)
    {
        char[] c = new char[theEnd - ptrIndex + 1];
        System.arraycopy(ptr, ptrIndex, c, 0, theEnd - ptrIndex + 1);
        logger.error("Error: Unable to allocate " + (new String(c)) + "!\n");
        System.exit(1);
    }

    /**
     *  Description of the Method
     *
     * @param  pat   Description of the Parameter
     * @param  stat  Description of the Parameter
     * @param  i     Description of the Parameter
     * @param  os    Description of the Parameter
     */
    private static void displaySMARTSPart(BasicQueryPattern pat,
        ParserState stat, int i, OutputStream os)
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

        int count;
        int j;
        int k;
        int l;

        BasicQueryAtom.displaySMARTSAtom(pat.queryAtoms[i].getAtom(), fp);
        pat.queryAtoms[i].setVisit(1);

        /* Generate Closure Definitions!    */
        for (j = 0; j < pat.queryBondsSize; j++)
        {
            if (pat.queryBonds[j].getVisit() != i)
            {
                if ((pat.queryBonds[j].getSource() == i) ||
                        (pat.queryBonds[j].getDestination() == i))
                {
                    k = (pat.queryBonds[j].getSource() == i)
                        ? pat.queryBonds[j].getDestination()
                        : pat.queryBonds[j].getSource();

                    if (pat.queryAtoms[k].getVisit() == 0)
                    {
                        while (stat.closure[stat.closindex] != -1)
                        {
                            stat.closindex = (stat.closindex + 1) % 100;
                        }

                        stat.closure[stat.closindex] = j;

                        if (stat.closindex > 9)
                        {
                            fp.printf("%%%d", stat.closindex);
                        }
                        else
                        {
                            fp.print(stat.closindex + '0');
                        }

                        stat.closindex++;
                    }
                }
            }
        }

        /* Generate Closure Bonds!    */
        count = 0;

        for (j = 0; j < pat.queryBondsSize; j++)
        {
            if (pat.queryBonds[j].getVisit() == i)
            {
                if (pat.queryBonds[j].getSource() == i)
                {
                    k = pat.queryBonds[j].getDestination();
                }
                else
                {
                    /* pat.bond[j].dst == i    */
                    k = pat.queryBonds[j].getSource();
                }

                if (pat.queryAtoms[k].getVisit() != 0)
                {
                    BasicQueryBond.displaySMARTSBond(pat.queryBonds[j]
                        .getBond(), fp);

                    for (l = 0; l < 100; l++)
                    {
                        if (stat.closure[l] == j)
                        {
                            break;
                        }
                    }

                    if (l > 9)
                    {
                        fp.printf("%%%d", l);
                    }
                    else
                    {
                        fp.print(l + '0');
                    }
                }
                else
                {
                    count++;
                }
            }
        }

        /* Generate Non-closure Bonds!    */
        for (j = 0; j < pat.queryBondsSize; j++)
        {
            if (pat.queryBonds[j].getVisit() == i)
            {
                if (pat.queryBonds[j].getSource() == i)
                {
                    k = pat.queryBonds[j].getDestination();
                }
                else
                {
                    /* pat.bond[j].dst == i    */
                    k = pat.queryBonds[j].getSource();
                }

                if (pat.queryAtoms[k].getVisit() == 0)
                {
                    if (count > 1)
                    {
                        fp.print('(');
                    }

                    BasicQueryBond.displaySMARTSBond(pat.queryBonds[j]
                        .getBond(), fp);
                    displaySMARTSPart(pat, stat, k, fp);

                    if (count > 1)
                    {
                        fp.print(')');
                    }

                    count--;
                }
            }
        }
    }

    /**
     *  Gets the chiralFlag attribute of the ParseSmart class
     *
     * @param  expr  Description of the Parameter
     * @return       The chiralFlag value
     */
    private static int getChiralFlag(QueryAtom expr)
    {
        int size = 0;
        QueryAtom[] stack = new QueryAtom[STACKSIZE];
        boolean lftest = true;
        QueryAtomBinary bAE = null;

        for (size = 0, stack[size] = expr; size >= 0; expr = stack[size])
        {
            switch (expr.getType())
            {
            case BasicQueryAtom.AE_LEAF:

                BasicQueryAtomValue lAE = (BasicQueryAtomValue) expr;

                if (lAE.label == BasicQueryAtom.AL_CHIRAL)
                {
                    return (lAE.value);
                }

                size--;

                break;

            case BasicQueryAtom.AE_ANDHI:
            case BasicQueryAtom.AE_ANDLO:
                bAE = (BasicQueryAtomBinary) expr;

                if (stack[size + 1] == bAE.getRight())
                {
                    size--;
                }
                else if (stack[size + 1] == bAE.getLeft())
                {
                    if (lftest)
                    {
                        size++;
                        stack[size] = bAE.getRight();
                    }
                    else
                    {
                        size--;
                    }
                }
                else
                {
                    size++;
                    stack[size] = bAE.getLeft();
                }

                break;

            case BasicQueryAtom.AE_OR:
                bAE = (BasicQueryAtomBinary) expr;

                if (stack[size + 1] == bAE.getRight())
                {
                    size--;
                }
                else if (stack[size + 1] == bAE.getLeft())
                {
                    if (!lftest)
                    {
                        size++;
                        stack[size] = bAE.getRight();
                    }
                    else
                    {
                        size--;
                    }
                }
                else
                {
                    size++;
                    stack[size] = bAE.getLeft();
                }

                break;

            case BasicQueryAtom.AE_NOT:

                BasicQueryAtomMono mAE = (BasicQueryAtomMono) expr;

                if (stack[size + 1] != mAE.next)
                {
                    size++;
                    stack[size] = mAE.next;
                }
                else
                {
                    lftest = !lftest;
                    size--;
                }

                break;

            case BasicQueryAtom.AE_RECUR:
                size--;

                break;

            //         default: Vector vvv=null;
            //                  vvv.add(new Object());
            //                  logger.error("AtomExpr type is not properly defined ParseSmart.getChiralFlag");
            //                  System.exit(1);
            }

            size--;

            if (size < 0)
            {
                return 0;
            }
        }

        return 0;
    }

    /**
     *  Description of the Method
     *
     * @param  pat  Description of the Parameter
     */
    private static void markGrowBonds(QueryPattern pat)
    {
        int i;
        BasicBitVector bv = new BasicBitVector();

        for (i = 0; i < pat.getBondsSize(); i++)
        {
            pat.getBond(i).setGrow(
                (bv.get(pat.getBond(i).getSource()) &&
                    bv.get(pat.getBond(i).getDestination())) ? false : true);

            bv.setBitOn(pat.getBond(i).getSource());
            bv.setBitOn(pat.getBond(i).getDestination());
        }
    }

    /**
     *  Description of the Method
     *
     * @param  pat  Description of the Parameter
     * @param  i    Description of the Parameter
     */
    private static void traverseSMARTS(QueryPattern pat, int i)
    {
        int j;
        int k;

        pat.getAtom(i).setVisit(ITRUE);

        for (j = 0; j < pat.getBondsSize(); j++)
        {
            if (pat.getBond(j).getVisit() == -1)
            {
                if (pat.getBond(j).getSource() == i)
                {
                    pat.getBond(j).setVisit(i);
                    k = pat.getBond(j).getDestination();

                    if (pat.getAtom(k).getVisit() != 0)
                    {
                        traverseSMARTS(pat, k);
                    }
                }
                else if (pat.getBond(j).getDestination() == i)
                {
                    pat.getBond(j).setVisit(i);
                    k = pat.getBond(j).getSource();

                    if (pat.getAtom(k).getVisit() != 0)
                    {
                        traverseSMARTS(pat, k);
                    }
                }
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  pat   Description of the Parameter
     * @param  stat  Description of the Parameter
     * @param  prev  Description of the Parameter
     * @param  part  Description of the Parameter
     * @return       Description of the Return Value
     */
    private QueryPattern createSMARTSParser(QueryPattern pat, ParserState stat,
        int prev, int part)
    {
        int vb = 0;
        QueryAtom aexpr;
        QueryBond bexpr;
        int index;

        bexpr = null;

        while (lexPtrIndex <= theEnd)
        {
            switch (lexPtr[lexPtrIndex++])
            {
            case ('.'):

                if ((bexpr != null) || (prev == -1))
                {
                    return parseSMARTSError(pat, bexpr);
                }

                prev = -1;

                break;

            case ('-'):
            case ('='):
            case ('#'):
            case (':'):
            case ('~'):
            case ('@'):
            case ('/'):
            case ('\\'):
            case ('!'):
                lexPtrIndex--;

                if ((prev == -1) || (bexpr != null))
                {
                    return parseSMARTSError(pat, bexpr);
                }

                bexpr = parseBondExpr(0);

                if (bexpr == null)
                {
                    return parseSMARTSError(pat, bexpr);
                }

                break;

            case ('('):

                if (STRICT)
                {
                    if ((prev == -1) || (bexpr != null))
                    {
                        lexPtrIndex--;

                        return parseSMARTSError(pat, bexpr);
                    }

                    pat = createSMARTSParser(pat, stat, prev, part);

                    if (pat == null)
                    {
                        return null;
                    }
                }
                else
                {
                    if (bexpr != null)
                    {
                        lexPtrIndex--;

                        return parseSMARTSError(pat, bexpr);
                    }

                    if (prev == -1)
                    {
                        index = pat.getAtomsSize();
                        pat = createSMARTSParser(pat, stat, -1, part);

                        if (pat == null)
                        {
                            return null;
                        }

                        if (index == pat.getAtomsSize())
                        {
                            return parseSMARTSError(pat, bexpr);
                        }

                        prev = index;
                    }
                    else
                    {
                        pat = createSMARTSParser(pat, stat, prev, part);

                        if (pat == null)
                        {
                            return null;
                        }
                    }
                }

                if (lexPtr[lexPtrIndex] != ')')
                {
                    return parseSMARTSError(pat, bexpr);
                }

                lexPtrIndex++;

                break;

            case (')'):
                lexPtrIndex--;

                if ((prev == -1) || (bexpr != null))
                {
                    return parseSMARTSError(pat, bexpr);
                }

                return pat;

            case ('%'):

                if (prev == -1)
                {
                    lexPtrIndex--;

                    return parseSMARTSError(pat, bexpr);
                }

                if (Character.isDigit(lexPtr[lexPtrIndex]) &&
                        Character.isDigit(lexPtr[lexPtrIndex + 1]))
                {
                    index = (10 * (lexPtr[lexPtrIndex] - '0')) +
                        (lexPtr[lexPtrIndex + 1] - '0');
                    lexPtrIndex += 2;
                }
                else
                {
                    return parseSMARTSError(pat, bexpr);
                }

                if (stat.closure[index] == -1)
                {
                    stat.closord[index] = bexpr;
                    stat.closure[index] = prev;
                }
                else if (stat.closure[index] != prev)
                {
                    BasicQueryBond.freeBondExpr(stat.closord[index]);

                    if (bexpr == null)
                    {
                        bexpr = BasicQueryBond.generateDefaultBond();
                    }

                    BasicQueryPattern.createBond(pat, bexpr, prev,
                        stat.closure[index]);
                    stat.closure[index] = -1;
                    bexpr = null;
                }
                else
                {
                    return parseSMARTSError(pat, bexpr);
                }

                break;

            case ('0'):
            case ('1'):
            case ('2'):
            case ('3'):
            case ('4'):
            case ('5'):
            case ('6'):
            case ('7'):
            case ('8'):
            case ('9'):
                lexPtrIndex--;

                if (prev == -1)
                {
                    return parseSMARTSError(pat, bexpr);
                }

                index = (lexPtr[lexPtrIndex++]) - '0';

                //System.out.println("Closure: "+index+" stat.closure[index]="+stat.closure[index]+" prev="+prev);
                if (stat.closure[index] == -1)
                {
                    stat.closord[index] = bexpr;
                    stat.closure[index] = prev;
                    bexpr = null;
                }
                else if (stat.closure[index] != prev)
                {
                    BasicQueryBond.freeBondExpr(stat.closord[index]);

                    if (bexpr == null)
                    {
                        bexpr = BasicQueryBond.generateDefaultBond();
                    }

                    BasicQueryPattern.createBond(pat, bexpr, prev,
                        stat.closure[index]);
                    stat.closure[index] = -1;
                    bexpr = null;
                }
                else
                {
                    //System.out.println("ERROR in closure bond.");
                    return parseSMARTSError(pat, bexpr);
                }

                break;

            case ('['):
                aexpr = parseAtomExpr(0);
                vb = (lexPtr[lexPtrIndex] == ':') ? getVectorBinding() : 0;

                if ((aexpr == null) || (lexPtr[lexPtrIndex] != ']'))
                {
                    return parseSMARTSError(pat, bexpr);
                }

                index = BasicQueryPattern.createAtom(pat, aexpr, part, vb);

                if (prev != -1)
                {
                    if (bexpr == null)
                    {
                        bexpr = BasicQueryBond.generateDefaultBond();
                    }

                    BasicQueryPattern.createBond(pat, bexpr, prev, index);
                    bexpr = null;
                }

                prev = index;
                lexPtrIndex++;

                break;

            default:
                lexPtrIndex--;
                aexpr = parseSimpleAtomPrimitive();

                if (aexpr == null)
                {
                    return parseSMARTSError(pat, bexpr);
                }

                index = BasicQueryPattern.createAtom(pat, aexpr, part);

                if (prev != -1)
                {
                    if (bexpr == null)
                    {
                        bexpr = BasicQueryBond.generateDefaultBond();
                    }

                    BasicQueryPattern.createBond(pat, bexpr, prev, index);
                    bexpr = null;
                }

                prev = index;
            }
        }

        if ((prev == -1) || (bexpr != null))
        {
            return parseSMARTSError(pat, bexpr);
        }

        return pat;
    }

    /**
     * @param  mlist  {@link java.util.Vector} of <tt>int[]</tt>
     * @param  mol    Description of the Parameter
     * @param  pat    Description of the Parameter
     */
    private void fastSingleMatch(Molecule mol, QueryPattern pat,
        List<int[]> mlist)
    {
        if (mol.isEmpty())
        {
            logger.error("Empty molecule '" + mol.getTitle() + "'");

            return;
        }

        Atom atom;
        Atom a1;
        Atom nbr;
        AtomIterator ait = mol.atomIterator();
        BitVector bitVector = new BasicBitVector(mol.getAtomsSize() + 1);
        int[] map = new int[pat.getAtomsSize()];
        List<NbrAtomIterator> nbrIterList = new Vector<NbrAtomIterator>();

        if (pat.getBondsSize() != 0)
        {
            if (nbrIterList instanceof Vector)
            {
                ((Vector) nbrIterList).setSize(pat.getBondsSize());
            }
        }

        int bcount;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (evalAtomExpr(pat.getAtom(0).getAtom(), atom))
            {
                map[0] = atom.getIndex();

                if (pat.getBondsSize() != 0)
                {
                    nbrIterList.set(0, null);
                }

                bitVector.clear();
                bitVector.setBitOn(atom.getIndex());

                for (bcount = 0; bcount >= 0;)
                {
                    //***entire pattern matched***
                    if (bcount == pat.getBondsSize())
                    {
                        //save full match here
                        mlist.add(map);
                        bcount--;

                        return;

                        //found a single match
                    }

                    //***match the next bond***
                    if (!pat.getBond(bcount).isGrow())
                    {
                        //just check bond here
                        if (nbrIterList.get(bcount) == null)
                        {
                            Bond bond = mol.getBond(
                                    map[pat.getBond(bcount).getSource()],
                                    map[pat.getBond(bcount).getDestination()]);

                            if ((bond != null) &&
                                    evalBondExpr(pat.getBond(bcount).getBond(),
                                        bond))
                            {
                                nbrIterList.set(bcount++,
                                    new BasicNbrAtomIterator(null, null));

                                if (bcount < pat.getBondsSize())
                                {
                                    nbrIterList.set(bcount, null);
                                }
                            }
                            else
                            {
                                nbrIterList.set(bcount--, null);
                            }
                        }
                        else
                        {
                            //bond must have already been visited - backtrack
                            bcount--;
                        }
                    }
                    else
                    {
                        //need to map atom and check bond
                        a1 = mol.getAtom(map[pat.getBond(bcount).getSource()]);

                        if (nbrIterList.get(bcount) == null)
                        {
                            //figure out which nbr atom we are mapping
                            //nbr = a1.beginNbrAtom(vi[bcount]);
                            NbrAtomIterator nait = a1.nbrAtomIterator();
                            nbrIterList.set(bcount, nait);
                            nbr = nait.nextNbrAtom();
                        }
                        else
                        {
                            bitVector.setBitOff(
                                map[pat.getBond(bcount).getDestination()]);

                            //nbr = a1.nextNbrAtom(vi[bcount]);
                            if (((BasicNbrAtomIterator) nbrIterList.get(
                                            bcount)).hasNext())
                            {
                                nbr =
                                    ((BasicNbrAtomIterator) nbrIterList.get(
                                            bcount)).nextNbrAtom();
                            }
                            else
                            {
                                nbr = null;
                            }
                        }

                        for (;
                                (nbr != null) &&
                                ((BasicNbrAtomIterator) nbrIterList.get(
                                        bcount)).hasNext();
                                nbr =
                                    ((BasicNbrAtomIterator) nbrIterList.get(
                                            bcount)).nextNbrAtom())
                        {
                            //a1.nextNbrAtom(vi[bcount]))
                            if (!bitVector.get(nbr.getIndex()))
                            {
                                if (evalAtomExpr(
                                            pat.getAtom(
                                                pat.getBond(bcount)
                                                .getDestination()).getAtom(),
                                            nbr) &&
                                        evalBondExpr(
                                            pat.getBond(bcount).getBond(),
                                            ((BasicNbrAtomIterator) nbrIterList
                                                .get(bcount)).actualBond()))
                                {
                                    bitVector.setBitOn(nbr.getIndex());
                                    map[pat.getBond(bcount).getDestination()] =
                                        nbr.getIndex();
                                    bcount++;

                                    if (bcount < pat.getBondsSize())
                                    {
                                        nbrIterList.set(bcount, null);
                                    }

                                    break;
                                }
                            }
                        }

                        if (nbr == null)
                        {
                            //no match - time to backtrack
                            bcount--;
                        }
                    }
                }
            }
        }
    }

    /**
     *  Gets the vectorBinding attribute of the ParseSmart class
     *
     * @return    The vectorBinding value
     */
    private int getVectorBinding()
    {
        int vb = 0;

        lexPtrIndex++;

        //skip colon
        if (Character.isDigit(lexPtr[lexPtrIndex]))
        {
            vb = 0;

            while (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                vb = (vb * 10) + ((lexPtr[lexPtrIndex++]) - '0');
            }
        }

        //System.out.println("parsed vb:"+vb);
        return (vb);
    }

    /**
     *  Description of the Method
     *
     * @param  level  Description of the Parameter
     * @return        Description of the Return Value
     */
    private QueryAtom parseAtomExpr(int level)
    {
        QueryAtom expr1;
        QueryAtom expr2;
        int prev;

        switch (level)
        {
        case (0):

            /* Low Precedence Conjunction    */
            expr1 = parseAtomExpr(1);

            if (expr1 == null)
            {
                return null;
            }

            while (lexPtr[lexPtrIndex] == ';')
            {
                lexPtrIndex++;
                expr2 = parseAtomExpr(1);

                if (expr2 == null)
                {
                    BasicQueryAtom.freeAtomExpr(expr1);

                    return null;
                }

                expr1 = BasicQueryAtom.buildAtomBin(BasicQueryAtom.AE_ANDLO,
                        expr1, expr2);
            }

            return expr1;

        case (1):

            /* Disjunction    */
            expr1 = parseAtomExpr(2);

            if (expr1 == null)
            {
                return null;
            }

            while (lexPtr[lexPtrIndex] == ',')
            {
                lexPtrIndex++;
                expr2 = parseAtomExpr(2);

                if (expr2 == null)
                {
                    BasicQueryAtom.freeAtomExpr(expr1);

                    return null;
                }

                expr1 = BasicQueryAtom.buildAtomBin(BasicQueryAtom.AE_OR, expr1,
                        expr2);
            }

            return (expr1);

        case (2):

            /* High Precedence Conjunction    */
            expr1 = parseAtomExpr(3);

            if (expr1 == null)
            {
                return null;
            }

            while ((lexPtr[lexPtrIndex] != ']') &&
                    (lexPtr[lexPtrIndex] != ';') &&
                    (lexPtr[lexPtrIndex] != ',') && (lexPtrIndex <= theEnd))
            {
                if (lexPtr[lexPtrIndex] == '&')
                {
                    lexPtrIndex++;
                }

                prev = lexPtrIndex;
                expr2 = parseAtomExpr(3);

                if (expr2 == null)
                {
                    if (prev != lexPtrIndex)
                    {
                        BasicQueryAtom.freeAtomExpr(expr1);

                        return null;
                    }
                    else
                    {
                        return (expr1);
                    }
                }

                expr1 = BasicQueryAtom.buildAtomBin(BasicQueryAtom.AE_ANDHI,
                        expr1, expr2);
            }

            return (expr1);

        case (3):

            /* Negation or Primitive    */
            if (lexPtr[lexPtrIndex] == '!')
            {
                lexPtrIndex++;
                expr1 = parseAtomExpr(3);

                if (expr1 == null)
                {
                    return null;
                }

                return (BasicQueryAtom.buildAtomNot(expr1));
            }

            return (parseComplexAtomPrimitive());
        }

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  level  Description of the Parameter
     * @return        Description of the Return Value
     */
    private QueryBond parseBondExpr(int level)
    {
        QueryBond expr1;
        QueryBond expr2;
        int prev;

        switch (level)
        {
        case (0):

            /* Low Precedence Conjunction    */
            expr1 = parseBondExpr(1);

            if (expr1 == null)
            {
                return null;
            }

            while (lexPtr[lexPtrIndex] == ';')
            {
                lexPtrIndex++;
                expr2 = parseBondExpr(1);

                if (expr2 == null)
                {
                    BasicQueryBond.freeBondExpr(expr1);

                    return null;
                }

                expr1 = BasicQueryBond.buildBondBin(BasicQueryBond.BE_ANDLO,
                        expr1, expr2);
            }

            return expr1;

        case (1):

            /* Disjunction    */
            expr1 = parseBondExpr(2);

            if (expr1 == null)
            {
                return null;
            }

            while (lexPtr[lexPtrIndex] == ',')
            {
                lexPtrIndex++;
                expr2 = parseBondExpr(2);

                if (expr2 == null)
                {
                    BasicQueryBond.freeBondExpr(expr1);

                    return null;
                }

                expr1 = BasicQueryBond.buildBondBin(BasicQueryBond.BE_OR, expr1,
                        expr2);
            }

            return expr1;

        case (2):

            /* High Precedence Conjunction    */
            expr1 = parseBondExpr(3);

            if (expr1 == null)
            {
                return null;
            }

            while ((lexPtr[lexPtrIndex] != ']') &&
                    (lexPtr[lexPtrIndex] != ';') &&
                    (lexPtr[lexPtrIndex] != ',') && (lexPtrIndex <= theEnd))
            {
                if (lexPtr[lexPtrIndex] == '&')
                {
                    lexPtrIndex++;
                }

                prev = lexPtrIndex;
                expr2 = parseBondExpr(3);

                if (expr2 == null)
                {
                    if (prev != lexPtrIndex)
                    {
                        BasicQueryBond.freeBondExpr(expr1);

                        return null;
                    }
                    else
                    {
                        return expr1;
                    }
                }

                expr1 = BasicQueryBond.buildBondBin(BasicQueryBond.BE_ANDHI,
                        expr1, expr2);
            }

            return expr1;

        case (3):

            /* Negation or Primitive    */
            if (lexPtr[lexPtrIndex] == '!')
            {
                lexPtrIndex++;
                expr1 = parseBondExpr(3);

                if (expr1 == null)
                {
                    return null;
                }

                return BasicQueryBond.buildBondNot(expr1);
            }

            return parseBondPrimitive();
        }

        return null;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private BasicQueryBond parseBondPrimitive()
    {
        switch (lexPtr[lexPtrIndex++])
        {
        case ('-'):
            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                    BasicQueryBond.BT_SINGLE);

        case ('='):
            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                    BasicQueryBond.BT_DOUBLE);

        case ('#'):
            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                    BasicQueryBond.BT_TRIPLE);

        case (':'):
            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                    BasicQueryBond.BT_AROM);

        case ('@'):
            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                    BasicQueryBond.BT_RING);

        case ('~'):
            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_CONST, ITRUE);

        case ('/'):

            if (lexPtr[lexPtrIndex] == '?')
            {
                lexPtrIndex++;

                return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                        BasicQueryBond.BT_UPUNSPEC);
            }

            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                    BasicQueryBond.BT_UP);

        case ('\\'):

            if (lexPtr[lexPtrIndex] == '?')
            {
                lexPtrIndex++;

                return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                        BasicQueryBond.BT_DOWNUNSPEC);
            }

            return BasicQueryBond.buildBondLeaf(BasicQueryBond.BL_TYPE,
                    BasicQueryBond.BT_DOWN);
        }

        lexPtrIndex--;

        return null;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private QueryAtom parseComplexAtomPrimitive()
    {
        QueryPattern pat;
        int index;

        switch (lexPtr[lexPtrIndex++])
        {
        case ('#'):

            if (!Character.isDigit(lexPtr[lexPtrIndex]))
            {
                if (lexPtr[lexPtrIndex] == 'N')
                {
                    lexPtrIndex++;

                    return BasicQueryAtom.buildAtomLeaf(
                            BasicQueryAtom.AL_ELECTRONEGATIVE, 0);
                }
                else
                {
                    return null;
                }
            }

            index = 0;

            while (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = (index * 10) + (lexPtr[lexPtrIndex++] - '0');
            }

            if (index > ELEMMAX)
            {
                lexPtrIndex--;

                return null;
            }
            else if (index == 0)
            {
                return null;
            }

            return BasicQueryAtom.generateElement(index);

        case ('$'):

            if (lexPtr[lexPtrIndex] != '(')
            {
                return null;
            }

            lexPtrIndex++;

            if (STRICT)
            {
                pat = parseSMARTSPart(BasicQueryPattern.allocPattern(), 0);
            }
            else
            {
                pat = parseSMARTSPattern();
            }

            if (pat == null)
            {
                return null;
            }

            if (lexPtr[lexPtrIndex] != ')')
            {
                BasicQueryPattern.freePattern(pat);

                return null;
            }

            lexPtrIndex++;

            return BasicQueryAtom.buildAtomRecurs(pat);

        case ('*'):
            return (BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_CONST,
                        ITRUE));

        case ('+'):

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + (lexPtr[lexPtrIndex++] - '0');
                }
            }
            else
            {
                index = 1;

                while (lexPtr[lexPtrIndex] == '+')
                {
                    lexPtrIndex++;
                    index++;
                }
            }

            return (BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_POSITIVE,
                        index));

        case ('-'):

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + (lexPtr[lexPtrIndex++] - '0');
                }
            }
            else
            {
                index = 1;

                while (lexPtr[lexPtrIndex] == '-')
                {
                    lexPtrIndex++;
                    index++;
                }
            }

            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_NEGATIVE,
                    index);

        case '@':

            if (lexPtr[lexPtrIndex] != '@')
            {
                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_CHIRAL,
                        BasicQueryAtom.AL_ANTICLOCKWISE);
            }

            lexPtrIndex++;

            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_CHIRAL,
                    BasicQueryAtom.AL_CLOCKWISE);

        case '^':

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + (lexPtr[lexPtrIndex++] - '0');
                }

                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_HYB,
                        index);
            }
            else
            {
                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_HYB, 1);
            }

        case ('0'):
        case ('1'):
        case ('2'):
        case ('3'):
        case ('4'):
        case ('5'):
        case ('6'):
        case ('7'):
        case ('8'):
        case ('9'):
            index = (lexPtr[lexPtrIndex - 1]) - '0';

            while (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = (index * 10) + (lexPtr[lexPtrIndex++] - '0');
            }

            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_MASS, index);

        case ('A'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('c'):
                return BasicQueryAtom.generateElement(89);

            case ('g'):
                return BasicQueryAtom.generateElement(47);

            case ('l'):
                return BasicQueryAtom.generateElement(13);

            case ('m'):
                return BasicQueryAtom.generateElement(95);

            case ('r'):
                return BasicQueryAtom.generateElement(18);

            case ('s'):
                return BasicQueryAtom.generateElement(33);

            case ('t'):
                return BasicQueryAtom.generateElement(85);

            case ('u'):
                return BasicQueryAtom.generateElement(79);
            }

            lexPtrIndex--;

            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_AROM, 0);

        case ('B'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('a'):
                return BasicQueryAtom.generateElement(56);

            case ('e'):
                return BasicQueryAtom.generateElement(4);

            case ('i'):
                return BasicQueryAtom.generateElement(83);

            case ('k'):
                return BasicQueryAtom.generateElement(97);

            case ('r'):
                return BasicQueryAtom.generateElement(35);
            }

            lexPtrIndex--;

            return BasicQueryAtom.generateElement(5);

        case ('C'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('a'):
                return BasicQueryAtom.generateElement(20);

            case ('d'):
                return BasicQueryAtom.generateElement(48);

            case ('e'):
                return BasicQueryAtom.generateElement(58);

            case ('f'):
                return BasicQueryAtom.generateElement(98);

            case ('l'):
                return BasicQueryAtom.generateElement(17);

            case ('m'):
                return BasicQueryAtom.generateElement(96);

            case ('o'):
                return BasicQueryAtom.generateElement(27);

            case ('r'):
                return BasicQueryAtom.generateElement(24);

            case ('s'):
                return BasicQueryAtom.generateElement(55);

            case ('u'):
                return BasicQueryAtom.generateElement(29);
            }

            lexPtrIndex--;

            return BasicQueryAtom.generateAromElem(6, IFALSE);

        case ('D'):

            if (lexPtr[lexPtrIndex] == 'y')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(66);
            }
            else if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + (lexPtr[lexPtrIndex++] - '0');
                }

                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_DEGREE,
                        index);
            }

            break;

        case ('E'):

            if (lexPtr[lexPtrIndex] == 'r')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(68);
            }
            else if (lexPtr[lexPtrIndex] == 's')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(99);
            }
            else if (lexPtr[lexPtrIndex] == 'u')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(63);
            }

            break;

        case ('F'):

            if (lexPtr[lexPtrIndex] == 'e')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(26);
            }
            else if (lexPtr[lexPtrIndex] == 'm')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(100);
            }
            else if (lexPtr[lexPtrIndex] == 'r')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(87);
            }

            return BasicQueryAtom.generateElement(9);

        case ('G'):

            if (lexPtr[lexPtrIndex] == 'a')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(31));
            }
            else if (lexPtr[lexPtrIndex] == 'd')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(64));
            }
            else if (lexPtr[lexPtrIndex] == 'e')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(32));
            }
            else if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + ((lexPtr[lexPtrIndex++]) - '0');
                }

                return (BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_GROUP,
                            index));
            }

            break;

        case ('H'):

            if (lexPtr[lexPtrIndex] == 'e')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(2));
            }
            else if (lexPtr[lexPtrIndex] == 'f')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(72));
            }
            else if (lexPtr[lexPtrIndex] == 'g')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(80));
            }
            else if (lexPtr[lexPtrIndex] == 'o')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(67));
            }
            else if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + (lexPtr[lexPtrIndex++] - '0');
                }

                return (BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_HCOUNT,
                            index));
            }

            return (BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_HCOUNT, 1));

        /* AtomExpr.buildAtomLeaf(AtomExpr.AL_HCOUNT,1) ???    */
        /* or else AtomExpr.generateElement(1) ???    */
        case ('I'):

            if (lexPtr[lexPtrIndex] == 'n')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(49));
            }
            else if (lexPtr[lexPtrIndex] == 'r')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(77));
            }

            return (BasicQueryAtom.generateElement(53));

        case ('K'):

            if (lexPtr[lexPtrIndex] == 'r')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(36));
            }

            return (BasicQueryAtom.generateElement(19));

        case ('L'):

            if (lexPtr[lexPtrIndex] == 'a')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(57));
            }
            else if (lexPtr[lexPtrIndex] == 'i')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(3));
            }
            else if (lexPtr[lexPtrIndex] == 'r')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(103));
            }
            else if (lexPtr[lexPtrIndex] == 'u')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(71));
            }

            break;

        case ('M'):

            if (lexPtr[lexPtrIndex] == 'd')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(101));
            }
            else if (lexPtr[lexPtrIndex] == 'g')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(12));
            }
            else if (lexPtr[lexPtrIndex] == 'n')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(25));
            }
            else if (lexPtr[lexPtrIndex] == 'o')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(42));
            }

            break;

        case ('N'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('a'):
                return (BasicQueryAtom.generateElement(11));

            case ('b'):
                return (BasicQueryAtom.generateElement(41));

            case ('d'):
                return (BasicQueryAtom.generateElement(60));

            case ('e'):
                return (BasicQueryAtom.generateElement(10));

            case ('i'):
                return (BasicQueryAtom.generateElement(28));

            case ('o'):
                return (BasicQueryAtom.generateElement(102));

            case ('p'):
                return (BasicQueryAtom.generateElement(93));
            }

            lexPtrIndex--;

            return (BasicQueryAtom.generateAromElem(7, IFALSE));

        case ('O'):

            if (lexPtr[lexPtrIndex] == 's')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(76));
            }

            return (BasicQueryAtom.generateAromElem(8, IFALSE));

        case ('P'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('a'):
                return (BasicQueryAtom.generateElement(91));

            case ('b'):
                return (BasicQueryAtom.generateElement(82));

            case ('d'):
                return (BasicQueryAtom.generateElement(46));

            case ('m'):
                return (BasicQueryAtom.generateElement(61));

            case ('o'):
                return (BasicQueryAtom.generateElement(84));

            case ('r'):
                return (BasicQueryAtom.generateElement(59));

            case ('t'):
                return (BasicQueryAtom.generateElement(78));

            case ('u'):
                return (BasicQueryAtom.generateElement(94));
            }

            lexPtrIndex--;

            return (BasicQueryAtom.generateElement(15));

        case ('Q'):

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + ((lexPtr[lexPtrIndex++]) - '0');
                }

                return (BasicQueryAtom.buildAtomLeaf(
                            BasicQueryAtom.AL_HEAVY_CONNECT, index));
            }

            break;

        case ('R'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('a'):
                return (BasicQueryAtom.generateElement(88));

            case ('b'):
                return (BasicQueryAtom.generateElement(37));

            case ('e'):
                return (BasicQueryAtom.generateElement(75));

            case ('h'):
                return (BasicQueryAtom.generateElement(45));

            case ('n'):
                return (BasicQueryAtom.generateElement(86));

            case ('u'):
                return (BasicQueryAtom.generateElement(44));
            }

            lexPtrIndex--;

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + ((lexPtr[lexPtrIndex++]) - '0');
                }
            }
            else
            {
                index = -1;
            }

            return (BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_RINGS,
                        index));

        case ('S'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('b'):
                return (BasicQueryAtom.generateElement(51));

            case ('c'):
                return (BasicQueryAtom.generateElement(21));

            case ('e'):
                return (BasicQueryAtom.generateElement(34));

            case ('i'):
                return (BasicQueryAtom.generateElement(14));

            case ('m'):
                return (BasicQueryAtom.generateElement(62));

            case ('n'):
                return (BasicQueryAtom.generateElement(50));

            case ('r'):
                return (BasicQueryAtom.generateElement(38));
            }

            lexPtrIndex--;

            return (BasicQueryAtom.generateAromElem(16, IFALSE));

        case ('T'):

            switch (lexPtr[lexPtrIndex++])
            {
            case ('a'):
                return (BasicQueryAtom.generateElement(73));

            case ('b'):
                return (BasicQueryAtom.generateElement(65));

            case ('c'):
                return (BasicQueryAtom.generateElement(43));

            case ('e'):
                return (BasicQueryAtom.generateElement(52));

            case ('h'):
                return (BasicQueryAtom.generateElement(90));

            case ('i'):
                return (BasicQueryAtom.generateElement(22));

            case ('l'):
                return (BasicQueryAtom.generateElement(81));

            case ('m'):
                return (BasicQueryAtom.generateElement(69));
            }

            lexPtrIndex--;

            break;

        case ('U'):
            return (BasicQueryAtom.generateElement(92));

        case ('V'):
            return (BasicQueryAtom.generateElement(23));

        case ('W'):
            return (BasicQueryAtom.generateElement(74));

        case ('X'):

            if (lexPtr[lexPtrIndex] == 'e')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(54));
            }
            else if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + ((lexPtr[lexPtrIndex++]) - '0');
                }

                return (BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_CONNECT,
                            index));
            }

            break;

        case ('Y'):

            if (lexPtr[lexPtrIndex] == 'b')
            {
                lexPtrIndex++;

                return (BasicQueryAtom.generateElement(70));
            }

            return (BasicQueryAtom.generateElement(39));

        case ('Z'):

            if (lexPtr[lexPtrIndex] == 'n')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(30);
            }
            else if (lexPtr[lexPtrIndex] == 'r')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(40);
            }

            break;

        case ('a'):

            if (lexPtr[lexPtrIndex] == 's')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateAromElem(33, ITRUE);
            }

            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_AROM, 1);

        case ('c'):
            return BasicQueryAtom.generateAromElem(6, ITRUE);

        case ('h'):

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + ((lexPtr[lexPtrIndex++]) - '0');
                }
            }
            else
            {
                index = 1;
            }

            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_IMPLICIT,
                    index);

        case ('n'):
            return BasicQueryAtom.generateAromElem(7, ITRUE);

        case ('o'):
            return BasicQueryAtom.generateAromElem(8, ITRUE);

        case ('p'):
            return BasicQueryAtom.generateAromElem(15, ITRUE);

        case ('r'):

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + ((lexPtr[lexPtrIndex++]) - '0');
                }

                if (index == 0)
                {
                    return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_RINGS,
                            0);
                }

                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_SIZE,
                        index);
            }

            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_RINGS, -1);

        case ('s'):

            if (lexPtr[lexPtrIndex] == 'i')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateAromElem(14, ITRUE);
            }

            return BasicQueryAtom.generateAromElem(16, ITRUE);

        case ('v'):

            if (Character.isDigit(lexPtr[lexPtrIndex]))
            {
                index = 0;

                while (Character.isDigit(lexPtr[lexPtrIndex]))
                {
                    index = (index * 10) + ((lexPtr[lexPtrIndex++]) - '0');
                }

                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_VALENCE,
                        index);
            }

            break;
        }

        lexPtrIndex--;

        return null;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private QueryAtom parseSimpleAtomPrimitive()
    {
        switch (lexPtr[lexPtrIndex++])
        {
        case '*':
            return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_CONST, ITRUE);

        case 'A':

            if (!STRICT)
            {
                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_AROM,
                        IFALSE);
            }
            else
            {
                break;
            }

        case 'B':

            if (lexPtr[lexPtrIndex] == 'r')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(35);
            }

            return BasicQueryAtom.generateElement(5);

        case 'C':

            if (lexPtr[lexPtrIndex] == 'l')
            {
                lexPtrIndex++;

                return BasicQueryAtom.generateElement(17);
            }

            return BasicQueryAtom.generateAromElem(6, IFALSE);

        case 'F':
            return BasicQueryAtom.generateElement(9);

        case 'I':
            return BasicQueryAtom.generateElement(53);

        case 'N':
            return BasicQueryAtom.generateAromElem(7, IFALSE);

        case 'O':
            return BasicQueryAtom.generateAromElem(8, IFALSE);

        case 'P':
            return BasicQueryAtom.generateElement(15);

        case 'S':
            return BasicQueryAtom.generateAromElem(16, IFALSE);

        case 'a':

            if (!STRICT)
            {
                return BasicQueryAtom.buildAtomLeaf(BasicQueryAtom.AL_AROM, 1);
            }
            else
            {
                break;
            }

        case 'c':
            return BasicQueryAtom.generateAromElem(6, ITRUE);

        case 'n':
            return BasicQueryAtom.generateAromElem(7, ITRUE);

        case 'o':
            return BasicQueryAtom.generateAromElem(8, ITRUE);

        case 'p':
            return BasicQueryAtom.generateAromElem(15, ITRUE);

        case 's':
            return BasicQueryAtom.generateAromElem(16, ITRUE);
        }

        lexPtrIndex++;

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  pat   Description of the Parameter
     * @param  expr  Description of the Parameter
     * @return       Description of the Return Value
     */
    private QueryPattern parseSMARTSError(QueryPattern pat, QueryBond expr)
    {
        if (expr != null)
        {
            BasicQueryBond.freeBondExpr(expr);
        }

        return SMARTSError(pat);
    }

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  part    Description of the Parameter
     * @return         Description of the Return Value
     */
    private QueryPattern parseSMARTSPart(QueryPattern result, int part)
    {
        ParserState stat = new ParserState();
        int i;
        for (i = 0; i < 100; i++)
        {
            stat.closure[i] = -1;
        }

        result = createSMARTSParser(result, stat, -1, part);


        if (result != null)
        {
            Vector<Integer> openClosures=null;
            for (i = 0; i < 100; i++)
            {
                if (stat.closure[i] != -1)
                {
                    BasicQueryBond.freeBondExpr(stat.closord[i]);
                    if(openClosures==null)openClosures=new Vector<Integer>();
                    openClosures.add(new Integer(stat.closure[i]));
                }
            }
            if (openClosures!=null)
            {
                for (int cIdx = 0; cIdx < openClosures.size(); cIdx++) {
                    logger.error("Open closure bond to atom index "+openClosures.get(cIdx)+". Use unused closure number or remove it.");
                }
                return (SMARTSError(result));
            }
            else
            {
                markGrowBonds(result);
                result.setChiral(false);

                for (i = 0; i < result.getAtomsSize(); i++)
                {
                    result.getAtom(i).setChiral(getChiralFlag(
                            result.getAtom(i).getAtom()));

                    if (result.getAtom(i).getChiral() != 0)
                    {
                        result.setChiral(true);
                    }
                }

                return (result);
            }
        }
        else
        {
            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private QueryPattern parseSMARTSPattern()
    {
        QueryPattern result = new BasicQueryPattern();
        result = BasicQueryPattern.allocPattern();

        while (lexPtr[lexPtrIndex] == '(')
        {
            lexPtrIndex++;
            result = parseSMARTSPart(result, result.getParts());

            if (result == null)
            {
                return null;
            }

            result.setParts(result.getParts() + 1);

            if (lexPtr[lexPtrIndex] != ')')
            {
                return SMARTSError(result);
            }

            lexPtrIndex++;

            if ((lexPtrIndex <= theEnd) || (lexPtr[lexPtrIndex] == ')'))
            {
                return result;
            }

            if (lexPtr[lexPtrIndex] != '.')
            {
                return SMARTSError(result);
            }

            lexPtrIndex++;
        }

        return parseSMARTSPart(result, 0);
    }

    /**
     *  Description of the Method
     *
     * @param  smarts  Description of the Parameter
     * @return         Description of the Return Value
     */
    private QueryPattern parseSMARTSString(String smarts)
    {
        return parseSMARTSString(smarts.toCharArray());
    }

    /**
     *  Description of the Method
     *
     * @param  ptr  Description of the Parameter
     * @return      Description of the Return Value
     */
    private QueryPattern parseSMARTSString(char[] ptr)
    {
        return parseSMARTSString(ptr, 0, ptr.length - 1);
    }

    /**
     *  Description of the Method
     *
     * @param  ptr      Description of the Parameter
     * @param  index    Description of the Parameter
     * @param  _theEnd  Description of the Parameter
     * @return          Description of the Return Value
     */
    private QueryPattern parseSMARTSString(char[] ptr, int index, int _theEnd)
    {
        QueryPattern result;
        theEnd = _theEnd;
        lexPtr = mainPtr = ptr;
        lexPtrIndex = mainPtrIndex = index;

        if ((ptr == null) || (lexPtrIndex > theEnd))
        {
            return null;
        }

        result = parseSMARTSPattern();

        if ((result != null) && (lexPtrIndex <= theEnd))
        {
            return SMARTSError(result);
        }

        return result;
    }

    /**
     * @param  ttab  {@link java.util.Vector} of <tt>boolean[1]</tt> -{@link java.util.Vector}
     * @param  pat   Description of the Parameter
     * @param  mol   Description of the Parameter
     */
    private void setupAtomMatchTable(List ttab, QueryPattern pat, Molecule mol)
    {
        int i;

        if (ttab instanceof Vector)
        {
            ((Vector) ttab).setSize(pat.getAtomsSize());
        }

        for (i = 0; i < pat.getAtomsSize(); i++)
        {
            ((Vector) ttab.get(i)).setSize(mol.getAtomsSize() + 1);
        }

        Atom atom;
        AtomIterator ait = mol.atomIterator();

        for (i = 0; i < pat.getAtomsSize(); i++)
        {
            ait.reset();

            while (ait.hasNext())
            {
                atom = ait.nextAtom();

                if (evalAtomExpr(pat.getAtom(0).getAtom(), atom))
                {
                    ((boolean[]) ((Vector) ttab.get(i)).get(atom.getIndex()))[0] =
                        true;
                }
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  pat  Description of the Parameter
     * @return      Description of the Return Value
     */
    private QueryPattern SMARTSError(QueryPattern pat)
    {
        char[] c = new char[theEnd - mainPtrIndex + 1];
        System.arraycopy(mainPtr, mainPtrIndex, c, 0,
            theEnd - mainPtrIndex + 1);
        logger.error("SMARTS Error: \"" + (String.valueOf(c)) + "\"");

        StringBuffer sb = new StringBuffer("              ");

        for (int cptr = mainPtrIndex; cptr < lexPtrIndex; cptr++)
        {
            sb.append(" ");
        }

        sb.append("^");
        logger.error(sb.toString());

        //force null pointer exception
        //Object obj=null;
        //obj.toString();
        BasicQueryPattern.freePattern(pat);

        return null;
    }

    /**
     * @param  vlex  {@link java.util.Vector} of type <tt>StringString</tt>
     * @param  s     Description of the Parameter
     * @return       Description of the Return Value
     */
    private String smartsLexReplace(String s, List<StringString> vlex)
    {
        StringString ssTmp;
        int j;
        int pos;
        String token;
        String repstr;

        for (pos = s.indexOf("$", 0); pos < s.length();
                pos = s.indexOf("$", pos))
        {
            //for (pos = 0,pos = s.find("$",pos);pos != string::npos;pos = s.find("$",pos))
            pos++;

            for (j = pos; j < s.length(); j++)
            {
                if (!Character.isUnicodeIdentifierStart(s.charAt(j)) &&
                        !Character.isDigit(s.charAt(j)) && (s.charAt(j) != '_'))
                {
                    break;
                }
            }

            if (pos == j)
            {
                continue;
            }

            token = s.substring(pos, j - pos);

            char[] ca = s.toCharArray();

            for (int i = 0; i < vlex.size(); i++)
            {
                ssTmp = (StringString) vlex.get(i);

                if (token.equals(ssTmp.getStringValue1()))
                {
                    repstr = "(" + ssTmp.getStringValue1() + ")";
                    System.arraycopy(repstr.toCharArray(), pos, ca, 0, j - pos);

                    //newString = repstr.substring(pos, j-pos);
                    //s.replace(pos,j-pos,repstr);
                    j = 0;
                }
            }

            pos = j;

            //return newString;
            return String.valueOf(ca);
        }

        return null;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
