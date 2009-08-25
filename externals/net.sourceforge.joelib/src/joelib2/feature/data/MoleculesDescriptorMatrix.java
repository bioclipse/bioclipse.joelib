///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculesDescriptorMatrix.java,v $
//  Purpose:  Holds all native value descriptors as double matrix for all known molecules.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:30 $
//            $Author: wegner $
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
package joelib2.feature.data;

import joelib2.feature.NativeValue;

import joelib2.feature.result.BooleanResult;

import joelib2.io.BasicReader;
import joelib2.io.IOType;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.PairData;

import joelib2.process.MoleculeProcessException;

import joelib2.process.types.DescriptorStatistic;

import joelib2.util.BasicMatrixHelper;
import joelib2.util.BasicMoleculeCacheHolder;

import joelib2.util.iterator.PairDataIterator;

import wsi.ra.tool.ArrayBinning;
import wsi.ra.tool.ArrayStatistic;
import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Purpose: Holds all native value descriptors as double matrix for all known
 *  molecules.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class MoleculesDescriptorMatrix implements java.io.Serializable,
    MoleculeCache
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            MoleculesDescriptorMatrix.class.getName());
    private final static String FILE_EXT = ".matrix";
    private final static String NORM_FILE_EXT = ".normalized";

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable binning;
    private Hashtable booleanValues = new Hashtable();
    private List desc2ignore;
    private Hashtable descNamesIndex;
    private String[] descriptorNames;
    private String moleculeIdentifier;
    private String[] moleculeIDs;
    private String[] moleculeNames;
    private Hashtable molIDsIndex;
    private Hashtable molNamesIndex;
    private boolean normalizeOnLoad = false;
    private String normalizeStatFile = null;
    private Hashtable notBooleanValues = new Hashtable();

    private DescriptorStatistic statistic;

    /**
     *  Description of the Field
     */
    private double[][] values = new double[0][0];

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the MoleculesDescriptorMatrix object
     */
    public MoleculesDescriptorMatrix()
    {
        statistic = new DescriptorStatistic();

        // load descriptors which should be ignored
        String value;

        if ((value = BasicPropertyHolder.instance().getProperty(this,
                            "descriptors2ignore")) == null)
        {
            logger.error("No file for descriptors to ignore defined.");
        }
        else
        {
            List tmpVec = BasicResourceLoader.readLines(value);

            if (tmpVec == null)
            {
                logger.error("File with descriptor names to ignore not found.");
            }

            desc2ignore = tmpVec;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "normalizeOnLoad");

        //        System.out.println("value:::"+value);
        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            normalizeOnLoad = true;
        }
        else
        {
            normalizeOnLoad = false;
        }

        if (normalizeOnLoad)
        {
            if ((value = BasicPropertyHolder.instance().getProperty(this,
                                "normalizeStatFile")) == null)
            {
                normalizeStatFile = null;
            }
            else
            {
                if (value.trim().length() == 0)
                {
                    normalizeStatFile = null;
                }
                else
                {
                    normalizeStatFile = value;
                }
            }
        }
    }

    //    private Hashtable descStatistic;
    // temporary variables for added descriptors

    /**
     *  Constructor for the IntResult object
     *
     * @param  _inType        Description of the Parameter
     * @param  _inFile        Description of the Parameter
     * @exception  Exception  Description of the Exception
     */
    public MoleculesDescriptorMatrix(IOType inType, String inFile)
        throws Exception
    {
        this();
        loadMatrix(inType, inFile);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Calculate descriptor normalization using the given descriptor statistic.
     *
     * @param  _statistic  Descriptor statistic to use for normalization
     * @return             <tt>true</tt> if all descriptors were normalized successfully
     */
    public boolean calcVarianceNorm(DescriptorStatistic _statistic)
    {
        //        calculateStatistic();
        //        logger.info("calculate variance normalization.");
        ArrayStatistic as;
        int sizeDesc = descriptorNames.length;
        int size;

        for (int pos = 0; pos < sizeDesc; pos++)
        {
            as = _statistic.getDescriptorStatistic(descriptorNames[pos]);

            //            System.out.println("NORM: "+descriptorNames[pos]);
            size = values[0].length;

            if (as != null)
            {
                for (int i = 0; i < size; i++)
                {
                    values[pos][i] = as.varianceNormalization(values[pos][i]);
                }
            }
            else
            {
                as = statistic.getDescriptorStatistic(descriptorNames[pos]);

                if (as != null)
                {
                    logger.warn(
                        "Using internal data set statistic for variance normalization for '" +
                        descriptorNames[pos] + "'.");

                    for (int i = 0; i < size; i++)
                    {
                        values[pos][i] = as.varianceNormalization(
                                values[pos][i]);
                    }
                }
                else
                {
                    logger.warn(" Skipping variance normalization for '" +
                        descriptorNames[pos] + "'.");
                }
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  target  Description of the Parameter
     * @return         Description of the Return Value
     */
    public MoleculeCache clone(MoleculeCache targetAsCache)
    {
        if (!(targetAsCache instanceof MoleculesDescriptorMatrix))
        {
            logger.error("target must be of type MoleculesDescriptorMatrix");

            return null;
        }

        MoleculesDescriptorMatrix target = (MoleculesDescriptorMatrix)
            targetAsCache;

        // double[][]
        if (this.values != null)
        {
            target.values =
                new double[this.values.length][this.values[0].length];

            for (int i = 0; i < this.values.length; i++)
            {
                for (int j = 0; j < this.values[0].length; j++)
                {
                    target.values[i][j] = this.values[i][j];
                }
            }
        }

        // String[]
        if (this.descriptorNames != null)
        {
            target.descriptorNames = new String[this.descriptorNames.length];
            System.arraycopy(this.descriptorNames, 0, target.descriptorNames, 0,
                this.descriptorNames.length);
        }

        if (this.moleculeNames != null)
        {
            target.moleculeNames = new String[this.moleculeNames.length];
            System.arraycopy(this.moleculeNames, 0, target.moleculeNames, 0,
                this.moleculeNames.length);
        }

        if (this.moleculeIDs != null)
        {
            target.moleculeIDs = new String[this.moleculeIDs.length];
            System.arraycopy(this.moleculeIDs, 0, target.moleculeIDs, 0,
                this.moleculeIDs.length);
        }

        //String
        if (this.moleculeIdentifier != null)
        {
            target.moleculeIdentifier = this.moleculeIdentifier;
        }

        //Hashtable
        if (this.descNamesIndex != null)
        {
            target.descNamesIndex = (Hashtable) this.descNamesIndex.clone();
        }

        if (this.molNamesIndex != null)
        {
            target.molNamesIndex = (Hashtable) this.molNamesIndex.clone();
        }

        if (this.molIDsIndex != null)
        {
            target.molIDsIndex = (Hashtable) this.molIDsIndex.clone();
        }

        //DescStatistic
        if (this.statistic != null)
        {
            target.statistic = (DescriptorStatistic) this.statistic.clone();
        }

        //Vector
        if (this.desc2ignore != null)
        {
            target.desc2ignore = (List) ((Vector) desc2ignore).clone();
        }

        //        target.descStatistic = this.descStatistic;
        //      this.binning = ((MoleculesDescriptorMatrix)target).binning;
        //      for(int i = 0; i < this.numAttributes(); i++)
        //      {
        //              this.deleteAttributeAt(i);
        //      }
        //
        //      for(int i = 0; i < ((MoleculesDescriptorMatrix)target).numAttributes(); i++)
        //      {
        //              this.insertAttributeAt(((MoleculesDescriptorMatrix)target).attribute(i),i);
        //      }
        //
        //      for(int i = 0; i < ((MoleculesDescriptorMatrix)target).molecules.getSize(); i++)
        //      {
        //              setMoleculeDescriptors(((MoleculesDescriptorMatrix)target).molecules.getMol(i), i);
        //      }
        //      this.statistic = DescStatistic.getDescStatistic(this.molecules);
        //      this.ClassAttributeName = ((MoleculesDescriptorMatrix)target).classAttribute().name();
        //      this.setClass(((MoleculesDescriptorMatrix)target).classAttribute());
        //      if(((MoleculesDescriptorMatrix)target).desc2ignore != null)
        //      {
        //              this.desc2ignore = new Vector();
        //              for(int i = 0; i < ((MoleculesDescriptorMatrix)target).desc2ignore.size(); i++)
        //              {
        //                      this.desc2ignore.add(((MoleculesDescriptorMatrix)target).desc2ignore.elementAt(i));
        //              }
        //      }
        return target;

        //      return null;
    }

    /**
     *  Description of the Method
     *
     * @param  fileName  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean existsMatrixFileFor(String fileName)
    {
        //FileInputStream fis = null;
        // try to open file
        try
        {
            String fn;

            if (normalizeOnLoad)
            {
                fn = fileName + NORM_FILE_EXT + FILE_EXT;
            }
            else
            {
                fn = fileName + FILE_EXT;
            }

            //fis = new FileInputStream(fn);
            new FileInputStream(fn);
        }
        catch (Exception ex)
        {
            return false;
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  fileName  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean fromFileFor(String fileName)
    {
        String fn;

        if (normalizeOnLoad)
        {
            fn = fileName + NORM_FILE_EXT;
        }
        else
        {
            fn = fileName;
        }

        fn = fn + FILE_EXT;

        //        if (MDMatrixCache.instance().contains(fn))
        //        {
        //            logger.info("Get " + fn + " from cache.");
        //            MDMatrixCache.instance().get(fn).clone(this);
        //            return true;
        //        }
        //        else
        //        {
        //            logger.info("Add " + fn + " to cache.");
        //            MDMatrixCache.instance().put(fn, this);
        //        }
        logger.info("Load descriptor matrix from " + fn);

        //            calcVarianceNorm();
        return fromFile(fn);
    }

    public Hashtable getBinning(int _bins)
    {
        return getBinning(_bins, false);
    }

    /**
     *  Description of the Method
     *
     * @param  _bins             Description of the Parameter
     * @param  forceCalculation  Description of the Parameter
     * @return                   Description of the Return Value
     */
    public Hashtable getBinning(int _bins, boolean forceCalculation)
    {
        if ((binning == null) || forceCalculation)
        {
            binning = new Hashtable(values.length);
        }

        ArrayBinning ab;
        ArrayStatistic as;
        int sizeDesc = descriptorNames.length;
        int size;

        for (int pos = 0; pos < sizeDesc; pos++)
        {
            as = statistic.getDescriptorStatistic(descriptorNames[pos]);

            if (as == null)
            {
                logger.error("No statistic available for '" +
                    descriptorNames[pos] + "'.");

                return null;
            }

            ab = new ArrayBinning(_bins, as);
            size = values[0].length;

            for (int i = 0; i < size; i++)
            {
                ab.add(values[pos][i]);
            }

            binning.put(descriptorNames[pos], ab);
        }

        //            System.out.println("binning: "+binning);
        return binning;
    }

    /**
     *  Gets the descContainsNaN attribute of the MoleculesDescriptorMatrix object
     *
     * @return    The descContainsNaN value
     */
    public String[] getDescContainsNaN()
    {
        int molSize = values[0].length;
        Hashtable vecNaN = new Hashtable(20);

        for (int desc_i = 0; desc_i < descriptorNames.length; desc_i++)
        {
            for (int mol_i = 0; mol_i < molSize; mol_i++)
            {
                if (Double.isNaN(values[desc_i][mol_i]))
                {
                    vecNaN.put(descriptorNames[desc_i], "");
                }
            }
        }

        int s = vecNaN.size();
        String[] descs = new String[s];
        int i = 0;

        for (Enumeration e = vecNaN.keys(); e.hasMoreElements();)
        {
            descs[i++] = (String) e.nextElement();

            //System.out.println(descs[i-1]);
        }

        return descs;
    }

    /**
     * Gets all descriptors from molecule by identifier.
     *
     * @param  _moleculeIdentifier  Molecule identifier
     * @return                      The descriptor values for this molecule
     */
    public double[] getDescFromMolByIdentifier(String _moleculeIdentifier)
    {
        return getDescFromMolByIdentifier(_moleculeIdentifier, false);
    }

    /**
     * Gets all descriptors from molecule by molecule index.
     *
     * @param  _position                           Descriptor index
     * @return                      The descriptor values for this molecule
     */
    public double[] getDescFromMolByIndex(int position)
    {
        return getDescFromMolByIndex(position, false);
    }

    /**
     * Gets all descriptors from a molecule.
     *
     * @param  _moleculeName                  Molecule name
     * @param  replaceNaN                        If <tt>true</tt> NaN values will be replaced by zero
     * @return                      The descriptor values for this molecule
     */
    public double[] getDescFromMolByName(String _moleculeName)
    {
        return getDescFromMolByName(_moleculeName, false);
    }

    /**
     * Gets all descriptor names.
     *
     * @return    All descriptor names
     */
    public String[] getDescNames()
    {
        return descriptorNames;
    }

    /**
     * Gets all values for a specific descriptor.
     *
     * @param  _descriptorName          Descriptor name
     * @return                      The descriptor values for all molecules
     */
    public double[] getDescValues(String _descriptorName)
    {
        return getDescValues(_descriptorName, false);
    }

    /**
     * Gets all values for a set of descriptor.
     *
     * @param  _descriptorNames          Descriptor names
     * @return                      The descriptor values for all molecules
     */
    public double[][] getDescValues(String[] _descriptorNames)
    {
        return getDescValues(_descriptorNames, false, null, null);
    }

    /**
     * Gets all values for a set of descriptor.
     *
     * @param  _descriptorNames          Descriptor names
     * @param  ifMolID                          Adds descriptors, if this molecule index does occur
     * @param  ifNotMolID                          Adds descriptors, if this molecule index does-NOT occur
     * @return                      The descriptor values for all molecules
     */
    public double[][] getDescValues(String[] _descriptorNames, int[] ifMolID,
        int[] ifNotMolID)
    {
        return getDescValues(_descriptorNames, false, ifMolID, ifNotMolID);
    }

    /**
     * Gets all stored descriptor values for all molecules.
     *
     * @return                      All stored descriptor values for all molecules
     */
    public double[][] getMatrix()
    {
        return getMatrix(false);
    }

    /**
     * Gets all molecule names.
     *
     * @return    All molecule names
     */
    public String[] getMolNames()
    {
        return moleculeNames;
    }

    /**
     *  Gets used descriptor statistic for this data set.
     *
     * @return             The used descriptor statistic
     */
    public DescriptorStatistic getStatistic()
    {
        return statistic;
    }

    //  public void checkForNaN()
    //  {
    //
    //  }
    public boolean isBooleanValue(String descriptor)
    {
        if (booleanValues.containsKey(descriptor))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  _inType        Description of the Parameter
     * @param  _inFile        Description of the Parameter
     * @return                Description of the Return Value
     * @exception  Exception  Description of the Exception
     */
    public boolean loadMatrix(IOType inType, String inFile) throws Exception
    {
        return loadMatrix(inType, inFile, true);
    }

    /**
     *  Description of the Method
     *
     * @param  _inType        Description of the Parameter
     * @param  _inFile        Description of the Parameter
     * @param  useCaching     Description of the Parameter
     * @return                Description of the Return Value
     * @exception  Exception  Description of the Exception
     */
    public boolean loadMatrix(IOType _inType, String _inFile,
        boolean useCaching) throws Exception
    {
        boolean add2Cache = false;
        String cacheName = null;

        if (useCaching)
        {
            if (normalizeOnLoad)
            {
                cacheName = _inFile + NORM_FILE_EXT;
            }
            else
            {
                cacheName = _inFile;
            }

            if (BasicMoleculeCacheHolder.instance().contains(cacheName))
            {
                logger.info("Get " + cacheName + " from cache.");
                ((MoleculesDescriptorMatrix) BasicMoleculeCacheHolder.instance()
                                                                     .get(
                        cacheName)).clone(this);

                return true;
            }
            else
            {
                add2Cache = true;
            }
        }

        // load descriptor binning if file exists
        if (existsMatrixFileFor(_inFile))
        {
            fromFileFor(_inFile);

            if (useCaching && add2Cache)
            {
                logger.info("Add " + cacheName + " to cache.");
                BasicMoleculeCacheHolder.instance().put(cacheName, this);
            }

            return true;
        }

        // load descriptors and molecules from file
        loadDescFromMols(_inType, _inFile);

        if (useCaching && add2Cache)
        {
            logger.info("Add " + cacheName + " to cache.");
            BasicMoleculeCacheHolder.instance().put(cacheName, this);
        }

        // write matrix to file
        this.writeMatrixFileFor(_inFile);

        //    checkForNaN();
        return true;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int numberOfDescriptors()
    {
        return descriptorNames.length;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int numberOfMolecules()
    {
        return values[0].length;
    }

    /**
     *  Sets descriptors for given molecule entry.
     *
     * @param  mol            The molecule holding the descriptor informations
     * @param  moleculeEntry  The index number of the descriptors to store in the matrix
     * @return                <tt>true</tt> if all descriptor values could be stored
     */
    public boolean setMoleculeDescriptors(Molecule mol, int moleculeEntry)
    {
        int s = values.length;

        for (int i = 0; i < s; i++)
        {
            values[i][moleculeEntry] = Double.NaN;
        }

        //    System.out.println("load descriptors for mol: "+moleculeEntry);
        PairData pairData;
        PairDataIterator gdit = mol.genericDataIterator();
        String descriptor;

        //    String               ignoreDesc       = PropertyHolder.instance().getProperties().getProperty("jcompchem.joelib2.feature.data.MoleculesDescriptorMatrix.ignoreDescriptor", "Entry_Number");
        // write descriptors to matrix element at: moleculeEntry
        String identifierValue = null;
        Integer integer;
        boolean ignoreDesc = false;

        while (gdit.hasNext())
        {
            pairData = gdit.nextPairData();
            descriptor = pairData.getKey();

            // ignore descriptors in list
            if (desc2ignore != null)
            {
                ignoreDesc = false;

                for (int i = 0; i < desc2ignore.size(); i++)
                {
                    if (descriptor.equals((String) desc2ignore.get(i)))
                    {
                        ignoreDesc = true;

                        break;
                    }
                }

                if (ignoreDesc)
                {
                    continue;
                }
            }

            // get unparsed identifier
            if (descriptor.equals(moleculeIdentifier))
            {
                identifierValue = (String) pairData.getKeyValue();
            }

            // parse data, if possible
            pairData = mol.getData(descriptor, true);

            if (pairData instanceof NativeValue)
            {
                integer = (Integer) descNamesIndex.get(descriptor);

                if (integer == null)
                {
                    // should never happen
                    logger.error("Descriptor '" + descriptor +
                        "' does not exist to build molecule descriptor matrix.");

                    //return false;
                }
                else
                {
                    if (pairData instanceof BooleanResult)
                    {
                        //System.out.println(descriptor+" is a boolean descriptor.");
                        //logger.debug(descriptor+" is a boolean descriptor.");
                        values[integer.intValue()][moleculeEntry] =
                            ((NativeValue) pairData).getDoubleNV();
                        booleanValues.put(descriptor, integer);
                    }
                    else
                    {
                        values[integer.intValue()][moleculeEntry] =
                            ((NativeValue) pairData).getDoubleNV();
                        notBooleanValues.put(descriptor, integer);
                    }

                    //            if(descriptor.equals("E_tor"))
                    //            {
                    //                  System.out.println("set E_tor ("+integer.intValue()+","+moleculeEntry+")="+values[integer.intValue()][moleculeEntry]);
                    //            }
                    //            System.out.println("set '"+descriptor+"' at ["+integer.intValue()+"]["+moleculeEntry+"]="+((NativeValue) data).getDoubleNV());
                }
            }
        }

        integer = new Integer(moleculeEntry);
        moleculeNames[moleculeEntry] = mol.getTitle();

        if (mol.getTitle() != null)
        {
            molNamesIndex.put(mol.getTitle(), integer);
        }

        moleculeIDs[moleculeEntry] = identifierValue;

        if (identifierValue != null)
        {
            molIDsIndex.put(identifierValue, integer);
        }

        return true;
    }

    /**
     *  Sets the molIdentifier attribute of the MoleculesDescriptorMatrix object
     *
     * @param  _moleculeIdentifier  The new molIdentifier value
     */
    public void setMolIdentifier(String _moleculeIdentifier)
    {
        moleculeIdentifier = _moleculeIdentifier;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        if (values == null)
        {
            return null;
        }

        StringBuffer sb = new StringBuffer((values[0].length + 2) *
                values.length * 15);

        // write molecule names
        int size = moleculeNames.length;

        for (int i = 0; i < size; i++)
        {
            sb.append(' ');
            sb.append('\'');
            sb.append(((moleculeNames[i] == null) ? "" : moleculeNames[i]));
            sb.append('\'');
        }

        sb.append('\n');

        // write descriptor names
        size = descriptorNames.length;

        for (int i = 0; i < size; i++)
        {
            sb.append(' ');
            sb.append(descriptorNames[i]);
        }

        sb.append('\n');

        // write descriptor boolean flags
        size = descriptorNames.length;

        for (int i = 0; i < size; i++)
        {
            sb.append(' ');

            if (booleanValues.containsKey(descriptorNames[i]))
            {
                sb.append("true");
            }
            else
            {
                sb.append("false");
            }
        }

        sb.append('\n');

        // write matrix
        //      System.out.println("values:"+values);
        //      System.out.println("v.l:"+values.length);
        //      System.out.println("v0.l:"+values[0].length);
        return BasicMatrixHelper.toTranspRectString(sb, values, " ").toString();
    }

    /**
     *  Description of the Method
     *
     * @param  _inFile  Description of the Parameter
     */
    public void writeMatrixFileFor(String _inFile)
    {
        String fn;

        if (normalizeOnLoad)
        {
            fn = _inFile + NORM_FILE_EXT + FILE_EXT;
        }
        else
        {
            fn = _inFile + FILE_EXT;
        }

        //        System.out.println("Normalize: "+normalizeOnLoad);
        PrintStream ps = null;

        try
        {
            ps = new PrintStream(new FileOutputStream(fn));
            ps.println(this.toString());
            logger.info("Matrix for " + _inFile);
            logger.info("  written to " + fn);
        }
        catch (Exception ex)
        {
            logger.warn(ex.toString());
            logger.warn("Matrix not written for " + _inFile);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  fileName  Description of the Parameter
     * @return           Description of the Return Value
     */
    private boolean fromFile(String fileName)
    {
        LineNumberReader lnr = null;
        String line;
        boolean ok = true;

        // try to open file
        try
        {
            lnr = new LineNumberReader(new InputStreamReader(
                        new FileInputStream(fileName)));

            StringTokenizer st;
            String token;
            int tokens = 0;
            int i;
            int mols = 0;
            int descs = 0;

            // read molecule names
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            st = new StringTokenizer(line, " \r\n\t");
            mols = tokens = st.countTokens();
            moleculeNames = new String[tokens];
            moleculeIDs = new String[tokens];
            i = 0;

            while (st.hasMoreTokens())
            {
                token = st.nextToken();

                if (token.equals("''"))
                {
                    moleculeNames[i] = "";
                }
                else
                {
                    moleculeNames[i] = token.substring(1, token.length() - 1);
                }

                //                System.out.println("#" + moleculeNames[i] + "#");
                i++;
            }

            // read descriptor names
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            st = new StringTokenizer(line, " \r\n\t");
            descs = tokens = st.countTokens();
            descriptorNames = new String[tokens];
            descNamesIndex = new Hashtable(tokens);
            molNamesIndex = new Hashtable(tokens);
            molIDsIndex = new Hashtable(tokens);
            i = 0;

            while (st.hasMoreTokens())
            {
                token = st.nextToken();
                descriptorNames[i] = token;
                descNamesIndex.put(token, new Integer(i));
                i++;
            }

            // read descriptor boolean informations
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            //System.out.println("Load from file "+fileName);
            i = 0;
            st = new StringTokenizer(line, " \r\n\t");

            while (st.hasMoreTokens())
            {
                token = st.nextToken();

                if (token.equalsIgnoreCase("true"))
                {
                    //System.out.println(descriptorNames[i]+" is a boolean descriptor");
                    booleanValues.put(descriptorNames[i], new Integer(i));
                }
                else if (token.equalsIgnoreCase("false"))
                {
                    notBooleanValues.put(descriptorNames[i], new Integer(i));
                }
                else
                {
                    logger.error(
                        "Third line in matrix file should contain 'true' or 'false' and is the 'isBooleanDescriptor'-flag.");
                    logger.error(
                        "Remove the actual 'yourFileName.matrix'-file and let JOELib generate it again with the 'isBooleanDescriptor'-line.");
                }

                i++;
            }

            // read matrix data
            values = new double[descs][mols];

            int m = 0;
            int d = 0;

            while ((line = lnr.readLine()) != null)
            {
                if (line.length() == 0 /*|| line.charAt(0)=='#'       */)
                {
                    continue;
                }

                st = new StringTokenizer(line, " \r\n\t");
                tokens = st.countTokens();

                //                System.out.println("line (" + m + "): " + line);
                d = 0;

                if (tokens == descs)
                {
                    while (st.hasMoreTokens())
                    {
                        token = st.nextToken();

                        try
                        {
                            values[d][m] = Double.parseDouble(token);
                        }
                        catch (NumberFormatException ex)
                        {
                            ok = false;
                            logger.error(ex.toString());
                        }

                        d++;
                    }
                }
                else
                {
                    logger.error("Wrong format in line " + lnr.getLineNumber());
                    ok = false;
                }

                m++;
            }
        }
        catch (IOException ex)
        {
            logger.error(ex.toString());
            ok = false;
        }

        // calculate descriptor statistic
        statistic = new DescriptorStatistic();

        ArrayStatistic as;
        int sizeDesc = descriptorNames.length;
        int size;

        for (int pos = 0; pos < sizeDesc; pos++)
        {
            as = new ArrayStatistic();
            size = values[0].length;

            for (int i = 0; i < size; i++)
            {
                if (!Double.isNaN(values[pos][i]))
                {
                    as.add(values[pos][i]);
                }
            }

            statistic.putArrayStatistic(descriptorNames[pos], as);
        }

        // store descriptor statistic, if it not already exists
        // this will store yourFile.matrix.statistic
        // BUT it should be yourFile.statistic
        //        if (!normalizeOnLoad && !statistic.existsStatisticFileFor(fileName))
        //        {
        //            statistic.writeStatisticFileFor(fileName);
        //        }
        //        System.out.println(""+descriptor+" "+arrayStat.toString());
        return ok;
    }

    /**
     * Gets all descriptors from molecule by identifier.
     *
     * If <tt>true</tt> NaN values will be replaced by zero.
     *
     * This is only recommended for some special descriptors, e.g. RDF
     * and autocorrelation. In general this should never be used.
     *
     * @param  _moleculeIdentifier  Molecule identifier
     * @param  replaceNaN                        If <tt>true</tt> NaN values will be replaced by zero
     * @return                      The descriptor values for this molecule
     */
    private double[] getDescFromMolByIdentifier(String _moleculeIdentifier,
        boolean replaceNaN)
    {
        int size = values.length;
        double[] array = new double[size];
        Integer tmpInt = (Integer) molIDsIndex.get(_moleculeIdentifier);

        if (tmpInt == null)
        {
            logger.error("Molecule identifier '" + _moleculeIdentifier +
                "' not found in descriptor matrix.");

            return null;
        }

        int position = tmpInt.intValue();

        if (replaceNaN)
        {
            for (int i = 0; i < size; i++)
            {
                if (Double.isNaN(values[i][position]))
                {
                    logger.warn("NaN entry in (" + descriptorNames[i] + "," +
                        moleculeNames[position] + ")" + "(" + i + "," +
                        position + ") set to 0.0");
                    array[i] = 0.0;
                }
                else
                {
                    array[i] = values[i][position];
                }
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                array[i] = values[i][position];
            }
        }

        return array;
    }

    /**
     * Gets all descriptors from molecule by molecule index.
     *
     * If <tt>true</tt> NaN values will be replaced by zero.
     *
     * This is only recommended for some special descriptors, e.g. RDF
     * and autocorrelation. In general this should never be used.
     *
     * @param  _position                          Descriptor index
     * @param  replaceNaN                        If <tt>true</tt> NaN values will be replaced by zero
     * @return                      The descriptor values for this molecule
     */
    private double[] getDescFromMolByIndex(int position, boolean replaceNaN)
    {
        int size = values.length;

        //    System.out.println("mol "+position+" with "+size+" descriptors");
        double[] array = new double[size];

        if (replaceNaN)
        {
            for (int i = 0; i < size; i++)
            {
                if (Double.isNaN(values[i][position]))
                {
                    logger.warn("NaN entry in (" + descriptorNames[i] + "," +
                        moleculeNames[position] + ")" + "(" + i + "," +
                        position + ") set to 0.0");
                    array[i] = 0.0;
                }
                else
                {
                    array[i] = values[i][position];
                }
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                array[i] = values[i][position];
            }
        }

        return array;
    }

    /**
     * Gets all descriptors from a molecule.
     *
     * If <tt>true</tt> NaN values will be replaced by zero.
     *
     * This is only recommended for some special descriptors, e.g. RDF
     * and autocorrelation. In general this should never be used.
     *
     * @param  _moleculeName                  Molecule name
     * @param  replaceNaN                        If <tt>true</tt> NaN values will be replaced by zero
     * @return                      The descriptor values for this molecule
     */
    private double[] getDescFromMolByName(String _moleculeName,
        boolean replaceNaN)
    {
        int size = values.length;
        double[] array = new double[size];
        Integer tmpInt = (Integer) molNamesIndex.get(_moleculeName);

        if (tmpInt == null)
        {
            logger.error("Molecule name '" + _moleculeName +
                "' not found in descriptor matrix.");

            return null;
        }

        int position = tmpInt.intValue();

        if (replaceNaN)
        {
            for (int i = 0; i < size; i++)
            {
                if (Double.isNaN(values[i][position]))
                {
                    logger.warn("NaN entry in (" + descriptorNames[i] + "," +
                        moleculeNames[position] + ")" + "(" + i + "," +
                        position + ") set to 0.0");
                    array[i] = 0.0;
                }
                else
                {
                    array[i] = values[i][position];
                }
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                array[i] = values[i][position];
            }
        }

        return array;
    }

    /**
     * Gets all values for a specific descriptor.
     *
     * If <tt>true</tt> NaN values will be replaced by zero.
     *
     * This is only recommended for some special descriptors, e.g. RDF
     * and autocorrelation. In general this should never be used.
     *
     * @param  _descriptorName          Descriptor name
     * @param  replaceNaN                        If <tt>true</tt> NaN values will be replaced by zero
     * @return                      The descriptor values for all molecules
     */
    private double[] getDescValues(String _descriptorName, boolean replaceNaN)
    {
        int size = values[0].length;
        double[] array = new double[size];
        Integer tmpInt = (Integer) descNamesIndex.get(_descriptorName);

        if (tmpInt == null)
        {
            logger.error("Descriptor '" + _descriptorName +
                "' not found in descriptor matrix.");

            return null;
        }

        int position = tmpInt.intValue();

        if (replaceNaN)
        {
            for (int i = 0; i < size; i++)
            {
                if (Double.isNaN(values[position][i]))
                {
                    logger.warn("NaN entry in (" + descriptorNames[position] +
                        "," + moleculeNames[i] + ")" + "(" + position + "," +
                        i + ") set to 0.0");
                    array[i] = 0.0;
                }
                else
                {
                    array[i] = values[position][i];
                }
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                array[i] = values[position][i];
            }
        }

        return array;
    }

    /**
     * Gets all values for a set of descriptor.
     *
     * If <tt>true</tt> NaN values will be replaced by zero.
     *
     * This is only recommended for some special descriptors, e.g. RDF
     * and autocorrelation. In general this should never be used.
     *
     * @param  _descriptorNames          Descriptor names
     * @param  replaceNaN                        If <tt>true</tt> NaN values will be replaced by zero
     * @param  ifMolID                          Adds descriptors, if this molecule index does occur
     * @param  ifNotMolID                          Adds descriptors, if this molecule index does-NOT occur
     * @return                      The descriptor values for all molecules
     */
    private double[][] getDescValues(String[] _descriptorNames,
        boolean replaceNaN, int[] ifMolID, int[] ifNotMolID)
    {
        // sort IF and IFNOT array, if available
        if (ifMolID != null)
        {
            Arrays.sort(ifMolID);
        }

        if (ifNotMolID != null)
        {
            Arrays.sort(ifNotMolID);
        }

        if ((ifNotMolID != null) && (ifMolID != null))
        {
            logger.error(
                "IF and IFNOT array defined. Only one identifier array can be used !");

            return null;
        }

        if ((values.length == 0) || (values[0].length == 0))
        {
            logger.error("Empty matrix.");

            return null;
        }

        int molSize = values[0].length;
        int descSize = _descriptorNames.length;
        double[][] array = null;

        if (ifNotMolID != null)
        {
            array = new double[descSize][molSize - ifNotMolID.length];
        }

        if (ifMolID != null)
        {
            molSize = ifMolID.length;
            array = new double[descSize][molSize];
        }

        if ((ifMolID == null) && (ifNotMolID == null))
        {
            array = new double[descSize][molSize];
        }

        Integer tmpInt;

        int ccind;

        //              logger.info("Descriptors: ");
        for (int desc_i = 0; desc_i < descSize; desc_i++)
        {
            tmpInt = (Integer) descNamesIndex.get(_descriptorNames[desc_i]);

            //                  logger.info(_descriptorNames[desc_i]);
            if (tmpInt == null)
            {
                logger.error("Descriptor '" + _descriptorNames[desc_i] +
                    "' not found in descriptor matrix.");

                return null;
            }

            int position = tmpInt.intValue();
            ccind = 0;

            if (replaceNaN)
            {
                for (int mol_i = 0; mol_i < molSize; mol_i++)
                {
                    if (ifNotMolID != null)
                    {
                        // skip entries with this ID number
                        if ((ccind < ifNotMolID.length) &&
                                (mol_i == ifNotMolID[ccind]))
                        {
                            ccind++;

                            continue;
                        }
                    }

                    if (ifMolID == null)
                    {
                        if (Double.isNaN(values[position][mol_i - ccind]))
                        {
                            logger.warn("NaN entry in (" +
                                descriptorNames[position] + "," +
                                moleculeNames[mol_i - ccind] + ")" + "(" +
                                position + "," + mol_i + ") set to 0.0");

                            //Object obj=null; obj.toString();
                            array[desc_i][mol_i - ccind] = 0.0;
                        }
                        else
                        {
                            array[desc_i][mol_i - ccind] =
                                values[position][mol_i];
                        }
                    }
                    else
                    {
                        if (Double.isNaN(values[position][ifMolID[mol_i]]))
                        {
                            logger.warn("NaN entry in (" +
                                descriptorNames[position] + "," +
                                moleculeNames[ifMolID[mol_i]] + ")" + "(" +
                                position + "," + mol_i + ") set to 0.0");

                            //Object obj=null; obj.toString();
                            array[desc_i][ifMolID[mol_i]] = 0.0;
                        }
                        else
                        {
                            array[desc_i][mol_i] =
                                values[position][ifMolID[mol_i]];
                        }
                    }
                }
            }
            else
            {
                for (int mol_i = 0; mol_i < molSize; mol_i++)
                {
                    if (ifNotMolID != null)
                    {
                        // skip entries with this ID number
                        if ((ccind < ifNotMolID.length) &&
                                (mol_i == ifNotMolID[ccind]))
                        {
                            ccind++;

                            continue;
                        }
                    }

                    if (ifMolID == null)
                    {
                        array[desc_i][mol_i - ccind] = values[position][mol_i];
                    }
                    else
                    {
                        array[desc_i][mol_i] = values[position][ifMolID[mol_i]];
                    }
                }
            }
        }

        //              StringBuffer sb=new StringBuffer(10000);
        //              MatrixHelper.toSimpleString(sb, array, " ");
        //              logger.info(sb);
        return array;
    }

    /**
     * Gets all stored descriptor values for all molecules.
     *
     * If <tt>true</tt> NaN values will be replaced by zero.
     *
     * This is only recommended for some special descriptors, e.g. RDF
     * and autocorrelation. In general this should never be used.
     *
     * @param  replaceNaN                        If <tt>true</tt> NaN values will be replaced by zero
     * @return                      All stored descriptor values for all molecules
     */
    private double[][] getMatrix(boolean replaceNaN)
    {
        if (replaceNaN == false)
        {
            return values;
        }

        int is = values.length;
        int js = values[0].length;
        double[][] tmp = new double[is][js];

        if (replaceNaN)
        {
            for (int i = 0; i < is; i++)
            {
                for (int j = 0; j < js; j++)
                {
                    if (Double.isNaN(values[i][j]))
                    {
                        logger.warn("NaN entry in (" + descriptorNames[i] +
                            "," + moleculeNames[j] + ")" + "(" + i + "," + j +
                            ") set to 0.0");

                        //Object obj=null; obj.toString();
                        tmp[i][j] = 0.0;
                    }
                    else
                    {
                        tmp[i][j] = values[i][j];
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < is; i++)
            {
                for (int j = 0; j < js; j++)
                {
                    tmp[i][j] = values[i][j];
                }
            }
        }

        return tmp;
    }

    /**
     *  Description of the Method
     *
     * @param  _inType        Description of the Parameter
     * @param  _inFile        Description of the Parameter
     * @return                Description of the Return Value
     * @exception  Exception  Description of the Exception
     */
    private boolean loadDescFromMols(IOType _inType, String _inFile)
        throws Exception
    {
        logger.info("Initializing " + _inFile + ".");

        MolDescCounter counts = new MolDescCounter(_inType, _inFile);

        // build arrays
        descriptorNames = new String[counts.numberDescriptors()];
        moleculeNames = new String[counts.numberMolecules()];
        moleculeIDs = new String[counts.numberMolecules()];

        // build the descriptor name index
        descNamesIndex = new Hashtable(counts.numberDescriptors());
        molNamesIndex = new Hashtable(counts.numberDescriptors());
        molIDsIndex = new Hashtable(counts.numberDescriptors());

        int index = 0;
        String name;

        for (Enumeration e = counts.availableDescriptors(); e.hasMoreElements();)
        {
            name = (String) e.nextElement();
            descNamesIndex.put(name, new Integer(index));
            descriptorNames[index] = name;
            index++;
        }

        // initialize matrix
        if (counts.numberDescriptors() == 0)
        {
            return false;
        }

        if (counts.numberMolecules() == 0)
        {
            return false;
        }

        values =
            new double[counts.numberDescriptors()][counts.numberMolecules()];

        //      System.out.println("iv.l:"+values.length);
        //      System.out.println("iv0.l:"+values[0].length);
        // load molecule descriptors into memory
        //    if (logger.isDebugEnabled())
        //    {
        //      logger.debug("Load " + _inFile + ".");
        //    }
        //        logger.info("Load and calculate statistic from " + _inFile + ".");
        //        statistic=DescStatistic.getDescStatistic(_inType, _inFile);
        BasicReader reader = null;
        int molCounter = 0;

        try
        {
            reader = new BasicReader(new FileInputStream(_inFile), _inType);
        }
        catch (Exception ex)
        {
            throw ex;
        }

        Molecule mol = new BasicConformerMolecule(_inType, _inType);

        for (;;)
        {
            try
            {
                if (!reader.readNext(mol))
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                logger.error(ex.toString());

                return false;
            }

            // process molecule
            if (!setMoleculeDescriptors(mol, molCounter))
            {
                return false;
            }

            try
            {
                statistic.process(mol, null);
            }
            catch (MoleculeProcessException ex)
            {
                logger.error(ex.toString());

                return false;
            }

            molCounter++;
        }

        //reader.close();
        reader = null;

        if (logger.isDebugEnabled())
        {
            System.out.print("Descriptor names:");

            for (int i = 0; i < descriptorNames.length; i++)
            {
                System.out.print(" " + descriptorNames[i]);
            }

            System.out.println("");

            System.out.println("Descriptor matrix (" + getMatrix().length +
                ", " + getMatrix()[0].length + "):\n" + toString());
        }

        // calculate descriptor variance normalization
        if (normalizeOnLoad)
        {
            String sf2use;
            DescriptorStatistic s4n = null;

            if (normalizeStatFile == null)
            {
                sf2use = _inFile;
                s4n = statistic;
            }
            else
            {
                sf2use = normalizeStatFile;
                s4n = DescriptorStatistic.getDescStatistic(_inType, sf2use);
            }

            logger.info(
                "Calculate descriptor variance normalization based on " +
                sf2use + ".");
            calcVarianceNorm(s4n);
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
