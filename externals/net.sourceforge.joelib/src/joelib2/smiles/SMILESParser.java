///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMILESParser.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2006/07/24 22:29:16 $
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
package joelib2.smiles;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.BondHelper;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicExternalBondData;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Parser for Simplified Molecular Input Line Entry System (SMILES) strings.
 *
 * <p>
 * Example:
 * <blockquote><pre>
 * Molecule mol=new Molecule();
 * String smiles="c1cc(OH)cc1";
 * if (!JOESmilesParser.smiToMol(mol, smiles, setTitle.toString()))
 * {
 *   System.err.println("SMILES entry \"" + smiles + "\" could not be loaded.");
 * }
 * System.out.println(mol.toString());
 * </pre></blockquote>
 *
 * @.author     wegnerj
 * @.wikipedia  Simplified molecular input line entry specification
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2006/07/24 22:29:16 $
 * @.cite smilesFormat
 * @.cite wei88
 * @.cite www89
 * @see joelib2.util.cdk.CDKTools
 */
public class SMILESParser implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(SMILESParser.class
            .getName());
    public static final char UP_BOND_FLAG = '/';
    public static final char DOWN_BOND_FLAG = '\\';

    //~ Instance fields ////////////////////////////////////////////////////////

    private char[] _buffer = new char[HelperMethods.BUFF_SIZE];
    private char[] _ptr;

    private List<Integer> aromaticAtoms;
    private int bondflags;
    private List<int[]> externalBonds;
    private int order;
    private List<int[]> path;

    /**
     * Index of the previous atom.
     */
    private int prev;
    private List<int[]> previous;
    private int ptrIndex;
    private List<SMILESClosureBond> ringClosure;
    private int theEnd;
    private List<boolean[]> visitedAtoms;
    private List<boolean[]> visitedBonds;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SMILESParser()
    {
    	aromaticAtoms = new Vector<Integer>();
        previous = new Vector<int[]>();
        ringClosure = new Vector<SMILESClosureBond>();
        externalBonds = new Vector<int[]>();
        path = new Vector<int[]>();
        visitedAtoms = new Vector<boolean[]>();
        visitedBonds = new Vector<boolean[]>();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @param mol    Description of the Parameter
     * @param smi    Description of the Parameter
     * @param title  Description of the Parameter
     * @return       Description of the Return Value
     */
    public static boolean smiles2molecule(Molecule mol, String smi,
        String title)
    {
        SMILESParser sp = new SMILESParser();
        mol.setTitle(title);

        if (!sp.smiles2molecule(mol, smi))
        {
            return false;
        }

        return true;
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @param smiles    Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean smiles2molecule(Molecule mol, String smiles)
    {
        bondflags = 0;
        order = 0;

        prev = 0;
        _ptr = null;
        ptrIndex = 0;
        theEnd = smiles.length() - 1;
        smiles.getChars(0, theEnd + 1, _buffer, 0);

        //              System.out.println("theEnd:" + theEnd);
        //              for (int i = 0; i < _buffer.length; i++)
        //              {
        //                      System.out.print(_buffer[i]);
        //              }
        //              System.out.println();
        previous.clear();
        ringClosure.clear();
        externalBonds.clear();
        path.clear();
        visitedAtoms.clear();
        visitedBonds.clear();
        aromaticAtoms.clear();

        if (!parseSmiles(mol))
        {
            //mol.endModify();
            //mol.clear();
            return (false);
        }

        return (true);
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @return     Description of the Return Value
     */
    private boolean capExternalBonds(Molecule mol)
    {
        if (externalBonds.size() == 0)
        {
            return (true);
        }

        Atom atom;
        int[] bond;

        for (int i = 0; i < externalBonds.size(); i++)
        {
            bond = (int[]) externalBonds.get(i);

            // create new dummy atom
            atom = mol.newAtom(true);
            atom.setAtomicNumber(0);
            atom.setType("*");

            // bond dummy atom to mol via external bond
            //            System.out.println("addBondExt");
            mol.addBond(bond[1], atom.getIndex(), bond[2], bond[3]);

            Bond refbond = atom.getBond(mol.getAtom(bond[1]));

            //record external bond information
            BasicExternalBondData xbd;

            if (mol.hasData(BasicExternalBondData.class.getName()))
            {
                xbd = (BasicExternalBondData) mol.getData(
                        BasicExternalBondData.class.getName());
            }
            else
            {
                xbd = new BasicExternalBondData();
                mol.addData(xbd);
            }

            xbd.setData(atom, refbond, bond[0]);
        }

        return (true);
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     */
    private void findAromaticBonds(Molecule mol, boolean[] aromAtoms)
    {
        path.clear();
        visitedAtoms.clear();
        visitedBonds.clear();
        ((Vector) visitedAtoms).ensureCapacity(mol.getAtomsSize() + 1);
        ((Vector) visitedBonds).ensureCapacity(mol.getBondsSize());
        ((Vector) path).ensureCapacity(mol.getAtomsSize() + 1);

        Bond bond;

        for (int i = 0; i <= mol.getBondsSize(); i++)
        {
            visitedBonds.add(new boolean[]{false});
        }

        BondIterator bit = mol.bondIterator();
        boolean[] btmp;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

//            System.out.println("FAB: "+bond.getBeginIndex()+" "+bond.getEndIndex()+" ");
            if (!aromAtoms[bond.getBeginIndex()] ||
                    !aromAtoms[bond.getEndIndex()])
            {
                btmp = (boolean[]) visitedBonds.get(bond.getIndex());
                btmp[0] = true;
            }
        }

        for (int i = 0; i <= mol.getAtomsSize(); i++)
        {
            visitedAtoms.add(new boolean[]{false});
            path.add(new int[]{0});
        }

        Atom atom;
        int size=mol.getAtomsSize();
//        for (int index = 1; index <= size; index++) {
//        	atom=mol.getAtom(index);
//        	System.out.println(""+index+" "+atom.toString()+" "+atom.getIndex());
//		}
//        System.out.println("===");
        
        AtomIterator ait = mol.atomIterator();
        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            btmp = (boolean[]) visitedAtoms.get(atom.getIndex());
//            System.out.println(atom.toString()+" "+(atom.getIndex())+" "+btmp[0]);

            if (!btmp[0] && aromAtoms[atom.getIndex()])
            {
                findAromaticBonds(mol, atom, 0);
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param mol    Description of the Parameter
     * @param atom   Description of the Parameter
     * @param depth  Description of the Parameter
     */
    private void findAromaticBonds(Molecule mol, Atom atom, int depth)
    {
        Bond bond;
        boolean[] btmp = (boolean[]) visitedAtoms.get(atom.getIndex());

//        System.out.println("findAromaticBonds:"+atom.getIndex()+" "+btmp[0]);
        if (btmp[0])
        {
            int j = depth-1;
            bond = mol.getBond(((int[]) path.get(j--))[0]);
            bond.setBondOrder(BondHelper.AROMATIC_BO);
            if (logger.isDebugEnabled())
            {
                logger.debug("bond " + bond.getIndex() + " (" +
                    bond.getBeginIndex() + "," + bond.getEndIndex() +
                    ") set to aromatic.");
            }

            while (j >= 0)
            {
                bond = mol.getBond(((int[]) path.get(j--))[0]);
                bond.setBondOrder(BondHelper.AROMATIC_BO);

                if (logger.isDebugEnabled())
                {
                    logger.debug("bond " + bond.getIndex() + " (" +
                        bond.getBeginIndex() + "," + bond.getEndIndex() +
                        ") set to aromatic.");
                }

                if ((bond.getBegin() == atom) || (bond.getEnd() == atom))
                {
                    break;
                }
            }
        }
        else
        {
            btmp = (boolean[]) visitedAtoms.get(atom.getIndex());
            btmp[0] = true;

            BondIterator bit = atom.bondIterator();
            boolean[] btmp2;
            int[] itmp;

            while (bit.hasNext())
            {
                bond = bit.nextBond();
                btmp2 = (boolean[]) visitedBonds.get(bond.getIndex());

                if (!btmp2[0])
                {
                    itmp = (int[]) path.get(depth);
                    itmp[0] = bond.getIndex();
                    btmp2[0] = true;
                    findAromaticBonds(mol, bond.getNeighbor(atom), depth + 1);
                }
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @return     Description of the Return Value
     */
    private boolean parseComplex(Molecule mol)
    {
        //              if (logger.isDebugEnabled())
        //              {
        //                      logger.debug("Parse complex SMILES pattern.");
        //              }
        String symbol = null;
        int element = 0;
        boolean arom = false;
        int isotope = 0;

        ptrIndex++;

        //grab isotope information
        if (Character.isDigit(_ptr[ptrIndex]))
        {
            StringBuffer isoBuffer = new StringBuffer(3);

            for (; (ptrIndex <= theEnd) && Character.isDigit(_ptr[ptrIndex]);
                    ptrIndex++)
            {
                isoBuffer.append(_ptr[ptrIndex]);
            }

            isotope = Integer.parseInt(isoBuffer.toString());
        }

        //parse element data
        if (Character.isUpperCase(_ptr[ptrIndex]))
        {
            switch (_ptr[ptrIndex])
            {
            case 'C':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 20;
                    symbol = "Ca";

                    break;

                case 'd':
                    element = 48;
                    symbol = "Cd";

                    break;

                case 'e':
                    element = 58;
                    symbol = "Ce";

                    break;

                case 'f':
                    element = 98;
                    symbol = "Cf";

                    break;

                case 'l':
                    element = 17;
                    symbol = "Cl";

                    break;

                case 'm':
                    element = 96;
                    symbol = "Cm";

                    break;

                case 'o':
                    element = 27;
                    symbol = "Co";

                    break;

                case 'r':
                    element = 24;
                    symbol = "Cr";

                    break;

                case 's':
                    element = 55;
                    symbol = "Cs";

                    break;

                case 'u':
                    element = 29;
                    symbol = "Cu";

                    break;

                default:
                    element = 6;
                    symbol = "C";
                    ptrIndex--;
                }

                break;

            case 'N':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 11;
                    symbol = "Na";

                    break;

                case 'b':
                    element = 41;
                    symbol = "Nb";

                    break;

                case 'd':
                    element = 60;
                    symbol = "Nd";

                    break;

                case 'e':
                    element = 10;
                    symbol = "Ne";

                    break;

                case 'i':
                    element = 28;
                    symbol = "Ni";

                    break;

                case 'o':
                    element = 102;
                    symbol = "No";

                    break;

                case 'p':
                    element = 93;
                    symbol = "Np";

                    break;

                default:
                    element = 7;
                    symbol = "N";
                    ptrIndex--;
                }

                break;

            case ('O'):
                ptrIndex++;

                if (_ptr[ptrIndex] == 's')
                {
                    element = 76;
                    symbol = "Os";
                }
                else
                {
                    element = 8;
                    symbol = "O";
                    ptrIndex--;
                }

                break;

            case 'P':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 91;
                    symbol = "Pa";

                    break;

                case 'b':
                    element = 82;
                    symbol = "Pb";

                    break;

                case 'd':
                    element = 46;
                    symbol = "Pd";

                    break;

                case 'm':
                    element = 61;
                    symbol = "Pm";

                    break;

                case 'o':
                    element = 84;
                    symbol = "Po";

                    break;

                case 'r':
                    element = 59;
                    symbol = "Pr";

                    break;

                case 't':
                    element = 78;
                    symbol = "Pt";

                    break;

                case 'u':
                    element = 94;
                    symbol = "Pu";

                    break;

                default:
                    element = 15;
                    symbol = "P";
                    ptrIndex--;
                }

                break;

            case ('S'):
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'b':
                    element = 51;
                    symbol = "Sb";

                    break;

                case 'c':
                    element = 21;
                    symbol = "Sc";

                    break;

                case 'e':
                    element = 34;
                    symbol = "Se";

                    break;

                case 'i':
                    element = 14;
                    symbol = "Si";

                    break;

                case 'm':
                    element = 62;
                    symbol = "Sm";

                    break;

                case 'n':
                    element = 50;
                    symbol = "Sn";

                    break;

                case 'r':
                    element = 38;
                    symbol = "Sr";

                    break;

                default:
                    element = 16;
                    symbol = "S";
                    ptrIndex--;
                }

                break;

            case 'B':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 56;
                    symbol = "Ba";

                    break;

                case 'e':
                    element = 4;
                    symbol = "Be";

                    break;

                case 'i':
                    element = 83;
                    symbol = "Bi";

                    break;

                case 'k':
                    element = 97;
                    symbol = "Bk";

                    break;

                case 'r':
                    element = 35;
                    symbol = "Br";

                    break;

                default:
                    element = 5;
                    symbol = "B";
                    ptrIndex--;
                }

                break;

            case 'F':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'e':
                    element = 26;
                    symbol = "Fe";

                    break;

                case 'm':
                    element = 100;
                    symbol = "Fm";

                    break;

                case 'r':
                    element = 87;
                    symbol = "Fr";

                    break;

                default:
                    element = 9;
                    symbol = "F";
                    ptrIndex--;
                }

                break;

            case 'I':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'n':
                    element = 49;
                    symbol = "In";

                    break;

                case 'r':
                    element = 77;
                    symbol = "Ir";

                    break;

                default:
                    element = 53;
                    symbol = "I";
                    ptrIndex--;
                }

                break;

            case 'A':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'c':
                    element = 89;
                    symbol = "Ac";

                    break;

                case 'g':
                    element = 47;
                    symbol = "Ag";

                    break;

                case 'l':
                    element = 13;
                    symbol = "Al";

                    break;

                case 'm':
                    element = 95;
                    symbol = "Am";

                    break;

                case 'r':
                    element = 18;
                    symbol = "Ar";

                    break;

                case 's':
                    element = 33;
                    symbol = "As";

                    break;

                case 't':
                    element = 85;
                    symbol = "At";

                    break;

                case 'u':
                    element = 79;
                    symbol = "Au";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;

            case 'D':
                ptrIndex++;

                if (_ptr[ptrIndex] == 'y')
                {
                    element = 66;
                    symbol = "Dy";
                }
                else
                {
                    ptrIndex--;

                    return (false);
                }

                break;

            case 'E':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'r':
                    element = 68;
                    symbol = "Er";

                    break;

                case 's':
                    element = 99;
                    symbol = "Es";

                    break;

                case 'u':
                    element = 63;
                    symbol = "Eu";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;

            case 'G':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 31;
                    symbol = "Ga";

                    break;

                case 'd':
                    element = 64;
                    symbol = "Gd";

                    break;

                case 'e':
                    element = 32;
                    symbol = "Ge";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;

            case 'H':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'e':
                    element = 2;
                    symbol = "He";

                    break;

                case 'f':
                    element = 72;
                    symbol = "Hf";

                    break;

                case 'g':
                    element = 80;
                    symbol = "Hg";

                    break;

                case 'o':
                    element = 67;
                    symbol = "Ho";

                    break;

                default:
                    element = 1;
                    symbol = "H";
                    ptrIndex--;
                }

                break;

            case 'K':
                ptrIndex++;

                if (_ptr[ptrIndex] == 'r')
                {
                    element = 36;
                    symbol = "Kr";
                }
                else
                {
                    element = 19;
                    symbol = "K";
                    ptrIndex--;
                }

                break;

            case 'L':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 57;
                    symbol = "La";

                    break;

                case 'i':
                    element = 3;
                    symbol = "Li";

                    break;

                case 'r':
                    element = 103;
                    symbol = "Lr";

                    break;

                case 'u':
                    element = 71;
                    symbol = "Lu";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;

            case 'M':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'd':
                    element = 101;
                    symbol = "Md";

                    break;

                case 'g':
                    element = 12;
                    symbol = "Mg";

                    break;

                case 'n':
                    element = 25;
                    symbol = "Mn";

                    break;

                case 'o':
                    element = 42;
                    symbol = "Mo";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;

            case 'R':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 88;
                    symbol = "Ra";

                    break;

                case 'b':
                    element = 37;
                    symbol = "Rb";

                    break;

                case 'e':
                    element = 75;
                    symbol = "Re";

                    break;

                case 'h':
                    element = 45;
                    symbol = "Rh";

                    break;

                case 'n':
                    element = 86;
                    symbol = "Rn";

                    break;

                case 'u':
                    element = 44;
                    symbol = "Ru";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;

            case 'T':
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'a':
                    element = 73;
                    symbol = "Ta";

                    break;

                case 'b':
                    element = 65;
                    symbol = "Tb";

                    break;

                case 'c':
                    element = 43;
                    symbol = "Tc";

                    break;

                case 'e':
                    element = 52;
                    symbol = "Te";

                    break;

                case 'h':
                    element = 90;
                    symbol = "Th";

                    break;

                case 'i':
                    element = 22;
                    symbol = "Ti";

                    break;

                case 'l':
                    element = 81;
                    symbol = "Tl";

                    break;

                case 'm':
                    element = 69;
                    symbol = "Tm";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;

            case ('U'):
                element = 92;
                symbol = "U";

                break;

            case ('V'):
                element = 23;
                symbol = "V";

                break;

            case ('W'):
                element = 74;
                symbol = "W";

                break;

            case ('X'):
                ptrIndex++;

                if (_ptr[ptrIndex] == 'e')
                {
                    element = 54;
                    symbol = "Xe";
                }
                else
                {
                    ptrIndex--;

                    return (false);
                }

                break;

            case ('Y'):
                ptrIndex++;

                if (_ptr[ptrIndex] == 'b')
                {
                    element = 70;
                    symbol = "Yb";
                }
                else
                {
                    element = 39;
                    symbol = "Y";
                    ptrIndex--;
                }

                break;

            case ('Z'):
                ptrIndex++;

                switch (_ptr[ptrIndex])
                {
                case 'n':
                    element = 30;
                    symbol = "Zn";

                    break;

                case 'r':
                    element = 40;
                    symbol = "Zr";

                    break;

                default:
                    ptrIndex--;

                    return (false);
                }

                break;
            }
        }
        else
        {
            arom = true;

            switch (_ptr[ptrIndex])
            {
            case 'c':
                element = 6;
                symbol = "C";

                break;

            case 'n':
                element = 7;
                symbol = "N";

                break;

            case 'o':
                element = 8;
                symbol = "O";

                break;

            case 'p':
                element = 15;
                symbol = "P";

                break;

            case 's':
                ptrIndex++;

                if (_ptr[ptrIndex] == 'e')
                {
                    element = 34;
                    symbol = "Se";
                }
                else
                {
                    element = 16;
                    symbol = "S";
                    ptrIndex--;
                }

                break;

            case 'a':
                ptrIndex++;

                if (_ptr[ptrIndex] == 's')
                {
                    element = 33;
                    symbol = "As";
                }
                else
                {
                    return (false);
                }

                break;

            default:
                return (false);
            }
        }

        //      t, stereochemistry, and charge
        Atom atom = mol.newAtom(true);
        int hcount = 0;
        int charge = 0;

        //  char tmpc[]=new char[2];
        for (ptrIndex++; (ptrIndex <= theEnd) && (_ptr[ptrIndex] != ']');
                ptrIndex++)
        {
            switch (_ptr[ptrIndex])
            {
            case '@':
                ptrIndex++;

                if (_ptr[ptrIndex] == '@')
                {
                    atom.setClockwiseStereo();
                }
                else
                {
                    atom.setAntiClockwiseStereo();
                    ptrIndex--;
                }

                break;

            case '-':
                ptrIndex++;

                if (Character.isDigit(_ptr[ptrIndex]))
                {
                    charge = -Character.digit(_ptr[ptrIndex], 10);
                }
                else
                {
                    charge--;
                    ptrIndex--;
                }

                break;

            case '+':
                ptrIndex++;

                if (Character.isDigit(_ptr[ptrIndex]))
                {
                    charge = Character.digit(_ptr[ptrIndex], 10);
                }
                else
                {
                    charge++;
                    ptrIndex--;
                }

                break;

            case 'H':
                ptrIndex++;

                if (Character.isDigit(_ptr[ptrIndex]))
                {
                    hcount = Character.digit(_ptr[ptrIndex], 10);
                }
                else
                {
                    hcount = 1;
                    ptrIndex--;
                }

                break;

            default:
                return (false);
            }
        }

        if (charge != 0)
        {
            atom.setFormalCharge(charge);
        }

        atom.setAtomicNumber(element);
        atom.setIsotope(isotope);
        atom.setType(symbol);
        System.out.println("atom.getIndex(): "+atom.getIndex());

        if (arom)
        {
            aromaticAtoms.add(new Integer(atom.getIndex()));
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("previous atom:" + prev);
        }

        if (prev != 0)
        {
            //need to add bond
            mol.addBond(prev, mol.getAtomsSize(), order, bondflags);
        }

        //set values
        prev = mol.getAtomsSize();
        order = 1;
        bondflags = 0;

        //now add hydrogens
        for (int i = 0; i < hcount; i++)
        {
            atom = mol.newAtom(true);
            atom.setAtomicNumber(1);
            atom.setType("H");

            //            System.out.println("AddBondH");
            mol.addBond(prev, mol.getAtomsSize(), 1);
        }

        return (true);
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @return     Description of the Return Value
     */
    private boolean parseExternalBond(Molecule mol)
    {
        int digit;
        char[] str = new char[10];
        int strEnd;

        //_ptr[ptrIndex] should == '&'
        ptrIndex++;

        switch (_ptr[ptrIndex])
        {
        // check for bond order indicators CC&=1.C&1
        case '-':
            order = 1;
            ptrIndex++;

            break;

        case '=':
            order = 2;
            ptrIndex++;

            break;

        case '#':
            order = 3;
            ptrIndex++;

            break;

        case ';':
            order = 5;
            ptrIndex++;

            break;

        case DOWN_BOND_FLAG:

            //chiral, but _order still == 1
            bondflags |= BondHelper.IS_TORDOWN;
            ptrIndex++;

            break;

        case UP_BOND_FLAG:

            // chiral, but _order still == 1
            bondflags |= BondHelper.IS_TORUP;
            ptrIndex++;

            break;

        default:

            // no bond indicator just leave order = 1
            break;
        }

        if (_ptr[ptrIndex] == '%')
        {
            // external bond indicator > 10
            ptrIndex++;
            str[0] = _ptr[ptrIndex];
            ptrIndex++;
            str[1] = _ptr[ptrIndex];
            strEnd = 1;

            //                str[2] = '\0';
        }
        else
        {
            // simple single digit external bond indicator
            str[0] = _ptr[ptrIndex];
            strEnd = 0;

            //          str[1] = '\0';
        }

        //      digit = atoi(str);      // convert indicator to digit
        digit = Integer.parseInt(String.valueOf(str, 0, strEnd));

        //check for dot disconnect closures
        int bondFlags;

        //check for dot disconnect closures
        int bondOrder;
        int[] itmp;

        for (int j = 0; j < externalBonds.size(); j++)
        {
            itmp = (int[]) externalBonds.get(j);

            if (itmp[0] == digit)
            {
                bondFlags = (bondflags > itmp[3]) ? bondflags : itmp[3];
                bondOrder = (order > itmp[2]) ? order : itmp[2];

                //                System.out.println("CheckBondClosures");
                mol.addBond(itmp[1], prev, bondOrder, bondFlags);

                externalBonds.remove(j);
                bondflags = 0;
                order = 0;

                return (true);
            }
        }

        //since no closures save another ext bond
        itmp = new int[4];
        itmp[0] = digit;
        itmp[1] = prev;
        itmp[2] = order;
        itmp[3] = bondflags;

        externalBonds.add(itmp);
        order = 1;
        bondflags = 0;

        return (true);
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @return     Description of the Return Value
     */
    private boolean parseRingBond(Molecule mol)
    {
        //              if (logger.isDebugEnabled())
        //              {
        //                      logger.debug("Parse SMILES ring bond.");
        //              }
        int digit;
        char[] str = new char[10];
        int strEnd;

        if (_ptr[ptrIndex] == '%')
        {
            ptrIndex++;
            str[0] = _ptr[ptrIndex];
            ptrIndex++;
            str[1] = _ptr[ptrIndex];
            strEnd = 2;

            //str[2] = '\0';
        }
        else
        {
            str[0] = _ptr[ptrIndex];
            strEnd = 1;
        }

        //str[1] = '\0';}
        //  digit = atoi(str);
        if (strEnd == 0)
        {
            digit = 0;
        }
        else
        {
            digit = Integer.parseInt(String.valueOf(str, 0, strEnd));
        }

        //System.out.println("parseRingBond:"+digit);
        int bf;

        int ord;
        SMILESClosureBond cBond;

        for (int j = 0; j < ringClosure.size(); j++)
        {
            cBond = (SMILESClosureBond) ringClosure.get(j);

            if (cBond.closureNumber == digit)
            {
                bf = (bondflags > cBond.bondflags) ? bondflags
                                                   : cBond.bondflags;
                ord = (order > cBond.order) ? order : cBond.order;

                if (logger.isDebugEnabled())
                {
                    logger.debug("Close ClosureBond: " + cBond.closureNumber +
                        " " + cBond.previous + " " + cBond.order + " " +
                        cBond.bondflags + " " + cBond.valence);
                }

                mol.addBond(cBond.previous, prev, ord, bf, cBond.valence);

                ringClosure.remove(j);
                bondflags = 0;
                order = 1;

                return (true);
            }
        }

        //store position to insert closure bond
        SMILESClosureBond closureBond = new SMILESClosureBond(digit, prev,
                order, bondflags, mol.getAtom(prev).getValence());

        //correct for multiple closure bonds to a single atom
        for (int j = 0; j < ringClosure.size(); j++)
        {
            cBond = (SMILESClosureBond) ringClosure.get(j);

            if (cBond.previous == prev)
            {
                closureBond.valence++;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Open ClosureBond: " + closureBond.closureNumber +
                " " + closureBond.previous + " " + closureBond.order + " " +
                closureBond.bondflags + " " + closureBond.valence);
        }

        ringClosure.add(closureBond);
        order = 1;
        bondflags = 0;

        return (true);
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @return     Description of the Return Value
     */
    private boolean parseSimple(Molecule mol)
    {
        //              if (logger.isDebugEnabled())
        //              {
        //                      logger.debug("Parse simple SMILES pattern.");
        //              }
        String symbol;
        int element;
        boolean arom = false;

        if (Character.isUpperCase(_ptr[ptrIndex]))
        {
            switch (_ptr[ptrIndex])
            {
            case 'C':
                ptrIndex++;

                if ((_ptr[ptrIndex] == 'l') && (ptrIndex <= theEnd))
                {
                    symbol = "Cl";
                    element = 17;
                }
                else
                {
                    symbol = "C";
                    element = 6;
                    ptrIndex--;
                }

                break;

            case 'N':
                element = 7;
                symbol = "N";

                break;

            case 'O':
                element = 8;
                symbol = "O";

                break;

            case 'S':
                element = 16;
                symbol = "S";

                break;

            case 'P':
                element = 15;
                symbol = "P";

                break;

            case 'F':
                element = 9;
                symbol = "F";

                break;

            case 'I':
                element = 53;
                symbol = "I";

                break;

            case 'B':
                ptrIndex++;

                if ((_ptr[ptrIndex] == 'r') && (ptrIndex <= theEnd))
                {
                    element = 35;
                    symbol = "Br";
                }
                else
                {
                    element = 5;
                    symbol = "B";
                    ptrIndex--;
                }

                break;

            default:
                return (false);
            }
        }
        else
        {
            arom = true;

            switch (_ptr[ptrIndex])
            {
            case 'c':
                element = 6;
                symbol = "C";

                break;

            case 'n':
                element = 7;
                symbol = "N";

                break;

            case 'o':
                element = 8;
                symbol = "O";

                break;

            case 'p':
                element = 15;
                symbol = "P";

                break;

            case 's':
                element = 16;
                symbol = "S";

                break;

            case '*':
                element = 0;
                symbol = "Du";

                break;

            default:
                return (false);
            }
        }

        Atom atom = mol.newAtom(true);
        atom.setAtomicNumber(element);
        atom.setType(symbol);
//        System.out.println("atom.getIndex()222: "+atom.getIndex());

        if (arom)
        {
            aromaticAtoms.add(new Integer(atom.getIndex()));
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("previous atom:" + prev);
        }

        if (prev != 0)
        {
            //need to add bond
            mol.addBond(prev, mol.getAtomsSize(), order, bondflags);
        }

        //set values
        prev = mol.getAtomsSize();
        order = 1;
        bondflags = 0;

        return (true);
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @return     Description of the Return Value
     */
    private boolean parseSmiles(Molecule mol)
    {
        //              if (logger.isDebugEnabled())
        //              {
        //                      logger.debug("Parse SMILES pattern.");
        //              }
        mol.beginModify();

        for (_ptr = _buffer; ptrIndex <= theEnd; ptrIndex++)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("parseSmiles:" + _ptr[ptrIndex] + " atoms:" +
                    mol.getAtomsSize() + " bonds:" + mol.getBondsSize());
            }

            if (Character.isDigit(_ptr[ptrIndex]) || (_ptr[ptrIndex] == '%'))
            {
                //ring open/close
                parseRingBond(mol);

                continue;
            }
            else if (_ptr[ptrIndex] == '&')
            {
                //external bond
                parseExternalBond(mol);

                continue;
            }
            else
            {
                switch (_ptr[ptrIndex])
                {
                case '.':
                    prev = 0;

                    break;

                case '(':

                    //                        System.out.print("888");
                    previous.add(new int[]{prev});

                    break;

                case ')':
                    prev = ((int[]) ((Vector) previous).lastElement())[0];

                    //                        System.out.print("999'"+_prev+"'999");
                    ((Vector) previous).removeElementAt(previous.size() - 1);

                    break;

                case '[':

                    if (!parseComplex(mol))
                    {
                        mol.clear();
                        mol.endModify();

                        return (false);
                    }

                    break;

                case '-':
                    order = 1;

                    break;

                case '=':
                    order = 2;

                    break;

                case '#':
                    order = 3;

                    break;

                case ':':
                    order = 5;

                    break;

                case DOWN_BOND_FLAG:
                    bondflags |= BondHelper.IS_TORDOWN;

                    break;

                case UP_BOND_FLAG:
                    bondflags |= BondHelper.IS_TORUP;

                    break;

                default:

                    if (!parseSimple(mol))
                    {
                        mol.endModify();
                        mol.clear();

                        return (false);
                    }
                }
            }

            // end switch
        }

        // end for _ptr
        // place dummy atoms for each unfilled external bond
        //        System.out.println("extBonds:"+_extbond.size());
        if (externalBonds.size() != 0)
        {
            capExternalBonds(mol);
        }

        //System.out.println("mol.getAtomsSize()"+mol.getAtomsSize());
        boolean[] aromAtoms = new boolean[mol.getAtomsSize()+1];
//        for (int index = 0; index < aromaticAtoms.size(); index++)
//        {
//        	System.out.println("((Integer) aromaticAtoms.get(index)).intValue() "+((Integer) aromaticAtoms.get(index)).intValue());    
//        }
        for (int index = 0; index < aromaticAtoms.size(); index++)
        {
            aromAtoms[((Integer) aromaticAtoms.get(index)).intValue()] = true;
        }

        // set aromatic bond orders
        findAromaticBonds(mol, aromAtoms);
        mol.endModify();
        // because unsetAromaticPerceived already called any other method
        // will force calculation anyway
        //JOEAromaticTyper.instance().assignAromaticFlags(mol);
        return (true);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
