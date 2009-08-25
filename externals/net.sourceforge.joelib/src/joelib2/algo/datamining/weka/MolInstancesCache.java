///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolInstancesCache.java,v $
//  Purpose:  Molecule caching class for Weka data mining instances.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Nikolas H. Fechner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/02/17 16:48:28 $
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
package joelib2.algo.datamining.weka;

import joelib2.feature.NativeValue;

import joelib2.feature.data.MoleculeCache;

import joelib2.io.IOType;

import joelib2.molecule.BasicMoleculeVector;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.molecule.types.PairData;

import joelib2.process.types.DescriptorBinning;
import joelib2.process.types.DescriptorStatistic;

import joelib2.util.BasicMoleculeCacheHolder;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

import wsi.ra.tool.ArrayBinning;
import wsi.ra.tool.ArrayStatistic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Molecule caching class for Weka data mining instances.
 *
 * @.author Nikolas H. Fechner
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license    GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:28 $
 */
public class MolInstancesCache extends Instances implements MoleculeCache
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private static Category logger = Category.getInstance(
            MolInstancesCache.class.getName());
    private final static String FILE_EXT = ".molcache";
    private static final String DEFAULT_CLASS_ATTRIBUTE = "CLASS_ATTRIBUTE";

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable binning;
    private String classAttributeName;
    private List desc2ignore;
    private String IdentifierValue;
    private String moleculeIdentifier;
    private MoleculeVector molecules;
    private Hashtable molIDsIndex;
    private Hashtable molNamesIndex;

    private DescriptorStatistic statistic;

    //~ Constructors ///////////////////////////////////////////////////////////

    public MolInstancesCache()
    {
        super("Cache", new FastVector(), 0);
        molecules = new BasicMoleculeVector();
        statistic = new DescriptorStatistic();
        molIDsIndex = new Hashtable();
        molNamesIndex = new Hashtable();

        //System.out.println("Using MolInstancesCache Version 0.1");
        //String value;

        /*if ((value = PropertyHolder.instance().getProperty(this,
                                                        "descriptors2ignore")) == null)
                        {
                                logger.error("No file for descriptors to ignore defined.");
                        }
                        else
                        {
                                Vector tmpVec = ResourceLoader.readLines(value);

                                if (tmpVec == null)
                                {
                                        logger.error("File with descriptor names to ignore not found.");
                                }

                                desc2ignore = tmpVec;
                        }

                        value = PropertyHolder.instance().getProperty(this, "normalizeOnLoad");*/
    }

    public MolInstancesCache(IOType ioType, String _inFile)
    {
        this(ioType, _inFile, true);
    }

    public MolInstancesCache(IOType ioType, String _inFile, boolean useCaching)
    {
        this(ioType, _inFile, true, DEFAULT_CLASS_ATTRIBUTE);
    }

    public MolInstancesCache(IOType ioType, String _inFile, boolean useCaching,
        String className)
    {
        this();

        try
        {
            if (!loadMatrix(ioType, _inFile, useCaching, className))
            {
                logger.error("Error while loading Matrix from File " + _inFile);

                //System.exit(1);
            }
        }
        catch (Exception e)
        {
            logger.error("Error while loading Matrix from File " + _inFile);

            //System.exit(1);
        }
    }

    private MolInstancesCache(MolInstancesCache cache)
    {
        super(cache);
        molecules = new BasicMoleculeVector();
        statistic = new DescriptorStatistic();

        //System.out.println("Using MolInstancesCache Version 0.09");

        /*String value;
        if ((value = PropertyHolder.instance().getProperty(this,"descriptors2ignore")) == null)
        {
                logger.error("No file for descriptors to ignore defined.");
        }
        else
        {
                Vector tmpVec = ResourceLoader.readLines(value);
                if (tmpVec == null)
                {
                        logger.error("File with descriptor names to ignore not found.");
                }
                desc2ignore = tmpVec;
        }
        value = PropertyHolder.instance().getProperty(this, "normalizeOnLoad");*/
    }

    private MolInstancesCache(MoleculeVector mols, Instances instances)
    {
        super(instances);
        molecules = mols;
        statistic = DescriptorStatistic.getDescStatistic(mols);
        molNamesIndex = new Hashtable();
        molIDsIndex = new Hashtable();
        buildIDs();

        for (int i = 0; i < mols.getSize(); i++)
        {
            molNamesIndex.put(mols.getMol(i).getTitle(), new Integer(i));
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#calcVarianceNorm(joelib2.process.types.DescStatistic)
     */
    public boolean calcVarianceNorm(DescriptorStatistic _statistic)
    {
        //        calculateStatistic();
        //        logger.info("calculate variance normalization.");
        String[] descriptorNames = getNames();
        ArrayStatistic as;
        int sizeDesc = descriptorNames.length;
        int size;

        for (int pos = 0; pos < sizeDesc; pos++)
        {
            as = _statistic.getDescriptorStatistic(descriptorNames[pos]);

            //            System.out.println("NORM: "+descriptorNames[pos]);
            //size = values[0].length;
            size = instance(pos).numAttributes();

            if (as != null)
            {
                for (int i = 0; i < size; i++)
                {
                    //values[pos][i] = as.varianceNormalization(values[pos][i]);
                    double d = instance(pos).value(i);
                    d = as.varianceNormalization(d);
                    instance(pos).setClassValue(d);
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
                        //values[pos][i] = as.varianceNormalization(values[pos][i]);
                        double d = instance(pos).value(i);
                        d = as.varianceNormalization(d);
                        instance(pos).setClassValue(d);
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
     * @param target        The MoleculeCache Object to generate
     * @return                         The cloned Object
     */
    public MoleculeCache clone(MoleculeCache target)
    {
        if (!(target instanceof MolInstancesCache))
        {
            logger.error("target must be of type MolInstancesCache");

            return null;
        }

        this.binning = ((MolInstancesCache) target).binning;

        for (int i = 0; i < this.numAttributes(); i++)
        {
            this.deleteAttributeAt(i);
        }

        for (int i = 0; i < ((MolInstancesCache) target).numAttributes(); i++)
        {
            this.insertAttributeAt(((MolInstancesCache) target).attribute(i),
                i);
        }

        for (int i = 0; i < ((MolInstancesCache) target).molecules.getSize();
                i++)
        {
            setMoleculeDescriptors(((MolInstancesCache) target).molecules
                .getMol(i), i);
        }

        this.statistic = DescriptorStatistic.getDescStatistic(this.molecules);
        this.classAttributeName = ((MolInstancesCache) target).classAttribute()
                                   .name();
        this.setClass(((MolInstancesCache) target).classAttribute());

        if (((MolInstancesCache) target).desc2ignore != null)
        {
            this.desc2ignore = new Vector();

            for (int i = 0; i < ((MolInstancesCache) target).desc2ignore.size();
                    i++)
            {
                this.desc2ignore.add(((MolInstancesCache) target).desc2ignore
                    .get(i));
            }
        }

        return target;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#existsMatrixFileFor(java.lang.String)
     */
    public boolean existsMatrixFileFor(String fileName)
    {
        //FileInputStream fis = null;
        // try to open file
        try
        {
            String fn;

            fn = fileName;

            //fis = new FileInputStream(fn);
            new FileInputStream(fn);
        }
        catch (Exception ex)
        {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#fromFileFor(java.lang.String)
     */
    public boolean fromFileFor(String fileName)
    {
        String fn;

        fn = fileName;

        //fn = fn + FILE_EXT;
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

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getBinning(int)
     */
    public Hashtable getBinning(int _bins)
    {
        return getBinning(_bins, false);
    }

    /** Generates a hashtable containg one ArrayBinning Object for each Attribute
     *
     */
    public Hashtable getBinning(int _bins, boolean forceCalculation)
    {
        if ((binning == null) || forceCalculation)
        {
            binning = new Hashtable(instance(0).numAttributes());
        }

        ArrayBinning ab;
        ArrayStatistic as;
        String[] descriptorNames = getNames();
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
            size = instance(0).numAttributes();

            for (int i = 0; i < size; i++)
            {
                ab.add(instance(i).value(i));
            }

            binning.put(descriptorNames[pos], ab);
        }

        //            System.out.println("binning: "+binning);
        return binning;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescContainsNaN()
     */
    public String[] getDescContainsNaN()
    {
        int molSize = instance(0).numAttributes();
        String[] descriptorNames = getNames();
        Hashtable vecNaN = new Hashtable(20);

        for (int desc_i = 0; desc_i < descriptorNames.length; desc_i++)
        {
            for (int mol_i = 0; mol_i < molSize; mol_i++)
            {
                if (Double.isNaN(instance(mol_i).value(desc_i)))
                {
                    vecNaN.put(descriptorNames[desc_i], "");
                }

                if (vecNaN.containsKey(descriptorNames[desc_i]))
                {
                    break;
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

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescFromMolByIdentifier(java.lang.String)
     */
    public double[] getDescFromMolByIdentifier(String _moleculeIdentifier)
    {
        //int size = instance(0).numAttributes();
        Integer tmpInt = (Integer) molIDsIndex.get(_moleculeIdentifier);

        if (tmpInt == null)
        {
            logger.error("Molecule identifier '" + _moleculeIdentifier +
                "' not found in descriptor matrix.");

            return null;
        }

        int position = tmpInt.intValue();
        MolInstance m = (MolInstance) super.instance(position);

        return m.m_AttValues;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescFromMolByIndex(int)
     */
    public double[] getDescFromMolByIndex(int position)
    {
        MolInstance m = (MolInstance) super.instance(position);

        return m.m_AttValues;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescFromMolByName(java.lang.String)
     */
    public double[] getDescFromMolByName(String _moleculeName)
    {
        int position = ((Integer) molNamesIndex.get(_moleculeName)).intValue();
        MolInstance m = (MolInstance) super.instance(position);

        return m.m_AttValues;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescNames()
     */
    public String[] getDescNames()
    {
        FastVector v = super.getAttributes();
        String[] s = new String[v.size()];

        for (int i = 0; i < v.size(); i++)
        {
            s[i] = ((Attribute) v.elementAt(i)).name();
        }

        return s;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescValues(java.lang.String)
     */
    public double[] getDescValues(String _descriptorName)
    {
        double[] d = new double[super.numInstances()];

        //FastVector v = super.m_Instances;
        Attribute att = super.attribute(_descriptorName);
        Enumeration e = super.enumerateInstances();
        int i = 0;

        while (e.hasMoreElements())
        {
            MolInstance m = (MolInstance) e.nextElement();
            d[i] = m.value(att);
            i++;
        }

        return d;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescValues(java.lang.String[])
     */
    public double[][] getDescValues(String[] _descriptorNames)
    {
        double[][] d = new double[_descriptorNames.length][];

        for (int i = 0; i < d.length; i++)
        {
            d[i] = getDescValues(_descriptorNames[i]);
        }

        return d;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getDescValues(java.lang.String[], int[], int[])
     */
    public double[][] getDescValues(String[] _descriptorNames, int[] ifMolID,
        int[] ifNotMolID)
    {
        if (ifMolID == null)
        {
            ifMolID = new int[0];
        }

        if (ifNotMolID == null)
        {
            ifNotMolID = new int[0];
        }

        Arrays.sort(ifMolID);
        Arrays.sort(ifNotMolID);

        double[][] d = new double[_descriptorNames.length][ifMolID.length];

        for (int i = 0; i < d.length; i++)
        {
            int a = (super.attribute(_descriptorNames[i])).index();
            double[] x = super.attributeToDoubleArray(a);

            for (int j = 0; j < d[i].length; j++)
            {
                d[i][j] = x[ifMolID[j]];
            }
        }

        return d;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getMatrix()
     */
    public double[][] getMatrix()
    {
        double[][] d = new double[super.numInstances()][super.numAttributes()];

        //double[][] t =        new double[super.numAttributes()][super.numInstances()];
        String[] s = new String[super.numAttributes()];

        for (int i = 0; i < s.length; i++)
        {
            s[i] = (super.attribute(i)).name();
        }

        double[][] t = getDescValues(s);

        for (int i = 0; i < t.length; i++)
        {
            for (int j = 0; j < t[i].length; j++)
            {
                d[j][i] = t[i][j];
            }
        }

        return d;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getMolNames()
     */
    public String[] getMolNames()
    {
        String[] s = new String[super.numInstances()];

        for (int i = 0; i < s.length; i++)
        {
            s[i] = ((MolInstance) super.instance(i)).getMolecule().getTitle();
        }

        return s;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#getStatistic()
     */
    public DescriptorStatistic getStatistic()
    {
        if (this.statistic == null)
        {
            return DescriptorStatistic.getDescStatistic(molecules);
        }

        return statistic;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#loadMatrix(joelib2.io.IOType, java.lang.String)
     */
    public boolean loadMatrix(IOType _inType, String _inFile) throws Exception
    {
        return loadMatrix(_inType, _inFile, true);
    }

    public boolean loadMatrix(IOType _inType, String _inFile,
        boolean useCaching) throws Exception
    {
        return loadMatrix(_inType, _inFile, useCaching,
                DEFAULT_CLASS_ATTRIBUTE);
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#loadMatrix(joelib2.io.IOType, java.lang.String, boolean)
     */
    public boolean loadMatrix(IOType _inType, String _inFile,
        boolean useCaching, String _classAttribute) throws Exception
    {
        String cacheName = _inFile;
        logger.info("Loading Matrix");

        if (useCaching)
        {
            if (BasicMoleculeCacheHolder.instance().contains(cacheName))
            {
                logger.info("Get " + cacheName + " from Cache");
                BasicMoleculeCacheHolder.instance().get(cacheName).clone(this);

                return true;
            }
        }

        if (existsMatrixFileFor(_inFile))
        {
            fromFileFor(_inFile);

            if (useCaching)
            {
                BasicMoleculeCacheHolder.instance().put(cacheName, this);
            }
        }

        try
        {
            classAttributeName = _classAttribute;

            FileInputStream in = new FileInputStream(_inFile);
            MoleculeVector mols = new BasicMoleculeVector(in);
            DescriptorBinning binning = DescriptorBinning.getDescBinning(mols);
            Enumeration enumeration = binning.getDescriptors();
            String[] atts = new String[binning.numberOfDescriptors()];
            int[] types = new int[binning.numberOfDescriptors()];
            int i = 0;
            setClassIndex(-1);

            while (enumeration.hasMoreElements())
            {
                atts[i] = ((String) enumeration.nextElement());

                if ((classAttributeName != null) &&
                        atts[i].equalsIgnoreCase(classAttributeName))
                {
                    //setClassIndex(i);
                    //System.out.println("Nominal at " + i);
                    types[i] = Attribute.NOMINAL;
                }
                else
                {
                    types[i] = Attribute.NUMERIC;
                }

                i++;
            }

            buildInstances(mols, atts, types);

            //System.out.println("Check2: " + this.numAttributes() + "\t" + this.numInstances());
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            e.printStackTrace();

            return false;
        }

        if (useCaching)
        {
            BasicMoleculeCacheHolder.instance().put(cacheName, this);
        }

        return true;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#numberOfDescriptors()
     */
    public int numberOfDescriptors()
    {
        return super.numAttributes();
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#numberOfMolecules()
     */
    public int numberOfMolecules()
    {
        return super.numInstances();
    }

    public boolean setMoleculeDescriptors(Molecule mol, int moleculeEntry)
    {
        MolInstance instance = getInstanceFor(mol);

        if (!super.checkInstance(instance))
        {
            logger.error("Instance generated for " + mol.getTitle() +
                " is not compatible with Instances!");

            return false;
        }

        int x = super.numInstances();

        //System.out.println(IdentifierValue);
        molIDsIndex.put(IdentifierValue, new Integer(x));
        molNamesIndex.put(mol.getTitle(), new Integer(x));
        super.add(instance);
        molecules.addMol(mol);

        if ((x + 1) != molecules.getSize())
        {
            System.out.println(x + "\t" + molecules.getSize());
        }

        return true;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#setMolIdentifier(java.lang.String)
     */
    public void setMolIdentifier(String _moleculeIdentifier)
    {
        moleculeIdentifier = _moleculeIdentifier;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#writeMatrixFileFor(java.lang.String)
     */
    public void writeMatrixFileFor(String _inFile)
    {
        String fn_mols = _inFile + "_mols.sdf"; // + FILE_EXT;
        String fn_arff = _inFile + ".arff";

        //System.out.println("Writing: " + molecules.getSize());
        try
        {
            FileWriter w_1 = new FileWriter(fn_mols);

            for (int i = 0; i < molecules.getSize(); i++)
            {
                w_1.write(molecules.getMol(i).toString());
            }

            w_1.close();
        }
        catch (Exception e)
        {
            logger.error("Exception while writing molecules!");
            e.printStackTrace();
        }

        try
        {
            FileWriter w_2 = new FileWriter(fn_arff);
            w_2.write(this.toString());
            w_2.close();
        }
        catch (Exception e)
        {
            logger.error("Exception while writing arff file!");
            e.printStackTrace();
        }
    }

    /**
     * Generates the molIDsIndex Hashtable
     *
     */
    private void buildIDs()
    {
        PairData pairData;
        String descriptor;
        String identifierValue = "";

        for (int i = 0; i < super.numInstances(); i++)
        {
            Molecule mol = ((MolInstance) super.instance(i)).getMolecule();

            for (int j = 0; j < super.numAttributes(); j++)
            {
                Attribute attribute = super.attribute(j);
                pairData = mol.getData(attribute.name(), true);
                descriptor = pairData.getKey();

                if (descriptor.equals(moleculeIdentifier))
                {
                    identifierValue = (String) pairData.getKeyValue();
                }

                IdentifierValue = identifierValue;
                molIDsIndex.put(identifierValue, new Integer(j));
            }
        }
    }

    private void buildInstances(MoleculeVector mols, String[] atts, int[] types)
    {
        //System.out.println("Building " + mols.getSize() + " Instances");
        DescriptorBinning binning = DescriptorBinning.getDescBinning(mols);
        FastVector attV = new FastVector(binning.numberOfDescriptors());
        Molecule mol;
        PairData pairData;
        Enumeration enumeration = binning.getDescriptors();

        //System.out.println(types.length + "\t" + types[0] + "\t" + types[11]);
        for (int i = 0; i < atts.length; i++)
        {
            if (types[i] == Attribute.NUMERIC)
            {
                attV.addElement(new Attribute(
                        (String) enumeration.nextElement(), attV.size()));
            }
            else if (types[i] == Attribute.NOMINAL)
            {
                Hashtable hashed = new Hashtable();

                //System.out.println(i + "\t" + atts[i]);
                for (int j = 0; j < mols.getSize(); j++)
                {
                    mol = mols.getMol(j);

                    // get unparsed data
                    pairData = mol.getData(atts[i], false);

                    //System.out.println("Generic Data: " + genericData.toString());
                    if (pairData != null)
                    {
                        if (pairData.getKeyValue() instanceof String)
                        {
                            hashed.put(pairData.getKeyValue(), "");
                        }
                        else
                        {
                            hashed.put(pairData.toString(), "");
                        }
                    }
                }

                // store list of nominal values in the Weka data structure
                FastVector attributeValues = new FastVector(hashed.size());
                String tmp;

                //System.out.println("Hash Size: " + hashed.size());
                for (Enumeration e = hashed.keys(); e.hasMoreElements();)
                {
                    tmp = (String) e.nextElement();
                    attributeValues.addElement(tmp);

                    //System.out.println("NOMINAL " + tmp);
                }

                attV.addElement(new Attribute(atts[i], attributeValues,
                        attV.size()));
            }
        }

        //System.out.println("Updating Instances");
        update(attV);

        //double[] vals;
        //System.out.println("Size: " + mols.getSize());
        for (int i = 0; i < mols.getSize(); i++)
        {
            mol = mols.getMol(i);

            setMoleculeDescriptors(mol, 0);
        }

        //System.out.println("Check3: " + this.numAttributes() + "\t" + this.numInstances());
    }

    private boolean fromFile(String name)
    {
        String fn_mols = name + "_mols.sdf";
        String fn_arff = name + ".arff";
        BufferedReader a_r;
        Instances instances;
        Instances inst;
        MoleculeVector mols;

        try
        {
            FileInputStream arff = new FileInputStream(fn_arff);
            a_r = new BufferedReader(new InputStreamReader(arff));
            instances = new Instances(a_r);
        }
        catch (Exception e)
        {
            logger.error("Unable to read file: " + fn_arff);

            return false;
        }

        try
        {
            FileInputStream ml = new FileInputStream(fn_mols);

            //a_r = new BufferedReader(new InputStreamReader(ml));
            mols = new BasicMoleculeVector(ml);
        }
        catch (Exception e)
        {
            logger.error("Unable to read file: " + fn_mols);

            return false;
        }

        inst = new Instances(instances);
        inst.delete();
        this.delete();

        for (int i = 0; i < this.numAttributes(); i++)
        {
            this.deleteAttributeAt(i);
        }

        for (int i = 0; i < instances.numAttributes(); i++)
        {
            this.insertAttributeAt(instances.attribute(i), i);

            if (instances.attribute(i).isNominal())
            {
                this.setClass(instances.attribute(i));
            }
        }

        //this.setClass(instances.classAttribute());
        for (int i = 0; i < instances.numInstances(); i++)
        {
            MolInstance temp = new MolInstance(mols.getMol(i),
                    instances.instance(i).weight(),
                    instances.instance(i).m_AttValues);

            //this.add(temp);
            inst.add(temp);
            setMoleculeDescriptors(mols.getMol(i), i);
        }

        MolInstancesCache cache = new MolInstancesCache(mols, inst);
        logger.info("" + cache.numAttributes() + "\t" +
            cache.numberOfDescriptors() + "\t" + cache.numInstances() + "\t" +
            cache.numberOfMolecules());
        logger.info("" + this.numAttributes() + "\t" +
            this.numberOfDescriptors() + "\t" + this.numInstances() + "\t" +
            this.numberOfMolecules());

        this.statistic = DescriptorStatistic.getDescStatistic(mols);
        this.binning = cache.binning;
        this.classAttributeName = this.classAttribute().name();

        //cache.clone(this);
        logger.info("" + this.numAttributes() + "\t" +
            this.numberOfDescriptors() + "\t" + this.numInstances() + "\t" +
            this.numberOfMolecules());

        return true;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.data.MoleculeCache#setMoleculeDescriptors(joelib2.molecule.Molecule, int)
     */
    private MolInstance getInstanceFor(Molecule mol)
    {
        double[] vals = new double[super.numAttributes()];

        PairData pairData;
        String descriptor;
        String identifierValue = "";

        for (int j = 0; j < vals.length; j++)
        {
            Attribute attribute = super.attribute(j);
            pairData = mol.getData(attribute.name(), true);
            descriptor = pairData.getKey();

            if (descriptor.equals(moleculeIdentifier))
            {
                identifierValue = (String) pairData.getKeyValue();
            }

            IdentifierValue = identifierValue;

            if (pairData == null)
            {
                vals[attribute.index()] = MolInstance.missingValue();
            }
            else
            {
                if (attribute.isNominal())
                {
                    // nominal
                    String tmpS = pairData.toString().trim();

                    //System.out.println("tmpS: " + tmpS);
                    if (tmpS.indexOf("\n") != -1)
                    {
                        logger.error("Descriptor " + attribute.name() +
                            " contains multiple lines and is not a valid nominal value.");
                    }
                    else
                    {
                        vals[attribute.index()] = attribute.indexOfValue(
                                pairData.toString());

                        if (vals[attribute.index()] == -1)
                        {
                            //System.out.println("Invalid:" + attribute.index());
                            logger.error("Invalid nominal value");

                            return null;
                        }
                    }
                }
                else
                {
                    // numeric
                    if (pairData instanceof NativeValue)
                    {
                        double tmpD = ((NativeValue) pairData).getDoubleNV();

                        if (Double.isNaN(tmpD))
                        {
                            vals[attribute.index()] = MolInstance
                                .missingValue();
                        }
                        else
                        {
                            vals[attribute.index()] = tmpD;
                        }
                    }
                    else
                    {
                        logger.error("Descriptor " + attribute.name() +
                            " is not a native value.");
                    }
                }
            }

            attribute.index();
        }

        MolInstance instance = new MolInstance(mol, 1, vals);

        return instance;
    }

    private String[] getNames()
    {
        FastVector att = this.getAttributes();
        String[] s = new String[att.size()];

        for (int i = 0; i < att.size(); i++)
        {
            Attribute t = (Attribute) att.elementAt(i);
            s[i] = t.name();
        }

        return s;
    }

    private void update(FastVector attV)
    {
        if (super.numAttributes() != 0)
        {
            logger.error("Instances not empty");

            //System.out.println(super.numAttributes());
            //System.out.println(super.attribute(0).name());
            //System.exit(1);
        }

        for (int i = 0; i < attV.size(); i++)
        {
            Attribute att = (Attribute) attV.elementAt(i);

            super.insertAttributeAt(att, i);
        }

        super.setClass(super.attribute(classAttributeName));

        //System.out.println("Class:" + super.attribute(ClassAttributeName).index() + "\t" + super.classAttribute().name());
        super.setClassIndex(super.attribute(classAttributeName).index());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
