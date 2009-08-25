///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicQueryPattern.java,v $
//  Purpose:  SMARTS pattern representation.
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
package joelib2.smarts;

import joelib2.smarts.atomexpr.BasicQueryAtom;
import joelib2.smarts.atomexpr.QueryAtom;

import joelib2.smarts.bondexpr.BasicQueryBond;
import joelib2.smarts.bondexpr.QueryBond;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Category;


/**
 * SMARTS pattern representation.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:39 $
 */
public class BasicQueryPattern implements java.io.Serializable, QueryPattern
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicQueryPattern.class.getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int atomsAllocated;

    /**
     *  Description of the Field
     */
    public int bondsAllocated;

    /**
     *  Description of the Field
     */
    public boolean ischiral;

    /**
     *  Description of the Field
     */
    public int parts;

    /**
     *  Description of the Field
     */
    public QueryAtomSpecification[] queryAtoms;

    /**
     *  Description of the Field
     */
    public int queryAtomsSize;

    /**
     *  Description of the Field
     */
    public QueryBondSpecification[] queryBonds;

    /**
     *  Description of the Field
     */
    public int queryBondsSize;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static BasicQueryPattern allocPattern()
    {
        BasicQueryPattern ptr = new BasicQueryPattern();

        //ptr = (Pattern*)malloc(sizeof(Pattern));
        if (ptr == null)
        {
            logger.error("Can not allocate new Pattern.");
            System.exit(1);

            //            ParseSmart.fatalAllocationError((new String("pattern")).toCharArray(), 0);
        }

        ptr.queryAtoms = null;
        ptr.atomsAllocated = 0;
        ptr.queryAtomsSize = 0;

        ptr.queryBonds = null;
        ptr.bondsAllocated = 0;
        ptr.queryBondsSize = 0;

        ptr.parts = 1;

        return ptr;
    }

    /**
     *  Description of the Method
     *
     * @param  pat  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static QueryPattern copyPattern(QueryPattern pat)
    {
        QueryPattern result;
        QueryAtom aexpr;
        QueryBond bexpr;
        int i;

        result = allocPattern();
        result.setParts(pat.getParts());

        for (i = 0; i < pat.getAtomsSize(); i++)
        {
            aexpr = BasicQueryAtom.copyAtomExpr(pat.getAtom(i).getAtom());
            createAtom(result, aexpr, pat.getAtom(i).getPart());
        }

        for (i = 0; i < pat.getBondsSize(); i++)
        {
            bexpr = BasicQueryBond.copyBondExpr(pat.getBond(i).getBond());
            createBond(result, bexpr, pat.getBond(i).getSource(),
                pat.getBond(i).getDestination());
        }

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  pat   Description of the Parameter
     * @param  expr  Description of the Parameter
     * @param  part  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static int createAtom(QueryPattern pat, QueryAtom expr, int part)
    {
        return createAtom(pat, expr, part, 0);
    }

    /**
     *  Description of the Method
     *
     * @param  pattern   Description of the Parameter
     * @param  expr  Description of the Parameter
     * @param  part  Description of the Parameter
     * @param  vectorBinding    vector binding
     * @return       Description of the Return Value
     */
    public static int createAtom(QueryPattern pattern, QueryAtom expr, int part,
        int vectorBinding)
    {
        if (pattern == null)
        {
            logger.error("No Pattern defined to create a query atom.");
        }

        int index;

        if (pattern.getAtomsSize() == pattern.getAtomsAllocated())
        {
            int tmpSize = pattern.getAtomsAllocated();
            pattern.setAtomsAllocated(pattern.getAtomsAllocated() + ATOMPOOL);

            BasicQueryAtomSpecification[] tmp =
                new BasicQueryAtomSpecification[pattern.getAtomsAllocated()];

            if (pattern.getAtoms() != null)
            {
                System.arraycopy(pattern.getAtoms(), 0, tmp, 0, tmpSize);
            }

            pattern.setQueryAtoms(tmp);

            //    if( pat.atom==null ) fatalAllocationError("atom pool");
            if (pattern.getAtoms() == null)
            {
                logger.error("Query atom pool can't be generated.");
            }
        }

        index = pattern.getAtomsSize();
        pattern.setAtomsSize(pattern.getAtomsSize() + 1);

        if (pattern.getAtoms()[index] == null)
        {
            pattern.setAtom(index, new BasicQueryAtomSpecification());
        }

        pattern.getAtom(index).setPart(part);
        pattern.getAtom(index).setAtom(expr);
        pattern.getAtom(index).setVectorBinding(vectorBinding);

        //vector binding
        return index;
    }

    /**
     *  Description of the Method
     *
     * @param  pat   Description of the Parameter
     * @param  expr  Description of the Parameter
     * @param  src   Description of the Parameter
     * @param  dst   Description of the Parameter
     * @return       Description of the Return Value
     */
    public static int createBond(QueryPattern pat, QueryBond expr, int src,
        int dst)
    {
        int index;

        if (pat.getBondsSize() == pat.getBondsAllocated())
        {
            int tmpSize = pat.getBondsAllocated();
            pat.setBondsAllocated(pat.getBondsAllocated() + BONDPOOL);

            BasicQueryBondSpecification[] tmp =
                new BasicQueryBondSpecification[pat.getBondsAllocated()];

            if (pat.getBonds() != null)
            {
                System.arraycopy(pat.getBonds(), 0, tmp, 0, tmpSize);
            }

            pat.setQueryBonds(tmp);

            if (pat.getBonds() == null)
            {
                logger.error("Query bond pool can't be generated.");
            }
        }

        index = pat.getBondsSize();
        pat.setBondsSize(pat.getBondsSize() + 1);

        if (pat.getBonds()[index] == null)
        {
            pat.setBond(index, new BasicQueryBondSpecification());
        }

        pat.getBond(index).setBond(expr);
        pat.getBond(index).setSource(src);
        pat.getBond(index).setDestination(dst);

        return index;
    }

    /**
     *  Description of the Method
     *
     * @param  pat  Description of the Parameter
     */
    public static void freePattern(QueryPattern pat)
    {
        int i;

        if (pat != null)
        {
            if (pat.getAtomsAllocated() != 0)
            {
                for (i = 0; i < pat.getAtomsSize(); i++)
                {
                    BasicQueryAtom.freeAtomExpr(pat.getAtoms()[i].getAtom());
                }

                pat.setQueryAtoms(null);
            }

            if (pat.getBondsAllocated() != 0)
            {
                for (i = 0; i < pat.getBondsSize(); i++)
                {
                    BasicQueryBond.freeBondExpr(pat.getBonds()[i].getBond());
                }

                pat.setQueryBonds(null);
            }

            pat = null;
        }
    }

    public QueryAtomSpecification getAtom(int index)
    {
        return getAtoms()[index];
    }

    /**
     * @return Returns the queryAtoms.
     */
    public QueryAtomSpecification[] getAtoms()
    {
        return queryAtoms;
    }

    /**
     * @return Returns the atomsAllocated.
     */
    public int getAtomsAllocated()
    {
        return atomsAllocated;
    }

    /**
     * @return Returns the queryAtomsSize.
     */
    public int getAtomsSize()
    {
        return queryAtomsSize;
    }

    public QueryBondSpecification getBond(int index)
    {
        return getBonds()[index];
    }

    /**
     * @return Returns the queryBonds.
     */
    public QueryBondSpecification[] getBonds()
    {
        return queryBonds;
    }

    /**
     * @return Returns the bondsAllocated.
     */
    public int getBondsAllocated()
    {
        return bondsAllocated;
    }

    /**
     * @return Returns the queryBondsSize.
     */
    public int getBondsSize()
    {
        return queryBondsSize;
    }

    /**
     * @return Returns the parts.
     */
    public int getParts()
    {
        return parts;
    }

    /**
     * @return Returns the ischiral.
     */
    public boolean isChiral()
    {
        return ischiral;
    }

    public void setAtom(int index, QueryAtomSpecification queryAtom)
    {
        getAtoms()[index] = queryAtom;
    }

    /**
     * @param atomsAllocated The atomsAllocated to set.
     */
    public void setAtomsAllocated(int atomsAllocated)
    {
        this.atomsAllocated = atomsAllocated;
    }

    /**
     * @param queryAtomsSize The queryAtomsSize to set.
     */
    public void setAtomsSize(int queryAtomsSize)
    {
        this.queryAtomsSize = queryAtomsSize;
    }

    public void setBond(int index, QueryBondSpecification queryBond)
    {
        getBonds()[index] = queryBond;
    }

    /**
     * @param bondsAllocated The bondsAllocated to set.
     */
    public void setBondsAllocated(int bondsAllocated)
    {
        this.bondsAllocated = bondsAllocated;
    }

    /**
     * @param queryBondsSize The queryBondsSize to set.
     */
    public void setBondsSize(int queryBondsSize)
    {
        this.queryBondsSize = queryBondsSize;
    }

    /**
     * @param ischiral The ischiral to set.
     */
    public void setChiral(boolean ischiral)
    {
        this.ischiral = ischiral;
    }

    /**
     * @param parts The parts to set.
     */
    public void setParts(int parts)
    {
        this.parts = parts;
    }

    /**
     * @param queryAtoms The queryAtoms to set.
     */
    public void setQueryAtoms(QueryAtomSpecification[] queryAtoms)
    {
        this.queryAtoms = queryAtoms;
    }

    /**
     * @param queryBonds The queryBonds to set.
     */
    public void setQueryBonds(QueryBondSpecification[] queryBonds)
    {
        this.queryBonds = queryBonds;
    }

    public String toString()
    {
        return toString("");
    }

    public String toString(String startLineWith)
    {
        ByteArrayOutputStream bs = new ByteArrayOutputStream(2000);
        PrintStream ps = new PrintStream(bs);

        int bi = 0;

        if (queryAtoms != null)
        {
            // Atom expressions
            for (int i = 0; i < queryAtoms.length; i++)
            {
                ps.print(startLineWith);
                BasicQueryAtom.dumpAtomExpr(queryAtoms[i].getAtom(), ps);

                //            ps.print(" with part: ");
                //            ps.print(atom[i].part);
                ps.print(" vb: ");
                ps.print(queryAtoms[i].getVectorBinding());
                ps.println();

                //          AtomExpr.displayAtomExpr(atom[i].expr, ps);
                if ((queryBonds != null) && (bi < queryBonds.length))
                {
                    // Bond expressions
                    //            BondExpr.displayBondExpr(bond[bi].expr, ps);
                    BasicQueryBond.dumpBondExpr(queryBonds[bi].getBond(), ps);
                    ps.print(" with grow: ");
                    ps.print(queryBonds[bi].isGrow());
                    ps.print(" src: ");
                    ps.print(queryBonds[bi].getSource());
                    ps.print(" dst: ");
                    ps.print(queryBonds[bi].getDestination());

                    //            ps.print(" visit: ");
                    //            ps.print(bond[bi].visit);
                    ps.println();
                    bi++;
                }
                else
                {
                    ps.println();
                }
            }
        }

        return bs.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
