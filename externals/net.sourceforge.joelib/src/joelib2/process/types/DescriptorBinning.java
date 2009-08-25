///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DescriptorBinning.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:38 $
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
package joelib2.process.types;

import joelib2.feature.NativeValue;

import joelib2.io.BasicIOType;
import joelib2.io.BasicReader;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.molecule.types.PairData;

import joelib2.process.BasicProcess;
import joelib2.process.MoleculeProcessException;

import joelib2.util.BasicProperty;

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

import java.net.URL;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Category;


/**
 * Calling processor classes if the filter rule fits.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:38 $
 */
public class DescriptorBinning extends BasicProcess
    implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            DescriptorBinning.class.getName());

    //  private final static JOEProperty[] ACCEPTED_PROPERTIES = new
    // JOEProperty[]{
    //      new JOEProperty("NUMBER_OF_BINS", "java.lang.Integer", "Number of bins to
    // create.", true),
    //      };
    private final static String FILE_EXT = ".binning";

    //~ Instance fields ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////

    private Hashtable bins = new Hashtable();

    private List desc2ignore;

    private int numberOfBins = -1;

    private DescriptorStatistic statistic;

    //~ Constructors ///////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////

    /**
     * Constructor for the DescSelectionWriter object
     */
    public DescriptorBinning()
    {
        statistic = new DescriptorStatistic();
        clear();

        // load descriptors which should be ignored
        String value;

        if ((value = BasicPropertyHolder.instance().getProperty(this,
                            "descriptors2ignore")) != null)
        {
            List tmpVec = BasicResourceLoader.readLines(value);

            if (tmpVec == null)
            {
                logger.error("File with descriptor names to ignore not found.");
            }

            desc2ignore = tmpVec;
        }

        numberOfBins = BasicPropertyHolder.instance().getInt(this,
                "numberOfBins", 1, Integer.MAX_VALUE, 20);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @param fileName
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static boolean existsBinningFileFor(String fileName)
    {
        //FileInputStream fis = null;
        // try to open file
        try
        {
            new FileInputStream(fileName + FILE_EXT);
        }
        catch (Exception ex)
        {
            return false;
        }

        return true;
    }

    /**
     * Gets the descBinning attribute of the DescBinning class
     *
     * @param inType
     *            Description of the Parameter
     * @param inFile
     *            Description of the Parameter
     * @param _numberOfBins
     *            Description of the Parameter
     * @return The descBinning value
     */
    public static DescriptorBinning getDescBinning(MoleculeVector molecules)
    {
        DescriptorBinning binning = new DescriptorBinning();

        binning.statistic = DescriptorStatistic.getDescStatistic(molecules);

        int size = molecules.getSize();
        Molecule mol;

        for (int i = 0; i < size; i++)
        {
            mol = molecules.getMol(i);

            try
            {
                binning.process(mol, null);
            }
            catch (MoleculeProcessException ex)
            {
                ex.printStackTrace();

                return null;
            }
        }

        return binning;
    }

    //  public static DescBinning getDescBinning(IOType inType, String inFile,
    // int _numberOfBins)
    //  {
    //    FileInputStream fis=null;
    //    try
    //    {
    //      fis=new FileInputStream(inFile);
    //    }
    //    catch (Exception ex)
    //    {
    //      ex.printStackTrace();
    //      return null;
    //    }
    //
    //    return getDescBinning(inType, fis, _numberOfBins);
    //  }
    //  public static DescBinning getDescBinning(IOType inType, InputStream
    // inStream, int _numberOfBins)

    /**
     * Gets the descBinning attribute of the DescBinning class
     *
     * @param inType
     *            Description of the Parameter
     * @param inFile
     *            Description of the Parameter
     * @param _numberOfBins
     *            Description of the Parameter
     * @return The descBinning value
     */
    public static DescriptorBinning getDescBinning(BasicIOType inType,
        String inFile)
    {
        return getDescBinning(inType, inFile, -1);
    }

    public static DescriptorBinning getDescBinning(BasicIOType inType,
        String inFile, int _numberOfBins)
    {
        DescriptorBinning binning = new DescriptorBinning();

        // load descriptor binning if file exists
        if (existsBinningFileFor(inFile))
        {
            binning.fromFileFor(inFile);

            return binning;
        }

        // create new descriptor binning
        //    descriptors = _descriptors;
        //        InputStream clonedIS = null;
        BasicReader reader = null;

        try
        {
            if (_numberOfBins < 1)
            {
                binning.init(inType, inFile);
            }
            else
            {
                binning.init(inType, inFile, _numberOfBins);
            }

            //      clonedIS=(InputStream)inStream.clone();
            reader = new BasicReader(new FileInputStream(inFile), inType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            logger.error(ex.getMessage());

            return null;
        }

        logger.info("Calculate descriptor binning.");

        Molecule mol = new BasicConformerMolecule(inType, inType);

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
                ex.printStackTrace();
                logger.error(ex.getMessage());

                return null;
            }

            try
            {
                binning.process(mol, null);
            }
            catch (MoleculeProcessException ex)
            {
                ex.printStackTrace();
                logger.error(ex.getMessage());

                return null;
            }
        }

        reader.close();
        reader = null;

        // store descriptor binning in file
        binning.writeBinningFileFor(inFile);

        return binning;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public boolean clear()
    {
        if (statistic == null)
        {
            return false;
        }

        /*
         * if (!statistic.clear()) { return false; }
         */

        //bins.clear();
        return true;
    }

    /**
     * Description of the Method
     *
     * @param fileName
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean fromFile(String fileName)
    {
        LineNumberReader lnr = null;
        String line;
        boolean ok = true;
        int VARS = 10;
        URL location = this.getClass().getClassLoader().getSystemResource(
                fileName);
        String fName;

        if (location != null)
        {
            fName = location.getFile();
        }
        else
        {
            fName = fileName;
        }

        // try to open file
        try
        {
            lnr = new LineNumberReader(new InputStreamReader(
                        new FileInputStream(fName)));

            StringTokenizer st;
            int tokens = 0;
            String token;
            int i;

            // get binning structure from first line
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            //            System.out.println("line:"+line);
            st = new StringTokenizer(line, " \r\n\t");
            tokens = st.countTokens();
            numberOfBins = tokens - VARS;

            if (numberOfBins <= 0)
            {
                logger.error("Negative number of bins.");

                return false;
            }

            // read data
            ArrayStatistic arrayStat = null;
            ArrayBinning arrayBinning = null;
            String descriptor = null;

            // define array statistic data types
            int count = 0;
            double sum = Double.NaN;
            double sumSq = Double.NaN;
            double stdDev = Double.NaN;
            double mean = Double.NaN;
            double min = Double.NaN;
            double max = Double.NaN;
            double shannonEntropy = Double.NaN;
            double entropy = Double.NaN;
            boolean containsNaN = false;
            int[] tmpA = new int[numberOfBins];

            // read statistic data
            while ((line = lnr.readLine()) != null)
            {
                if (line.length() == 0 /* || line.charAt(0)=='#' */)
                {
                    continue;
                }

                st = new StringTokenizer(line, " \r\n\t");
                tokens = st.countTokens();

                //                System.out.println("line ("+tokens+", b="+numberOfBins+"):
                // "+line);
                i = 0;

                if (tokens == (VARS + numberOfBins))
                {
                    while (st.hasMoreTokens())
                    {
                        i++;
                        token = st.nextToken();

                        try
                        {
                            switch (i)
                            {
                            case 1:
                                descriptor = token;

                                break;

                            case 2:
                                count = (int) Double.parseDouble(token);

                                break;

                            case 3:
                                shannonEntropy = Double.parseDouble(token);

                                break;

                            case 4:
                                entropy = (int) Double.parseDouble(token);

                                break;

                            case 5:
                                min = Double.parseDouble(token);

                                break;

                            case 6:
                                max = Double.parseDouble(token);

                                break;

                            case 7:
                                sum = Double.parseDouble(token);

                                break;

                            case 8:
                                sumSq = Double.parseDouble(token);

                                break;

                            case 9:
                                mean = Double.parseDouble(token);

                                break;

                            case 10:
                                stdDev = Double.parseDouble(token);

                                break;

                            case 11:
                                containsNaN = Boolean.valueOf(token)
                                                     .booleanValue();

                                break;

                            default:

                                //                                    System.out.print(" "+(i-VARS-1)+"="+token);
                                tmpA[i - VARS - 1] = Integer.parseInt(token);

                                break;
                            }
                        }
                        catch (NumberFormatException ex)
                        {
                            ok = false;
                            ex.printStackTrace();
                            logger.error(ex.toString());
                        }
                    }

                    arrayStat = new ArrayStatistic(count, min, max, sum, sumSq,
                            mean, stdDev);
                    statistic.putArrayStatistic(descriptor, arrayStat);
                    arrayBinning = new ArrayBinning(numberOfBins, arrayStat);
                    arrayBinning.shannonEntropy = shannonEntropy;
                    arrayBinning.entropy = entropy;
                    arrayBinning.binning = new int[numberOfBins];
                    arrayBinning.containsNaN = containsNaN;
                    System.arraycopy(tmpA, 0, arrayBinning.binning, 0,
                        numberOfBins);
                    bins.put(descriptor, arrayBinning);

                    //                    System.out.print(""+descriptor+"
                    // "+arrayBinning.toString());
                }
                else
                {
                    logger.error("Wrong format in line " + lnr.getLineNumber());
                    ok = false;
                }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            logger.error(ex.toString());
            ok = false;
        }

        return ok;
    }

    public boolean fromFileFor(String fileName)
    {
        String fn = fileName + FILE_EXT;
        logger.info("Load descriptor binning from " + fn);

        return fromFile(fn);
    }

    /**
     * Gets the descriptorBinning attribute of the DescBinning object
     *
     * @param descriptor
     *            Description of the Parameter
     * @return The descriptorBinning value
     */
    public ArrayBinning getDescriptorBinning(String descriptor)
    {
        if (statistic == null)
        {
            return null;
        }

        ArrayBinning arrayBinning = (ArrayBinning) bins.get(descriptor);

        if (arrayBinning == null)
        {
            logger.error("There exist no descriptor binning for '" +
                descriptor + "'");

            return null;
        }

        arrayBinning.calculateDerived();

        return arrayBinning;
    }

    /**
     * Gets the descriptors attribute of the DescBinning object
     *
     * @return The descriptors value
     */
    public Enumeration getDescriptors()
    {
        if (statistic == null)
        {
            return null;
        }

        return bins.keys();
    }

    /**
     * Gets the descStatistic attribute of the DescBinning object
     *
     * @return The descStatistic value
     */
    public DescriptorStatistic getDescStatistic()
    {
        return statistic;
    }

    /**
     * Description of the Method
     *
     * @param _statistic
     *            Description of the Parameter
     * @param _numberOfBins
     *            Description of the Parameter
     */
    public void init(DescriptorStatistic _statistic)
    {
        statistic = _statistic;
    }

    /**
     * Description of the Method
     *
     * @param _statistic
     *            Description of the Parameter
     * @param _numberOfBins
     *            Description of the Parameter
     */
    public void init(DescriptorStatistic _statistic, int _numberOfBins)
    {
        statistic = _statistic;
        numberOfBins = _numberOfBins;
    }

    /**
     * Description of the Method
     *
     * @param inType
     *            Description of the Parameter
     * @param _numberOfBins
     *            Description of the Parameter
     * @param inFile
     *            Description of the Parameter
     * @exception Exception
     *                Description of the Exception
     */
    public void init(BasicIOType inType, String inFile) throws Exception
    {
        statistic = DescriptorStatistic.getDescStatistic(inType, inFile);
    }

    /**
     * Description of the Method
     *
     * @param inType
     *            Description of the Parameter
     * @param _numberOfBins
     *            Description of the Parameter
     * @param inFile
     *            Description of the Parameter
     * @exception Exception
     *                Description of the Exception
     */
    public void init(BasicIOType inType, String inFile, int _numberOfBins)
        throws Exception
    {
        numberOfBins = _numberOfBins;
        statistic = DescriptorStatistic.getDescStatistic(inType, inFile);

        //        System.out.println("Descriptor statistic for binning:\n " +
        // statistic.toString());
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public BasicProperty[] neededProperties()
    {
        //    return ACCEPTED_PROPERTIES;
        return null;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public int numberOfDescriptors()
    {
        if (statistic == null)
        {
            return -1;
        }

        return bins.size();
    }

    /**
     * Description of the Method
     *
     * @param mol
     *            Description of the Parameter
     * @param properties
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception MoleculeProcessException
     *                Description of the Exception
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        boolean process = false;

        if (statistic != null)
        {
            try
            {
                super.process(mol, properties);
            }
            catch (MoleculeProcessException e)
            {
                throw new MoleculeProcessException("Properties for " +
                    this.getClass().getName() + " not correct.");
            }

            PairData pairData;
            PairDataIterator gdit = mol.genericDataIterator();
            ArrayStatistic arrayStat;
            String descriptor;
            double value = 0.0;
            ArrayBinning arrayBinning;
            boolean ignoreDesc = false;

            while (gdit.hasNext())
            {
                pairData = gdit.nextPairData();
                descriptor = pairData.getKey();

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

                // parse data, if possible
                pairData = mol.getData(descriptor, true);

                // check descriptor binning entry
                if (bins.containsKey(descriptor))
                {
                    arrayBinning = (ArrayBinning) bins.get(descriptor);
                }
                else
                {
                    // check descriptor statistic entry
                    arrayStat = statistic.getDescriptorStatistic(descriptor);

                    if (arrayStat == null)
                    {
                        logger.error("Statistic for " + descriptor +
                            " does not exist.");
                        process = false;

                        break;
                    }

                    arrayBinning = new ArrayBinning(numberOfBins, arrayStat);
                    bins.put(descriptor, arrayBinning);
                }

                if (pairData instanceof NativeValue)
                {
                    value = ((NativeValue) pairData).getDoubleNV();

                    if (arrayBinning.add(value) == -1)
                    {
                        logger.error("Out of range (" + value + ") in " +
                            descriptor);
                        process = false;

                        break;
                    }
                }
            }
        }

        return process;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public String toString()
    {
        if (statistic == null)
        {
            return null;
        }

        StringBuffer sb = new StringBuffer(10000);

        //sb.append("Descriptor Binning\n");
        sb.append(
            "#Descriptor Count ShannonEntropy Entropy Min Max Sum SumSq Mean StdDev NaN");

        for (int i = 1; i <= numberOfBins; i++)
        {
            sb.append(" bin");
            sb.append(i);
        }

        sb.append("\n");

        ArrayBinning arrayBinning;
        String descriptor;

        for (Enumeration e = getDescriptors(); e.hasMoreElements();)
        {
            descriptor = (String) e.nextElement();

            //            sb.append(showDescriptorStatistic((String)e.nextElement()));
            arrayBinning = (ArrayBinning) bins.get(descriptor);
            sb.append(descriptor);
            sb.append(' ');
            sb.append(arrayBinning.toString());
        }

        return sb.toString();
    }

    public void writeBinningFileFor(String _inFile)
    {
        String filename = _inFile + FILE_EXT;
        PrintStream ps = null;

        try
        {
            ps = new PrintStream(new FileOutputStream(filename));
            ps.println(this.toString());
            logger.info("Binning for " + _inFile);
            logger.info("  written to " + filename);
        }
        catch (Exception ex)
        {
            logger.warn(ex.toString());
            logger.warn("Binning not written for " + _inFile);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
