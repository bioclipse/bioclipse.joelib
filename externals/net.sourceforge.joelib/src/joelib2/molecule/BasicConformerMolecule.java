///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicConformerMolecule.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.23 $
//            $Date: 2007/03/03 00:03:49 $
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
package joelib2.molecule;

import joelib2.algo.morgan.Morgan;
import joelib2.algo.morgan.types.BasicTieResolver;

import joelib2.data.BasicProtonationModel;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomIsCarbon;
import joelib2.feature.types.atomlabel.AtomIsChiral;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.atomlabel.AtomIsNitrogen;
import joelib2.feature.types.bondlabel.BondIsRotor;
import joelib2.feature.types.bondlabel.BondKekuleType;

import joelib2.io.IOType;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;
import joelib2.io.PropertyWriter;

import joelib2.math.BasicVector3D;
import joelib2.math.Vector3D;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.BasicPose;
import joelib2.molecule.types.BasicResidue;
import joelib2.molecule.types.BasicVirtualBond;
import joelib2.molecule.types.PairData;
import joelib2.molecule.types.Pose;
import joelib2.molecule.types.Residue;
import joelib2.molecule.types.VirtualBond;

import joelib2.ring.Ring;
import joelib2.ring.RingFinderSSSR;
import joelib2.ring.Rings;

import joelib2.rotor.RotorHelper;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import joelib2.util.database.AbstractDatabase;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BasicAtomIterator;
import joelib2.util.iterator.BasicBondIterator;
import joelib2.util.iterator.BasicConformerIterator;
import joelib2.util.iterator.BasicNativeValueIterator;
import joelib2.util.iterator.BasicPairDataIterator;
import joelib2.util.iterator.BasicResidueIterator;
import joelib2.util.iterator.BasicRingIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NbrAtomIterator;
import joelib2.util.iterator.PairDataIterator;
import joelib2.util.iterator.ResidueIterator;

import joelib2.util.types.AtomDouble;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Molecule representation.
 *
 * <p>
 * There are different possibilities to build a molecule.
 * <ol>
 * <li>Using SMILES notation to build a molecule.<br>
 * <blockquote><pre>
 * Molecule mol=new Molecule();
 * String smiles="c1cc(OH)cc1";
 * if (!JOESmilesParser.smiToMol(mol, smiles, setTitle.toString()))
 * {
 *   System.err.println("SMILES entry \"" + smiles + "\" could not be loaded.");
 * }
 * System.out.println(mol.toString());
 * </pre></blockquote>
 * </li>
 *
 * <li>Using plain atoms and bonds to build a molecule.<br>
 * <blockquote><pre>
 * Molecule mol = new Molecule();
 * // start molecule modification
 * mol.beginModify();
 * mol.reserveAtoms(2);
 *
 * // build carbon atom
 * Atom C = new Atom();
 * atomC.setAtomicNum(6);
 *
 * // build molecule
 * for (int i=0; i&lt;2; i++) {
 *   mol.addAtom(atom);
 * }
 *
 * // add double bond
 * mol.addBond(1,2, 2);
 *
 * //end molecule modification and store all
 * //coordinates in coordinate array
 * mol.endModify();
 * </pre></blockquote>
 * </li>
 * 2D coordinates can be generated using {@link joelib2.util.cdk.CDKTools} which uses
 * the structure layout module from the <a href="http://cdk.sourceforge.net/">CDK</a>.<br>
 * 3D coordinates can be
 * generated using an external processing module ({@link joelib2.ext.External}), if available,
 * for 3D generation programs, like
 * <a href="http://www2.chemie.uni-erlangen.de/software/corina/" target="_top">Corina</a>.
 *
 * <p>
 * For speed optimization of loading descriptor molecule files have a
 * look at the {@link joelib2.feature.ResultFactory}.
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.license    GPL
 * @.cvsversion    $Revision: 1.23 $, $Date: 2007/03/03 00:03:49 $
 * @.cite clr98complexity
 * @.cite zup89c
 * @.cite fig96
 * @.cite gm78
 * @.cite smilesFormat
 * @see joelib2.smiles.SMILESParser
 * @see joelib2.util.cdk.CDKTools
 * @see joelib2.ext.External
 * @see joelib2.ext.Title2Data
 */
public class BasicConformerMolecule extends AbstractConformerMolecule
    implements Cloneable, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private static final String INVALID_CONFORMER_REFERENCE =
        "Pose references invalid conformer";

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            BasicConformerMolecule.class.getName());

    /**
     *  Molecule flag: H atoms added molecule.
     */
    public final static int H_ADDED = (1 << 1);

    /**
     *  Molecule flag: PH value perceived.
     */
    public final static int PH_CORRECTED = (1 << 2);

    /**
     *  Molecule flag: aromaticity corrected molecule.
     */
    public final static int AROMATICITY_CORRECTED = (1 << 3);

    /**
     *  Molecule flag: chain molecule.
     */
    public final static int IS_CHAIN = (1 << 4);
    private final static String DEFAULT_PCHARGE_VENDOR = "joelib:partialCharge";

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    protected double[] actualPose3D;

    /**
     *  Description of the Field
     */
    protected int actualPose3Dindex;

    /**
     * Automatic formal charge calculation.
     */
    protected boolean assignFormalCharge;

    /**
     * Automatic partial charge calculation.
     */
    protected boolean assignPartialCharge;

    /**
     *  Atoms of this molecule.
     *
     * @see    joelib2.molecule.Atom
     */
    protected List<Atom> atoms;

    /**
     *  Bonds of this molecule.
     *
     * @see    joelib2.molecule.Bond
     */
    protected List<Bond> bonds;

    protected List<double[]> conformers;

    /**
     *  Coordinate array of the atom positions.
     */
    protected double[] coords3Darr;
    protected Molecule deprotonated;

    /**
     *  Energy of this molecule.
     */
    protected double energy;

    /**
     *  Molecule flags.
     *
     * @see    joelib2.molecule.Molecule#HAS_SSSR
     * @see    joelib2.molecule.Molecule#RINGS_ASSIGNED
     * @see    joelib2.molecule.Molecule#AROMATICITY_ASSIGNED
     * @see    joelib2.molecule.Molecule#ATOMTYPES_ASSIGNED
     * @see    joelib2.molecule.Molecule#CHIRALITY_ASSIGNED
     * @see    joelib2.molecule.Molecule#PCHARGE_ASSIGNED
     * @see    joelib2.molecule.Molecule#HYBRID_ASSIGNED
     * @see    joelib2.molecule.Molecule#IMPVAL_ASSIGNED
     * @see    joelib2.molecule.Molecule#KEKULE_ASSIGNED
     * @see    joelib2.molecule.Molecule#CLOSURE_ASSIGNED
     * @see    joelib2.molecule.Molecule#H_ADDED
     * @see    joelib2.molecule.Molecule#PH_CORRECTED
     * @see    joelib2.molecule.Molecule#AROMATICITY_CORRECTED
     * @see    joelib2.molecule.Molecule#IS_CHAIN
     * @see    joelib2.molecule.Molecule#IS_CURRENT_CONFORMER
     */
    protected int flags;

    /**
     *  Holds additional molecule data, e.g. atom, bond or moelcule properties.
     */
    protected BasicDataHolder genericData;

    /**
     *  Molecule input type.
     */
    protected IOType inputType;

    /**
     *  Modification counter.
     *
     * @see #getModificationCounter()
     */
    transient protected int modificationCounter;

    protected boolean occuredKekulizationError;

    /**
     *  Molecule output type.
     */
    protected IOType outputType;

    /**
     *  Energy of this molecule.
     */
    protected String partialChargeVendor;

    protected List<Pose> poses;

    /**
     * Residues for this molecule.
     */
    protected List<Residue> residues;

    /**
     *  Title for this molecule.
     */
    protected String title;
    transient private int hash = 0;
    transient private boolean moleculeHashing;
    transient private List<VirtualBond> virtualBonds;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the <tt>Molecule</tt>. The 'Structured Data File'
     *  (SDF) molecule data format is used for default.
     *
     * @see #Molecule(IOType, IOType)
     * @see #Molecule(Molecule)
     * @see #Molecule(Molecule, boolean)
     * @see #Molecule(Molecule, boolean, String[])
     * @.cite mdlMolFormat
     */
    public BasicConformerMolecule()
    {
        this(DEFAULT_IO_TYPE, DEFAULT_IO_TYPE);
    }

    /**
     * Clones the molecule without data elements.
     *
     * @param  source  The source molecule
     *
     * @see #Molecule()
     * @see #Molecule(IOType, IOType)
     * @see #Molecule(Molecule, boolean)
     * @see #Molecule(Molecule, boolean, String[])
     */
    public BasicConformerMolecule(final Molecule source)
    {
        this(source, false, null);
    }

    /**
     *  Constructor for the <tt>Molecule</tt>.
     *
     * @param  itype  input type for this molecule
     * @param  otype  output type for this molecule
     *
     * @see #Molecule()
     * @see #Molecule(Molecule)
     * @see #Molecule(Molecule, boolean)
     * @see #Molecule(Molecule, boolean, String[])
     */
    public BasicConformerMolecule(IOType itype, IOType otype)
    {
        modificationCounter = 0;
        energy = 0.0f;
        inputType = itype;
        outputType = otype;
        atoms = new Vector<Atom>(20, 20);
        bonds = new Vector<Bond>(20, 20);
        genericData = new BasicDataHolder(this, 250);
        title = "";
        coords3Darr = null;
        flags = 0;
        conformers = new Vector<double[]>();
        actualPose3D = null;
        poses = new Vector<Pose>();
        residues = new Vector<Residue>();
        actualPose3Dindex = 0;
        assignPartialCharge = true;
        assignFormalCharge = true;
    }

    /**
     * Constructor for the <tt>Molecule</tt>.
     *
     * @param  source  The source molecule
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     *
     * @see #Molecule()
     * @see #Molecule(IOType, IOType)
     * @see #Molecule(Molecule)
     * @see #Molecule(Molecule, boolean)
     * @see #Molecule(Molecule, boolean, String[])
     */
    public BasicConformerMolecule(final Molecule source, boolean cloneDesc)
    {
        this(source, cloneDesc, null);
    }

    /**
     * Constructor for the <tt>Molecule</tt>.
     *
     * @param  source  The source molecule
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @param  descriptors  the descriptors to clone. If <tt>null</tt> all descriptors are cloned
     *
     * @see #Molecule()
     * @see #Molecule(IOType, IOType)
     * @see #Molecule(Molecule)
     * @see #Molecule(Molecule, boolean)
     */
    public BasicConformerMolecule(final Molecule source, boolean cloneDesc,
        String[] descriptors)
    {
        this();
        set(source, cloneDesc, descriptors);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds (cloned) atom to the <tt>Molecule</tt>.
     * Also checks for any bonds that should be made to the new atom.
     * Atoms starts with the index 1, which is a difference to bonds
     * which starts with the bond index 0.
     *
     * <p>
     * It's recommended calling the <tt>beginModify()</tt> method before the
     * first atom will be added and calling the <tt>endModify()</tt> method
     * after the last atom has been added. This causes, that the coordinates
     * of the atoms will also be stored in a coordinate array in the molecule.
     *
     * @param  atom  The <tt>Atom</tt> to add, which will be deep cloned.
     * @return       <tt>true</tt> if successfull
     *
     * @see #addBond(int, int, int)
     * @see #addBond(int, int, int, int)
     * @see #addBond(int, int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #beginModify()
     * @see #endModify()
     * @see #endModify(boolean)
     */
    public synchronized boolean addAtomClone(Atom atom)
    {
        beginModify();

        Atom joeatom;
        joeatom = (Atom) atom.clone();
        joeatom.setIndex(this.getAtomsSize() + 1);
        joeatom.setParent(this);
        atoms.add(joeatom);

        if (logger.isDebugEnabled())
        {
            logger.debug("Add atom " + atoms.get(this.getAtomsSize()) +
                " with index " + joeatom.getIndex());
        }

        checkVirtualBonds(joeatom);
        endModify();

        return true;
    }

    /**
     * Adds a bond to the <tt>Molecule</tt>.
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * @param  first      The start atom index (atoms begins with index 1)
     * @param  second     The end atom index (atoms begins with index 1)
     * @param  order      The bond order
     * @return         <tt>true</tt> if successfull
     *
     * @see #addAtomClone(Atom)
     * @see #addBond(int, int, int, int)
     * @see #addBond(int, int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #connectTheDots()
     */
    public boolean addBond(int first, int second, int order)
    {
        return addBond(first, second, order, 0, -1);
    }

    /**
     * Adds a bond to the <tt>Molecule</tt>.
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * @param  first      The start atom index (atoms begins with index 1)
     * @param  second     The end atom index (atoms begins with index 1)
     * @param  order      The bond order
     * @param  flags     The stereo flag
     * @return            <tt>true</tt> if successfull
     *
     * @see #addBond(int, int, int)
     * @see #addBond(int, int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #connectTheDots()
     */
    public boolean addBond(int first, int second, int order, int flags)
    {
        return addBond(first, second, order, flags, -1);
    }

    /**
     * Adds a bond to the <tt>Molecule</tt>.
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * @param  first      The start atom index (atoms begins with index 1)
     * @param  second     The end atom index (atoms begins with index 1)
     * @param  order      The bond order
     * @param  stereo     The stereo flag
     * @param  insertpos  The position at which the bond should be inserted
     * @return            <tt>true</tt> if successfull
     *
     * @see #addAtomClone(Atom)
     * @see #addBond(int, int, int)
     * @see #addBond(int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #connectTheDots()
     */
    public synchronized boolean addBond(int first, int second, int order,
        int stereo, int insertpos)
    {
        beginModify();

        boolean allFine = false;

        if (logger.isDebugEnabled())
        {
            logger.debug("addBond:" + first + " " + second + " " + order + " " +
                stereo + " " + insertpos);
        }

        if (first == second)
        {
            logger.error("'Loop-Bond' is not allowed for atom " + first);

            allFine = false;
        }
        else
        {
            if ((first <= getAtomsSize()) && (second <= getAtomsSize()))
            {
                //atoms exist
                Bond bond = newBond();

                if (bond == null)
                {
                    endModify();
                    allFine = false;
                }
                else
                {
                    allFine = addBond(bond, getAtom(first), getAtom(second),
                            order, stereo, insertpos);
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Add virtual bond");
                }

                //at least one atom doesn't exist yet - add to bond queue
                if (virtualBonds == null)
                {
                    virtualBonds = new LinkedList<VirtualBond>();
                }

                virtualBonds.add(new BasicVirtualBond(first, second, order,
                        stereo));
            }

            allFine = true;
        }

        endModify();

        //  BondIterator bit = this.bondIterator();
        //  Bond bond;
        //  StringBuffer sb=new StringBuffer(1000);
        //  while(bit.hasNext())
        //  {
        //    bond = bit.nextBond();
        //    sb.append("BOND:");
        //    sb.append(bond.getIdx());
        //    sb.append(' ');
        //    sb.append(bond.getBeginAtomIdx());
        //    sb.append(' ');
        //    sb.append(bond.getEndAtomIdx());
        //  }
        //  System.out.println(sb.toString());
        return allFine;
    }

    /**
    * Adds (cloned) bond to the <tt>Molecule</tt>.
    * Bonds starts with the index 0, which is a difference to atoms
    * which starts with the atom index 1.
    *
    * @param  bond  The <tt>Bond</tt> to add, which will be deep cloned.
    * @return       Description of the Return Value
    *
    * @see #addAtomClone(Atom)
    * @see #addBond(int, int, int)
    * @see #addBond(int, int, int, int)
    * @see #addBond(int, int, int, int, int)
    * @see #connectTheDots()
    */
    public synchronized boolean addBondClone(Bond bond)
    {
        return (addBond(bond.getBeginIndex(), bond.getEndIndex(),
                    bond.getBondOrder(), bond.getFlags()));
    }

    /**
     * Adds conformer coordinates to this molecule.
     *
     * @param  conformer  The conformer coordinates
     */
    public synchronized void addConformer(double[] newConformer)
    {
        conformers.add(newConformer);
    }

    /**
     *  Adds a <tt>JOEGenericData</tt> object to this molecule but don't overwrite
     *  existing data elements with the same name, if they exists already.
     *
     * <p>
     * There exist a lot of default data types which where defined in
             * {@link JOEDataType}. These data types are used for caching ring
             * searches and storing special data types like comments or virtual bonds.
             * Furthermore there exist the most important data type {@link BasicPairData}
             * for storing descriptor values. Read the {@link JOEDataType} description
             * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link BasicPairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  data  The new data value
     * @see #addData(JOEGenericData, boolean)
     * @see #getData(JOEDataType)
     * @see #getData(String)
     * @see #getData(String, boolean)
     * @see #genericDataIterator()
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    public synchronized void addData(PairData data)
    {
        //        if (genericData.hasData(data.getKey()))
        //        {
        //            logger.warn("" + data.getKey() +
        //                " exists already. New data entry was not stored.");
        //        }
        //
        genericData.addData(data, false);
    }

    /**
     *  Adds a <tt>JOEGenericData</tt> object to this molecule.
     *
     * <p>
     * There exist a lot of default data types which where defined in
             * {@link JOEDataType}. These data types are used for caching ring
             * searches and storing special data types like comments or virtual bonds.
             * Furthermore there exist the most important data type {@link BasicPairData}
             * for storing descriptor values. Read the {@link JOEDataType} description
             * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link BasicPairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  data  The new data value
     * @param  overwrite  Overwrite already existing data element, if <tt>true</tt>
     * @see #addData(JOEGenericData)
     * @see #getData(JOEDataType)
     * @see #getData(String)
     * @see #getData(String, boolean)
     * @see #genericDataIterator()
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    public synchronized void addData(PairData data, boolean overwrite)
    {
        genericData.addData(data, overwrite);
    }

    /**
     * Adds hydrogens atoms to this molecule.
     * The pH value will be corrected.
     *
     * @return    <tt>true</tt> if successfull
     * @see BasicProtonationModel
     */
    public synchronized boolean addHydrogens()
    {
        return ProtonationHelper.addHydrogens(this, false, true, true);
    }

    /**
     * Adds hydrogens atoms to this molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value will be corrected.
     *
     * @param  polaronly  Add only polar hydrogens, if <tt>true</tt>
     * @return            <tt>true</tt> if successfull
     * @see BasicProtonationModel
     */
    public synchronized boolean addHydrogens(boolean polaronly)
    {
        return ProtonationHelper.addHydrogens(this, polaronly, true, true);
    }

    /**
     * Adds hydrogens atoms to the given atom.
     * The pH value will not be corrected.
     *
     * @param  atom  The atom to which the hydogens should be added
     * @return               <tt>true</tt> if successfull
     */
    public synchronized boolean addHydrogens(Atom atom)
    {
        return ProtonationHelper.addHydrogens(this, atom);
    }

    /**
     * Adds hydrogens atoms to this molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value correction mode can be choosen.
     * Dont't use (experimental) coordinate vector for added hydrogens.
     *
     * @param  polaronly     Add only polar hydrogens, if <tt>true</tt>
     * @param  correctForPH  Corrects molecule for pH if <tt>true</tt>
     * @return               <tt>true</tt> if successfull
     * @see BasicProtonationModel
     */
    public synchronized boolean addHydrogens(boolean polaronly,
        boolean correctForPH)
    {
        return ProtonationHelper.addHydrogens(this, polaronly, correctForPH,
                true);
    }

    /**
     * Adds hydrogens atoms to this molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value correction mode can be choosen.
     * The (experimental) coordinate vector for added hydrogens mode can be choosen.
     *
     * <p>
     * If useCoordV is <tt>true</tt> the coordinate vector for added H atoms will be used.
     * This will be slower, because <tt>endModify()</tt> will be called after
     * every added hydrogen atom. If useCoordV is <tt>false</tt> the coordinate vector
     * for added H atoms will be ignored, which will be faster.

     *
     * @param  polaronly     Add only polar hydrogens, if <tt>true</tt>
     * @param  correctForPH  Corrects molecule for pH if <tt>true</tt>
     * @param  useCoordV     The coordinate vector for added hydrogens will be used if <tt>true</tt>
     * @return               <tt>true</tt> if successfull
     * @see BasicProtonationModel
     * @see #endModify()
     * @see #endModify(boolean)
     */
    public synchronized boolean addHydrogens(boolean polaronly,
        boolean correctForPH, boolean useCoordV)
    {
        return ProtonationHelper.addHydrogens(this, polaronly, correctForPH,
                useCoordV);
    }

    /**
     * Add polar hydrogens to molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value will be corrected.
     *
     * @return    Description of the Return Value
     * @see BasicProtonationModel
     */
    public synchronized boolean addPolarHydrogens()
    {
        return addHydrogens(true);
    }

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     */
    public void addPose(Pose pose)
    {
        //Check that the pose references a valid conformer
        if (pose.getConformer() >= (int) getConformersSize())
        {
            //throw new Exception("WARNING! Pose does not reference a valid conformer");
            logger.error(INVALID_CONFORMER_REFERENCE);

            return;
        }

        poses.add(pose);
    }

    /**
     * Adds residue information to this molecule.
     *
     * @param  residue  The residue information
     * @return          <tt>true</tt> if successfull
     */
    public synchronized boolean addResidue(Residue residue2add)
    {
        beginModify();

        Residue residue = (Residue) residue2add.clone();
        residue.setIndex(residues.size());
        residues.add(residue);

        endModify();

        return true;
    }

    /**
     * Aligns atom a1 on p1 and atom a2 along start-to-end vector.
     *
     * @param  atom1  first atom
     * @param  atom2  second atom
     * @param  start  start point
     * @param  end  end point
     */
    public synchronized void align(Atom atom1, Atom atom2, Vector3D start,
        Vector3D end)
    {
        RotorHelper.align(this, atom1, atom2, start, end);
    }

    /**
     * Gets an iterator over all atoms in this molecule.
     *
     * Possibility one:
     * <blockquote><pre>
     * AtomIterator ait = mol.atomIterator();
     * Atom atom;
     * while (ait.hasNext())
     * {
     *   atom = ait.nextAtom();
     *
     * }
     * </pre></blockquote>
     *
     * Atoms starts with the index 1, which is a difference to bonds
     * which starts with the bond index 0.
     * Possibility two:
     * <blockquote><pre>
     * int atoms = mol.numAtoms();
     * Atom atom;
     * for (int i=1; i&lt;=atoms; i++)
     * {
     *   atom=mol.getAtom(i);
     *
     * }
     * </pre></blockquote>
     *
     * @return    the atom iterator for this molecule
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     */
    public BasicAtomIterator atomIterator()
    {
        return new BasicAtomIterator(atoms);
    }

    /**
     * Begins modification of atoms and increase modification counter.
     *
     * The modification will be only processed if <tt>getMod()</tt> is 0!!!
     * If you have called beginModify/endModify twice you can not expect
     * that these changes are already available correctly.
     * This fits especially for deleted and added atoms, because endModify
     * updates the atomId's, writes the atom coordinates to the rotamer
     * arrays and checks the aromaticity.
     *
     * @see #endModify()
     * @see #endModify(boolean)
     * @see #decrementMod()
     * @see #incrementMod()
     * @see #getModificationCounter()
     */
    public synchronized void beginModify()
    {
        //        if(logger.isDebugEnabled())logger.debug("beginModify");
        //suck coordinates from _c into _v for each atom
        if ((modificationCounter == 0) && !isEmpty())
        {
            ConformerAtom atom;
            BasicAtomIterator ait = this.atomIterator();

            while (ait.hasNext())
            {
                atom = (ConformerAtom) ait.nextAtom();
                atom.setCoords3D();
                atom.clearCoords3Darr();
            }

            //                  ConformerIterator cit = conformerIterator();
            //                  double[] conformer;
            //
            //                  while (cit.hasNext())
            //                  {
            //                          conformer = cit.nextConformer();
            //                          conformer = null;
            //                  }
            clearCoords3Darr();
            conformers.clear();

            //        deletePoses();
            //Destroy rotamer list if necessary
            //        if (getData("RotamerList")!=null) {
            //            JOERotamerList tmp = getData("RotamerList");
            //            tmp=null;
            //            deleteData(oeRotamerList);
            //          }
        }

        modificationCounter++;
    }

    /**
     *  Gets an iterator over all bonds.
     *
     * Possibility one:
     * <blockquote><pre>
     * BondIterator bit = bondIterator();
     * Bond bond;
     * while (bit.hasNext())
     * {
     *   bond = bit.nextBond();
     *
     * }
     * </pre></blockquote>
     *
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * Possibility two:
     * <blockquote><pre>
     * int bonds = mol.numBonds();
     * Bond bond;
     * for (int i=0; i&lt;bonds; i++)
     * {
     *   bond=mol.getBond(i);
     *
     * }
     * </pre></blockquote>
     *
     * @return    the bond iterator
     * @see #atomIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     */
    public BasicBondIterator bondIterator()
    {
        return new BasicBondIterator(bonds);
    }

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     */
    public void changePosesToConformers()
    {
        //If there aren't any poses don't do anything
        if (poses.size() != 0)
        {
            //Generate the coordinates of all the poses
            int poseI;
            List<double[]> dconf = new Vector<double[]>();
            double[] conf;

            for (poseI = 0; poseI < getPosesSize(); poseI++)
            {
                conf = new double[3 * this.getAtomsSize()];
                dconf.add(conf);
                getPoseCoordinates(poseI, conf);
            }

            //Now that we have the coordinates clear the pose info for Molecule
            deletePoses();

            //Assign the pose coordinates to the conformers
            setConformers(dconf);
        }
    }

    /**
     * Clears molecule.
     *
     * @return    <tt>true</tt> if successfull
     */
    public synchronized boolean clear()
    {
        //              Atom atom;
        //              Bond bond;
        //    AtomIterator       ait        = this.atomIterator();
        //    BondIterator       bit        = this.bondIterator();
        //    while (ait.hasNext())
        //    {
        //      atom = ait.nextAtom();
        //      destroyAtom(atom);
        //      atom = null;
        //    }
        atoms.clear();

        //    while (bit.hasNext())
        //    {
        //      bond = bit.nextBond();
        //      destroyBond(bond);
        //      bond = null;
        //    }
        bonds.clear();

        //Delete residues
        //    int                ii;
        //    Object             obj;
        //    for (ii = 0; ii < _residue.size(); ii++)
        //    {
        //      obj = _residue.get(ii);
        //      obj = null;
        //    }
        residues.clear();

        //clear out the multiconformer data
        //    ConformerIterator  cit        = conformerIterator();
        //double[] conformer;
        //    while (cit.hasNext())
        //    {
        //      conformer = cit.nextConformer();
        //      cit.remove();
        //    }
        conformers.clear();

        //
        //    //Clear out the pose data
        //    deletePoses();
        genericData.clear();

        clearCoords3Darr();
        flags = 0;
        modificationCounter = 0;

        return true;
    }

    /**
     *  Clones molecule without additional data (e.g. descriptor data).
     *
     * @return    the cloned molecule
     */
    public synchronized Object clone()
    {
        return (new BasicConformerMolecule(this));
    }

    /**
     * Clones this molecule.
     *
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @return            the cloned molecule
     */
    public synchronized Object clone(boolean cloneDesc)
    {
        return (new BasicConformerMolecule(this, cloneDesc));
    }

    /**
     * Clones this molecule.
     *
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @return            the cloned molecule
     */
    public synchronized Object clone(boolean cloneDesc, String[] descriptors)
    {
        return (new BasicConformerMolecule(this, cloneDesc, descriptors));
    }

    /**
     * Gets an iterator over all conformers.
     *
     * <blockquote><pre>
     * ConformerIterator cit = mol.conformerIterator();
     * double conformer[];
     * while (cit.hasNext())
     * {
     *   conformer = cit.nextConformer();
     *
     * }
     * </pre></blockquote>
     *
     * @return    the conformer iterator
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     */
    public BasicConformerIterator conformerIterator()
    {
        return new BasicConformerIterator(conformers);
    }

    /**
     * Copies given conformer to this molecule.
     *
     * @param  src    the conformer
     * @param  idx  the index of the conformer
     */
    public void copyConformer(double[] src, int idx)
    {
        //    assert _vconf.size()!=0 : idx < _vconf.size();
        if ((conformers.size() == 0) || (idx >= conformers.size()))
        {
            logger.error("Conformer array is not defined or to small.");

            return;
        }

        double[] dtmp = (double[]) conformers.get(idx);
        System.arraycopy(src, 0, dtmp, 0, 3 * getAtomsSize());
    }

    /**
     * Copies given conformer to this molecule.
     *
     * @param  src    the conformer
     * @param  idx  the index of the conformer
     */
    public void copyConformer(float[] src, int idx)
    {
        //    assert _vconf.size()!=0 : idx < _vconf.size();
        if ((conformers.size() == 0) || (idx >= conformers.size()))
        {
            logger.error("Conformer array is not defined or to small.");

            return;
        }

        double[] dtmp = (double[]) conformers.get(idx);
        int atomI;

        for (atomI = 0; atomI < getAtomsSize(); atomI++)
        {
            dtmp[atomI * 3] = (double) src[atomI * 3];
            dtmp[(atomI * 3) + 1] = (double) src[(atomI * 3) + 1];
            dtmp[(atomI * 3) + 2] = (double) src[(atomI * 3) + 2];
        }
    }

    /**
     * Corrects pH value of the molecule.
     *
     * @return    <tt>true</tt> if successfull
     * @see BasicProtonationModel
     */
    public synchronized void correctForPH()
    {
        if (!isCorrectedForPH())
        {
            BasicProtonationModel.instance().correctForPH(this);
        }
    }

    /**
     * Returns the number of the current pose.
     * @return The number of the current pose.  If no poses are present 0 is returned.
     */
    public int currentPoseIndex()
    {
        int index = 0;

        if (poses.size() != 0)
        {
            index = actualPose3Dindex;
        }

        return index;
    }

    /**
     * Decrease modification counter.
     *
     * @see #incrementMod()
     * @see #getModificationCounter()
     * @see #beginModify()
     * @see #endModify()
     * @see #endModify(boolean)
     */
    public void decrementMod()
    {
        modificationCounter--;
    }

    /**
     * Delete atom from molecule.
     *
     * @param  atom  The atom to delete
     * @return       <tt>true</tt> if successfull
     */
    public synchronized boolean deleteAtom(Atom atom)
    {
        if (this.getModificationCounter() == 0)
        {
            throw new RuntimeException(
                "Begin modify must be called before removing atoms.  Check atom object (cloned?).");
        }

        boolean deleted = false;

        if (atom.getParent() != this)
        {
            logger.error("Atom can only be deleted in parent molecule.");
        }
        else
        {
            if (AtomIsHydrogen.isHydrogen(atom))
            {
                //System.out.println("delete hydrogen atom");
                deleted = deleteHydrogen(atom);
            }
            else
            {
                beginModify();

                //don't need to do anything with coordinates b/c
                //beginModify() blows away coordinates
                //System.out.println("delete normal atom");
                //find bonds to delete
                List<Bond> vdb = new Vector<Bond>();
                NbrAtomIterator nait = atom.nbrAtomIterator();

                while (nait.hasNext())
                {
                    nait.nextNbrAtom();
                    vdb.add(nait.actualBond());
                }

                for (int j = 0; j < vdb.size(); j++)
                {
                    //System.out.println("delete bond");
                    deleteBond((Bond) vdb.get(j));
                }

                //delete bonds
                atoms.remove(atom.getIndex() - 1);

                //reset all the indices to the atoms
                int idx = 1;
                Atom tatom;
                AtomIterator ait = this.atomIterator();

                while (ait.hasNext())
                {
                    tatom = ait.nextAtom();

                    //System.out.println("old idx:"+tatom.getIdx()+" set idx:"+idx+" atom "+ ((Object)tatom));
                    tatom.setIndex(idx);
                    idx++;
                }

                endModify();
                deleted = true;
            }
        }

        return deleted;
    }

    /**
     * Delete the given <tt>Bond</tt> from this molecule.
     *
     * @param  bond  The bond to delete
     * @return       <tt>true</tt> if successfull
     */
    public boolean deleteBond(Bond bond)
    {
        boolean deleted = false;

        if (bond.getParent() != this)
        {
            logger.error(
                "Bond can only be deleted in parent molecule. Check bond object (cloned?).");
        }
        else
        {
            if (bond == null)
            {
                // nothing to do !
                deleted = true;
            }
            else
            {
                // start deleting bond
                beginModify();

                if (bond.getBegin() == null)
                {
                    logger.warn("Begin atom does not exist.");
                }
                else
                {
                    (bond.getBegin()).deleteBond(bond);
                }

                if (bond.getEnd() == null)
                {
                    logger.warn("End atom does not exist.");
                }
                else
                {
                    (bond.getEnd()).deleteBond(bond);
                }

                bonds.remove(bond.getIndex());

                Bond tbond;
                BasicBondIterator bit = this.bondIterator();
                int index = 0;

                while (bit.hasNext())
                {
                    tbond = bit.nextBond();
                    tbond.setIndex(index);
                    index++;
                }

                endModify();
                deleted = true;
            }
        }

        return deleted;
    }

    /**
     * Delete conformer from molecule.
     *
     * @param  idx  The conformer number
     */
    public void deleteConformer(int idx)
    {
        if ((idx < 0) || (idx >= conformers.size()))
        {
            return;
        }

        conformers.remove(idx);
    }

    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return    Description of the Return Value
     */
    public boolean deleteData(String name)
    {
        return genericData.deleteData(name);
    }

    /**
     *  Delete all data elements which are <tt>equal</tt> to the given <tt>
     *  JOEGenericData</tt> element.
     *
     * @param  genData  the element to delete
     */
    public void deleteData(PairData genData)
    {
        genericData.deleteData(genData);
    }

    /**
     *  Description of the Method
     *
     * @param  dataList  Description of the Parameter
     */
    public void deleteData(List dataList)
    {
        genericData.deleteData(dataList);
    }

    /**
     * Deletes hydrogen atom.
     *
     * <p>
     * <blockquote><pre>
     * mol.beginModify();
     * deleteHydrogen(atom );
     * mol.endModify();
     * </pre></blockquote>
     *
     * @param  atom  Hydrogen atom to delete
     * @return       <tt>true</tt> if successfull
     */
    public boolean deleteHydrogen(Atom atom)
    {
        return ProtonationHelper.deleteHydrogen(this, atom);
    }

    /**
     * Delete all hydrogen atoms from molecule.
     *
     * <p>
             * <blockquote><pre>
             * mol.beginModify();
             * deleteHydrogens();
             * mol.endModify();
             * </pre></blockquote>
             *
     * @return    <tt>true</tt> if successfull
     */
    public synchronized boolean deleteHydrogens()
    {
        return ProtonationHelper.deleteHydrogens(this);
    }

    /**
     * Delete all hydrogen atoms from given atom.
     *
     * <p>
     * <blockquote><pre>
     * mol.beginModify();
     * deleteHydrogens(atom);
     * mol.endModify();
     * </pre></blockquote>
     *
     * @param  atom  Atom from which hydrogen atoms should be deleted
     * @return    <tt>true</tt> if successfull
     */
    public synchronized boolean deleteHydrogens(Atom atom)
    {
        return ProtonationHelper.deleteHydrogens(this, atom);
    }

    /**
     * Delete all non polar hydrogens from molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     *
     * @return    <tt>true</tt> if successfull
     */
    public synchronized boolean deleteNonPolarHydrogens()
    {
        return ProtonationHelper.deleteNonPolarHydrogens(this);
    }

    /**
     *  Deletes all pose information for the OEMol.
     *
     *  Deletes all pose information for the OEMol. Deletes all pose information
     *  for the OEMol. Deletes all pose information for the OEMol. Deletes all
     *  pose information for the OEMol. Deletes all pose information for the
     *  OEMol. Deletes a specified pose.
     *
     */
    public void deletePose(int poseIndex)
    {
        //Check that a valid pose is being deleted.
        if ((poseIndex >= 0) && (poseIndex < getPosesSize()))
        {
            //If this is the last pose just call DeletePoses.
            if (getPosesSize() == 1)
            {
                deletePoses();
            }
            else
            {
                //Delete the pose
                poses.remove(poseIndex);
            }
        }
    }

    /**
     *  Deletes all pose information for the OEMol.
     */
    public void deletePoses()
    {
        //If there are no poses don't do anything
        if (poses.size() == 0)
        {
            return;
        }

        //If the atom coordinate array is pointing to the pose
        //array change it to point to the 1st conformer
        if ((coords3Darr == actualPose3D) && (coords3Darr != null))
        {
            if (this.conformers.size() != 0)
            {
                //coords3Darr = (double[]) this.conformers.get(0);
                this.setCoords3Darr((double[]) this.conformers.get(0));
            }
            else
            {
                clearCoords3Darr();
            }
        }

        //Free the pose coordinate array
        if (actualPose3D != null)
        {
            clearActualPose3D();
        }

        poses.clear();

        actualPose3Dindex = 0;
    }

    /**
     *  Description of the Method
     *
     * @param  residue  Description of the Parameter
     * @return          Description of the Return Value
     */
    public synchronized boolean deleteResidue(Residue residue)
    {
        int idx = residue.getIndex();

        for (int i = idx; i < residues.size(); i++)
        {
            residues.get(i).setIndex(i - 1);
        }

        residues.remove(idx);

        return (true);
    }

    /**
     * Ends modification of atoms and decrease modification counter.
     * All flags will be deleted.
     *
     * The modification will be  only processed if <tt>getMod()</tt> is 0!!!
     * If you have called beginModify/endModify twice you can not expect
     * that these changes are already available correctly.
     * This fits especially for deleted and added atoms, because endModify
     * updates the atomId's, writes the atom coordinates to the rotamer
     * arrays and checks the aromaticity.
     *
     * @see #endModify(boolean)
     * @see #beginModify()
     * @see #decrementMod()
     * @see #incrementMod()
     * @see #getModificationCounter()
     */
    public synchronized void endModify()
    {
        endModify(true, true);
    }

    public synchronized void endModify(boolean nukeLabels)
    {
        endModify(nukeLabels, true);
    }

    /**
     * Ends modification of atoms and decrease modification counter.
     *
     * The modification will be  only processed if <tt>getMod()</tt> is 0!!!
     * If you have called beginModify/endModify twice you can not expect
     * that these changes are already available correctly.
     * This fits especially for deleted and added atoms, because endModify
     * updates the atomId's, writes the atom coordinates to the rotamer
     * arrays and checks the aromaticity.
     *
     * @param  nukeData  if <tt>true</tt> all flags will be deleted
     * @see #endModify()
     * @see #beginModify()
     * @see #decrementMod()
     * @see #incrementMod()
     * @see #getModificationCounter()
     */
    public synchronized void endModify(boolean nukeLabels, boolean correctForPH)
    {
        if (getModificationCounter() == 0)
        {
            throw new RuntimeException(
                "Modification counter is negative - endModify() called too many times");
            //            logger.error(
            //                "Modification counter is negative - EndModify() called too many times");
        }
        else
        {
            modificationCounter--;

            if (getModificationCounter() == 0)
            {
                if (correctForPH)
                {
                    this.correctForPH();
                }

                if (nukeLabels)
                {
                    if (this.hasFlags(PH_CORRECTED))
                    {
                        flags = 0;
                        setFlags(PH_CORRECTED);
                    }
                    else
                    {
                        flags = 0;
                    }

                    // nuke atom label data
                    List aProps = FeatureHelper.instance().getAtomLabelFeatures(
                            false);

                    for (int i = 0; i < aProps.size(); i++)
                    {
                        deleteData((String) aProps.get(i));
                    }

                    // nuke bond label data
                    List bProps = FeatureHelper.instance()
                                               .getBondLabelFeatures();

                    for (int i = 0; i < bProps.size(); i++)
                    {
                        deleteData((String) bProps.get(i));
                    }

                    // nuke atom depend data
                    deleteData(RingFinderSSSR.getName());
                }

                MoleculeHelper.correctFormalCharge(this);

                this.clearCoords3Darr();

                //leave generic data alone for now - just nuke it on clear()
                //if (hasData("Comment")) { Object tmp=getData("Comment"); tmp=null;);
                //_vdata.clear();
                if (!isEmpty())
                {
                    //if atoms present convert coords into array
                    double[] conf = new double[(getAtomsSize() * 3)];
                    coords3Darr = conf;

                    int idx = 0;
                    ConformerAtom atom;
                    BasicAtomIterator ait = this.atomIterator();

                    while (ait.hasNext())
                    {
                        atom = (ConformerAtom) ait.nextAtom();
                        atom.setIndex(idx + 1);
                        (atom.getCoords3D()).get(coords3Darr, idx * 3);
                        atom.setCoords3Darr(coords3Darr);
                        idx++;
                    }

                    conformers.add(conf);

                    //kekulize structure
                    kekulize();

                    // TODO: Avoid hashcode calculation, because this has
                    // TODO: runtime O(N^3), allow flag
                    // reset hashcode
                    reHash();
                }
                BondIterator bit = this.bondIterator();
                Bond bond = null;
                while (bit.hasNext())
                {
                    bond = bit.nextBond();
                    IsomerismHelper.getCisTransFrom2D3D(bond, true);
                }

            }
        }

        occuredKekulizationError = false;
    }

    /**
     * Checks if two molecules are equal, ignoring descriptor values.
     *
     * This method uses the full equality check if the atom and bonds.
     */
    public boolean equals(Object obj)
    {
        boolean equal = false;

        if (obj instanceof Molecule)
        {
            equal = equals((Molecule) obj);
        }

        return equal;
    }

    /**
     * Checks if two molecules are equal, ignoring descriptor values.
     *
     * This method uses the full equality check if the atom and bonds.
     */
    public boolean equals(Molecule other)
    {
        boolean equal = false;

        if (this.getAtomsSize() == other.getAtomsSize())
        {
            if (this.getBondsSize() == other.getBondsSize())
            {
            	equal = true;
                Atom aThis;
                Atom aOther;
                Bond bThis;
                Bond bOther;
                AtomIterator aitThis = this.atomIterator();
                AtomIterator aitOther = other.atomIterator();
                BondIterator bitThis = this.bondIterator();
                BondIterator bitOther = other.bondIterator();

                while (aitThis.hasNext() && aitOther.hasNext())
                {
                    aThis = aitThis.nextAtom();
                    aOther = aitOther.nextAtom();

                    if (!aThis.equals(aOther))
                    {
                        equal = false;

                        break;
                    }
                }

                if (equal)
                {
                    while (bitThis.hasNext() && bitOther.hasNext())
                    {
                        bThis = bitThis.nextBond();
                        bOther = bitOther.nextBond();

                        if (!bThis.equals(bOther))
                        {
                            equal = false;

                            break;
                        }
                    }
                }
            }
            else
            {
        	equal=false;
            }
        }
        else
        {
        	equal=false;
        }

        return equal;
    }

    /**
     * Returns <tt>true</tt> if this bond exists.
     *
     * @param  bgn  atom index of the start atom
     * @param  end  atom index of the end atom
     * @return <tt>true</tt> if this bond exists
     */
    public boolean existsBond(int bgn, int end)
    {
        boolean existsBond = false;

        if (this.getAtomsSize() < Math.max(bgn, end))
        {
            existsBond = false;
        }
        else
        {
            if (this.getBond(bgn, end) != null)
            {
                existsBond = true;
            }
        }

        return existsBond;
    }

    /**
     *  Locates all atoms for which there exists a path to <tt>second</tt> without
     *  going through <tt>first</tt> children does not include <tt>second</tt> .
     *
     * @param  children  Description of the Parameter
     * @param  first     Description of the Parameter
     * @param  second    Description of the Parameter
     */
    public void findChildren(List<Atom> children, int first, int second)
    {
        int index;
        BasicBitVector used = new BasicBitVector();
        BasicBitVector curr = new BasicBitVector();
        BasicBitVector next = new BasicBitVector();

        used.setBitOn(first);
        used.setBitOn(second);
        curr.setBitOn(second);

        Atom atom;
        Bond bond;

        while (!curr.isEmpty())
        {
            next.clear();

            //        for(i=curr.nextSetBit(0); i>=0; i=curr.nextSetBit(i+1))
            for (index = curr.nextBit(-1); index != curr.endBit();
                    index = curr.nextBit(index))
            {
                atom = getAtom(index);

                BondIterator bit = atom.bondIterator();

                while (bit.hasNext())
                {
                    bond = bit.nextBond();

                    if (!used.bitIsOn(bond.getNeighborIndex(atom)))
                    {
                        next.setBitOn(bond.getNeighborIndex(atom));
                    }
                }
            }

            used.orSet(next);
            curr.set(next);
        }

        used.setBitOff(first);
        used.setBitOff(second);
        used.toVectorWithIntArray(children);
    }

    /**
     *  locates all atoms for which there exists a path to 'second' without going
     *  through 'first' children does not include 'second'
     *
     * @param  children  Description of the Parameter
     * @param  bgn       Description of the Parameter
     * @param  end       Description of the Parameter
     */
    public void findChildren(List<Atom> children, Atom bgn, Atom end)
    {
        BasicBitVector used = new BasicBitVector();
        BasicBitVector curr = new BasicBitVector();
        BasicBitVector next = new BasicBitVector();

        used.set(bgn.getIndex());
        used.set(end.getIndex());
        curr.set(end.getIndex());
        children.clear();

        int index;
        Atom atom;
        Atom nbr;

        for (;;)
        {
            next.clear();

            //        for(i=curr.nextSetBit(0); i>=0; i=curr.nextSetBit(i+1))
            for (index = curr.nextBit(-1); index != curr.endBit();
                    index = curr.nextBit(index))
            {
                atom = getAtom(index);

                NbrAtomIterator nait = atom.nbrAtomIterator();

                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();

                    if (!used.get(nbr.getIndex()))
                    {
                        children.add(nbr);
                        next.set(nbr.getIndex());
                        used.set(nbr.getIndex());
                    }
                }
            }

            if (next.size() == 0)
            {
                break;
            }

            curr.set(next);
        }
    }

    /**
     *  each vector<int> contains the atom numbers of a contig fragment the
     *  vectors are sorted by size from largest to smallest
     *
     * @param  fagments  Description of the Parameter
     */
    public synchronized void findLargestFragment(BitVector fagments)
    {
        int index;
        Atom atom;
        Bond bond;

        BasicAtomIterator ait = this.atomIterator();
        BasicBitVector used = new BasicBitVector();
        BasicBitVector curr = new BasicBitVector();
        BasicBitVector next = new BasicBitVector();
        BasicBitVector frag = new BasicBitVector();

        fagments.clear();

        while (used.countBits() < getAtomsSize())
        {
            curr.clear();
            frag.clear();
            ait.reset();

            while (ait.hasNext())
            {
                atom = ait.nextAtom();

                if (!used.bitIsOn(atom.getIndex()))
                {
                    curr.setBitOn(atom.getIndex());

                    break;
                }
            }

            frag.orSet(curr);

            while (!curr.isEmpty())
            {
                next.clear();

                //            for(j=curr.nextSetBit(0); j>=0; j=curr.nextSetBit(j+1))
                for (index = curr.nextBit(-1); index != curr.endBit();
                        index = curr.nextBit(index))
                {
                    atom = getAtom(index);

                    BondIterator bit = atom.bondIterator();

                    while (bit.hasNext())
                    {
                        bond = bit.nextBond();

                        if (!used.bitIsOn(bond.getNeighborIndex(atom)))
                        {
                            next.setBitOn(bond.getNeighborIndex(atom));
                        }
                    }
                }

                used.orSet(curr);
                used.orSet(next);
                frag.orSet(next);
                curr.set(next);
            }

            if ((fagments.size() == 0) ||
                    (fagments.countBits() < frag.countBits()))
            {
                fagments.set(frag);
            }
        }
    }

    /**
     * Gets an iterator over the generic data elements of this molecule.
     *
     * There exist a lot of default data types which where defined in
             * {@link JOEDataType}. These data types are used for caching ring
             * searches and storing special data types like comments or virtual bonds.
             * Furthermore there exist the most important data type {@link BasicPairData}
             * for storing descriptor values. Read the {@link JOEDataType} description
             * for details.
             *
     * <p>
     * <ol>
     * <li>For getting all data elements the simple data iterator can be used.<br>
     * <blockquote><pre>
     * GenericDataIterator gdit = mol.genericDataIterator();
     * JOEGenericData genericData;
     * while(gdit.hasNext())
     * {
     *   genericData = gdit.nextGenericData();
     *
     * }
     * </pre></blockquote>
     * </li>
     *
     * <li>For getting all descriptor data elements a slightly modified iterator can be used.<br>
     * <blockquote><pre>
     * GenericDataIterator gdit = mol.genericDataIterator();
     * JOEGenericData genericData;
     * while(gdit.hasNext())
     * {
     *   genericData = gdit.nextGenericData();
     *   if (genericData.getDataType() == JOEDataType.JOE_PAIR_DATA)
     *   {
     *           PairData pairData = (PairData)genericData;
     *
     *   }
     * }
     * </pre></blockquote>
     * </li>
     *
     * <li>For getting all native value descriptors the simple data iterator can be used, also.
     * Because this is a little bit tricky, the {@link #nativeValueIterator()} would be
     * the recommended method for getting these values. Though the complex access
     * will be presented, because it's really instructive for understanding the
     * data access under JOELib.<br>
     * <blockquote><pre>
     * GenericDataIterator gdit = mol.genericDataIterator();
     * JOEGenericData genericData;
     * while(gdit.hasNext())
     * {
     *   genericData = gdit.nextGenericData();
     *   if (genericData.getDataType() == JOEDataType.JOE_PAIR_DATA)
     *   {
     *           PairData pairData = (PairData)genericData;
     *           // data must be parsed to check data type
     *           genericData = mol.getData(pairData.getAttribute(), true);
     *           pairData = (PairData)genericData;
     *             if(JOEHelper.hasInterface(pairData, "NativeValue"))
     *             {
     *        double tmpDbl=((NativeValue) pairData).getDoubleNV();
     *
     *             }
     *   }
     * }
     * </pre></blockquote>
     * </li>
     * </ol>
     *
     * @return    the generic data iterator
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #nativeValueIterator()
     * @see #getData(String)
     * @see #getData(String, boolean)
     * @see #addData(JOEGenericData)
     * @see #addData(JOEGenericData, boolean)
     * @see FeatureHelper#getAvailableFeatures(Molecule)
     * @see JOEDataType
     * @see JOEGenericData
     */
    public BasicPairDataIterator genericDataIterator()
    {
        return genericData.genericDataIterator();
    }

    public double[] getActualPose3D()
    {
        return actualPose3D;
    }

    /**
     *  Gets an atom of the <tt>Molecule</tt> object Atom index must be between <tt>
     *  1</tt> to <tt>numAtoms()</tt> .
     *
     * @param  idx  Description of the Parameter
     * @return      The atom value
     */
    public Atom getAtom(int idx)
    {
        Atom atom = null;

        if ((idx < 1) || (idx > getAtomsSize()))
        {
            // throw new Exception("Requested Atom Out of Range");
            logger.error("Requested atom (" + idx + ") out of range (1-" +
                getAtomsSize() + ").");
        }
        else
        {
            // System.out.println("get: "+(idx - 1)+" "+_atom.get(idx - 1));
            atom = (Atom) atoms.get(idx - 1);
        }

        return atom;
    }

    /**
     * @return Returns the atoms.
     */
    public List getAtoms()
    {
        return atoms;
    }

    /**
     * Returns the number of atoms.
     *
     * @return    The number of atoms
     */
    public int getAtomsSize()
    {
        return atoms.size();
    }

    /**
     *  Gets a bond of the <tt>Molecule</tt> object. Bond index must be between <tt>
     *  0</tt> to <tt>(numBonds()-1)</tt> .
     *
     * @param  idx  Description of the Parameter
     * @return      The bond value
     */
    public Bond getBond(int idx)
    {
        Bond bond = null;

        if ((idx < 0) || (idx >= getBondsSize()))
        {
            //        throw new Exception("Requested Bond Out of Range");
            logger.error("Requested bond (" + idx + ") out of range (0-" +
                (getBondsSize() - 1) + ").");
        }
        else
        {
            bond = (Bond) bonds.get(idx);
        }

        return bond;
    }

    /**
     *  Gets the bond attribute of the <tt>Molecule</tt> object Atom index must be
     *  between <tt>1</tt> to <tt>numAtoms()</tt> .
     *
     * @param  bgn  atom index of the start atom
     * @param  end  atom index of the end atom
     * @return      The bond value
     */
    public Bond getBond(int bgn, int end)
    {
        return getBond(getAtom(bgn), getAtom(end));
    }

    /**
     *  Gets the bond attribute of the <tt>Molecule</tt> object
     *
     * @param  bgn  Description of the Parameter
     * @param  end  Description of the Parameter
     * @return      The bond value
     */
    public Bond getBond(Atom bgn, Atom end)
    {
        Atom nbr;
        NbrAtomIterator nait = bgn.nbrAtomIterator();
        Bond bond = null;

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();

            if (nbr == end)
            {
                bond = nait.actualBond();

                break;
            }
        }

        return bond;
    }

    /**
     * @return Returns the bonds.
     */
    public List getBonds()
    {
        return bonds;
    }

    /**
     * Returns the number of bonds.
     *
     * @return    The number of bonds
     */
    public int getBondsSize()
    {
        return bonds.size();
    }

    /**
     *  Gets the conformer attribute of the Molecule object
     *
     * @param  index  Description of the Parameter
     * @return    The conformer value
     */
    public double[] getConformer(int index)
    {
        return (double[]) conformers.get(index);
    }

    /**
     * Returns a {@link java.util.Vector} of all conformer coordinates (<tt>double[]</tt> values).
     *
     * @return    The conformers coordinates
     */
    public List getConformers()
    {
        return conformers;
    }

    /**
     * Returns the number of conformers of this molecule.
     *
     * @return    The number of conformers of this molecule
     */
    public int getConformersSize()
    {
        return conformers.size();
    }

    /**
     * Gets the coordinate array of this molecule.
     *
     * @return    The coordinates array
     */
    public double[] getCoords3Darr()
    {
        return coords3Darr;
    }

    /**
     * Returns the data entry with the given name, if multiple data entries
     * exists only the first one is taken.
     * If the data element is a {@link BasicPairData} element with a unparsed
     * String value, the descriptor value will be automatically parsed from
     * a String value to the representing result ({@link FeatureResult}) class.
     * If no data element with this name exists in this molecule, <tt>null</tt>
     * is returned.
     *
     * <p>
     * There exist a lot of default data types which where defined in
             * {@link JOEDataType}. These data types are used for caching ring
             * searches and storing special data types like comments or virtual bonds.
             * Furthermore there exist the most important data type {@link BasicPairData}
             * for storing descriptor values. Read the {@link JOEDataType} description
             * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link BasicPairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  name      The data element (descriptor) name
     * @return     <tt>null</tt> if no element with this name exists
     * @see #getData(JOEDataType)
     * @see #getData(String, boolean)
     * @see #genericDataIterator()
     * @see #addData(JOEGenericData)
     * @see #addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureResult
     * @see BasicDataHolder#getData(String, boolean)
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    public PairData getData(String name)
    {
        return genericData.getData(name, true);
    }

    /**
     * Returns the data entry with the given name, if multiple data entries
     * exists only the first one is taken.
     * If the data element is a {@link BasicPairData} the <tt>parse</tt> flag
     * can be used to parse this data elements.
     * If no data element with this name exists in this molecule, <tt>null</tt>
     * is returned.
     *
     * <p>
     * There exist a lot of default data types which where defined in
             * {@link JOEDataType}. These data types are used for caching ring
             * searches and storing special data types like comments or virtual bonds.
             * Furthermore there exist the most important data type {@link BasicPairData}
             * for storing descriptor values. Read the {@link JOEDataType} description
             * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link BasicPairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  name       The data element (descriptor) name
     * @param  parse   Parse data element
     * @return     <tt>null</tt> if no element with this name exists
     * @see #getData(JOEDataType)
     * @see #getData(String)
     * @see #genericDataIterator()
     * @see #addData(JOEGenericData)
     * @see #addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureResult
     * @see BasicDataHolder#getData(String, boolean)
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    public PairData getData(String name, boolean parse)
    {
        return genericData.getData(name, parse);
    }

    /**
     * Returns the number of data elements in this molecule.
     *
     * @return    number of data elements in this molecule
     */
    public int getDataSize()
    {
        return genericData.size();
    }

    /**
     * Caching mechanism for deprotonated structure.
     *
     * The deprotonated molecular structure is very often used by feature
     * calculation algorithms, because the deprotonation step costs a lot of time
     * this method allows us to access the deprotonated structure, which is chached
     * internally.
     *
     * @return Returns the deprotonated molecular structure.
     */
    public Molecule getDeprotonated()
    {
        if (deprotonated == null)
        {
            Molecule cloned = (Molecule) this.clone(false);

            if (cloned.deleteHydrogens())
            {
                deprotonated = cloned;
            }
        }

        return deprotonated;
    }

    /**
     *  Gets the energy attribute of the <tt>Molecule</tt> object
     *
     * @return    The energy value
     */
    public double getEnergy()
    {
        return (energy);
    }

    /**
     *  Gets the firstAtom attribute of the <tt>Molecule</tt> object
     *
     * @return    The firstAtom value
     */
    public Atom getFirstAtom()
    {
        return ((atoms.size() == 0) ? null : (Atom) atoms.get(0));
    }

    /**
     *  Gets the flags attribute of the <tt>Molecule</tt> object
     *
     * @return    The flags value
     * @see       joelib2.molecule.Molecule#HAS_SSSR
     * @see       joelib2.molecule.Molecule#RINGS_ASSIGNED
     * @see       joelib2.molecule.Molecule#AROMATICITY_ASSIGNED
     * @see       joelib2.molecule.Molecule#ATOMTYPES_ASSIGNED
     * @see       joelib2.molecule.Molecule#CHIRALITY_ASSIGNED
     * @see       joelib2.molecule.Molecule#PCHARGE_ASSIGNED
     * @see       joelib2.molecule.Molecule#HYBRID_ASSIGNED
     * @see       joelib2.molecule.Molecule#IMPVAL_ASSIGNED
     * @see       joelib2.molecule.Molecule#KEKULE_ASSIGNED
     * @see       joelib2.molecule.Molecule#CLOSURE_ASSIGNED
     * @see       joelib2.molecule.Molecule#H_ADDED
     * @see       joelib2.molecule.Molecule#PH_CORRECTED
     * @see       joelib2.molecule.Molecule#AROMATICITY_CORRECTED
     * @see       joelib2.molecule.Molecule#IS_CHAIN
     * @see       joelib2.molecule.Molecule#IS_CURRENT_CONFORMER
     */
    public int getFlags()
    {
        return (flags);
    }

    /**
     * Returns the number of heavy atoms.
     * That is the number of all atoms except hydrogen atoms.
     *
     * @return    The number of heavy atoms
     */
    public int getHeavyAtomsNumber()
    {
        Atom atom;
        BasicAtomIterator ait = this.atomIterator();
        int count = 0;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (!AtomIsHydrogen.isHydrogen(atom))
            {
                count++;
            }
        }

        return count;
    }

    /**
     *  Gets the inputType attribute of the <tt>Molecule</tt> object
     *
     * @return    The inputType value
     */
    public IOType getInputType()
    {
        return inputType;
    }

    /**
     *  Gets the mod attribute of the <tt>Molecule</tt> object
     *
     * @return    The mod value
     */
    public int getModificationCounter()
    {
        return modificationCounter;
    }

    /**
     *  Gets the outputType attribute of the <tt>Molecule</tt> object
     *
     * @return    The outputType value
     */
    public IOType getOutputType()
    {
        return (outputType);
    }

    public String getPartialChargeVendor()
    {
        String vendor = DEFAULT_PCHARGE_VENDOR;

        if (!this.isAssignPartialCharge())
        {
            vendor = partialChargeVendor;
        }

        return vendor;
    }

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     */
    public BasicPose getPose(int index)
    {
        return (BasicPose) poses.get(index);
    }

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     *
     */
    public void getPoseCoordinates(int poseI, double[] xyz)
    {
        //check that the pose is valid
        if (poseI >= getPosesSize())
        {
            //throw new Exception("WARNING! Invalid pose specified");
            logger.error("Invalid pose specified!");
        }
        else
        {
            //Check that the pose references a valid conformer
            if (((BasicPose) poses.get(poseI)).getConformer() >=
                    (int) getConformersSize())
            {
                //throw new Exception("WARNING! Pose references invalid conformer");
                logger.error(INVALID_CONFORMER_REFERENCE);
            }
            else
            {
                //Check that xyz is not NULL
                if (xyz != null)
                {
                    //Generate coordinates for the pose
                    int atomI;
                    double[] conf = getConformer(((BasicPose) poses.get(poseI))
                            .getConformer());

                    for (atomI = 0; atomI < (3 * this.getAtomsSize()); atomI++)
                    {
                        xyz[atomI] = conf[atomI];
                    }

                    ((BasicPose) poses.get(poseI)).getCoordinateTransformation()
                     .transform(xyz, this.getAtomsSize());
                }
            }
        }
    }

    public List getPoses()
    {
        return poses;
    }

    public final int getPosesSize()
    {
        return poses.size();
    }

    /**
     *  Gets the residue attribute of the Molecule object
     *
     * @param  idx  Description of the Parameter
     * @return      The residue value
     */
    public Residue getResidue(int idx)
    {
        if ((idx < 0) || (idx >= getResiduesSize()))
        {
            throw new IndexOutOfBoundsException("Residue with index " + idx +
                " does'nt exist.");

            //return null;
        }

        return residues.get(idx);
    }

    /**
     * Returns the number of residues.
     *
     * @return    The number of residues
     */
    public int getResiduesSize()
    {
        return residues.size();
    }

    /**
     * Gets iterator for the Smallest Set of Smallest Rings (SSSR).
     *
     * <blockquote><pre>
     * RingIterator rit = mol.getRingIterator();
     * JOERing ring;
     * while(rit.hasNext())
     * {
     *   ring = rit.nextRing();
     *
     * }
     * </pre></blockquote>
     *
     * @return     The ring iterator
     * @see #getSSSR()
     * @see #findSSSR()
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     * @see BasicRingIterator
     * @.cite fig96
     */
    public BasicRingIterator getRingIterator()
    {
        return new BasicRingIterator(getSSSR());
    }

    /**
     * Returns the number of rotatable bonds.
     *
     * @return    The number of rotatable bonds
     */
    public int getRotorsSize()
    {
        Bond bond;
        BasicBondIterator bit = this.bondIterator();
        int count = 0;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (BondIsRotor.isRotor(bond))
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets the Smallest Set of Smallest Rings (SSSR).
     *
     * @return    {@link java.util.Vector} of <tt>JOERing</tt>
     *
     * @see #getRingIterator()
     * @see #findSSSR()
     * @.cite fig96
     */
    public synchronized List getSSSR()
    {
        Rings ringData;
        List rings = null;

        try
        {
            ringData = (Rings) FeatureHelper.instance().featureFrom(this,
                    RingFinderSSSR.getName());
            rings = ringData.getRings();
			// assign parent after loaded from that molecule
            for (int i = 0; i < rings.size(); i++)
            {
                Ring ring = (Ring) rings.get(i);
                ring.setParent(this);
            }
        }
        catch (FeatureException e)
        {
            logger.error("Could not get SSSR data: " + e.getMessage());
        }

        return rings;
    }

    /**
     *  Gets the title attribute of the <tt>Molecule</tt> object.
     *
     * @return    The title value
     */
    public final String getTitle()
    {
        return title;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean has2D()
    {
        boolean hasX;
        boolean hasY;
        boolean hasZ;
        Atom atom;
        BasicAtomIterator ait = this.atomIterator();

        hasX = hasY = false;
        hasZ = false;

        boolean has2D = false;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (!hasX && (atom.get3Dx() != 0.0))
            {
                hasX = true;
            }

            if (!hasY && (atom.get3Dy() != 0.0))
            {
                hasY = true;
            }

            if (!hasZ && (atom.get3Dz() != 0.0))
            {
                hasZ = true;
            }

            if (hasX && hasY && !hasZ)
            {
                has2D = true;
            }
        }

        return has2D;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean has3D()
    {
        boolean hasX;
        boolean hasY;
        boolean hasZ;
        Atom atom;
        BasicAtomIterator ait = this.atomIterator();

        hasX = hasY = hasZ = false;

        boolean has3D = false;

        if (this.coords3Darr != null)
        {
            while (ait.hasNext())
            {
                atom = ait.nextAtom();

                if (!hasX && (atom.get3Dx() != 0.0))
                {
                    hasX = true;
                }

                if (!hasY && (atom.get3Dy() != 0.0))
                {
                    hasY = true;
                }

                if (!hasZ && (atom.get3Dz() != 0.0))
                {
                    hasZ = true;
                }

                if (hasX && hasY && hasZ)
                {
                    has3D = true;
                }
            }
        }

        return has3D;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasAromaticCorrected()
    {
        return (hasFlags(AROMATICITY_CORRECTED));
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasChainsPerceived()
    {
        return (hasFlags(IS_CHAIN));
    }

    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return    <tt>true</tt> if the generic attribute/value pair exists
     */
    public boolean hasData(String name)
    {
        return genericData.hasData(name);
    }

    /**
     * Calculates the hashcode of a molecule using the methods <tt>AbstractDatabase.getHashcode</tt>
     * and <tt>AbstractDatabase.getSMILESHashcode</tt>.
     */
    public synchronized int hashCode()
    {
        if (hash == 0)
        {
            int hashCode;

            if (moleculeHashing)
            {
                // ensure unique renumbering
                Morgan morgan = new Morgan(new BasicTieResolver());
                Molecule tMol = (Molecule) this.clone(false);
                morgan.calculate(tMol);

                Molecule rMol = morgan.renumber(tMol);

                // hashcode without cis/trans and chirality informations
                hashCode = AbstractDatabase.getHashcode(rMol);

                // hashcode WITH cis/trans and chirality informations
                hashCode = (31 * hashCode) +
                    AbstractDatabase.getSMILESHashcode(rMol);
            }
            else
            {
                hashCode = super.hashCode();
            }

            hash = hashCode;
        }

        return hash;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasHydrogensAdded()
    {
        return (hasFlags(H_ADDED));
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasNonZeroCoords()
    {
        Atom atom;
        BasicAtomIterator ait = this.atomIterator();
        boolean notZero = false;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (atom.getCoords3D().notEquals(BasicVector3D.ZERO))
            {
                notZero = true;

                break;
            }
        }

        return notZero;
    }

    /**
     * Increase modification counter.
     *
     * @see #decrementMod()
     * @see #getModificationCounter()
     * @see #beginModify()
     * @see #endModify()
     * @see #endModify(boolean)
     */
    public void incrementMod()
    {
        modificationCounter++;
    }

    /**
     *  Description of the Method
     *
     * @param  atom  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean insertAtom(Atom atom)
    {
        beginModify();

        Atom newAtom = newAtom();
        newAtom = atom;
        newAtom.setIndex(this.getAtomsSize() + 1);
        newAtom.setParent(this);
        atoms.add(newAtom);

        if (virtualBonds != null)
        {
            //add bonds that have been queued
            BasicVirtualBond virtualB;
            Vector verase = new Vector();

            for (int vbIdx = 0; vbIdx < virtualBonds.size(); vbIdx++)
            {
                virtualB = (BasicVirtualBond) virtualBonds.get(vbIdx);

                if ((virtualB.getBeginAtom() > this.getAtomsSize()) ||
                        (virtualB.getEndAtom() > this.getAtomsSize()))
                {
                    continue;
                }

                if ((newAtom.getIndex() == virtualB.getBeginAtom()) ||
                        (newAtom.getIndex() == virtualB.getEndAtom()))
                {
                    addBond(virtualB.getBeginAtom(), virtualB.getEndAtom(),
                        virtualB.getOrder());
                    //verase.add(genericData);
                }
            }

            if (verase.size() != 0)
            {
                virtualBonds = null;
                //deleteData(verase);
            }
        }

        endModify();

        return true;
    }

    /**
     *  Gets the flag if the automatic calculation of the formal charge of the
     *  atoms is allowed. This is for example used in the PH value correction
     *  method.
     *
     * @return    <tt>true</tt> if the calculation of the formal charge is
     *      allowed.
     */
    public boolean isAssignFormalCharge()
    {
        return (assignFormalCharge);
    }

    /**
     *  Gets the flag if the automatic calculation of the partial charge of the
     *  atoms is allowed.
     *
     * @return    <tt>true</tt> if the calculation of the partial charge is
     *      allowed.
     * @see joelib2.molecule.charge.GasteigerMarsili
     * @.cite gm78
     */
    public boolean isAssignPartialCharge()
    {
        return (assignPartialCharge);
    }

    /**
     *  Gets the chiral attribute of the Molecule object
     *
     * @return    The chiral value
     */
    public boolean isChiral()
    {
        Atom atom;
        BasicAtomIterator ait = this.atomIterator();
        boolean isChiral = false;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if ((AtomIsCarbon.isCarbon(atom) ||
                        AtomIsNitrogen.isNitrogen(atom)) &&
                    (AtomHeavyValence.valence(atom) > 2) &&
                    AtomIsChiral.isChiral(atom))
            {
                isChiral = true;

                break;
            }
        }

        return isChiral;
    }

    /**
     *  Gets the correctedForPH attribute of the Molecule object
     *
     * @return    The correctedForPH value
     */
    public boolean isCorrectedForPH()
    {
        return (hasFlags(PH_CORRECTED));
    }

    /**
     * Returns <tt>true</tt> if this molecule contains no atoms.
     *
     * @return   <tt>true</tt> if this molecule contains no atoms
     */
    public boolean isEmpty()
    {
        return (this.getAtomsSize() == 0);
    }

    /**
     * @return Returns the uniqueHash.
     */
    public boolean isMoleculeHashing()
    {
        return moleculeHashing;
    }

    /**
     * @return Returns the occuredKekulizationError.
     */
    public boolean isOccuredKekulizationError()
    {
        return occuredKekulizationError;
    }

    /**
     * Kekulizes the molecule.
     *
     * @return    <tt>true</tt> if successfull
     */
    public boolean kekulize()
    {
        boolean kekulized = false;

        if (!occuredKekulizationError)
        {
            if (getAtomsSize() > 255)
            {
                logger.error(
                    "Only molecules with less than 255 atoms will be kekulized.");
                kekulized = false;
            }
            else
            {
                Bond bond;
                BasicBondIterator bit = this.bondIterator();
                int newBO;
                boolean allProcessed = true;

                while (bit.hasNext())
                {
                    bond = bit.nextBond();
                    newBO = BondKekuleType.getKekuleType(bond);

                    if (newBO == -1)
                    {
                        occuredKekulizationError = true;
                        allProcessed = false;
                    }

                    if (newBO == KekuleHelper.KEKULE_SINGLE)
                    {
                        bond.setBondOrder(1);
                    }
                    else if (newBO == KekuleHelper.KEKULE_DOUBLE)
                    {
                        bond.setBondOrder(2);
                    }
                    else if (newBO == KekuleHelper.KEKULE_TRIPLE)
                    {
                        bond.setBondOrder(3);
                    }
                }

                if (allProcessed)
                {
                    kekulized = true;
                }
                else
                {
                    kekulized = false;
                }
            }
        }

        return kekulized;
    }

    /**
     *  Gets an iterator over native descriptor values (<tt>int</tt> or <tt>double</tt>)
     * of this molecule.
     *
     * <blockquote><pre>
     * NativeValueIterator nativeIt = mol.nativeValueIterator();
     * double value;
     * String descName;
     * while (nativeIt.hasNext())
     * {
     *   value = nativeIt.nextDouble();
     *   descName = nativeIt.actualName();
     *
     * }
     * </pre></blockquote>
     *
     * @return    The native value iterator
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     */
    public BasicNativeValueIterator nativeValueIterator()
    {
        return new BasicNativeValueIterator(this,
                genericData.genericDataIterator());
    }

    /**
     * Creates only a new atom.
     *
     * @return    the created atom
     * @see #newBond()
     * @see #addAtomClone(Atom)
     * @see #newAtom()
     */
    public Atom newAtom()
    {
        return newAtom(false);
    }

    /**
     * Creates a new atom and adds it to molecule.
     *
     * @return    the new atom
     * @see #addAtomClone(Atom)
     * @see #newAtom()
     */
    public Atom newAtom(boolean add)
    {
        Atom newAtom = new BasicConformerAtom();
        newAtom.setParent(this);
        newAtom.setIndex(this.getAtomsSize() + 1);

        if (logger.isDebugEnabled())
        {
            logger.debug("Created new atom " + atoms.get(this.getAtomsSize()) +
                " with index " + newAtom.getIndex());
        }

        if (add)
        {
            beginModify();
            atoms.add(newAtom);
            checkVirtualBonds(newAtom);
            endModify();
        }

        return newAtom;
    }

    /**
     * Creates only a new bond.
     *
     * @return    the created bond
     * @see #newAtom()
     */
    public Bond newBond()
    {
        Bond bond = new BasicBond();
        bond.setParent(this);

        return bond;
    }

    /**
     * Returns and adds new residue informations for this molecule.
     *
     * @return    The residue informations.
     */
    public Residue newResidue()
    {
        Residue newresidue = new BasicResidue();
        newresidue.setIndex(residues.size());
        residues.add(newresidue);

        return newresidue;
    }

    public synchronized int reHash()
    {
        hash = 0;

        return hashCode();
    }

    /**
     * @param  renumbered  of type <tt>Atom</tt>
     */
    public void renumberAtoms(List<Atom> renumbered)
    {
        if (isEmpty())
        {
            return;
        }

        List<Atom> renumClone;

        if (renumbered instanceof Vector)
        {
            renumClone = (Vector) ((Vector) renumbered).clone();
        }
        else
        {
            renumClone = new Vector<Atom>();

            for (int index = 0; index < renumbered.size(); index++)
            {
                renumClone.add(renumbered.get(index));
            }
        }

        //    va = v;
        ConformerAtom atom;
        BasicAtomIterator ait = new BasicAtomIterator(renumClone);

        if ((renumClone.size() != 0) && (renumClone.size() < getAtomsSize()))
        {
            //make sure all atoms are represented in the vector
            BasicBitVector atomBits = new BasicBitVector();

            while (ait.hasNext())
            {
                atom = (ConformerAtom) ait.nextAtom();

                //bv |= atom.getIdx();
                atomBits.set(atom.getIndex());
            }

            ait = this.atomIterator();

            while (ait.hasNext())
            {
                atom = (ConformerAtom) ait.nextAtom();

                if (!atomBits.get(atom.getIndex()))
                {
                    renumClone.add(atom);
                }
            }
        }

        int confI;
        int atomI;
        double[] conf;
        double[] ctmp = new double[getAtomsSize() * 3];

        for (confI = 0; confI < getConformersSize(); confI++)
        {
            conf = getConformer(confI);
            ait = new BasicAtomIterator(renumClone);
            atomI = 0;

            while (ait.hasNext())
            {
                atom = (ConformerAtom) ait.nextAtom();
                System.arraycopy(conf, atom.getCoordinateIdx(), ctmp, atomI * 3,
                    3);
                atomI++;
            }

            System.arraycopy(ctmp, 0, conf, 0, 3 * getAtomsSize());
        }

        ait.reset();
        atomI = 1;

        while (ait.hasNext())
        {
            atom = (ConformerAtom) ait.nextAtom();
            atom.setIndex(atomI);
            atomI++;
        }

        //set the atom vector
        atoms = renumClone;
    }

    /**
     * Reserves a initial capacity of atoms.
     *
     * @param  natoms  The number of atoms to reserve
     */
    public void reserveAtoms(int natoms)
    {
        if ((natoms != 0) && (modificationCounter != 0))
        {
            ((Vector) atoms).ensureCapacity(natoms);
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public ResidueIterator residueIterator()
    {
        return new BasicResidueIterator(residues);
    }

    /**
     *  Clones molecule without additional data (e.g. descriptor data).
     *
     * @param  source  Description of the Parameter
     * @return         Description of the Return Value
     */
    public Molecule set(final Molecule source)
    {
        return set(source, false, null);
    }

    /**
     *  Clones molecule.
     * Missing descriptor entries, defined in <tt>descriptors</tt> will be
     * ignored.
     *
     * @param  source     Description of the Parameter
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @param  descriptors  descriptors to clone. If <tt>null</tt> all descriptors are cloned
     * @return            The new molecule
     */
    public Molecule set(final Molecule source, boolean cloneDesc,
        String[] descriptors)
    {
        return set(source, cloneDesc, descriptors, false);
    }

    /**
     *  Clones molecule.
     *
     * Note that the atom and bond labels are also stored as features. If you do not clone
     * those features, they must be calculated recalculated for each cloned molecule.
     *
     * @param  source     Description of the Parameter
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @param  descriptors  descriptors to clone. If <tt>null</tt> all descriptors are cloned
     * @param  addDescs  Missing descriptor entries, defined in <tt>descriptors</tt>
     *          will be automatically added to the molecule if <tt>true</tt>
     * @return            The new molecule
     */
    public Molecule set(final Molecule source, boolean cloneDesc,
        String[] descriptors, boolean addFeatures)
    {
        Atom atom;
        Bond bond;
        AtomIterator ait = source.atomIterator();
        BondIterator bit = source.bondIterator();

        clear();
        beginModify();

        ((Vector) atoms).ensureCapacity(source.getAtomsSize());
        ((Vector) bonds).ensureCapacity(source.getBondsSize());

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            addAtomClone(atom);
        }

        while (bit.hasNext())
        {
            bond = bit.nextBond();
            addBondClone(bond);
        }

        inputType = source.getInputType();
        outputType = source.getOutputType();

        this.title = source.getTitle();
        this.energy = source.getEnergy();

        endModify();

        //Copy Residue information
        int numRes = source.getResiduesSize();

        if (numRes > 0)
        {
            int resIdx;
            Residue sourceR = null;
            Residue res = null;
            Atom sourceA = null;

            for (resIdx = 0; resIdx < numRes; resIdx++)
            {
                res = newResidue();
                sourceR = source.getResidue(resIdx);
                res.setName(sourceR.getName());
                res.setNumber(sourceR.getNumber());
                res.setChain(sourceR.getChain());
                res.setChainNumber(sourceR.getChainNumber());

                BasicAtomIterator ait2 = sourceR.atomIterator();

                while (ait2.hasNext())
                {
                    sourceA = ait2.nextAtom();
                    atom = getAtom(sourceA.getIndex());
                    res.addAtom(atom);
                    res.setAtomID(atom, sourceR.getAtomID(sourceA));
                    res.setHeteroAtom(atom, sourceR.isHeteroAtom(sourceA));
                    res.setSerialNumber(atom, sourceR.getSerialNumber(sourceA));
                }
            }
        }

        //Copy conformer information
        if (source instanceof ConformerMolecule)
        {
            if (((ConformerMolecule) source).getConformersSize() > 1)
            {
                int confI;
                int atomI;
                List<double[]> conf = new Vector<double[]>();
                double[] xyz = null;

                for (confI = 0;
                        confI <
                        ((ConformerMolecule) source).getConformersSize();
                        confI++)
                {
                    xyz =
                        new double[3 *
                        ((ConformerMolecule) source).getAtomsSize()];

                    for (atomI = 0; atomI < (int) (3 * source.getAtomsSize());
                            atomI++)
                    {
                        xyz[atomI] =
                            ((ConformerMolecule) source).getConformer(confI)[atomI];
                    }

                    conf.add(xyz);
                }

                setConformers(conf);
            }
        }

        //    //Copy rotamer list
        //    JOERotamerList rml = (JOERotamerList) src.getData(oeRotamerList);
        //    //if (rml!=null) {System.out.println("DEBUG : OEMol assignment operator.  Source HAS RotamerList");}
        //    //else  {System.out.println("DEBUG : OEMol assignment operator.  Source does NOT have RotamerList");}
        //    if (rml!=null && rml.numAtoms() == src.numAtoms()) {
        //        //Destroy old rotamer list if necessary
        //        if ((JOERotamerList)getData(oeRotamerList)) {
        //            deleteData(oeRotamerList);
        //          }
        //
        //        //Set base coordinates
        //        JOERotamerList cp_rml = new JOERotamerList();
        //        int k,l;
        //        Vector bc;
        //        double[] c  = null;
        //        double[] cc = null;
        //        for (k=0 ; k<rml.numBaseCoordinateSets() ; k++) {
        //            c = new double [3*rml.numAtoms()];
        //            cc = rml.getBaseCoordinateSet(k);
        //            for (l=0 ; l<3*rml.numAtoms() ; l++) c[l] = cc[l];
        //            bc.put(c);
        //          }
        //        if (rml.numBaseCoordinateSets()) cp_rml.setBaseCoordinateSets(bc,rml.numAtoms());
        //
        //        //Set reference array
        //        char[] ref = new char [rml.numRotors()*4];
        //        rml.getReferenceArray(ref);
        //        cp_rml.setup( this, ref, rml.numRotors() );
        //        delete [] ref;
        //
        //        //Set Rotamers
        //        char[] rotamers = new char [(rml.numRotors()+1)*rml.numRotamers()];
        //
        //        RotamerIterator rit = rotamerIterator();
        //        int idx=0;
        //        char[] _rotamers;
        //
        //        while(rit.hasNext())
        //        {
        //          _rotamers = rit.nextRotamer();
        //          //memcpy(&rotamers[idx], (const char*)*kk, sizeof( char)*(rml.numRotors()+1));
        //          //idx += sizeof( char)*(rml.numRotors()+1);
        //          System.arraycopy(kk, 0, rotamers, idx, rml.numRotors()+1);
        //          idx += rml.numRotors()+1;
        //        }
        //
        //        cp_rml.addRotamers( rotamers, rml.numRotamers());
        //        delete [] rotamers;
        //        setData(cp_rml);
        //      }
        //Copy pose information
        //    _pose = src._pose.clone();
        //    if (_pose.size()!=0) setPose(src.currentPoseIndex());
        // copy simple pair data entries
        if (cloneDesc)
        {
            // clone all descriptor values
            if (descriptors == null)
            {
                PairDataIterator gdit = source.genericDataIterator();
                PairData sourcePD;

                while (gdit.hasNext())
                {
                    sourcePD = gdit.nextPairData();

                    BasicPairData targetPD = new BasicPairData();
                    targetPD.setKey(sourcePD.getKey());

                    Object obj = sourcePD.getKeyValue();

                    if (obj instanceof FeatureResult)
                    {
                        targetPD.setKeyValue(((FeatureResult) obj).clone());
                    }
                    else
                    {
                        targetPD.setKeyValue(sourcePD.toString(getInputType()));
                    }

                    this.addData(targetPD);
                }
            }

            // clone only given descriptor values
            else
            {
                int size = descriptors.length;
                PairData sourcePD;

                for (int i = 0; i < size; i++)
                {
                    sourcePD = source.getData(descriptors[i]);

                    // descriptor to clone
                    if (sourcePD == null)
                    {
                        if (addFeatures)
                        {
                            // If  not available we'll try to calculate this descriptor
                            try
                            {
                                FeatureHelper.featureFrom(this, descriptors[i],
                                    true);
                            }
                            catch (FeatureException de)
                            {
                                // that's a serious problem the user should know
                                logger.error("Descriptor '" + descriptors[i] +
                                    "' was not cloned, because it is not available" +
                                    " and could not be calculated in" +
                                    getTitle() + ".");

                                continue;
                            }

                            sourcePD = source.getData(descriptors[i]);
                        }
                        else
                        {
                            // be quiet, if descriptors to clone are not available.
                            // Often, the descriptors to clone are only defined
                            // for speeding up the cloning process in some
                            // descriptor calculation methods.
                            //                                                  logger.warn(
                            //                                                          "Descriptor '"
                            //                                                                  + descriptors[i]
                            //                                                                  + "' was not cloned, because it is not available"
                            //                                                                  + " in"
                            //                                                                  + getTitle()
                            //                                                                  +".");
                            continue;
                        }
                    }

                    BasicPairData targetPD = new BasicPairData();
                    targetPD.setKey(sourcePD.getKey());

                    //          dp.setValue(((PairData) genericData).toString(getInputType()));
                    Object obj = sourcePD.getKeyValue();

                    if (obj instanceof FeatureResult)
                    {
                        targetPD.setKeyValue(((FeatureResult) obj).clone());
                    }
                    else
                    {
                        targetPD.setKeyValue(sourcePD.toString(getInputType()));
                    }

                    this.addData(targetPD);
                }
            }
        }

        return this;
    }

    public void setActualPose3D(double[] poseArr)
    {
        actualPose3D = poseArr;
    }

    /**
     *  Sets the add attribute of the <tt>Molecule</tt> object
     *
     * @param  source  The new add value
     * @return         Description of the Return Value
     */
    public Molecule setAdd(final Molecule source)
    {
        Molecule src = source;
        Atom atom;
        Bond bond;
        AtomIterator ait = src.atomIterator();
        BondIterator bit = src.bondIterator();

        beginModify();

        int prevatms = getAtomsSize();

        title += ("_" + src.getTitle());

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            addAtomClone(atom);
        }

        while (bit.hasNext())
        {
            bond = bit.nextBond();
            addBondClone(bond);
            addBond(bond.getBeginIndex() + prevatms,
                bond.getEndIndex() + prevatms, bond.getBondOrder());
        }

        endModify();

        return this;
    }

    /**
     *  Sets the aromaticCorrected attribute of the <tt>Molecule</tt> object
     */
    public void setAromaticCorrected()
    {
        setFlags(AROMATICITY_CORRECTED);
    }

    /**
     *  Sets the flag if the automatic calculation of the formal charge of the
     *  atoms is allowed. This is for example used in the PH value correction
     *  method.
     *
     * @param  assign  <tt>true</tt> if the calculation of the formal charge is
     *      allowed.
     */
    public void setAssignFormalCharge(boolean assign)
    {
        assignFormalCharge = assign;
    }

    /**
     *  Sets the automaticPartialCharge attribute of the <tt>Molecule</tt> object
     *
     * @param  assign  The new automaticPartialCharge value
     */
    public void setAssignPartialCharge(boolean assign)
    {
        assignPartialCharge = assign;
    }

    /**
     *  Sets the chainsPerceived attribute of the <tt>Molecule</tt> object
     */
    public void setChainsPerceived()
    {
        setFlags(IS_CHAIN);
    }

    /**
     *  Sets the conformer attribute of the Molecule object
     *
     * @param  index  The new conformer value
     */
    public void setConformer(int index, double[] conformer)
    {
        conformers.set(index, conformer);
    }

    /**
     *  Sets the conformers attribute of the Molecule object
     *
     * @param  conformers  The new conformers value (v is of type double[])
     */
    public void setConformers(List<double[]> newConfs)
    {
        conformers.clear();
        ((Vector) conformers).ensureCapacity(newConfs.size());

        for (int i = 0; i < newConfs.size(); i++)
        {
            conformers.add(newConfs.get(i));
        }

        coords3Darr = (conformers.size() == 0) ? null
                                               : (double[]) conformers.get(0);
    }

    public void setCoords3Darr(double[] c3Darr)
    {
        coords3Darr = c3Darr;

        ConformerAtom atom;
        BasicAtomIterator ait = this.atomIterator();

        while (ait.hasNext())
        {
            atom = (ConformerAtom) ait.nextAtom();
            atom.setCoords3Darr(coords3Darr);
        }
    }

    /**
     *  Sets the correctedForPH attribute of the <tt>Molecule</tt> object
     */
    public void setCorrectedForPH()
    {
        setFlags(PH_CORRECTED);
    }

    /**
     *  Sets the energy attribute of the <tt>Molecule</tt> object
     *
     * @param  energy  The new energy value
     */
    public void setEnergy(double newEnergy)
    {
        energy = newEnergy;
    }

    /**
     *  Sets the hydrogensAdded attribute of the <tt>Molecule</tt> object
     */
    public void setHydrogensAdded()
    {
        setFlags(H_ADDED);
    }

    /**
     *  Sets the inputType attribute of the <tt>Molecule</tt> object
     *
     * @param  type  The new inputType value
     */
    public void setInputType(IOType type)
    {
        inputType = type;
    }

    /**
     * @param uniqueHash The uniqueHash to set.
     */
    public void setMoleculeHashing(boolean calculate)
    {
        this.moleculeHashing = calculate;
        hash = 0;
    }

    /**
     * @param occuredKekulizationError The occuredKekulizationError to set.
     */
    public void setOccuredKekulizationError(boolean occuredKekulizationError)
    {
        this.occuredKekulizationError = occuredKekulizationError;
    }

    /**
     *  Sets the outputType attribute of the <tt>Molecule</tt> object
     *
     * @param  type  The new outputType value
     */
    public void setOutputType(IOType type)
    {
        outputType = type;
    }

    public void setPartialChargeVendor(String vendor)
    {
        partialChargeVendor = vendor;
    }

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     *
     */
    public void setPose(int poseI)
    {
        //check that the pose is valid
        if (poseI >= getPosesSize())
        {
            //throw new Exception("WARNING! Invalid pose specified");
            logger.error("Invalid pose specified!");
        }
        else
        {
            //Check that the pose references a valid conformer
            if (((BasicPose) poses.get(poseI)).getConformer() >=
                    (int) getConformersSize())
            {
                //throw new Exception("WARNING! Pose references invalid conformer");
                logger.error(INVALID_CONFORMER_REFERENCE);
            }
            else
            {
                //Make sure the pose coordinate array has memory
                if (actualPose3D == null)
                {
                    actualPose3D = new double[3 * this.getAtomsSize()];
                }

                //Generate coordinates for the pose
                int atomI;
                double[] conf = getConformer(((BasicPose) poses.get(poseI))
                        .getConformer());

                for (atomI = 0; atomI < (3 * this.getAtomsSize()); atomI++)
                {
                    actualPose3D[atomI] = conf[atomI];
                }

                ((BasicPose) poses.get(poseI)).getCoordinateTransformation()
                 .transform(actualPose3D, this.getAtomsSize());

                //Point the atom coordinate pointer to the coordinates of the pose
                this.coords3Darr = actualPose3D;

                //Remember current pose
                actualPose3Dindex = poseI;
            }
        }
    }

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     */
    public void setPoses(List<Pose> newPoses)
    {
        //Check that all the poses reference valid conformers
        int poseI;
        boolean posesOK = true;

        for (poseI = 0; poseI < newPoses.size(); poseI++)
        {
            if (((BasicPose) newPoses.get(poseI)).getConformer() >=
                    (int) getConformersSize())
            {
                //throw new Exception("WARNING! Poses do not reference valid conformers");
                logger.error(INVALID_CONFORMER_REFERENCE);
                posesOK = false;

                break;
            }
        }

        if (posesOK)
        {
            //Set the poses
            poses = newPoses;

            //If the atom coordinate array was looking at poses have it look at the first new pose
            if (poses.size() != 0)
            {
                if (coords3Darr == actualPose3D)
                {
                    setPose(0);
                }
            }
            else if (this.conformers.size() != 0)
            {
                useConformer(0);
            }
            else
            {
                clearCoords3Darr();
            }
        }
    }

    /**
     *  Sets the title attribute of the <tt>Molecule</tt> object
     *
     * @param  title  The new title value
     */
    public void setTitle(String newTitle)
    {
        if (newTitle == null)
        {
            title = "";
        }
        else
        {
            title = newTitle;
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        return toString(getOutputType(), true);
    }

    /**
     *  Description of the Method
     *
     * @param  type  Description of the Parameter
     * @return       Description of the Return Value
     */
    public String toString(IOType type)
    {
        return toString(type, true);
    }

    /**
     *  Description of the Method
     *
     * @param  writeDesc  Description of the Parameter
     * @return                   Description of the Return Value
     */
    public String toString(boolean writeDesc)
    {
        return toString(getOutputType(), writeDesc);
    }

    /**
     *  Description of the Method
     *
     * @param  type              Description of the Parameter
     * @param  writeDesc  Description of the Parameter
     * @return                   Description of the Return Value
     */
    public String toString(IOType type2use, boolean writeDesc)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
        IOType type = type2use;

        if (type == null)
        {
            logger.warn("Output type not defined, using default '" +
                DEFAULT_IO_TYPE.getName() + "'.");
            type = DEFAULT_IO_TYPE;
        }

        MoleculeFileIO writer = null;
        boolean successfull = true;

        try
        {
            writer = MoleculeFileHelper.getMolWriter(baos, type);

            if (!writer.writeable())
            {
                logger.warn(type.getRepresentation() + " is not writeable.");
                successfull = false;
            }

            if (successfull)
            {
                if (!writeDesc && (writer instanceof PropertyWriter))
                {
                    ((PropertyWriter) writer).write(this, null, false, null);
                }
                else
                {
                    writer.write(this, null);
                }
            }
        }
        catch (IOException ex)
        {
            logger.error("Unable to write molecule: " + ex.getMessage());
            ex.printStackTrace();
            successfull = false;
        }
        catch (MoleculeIOException ex)
        {
            logger.error("Unable to write molecule: " + ex.getMessage());
            ex.printStackTrace();
            successfull = false;
        }
        catch (Exception ex)
        {
            logger.error("Unable to write molecule: " + ex.getMessage());
            ex.printStackTrace();
            successfull = false;
        }

        String moleculeS = null;

        if (successfull)
        {
            moleculeS = baos.toString();
        }

        return moleculeS;
    }

    /**
     *  Sets the conformer attribute of the Molecule object
     *
     * @param  index  The new conformer value
     */
    public void useConformer(int index)
    {
        coords3Darr = (double[]) conformers.get(index);
    }

    protected void clearActualPose3D()
    {
        this.setActualPose3D(null);
    }

    protected void clearCoords3Darr()
    {
        this.setCoords3Darr(null);
    }

    /**
     * Destructor for this molecule.
     */
    protected void finalize() throws Throwable
    {
        conformers.clear();
        genericData.clear();
        atoms.clear();
        bonds.clear();
        super.finalize();
    }

    /**
     *  Has this molecules set flags.
     *
     * @param  flag  molecule flag
     * @return       <tt>true</tt> if the flag was set
     */
    protected boolean hasFlags(int flag)
    {
        return (((flags & flag) != 0) ? true : false);
    }

    /**
     * @param deprotonated The deprotonated to set.
     */
    protected void setDeprotonated(Molecule deprotonated)
    {
        this.deprotonated = deprotonated;
    }

    /**
     *  Sets a molecule flag for this <tt>Molecule</tt> object.
     *
     * @param  flag  The new flag value
     */
    protected void setFlags(int flag)
    {
        flags |= flag;
    }

    /**
     *  Sets a molecule flag for this <tt>Molecule</tt> object.
     *
     * @param  flag  The new flag value
     */
    protected void unsetFlags(int flag)
    {
        flags &= ~flag;
    }

    /**
         * @param atom
         * @param atom2
         * @param order
         * @param stereo
         * @return
         */
    private boolean addBond(Bond bond, Atom bgn, Atom end, int order,
        int stereo, int insertpos)
    {
        boolean allFine = false;

        if ((bgn == null) || (end == null))
        {
            //        throw new Exception("Unable to add bond - invalid atom index");
            logger.error("Unable to add bond - invalid atom index");
            allFine = false;
        }
        else
        {
            bond.set(this.getBondsSize(), bgn, end, order, stereo);
            bond.setParent(this);
            bonds.add(bond);

            if (insertpos == -1)
            {
                bgn.addBond(bond);
                end.addBond(bond);
            }
            else
            {
                if (insertpos >= bgn.getValence())
                {
                    bgn.addBond(bond);
                }
                else
                {
                    //need to insert the bond for the connectivity order to be preserved
                    //otherwise stereochemistry gets screwed up
                    NbrAtomIterator nait = bgn.nbrAtomIterator();
                    nait.setIndex(insertpos);

                    //                    int counter = 0;
                    //                    while (counter != insertpos)
                    //                    {
                    //                        counter++;
                    //                        nait.nextNbrAtom();
                    //                    }
                    //                    System.out.println("AtomBonds:"+bgn.getValence());
                    bgn.insertBond(nait, bond);

                    //                    System.out.println("insert("+insertpos+") "+bgn.getIdx());
                    //                    System.out.println("AtomBonds:"+bgn.getValence());
                }

                end.addBond(bond);
            }

            allFine = true;
        }

        return allFine;
    }

    /**
     * @param newAtom
     */
    private void checkVirtualBonds(Atom newAtom)
    {
        if (virtualBonds != null)
        {
            //add bonds that have been queued
            BasicVirtualBond virtualBond;
            List<BasicVirtualBond> found = new Vector<BasicVirtualBond>();

            for (int vbIdx = 0; vbIdx < virtualBonds.size(); vbIdx++)
            {
                virtualBond = (BasicVirtualBond) virtualBonds.get(vbIdx);

                if ((virtualBond.getBeginAtom() > this.getAtomsSize()) ||
                        (virtualBond.getEndAtom() > this.getAtomsSize()))
                {
                    continue;
                }

                if ((newAtom.getIndex() == virtualBond.getBeginAtom()) ||
                        (newAtom.getIndex() == virtualBond.getEndAtom()))
                {
                    addBond(virtualBond.getBeginAtom(),
                        virtualBond.getEndAtom(), virtualBond.getOrder());
                    found.add(virtualBond);
                }
            }

            if (found.size() != 0)
            {
                for (int foundI = 0; foundI < found.size(); foundI++)
                {
                    virtualBond = (BasicVirtualBond) found.get(foundI);
                    virtualBonds.remove(virtualBond);
                }
            }
        }
    }

    /**
     * @param atoms The atoms to set.
     */
    private void setAtoms(List<Atom> atoms)
    {
        this.atoms = atoms;
    }

    /**
     * @param bonds The bonds to set.
     */
    private void setBonds(List<Bond> bonds)
    {
        this.bonds = bonds;
    }

    /**
     *  Description of the Method
     *
     * @param  atom1  Description of the Parameter
     * @param  atom2  Description of the Parameter
     * @return    Description of the Return Value
     */
    private boolean sortAtomZ(final AtomDouble atom1, final AtomDouble atom2)
    {
        return (atom1.getDoubleValue() < atom2.getDoubleValue());
    }

    /**
     *  Description of the Method Description of the Method Description of the
     *  Method Description of the Method Description of the Method
     *
     * @param  list1  Description of the Parameter
     * @param  list2  Description of the Parameter
     * @return    Description of the Return Value
     * @return    Description of the Return Value
     */
    private boolean sortVVInt(final List list1, final List list2)
    {
        return (list1.size() > list2.size());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
