///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DescriptorStatistic.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.4 $
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

import joelib2.io.BasicReader;
import joelib2.io.IOType;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.molecule.types.PairData;

import joelib2.process.BasicProcess;
import joelib2.process.MoleculeProcessException;

import joelib2.util.BasicProperty;

import joelib2.util.iterator.PairDataIterator;

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
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Calling processor classes if the filter rule fits.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/02/17 16:48:38 $
 */
public class DescriptorStatistic extends BasicProcess
    implements java.io.Serializable, Cloneable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            DescriptorStatistic.class.getName());

    //  private final static  JOEProperty[]  ACCEPTED_PROPERTIES    = new JOEProperty[]{
    //      new JOEProperty("SKIP_WRITER", "joelib2.io.MoleculeFileType", "Writer for skipped molecule entries.", true),
    //      new JOEProperty("DELIMITER", "java.lang.String", "Delimiter between descriptors in flat mode.", true),
    //      new JOEProperty("COMMENT", "java.lang.String", "Comment character of the first line in flat mode.", true)
    //      };
    private final static String FILE_EXT = ".statistic";

    //~ Instance fields ////////////////////////////////////////////////////////

    private List desc2ignore;

    private Hashtable notNative = new Hashtable(50);
    private Hashtable statistic = new Hashtable(50);

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescSelectionWriter object
     */
    public DescriptorStatistic()
    {
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
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @param fileName  Description of the Parameter
     * @return          Description of the Return Value
     */
    public static boolean existsStatisticFileFor(String fileName)
    {
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

    //  public static DescStatistic getDescStatistic(IOType inType, String inFile)
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
    //    return getDescStatistic(inType, fis);
    //  }
    public static DescriptorStatistic getDescStatistic(MoleculeVector molecules)
    {
        DescriptorStatistic statistic = new DescriptorStatistic();
        int size = molecules.getSize();

        Molecule mol;

        for (int i = 0; i < size; i++)
        {
            mol = molecules.getMol(i);

            try
            {
                statistic.process(mol, null);
            }
            catch (MoleculeProcessException ex)
            {
                logger.error(ex.toString());
                statistic = null;

                return null;
            }
        }

        return statistic;
    }

    /**
     * Gets the descStatistic attribute of the DescStatistic class
     *
     * @param inType    Description of the Parameter
     * @param inFile    Description of the Parameter
     * @return          The descStatistic value
     */
    public static DescriptorStatistic getDescStatistic(IOType inType,
        String inFile)
    {
        DescriptorStatistic statistic = new DescriptorStatistic();

        // load descriptor statistic if file exists
        if (existsStatisticFileFor(inFile))
        {
            statistic.fromFileFor(inFile);

            return statistic;
        }

        // create new descriptor statistic
        BasicReader reader = null;

        try
        {
            reader = new BasicReader(new FileInputStream(inFile), inType);
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());

            return null;
        }

        logger.info("Calculate descriptor statistic.");

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
                logger.error(ex.getMessage());
                statistic = null;

                return null;
            }

            try
            {
                statistic.process(mol, null);
            }
            catch (MoleculeProcessException ex)
            {
                logger.error(ex.getMessage());
                statistic = null;

                return null;
            }
        }

        //reader.close();
        // store descriptor statistic in file
        statistic.writeStatisticFileFor(inFile);

        return statistic;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean clear()
    {
        //        if(statistic==null)return false;
        statistic.clear();

        return true;
    }

    public Object clone()
    {
        DescriptorStatistic cloned = new DescriptorStatistic();
        cloned.notNative = (Hashtable) notNative.clone();
        cloned.statistic = (Hashtable) statistic.clone();
        cloned.desc2ignore = (List) ((Vector) desc2ignore).clone();

        return cloned;
    }

    /**
     * Description of the Method
     *
     * @param fileName  Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean fromFile(String fileName)
    {
        LineNumberReader lnr = null;
        String line;
        boolean ok = true;
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

            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            StringTokenizer st;
            int i;
            ArrayStatistic arrayStat = null;
            String descriptor = null;
            String noNativeName = null;
            int tokens;

            // define array statistic data types
            int count = 0;
            double sum = Double.NaN;
            double sumSq = Double.NaN;
            double stdDev = Double.NaN;
            double mean = Double.NaN;
            double min = Double.NaN;
            double max = Double.NaN;

            // read statistic data
            String token;

            while ((line = lnr.readLine()) != null)
            {
                if (line.length() == 0 /*|| line.charAt(0)=='#' */)
                {
                    continue;
                }

                st = new StringTokenizer(line, " \r\n\t");
                tokens = st.countTokens();

                //                System.out.println("line ("+tokens+"): "+line);
                i = 0;

                if (tokens == 8)
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
                                min = Double.parseDouble(token);

                                break;

                            case 4:
                                max = Double.parseDouble(token);

                                break;

                            case 5:
                                sum = Double.parseDouble(token);

                                break;

                            case 6:
                                sumSq = Double.parseDouble(token);

                                break;

                            case 7:
                                mean = Double.parseDouble(token);

                                break;

                            case 8:
                                stdDev = Double.parseDouble(token);

                                break;
                            }
                        }
                        catch (NumberFormatException ex)
                        {
                            ok = false;
                            logger.error(ex.toString());
                        }
                    }

                    arrayStat = new ArrayStatistic(count, min, max, sum, sumSq,
                            mean, stdDev);

                    statistic.put(descriptor, arrayStat);

                    //                    System.out.println(""+descriptor+" "+arrayStat.toString());
                }
                else if (tokens == 3)
                {
                    while (st.hasMoreTokens())
                    {
                        i++;
                        token = st.nextToken();

                        switch (i)
                        {
                        case 1:
                            descriptor = token;

                            break;

                        case 2:
                            count = (int) Double.parseDouble(token);

                            break;

                        case 3:
                            noNativeName = token;

                            break;
                        }
                    }

                    arrayStat = new ArrayStatistic();
                    arrayStat.count = count;
                    statistic.put(descriptor, arrayStat);
                    notNative.put(descriptor, noNativeName);
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
            logger.error(ex.toString());
            ok = false;
        }

        return ok;
    }

    public boolean fromFileFor(String fileName)
    {
        String fn = fileName + FILE_EXT;
        logger.info("Load descriptor statistic from " + fn);

        return fromFile(fn);
    }

    /**
     *  Gets the descriptors attribute of the DescStatistic object
     *
     * @return   The descriptors value
     */
    public Enumeration getDescriptors()
    {
        //      if(statistic==null)return null;
        return statistic.keys();
    }

    /**
     *  Gets the descriptorStatistic attribute of the DescStatistic object
     *
     * @param descriptor  Description of the Parameter
     * @return            The descriptorStatistic value
     */
    public ArrayStatistic getDescriptorStatistic(String descriptor)
    {
        //        if(statistic==null)return null;
        ArrayStatistic arrayStat = (ArrayStatistic) statistic.get(descriptor);

        if (arrayStat == null)
        {
            logger.error("There exist no descriptor statistic for '" +
                descriptor + "'");

            return null;
        }

        arrayStat.calculateDerived();

        return arrayStat;
    }

    public int getNumberOfDescriptors()
    {
        if (statistic == null)
        {
            return -1;
        }

        return statistic.size();
    }

    /**
     *  Description of the Method
     *
     * @param descriptor  Description of the Parameter
     * @return            Description of the Return Value
     */
    public boolean hasDescriptorStatistic(String descriptor)
    {
        //        if(statistic==null)return false;
        return statistic.containsKey(descriptor);
    }

    /*-------------------------------------------------------------------------*
     * public  methods
     *-------------------------------------------------------------------------*/

    /**
     *  Gets the descriptorStatistic attribute of the DescStatistic object
     *
     * @param descriptor  Description of the Parameter
     * @return            The descriptorStatistic value
     */
    public boolean isNative(String descriptor)
    {
        return !notNative.containsKey(descriptor);
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public BasicProperty[] neededProperties()
    {
        //    return ACCEPTED_PROPERTIES;
        return null;
    }

    /**
     *  Description of the Method
     *
     * @param mol                      Description of the Parameter
     * @param properties               Description of the Parameter
     * @return                         Description of the Return Value
     * @exception MoleculeProcessException  Description of the Exception
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
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

        //    System.out.println("processing:::"+mol.getTitle());
        PairData pairData;
        PairDataIterator gdit = mol.genericDataIterator();
        ArrayStatistic arrayStat;
        String descriptor;

        //        String ignoreDesc = PropertyHolder.instance().getProperties().getProperty("jcompchem.joelib2.process.DescStatistic.ignoreDescriptor", "Entry_Number");        while (gdit.hasNext())
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
                        //            System.out.println("ignore " + desc2ignore.get(i));
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

            // check descriptor statistic entry
            if (statistic.containsKey(descriptor))
            {
                arrayStat = (ArrayStatistic) statistic.get(descriptor);
            }
            else
            {
                arrayStat = new ArrayStatistic();
                statistic.put(descriptor, arrayStat);
            }

            if (pairData instanceof NativeValue)
            {
                arrayStat.add(((NativeValue) pairData).getDoubleNV());
            }
            else
            {
                arrayStat.count += 1;

                String notNativeName = pairData.getKeyValue().getClass()
                                               .getName();

                if (!notNative.containsKey(descriptor))
                {
                    notNative.put(descriptor, notNativeName);
                }
            }
        }

        return true;
    }

    /**
     * Description of the Method
     *
     * @param _desc  Description of the Parameter
     * @param as     Description of the Parameter
     * @return       Description of the Return Value
     */
    public Object putArrayStatistic(String _desc, ArrayStatistic as)
    {
        return statistic.put(_desc, as);
    }

    /**
     *  Description of the Method
     *
     * @param descriptor  Description of the Parameter
     * @return            Description of the Return Value
     */
    public String showDescriptorStatistic(String descriptor)
    {
        //        if(statistic==null)return null;
        ArrayStatistic arrayStat = (ArrayStatistic) statistic.get(descriptor);

        if (arrayStat == null)
        {
            logger.error("There exist no descriptor statistic for '" +
                descriptor + "'");

            return null;
        }

        arrayStat.calculateDerived();

        StringBuffer sb = new StringBuffer(100);
        sb.append(descriptor);
        sb.append('\n');
        sb.append(arrayStat.toString());
        sb.append('\n');

        return sb.toString();
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String toString()
    {
        //        if(statistic==null)return null;
        StringBuffer sb = new StringBuffer(10000);

        sb.append("#Descriptor Count Min Max Sum SumSq Mean StdDev\n");

        ArrayStatistic arrayStat;
        String descriptor;
        String noNativeName;

        for (Enumeration e = getDescriptors(); e.hasMoreElements();)
        {
            descriptor = (String) e.nextElement();

            //            sb.append(showDescriptorStatistic((String)e.nextElement()));
            if (notNative.containsKey(descriptor))
            {
                arrayStat = getDescriptorStatistic(descriptor);
                noNativeName = (String) notNative.get(descriptor);
                sb.append(descriptor);
                sb.append(' ');
                sb.append((int) arrayStat.count);
                sb.append(' ');
                sb.append(noNativeName);
            }
            else
            {
                arrayStat = getDescriptorStatistic(descriptor);
                arrayStat.calculateDerived();
                sb.append(descriptor);
                sb.append(' ');
                sb.append((int) arrayStat.count);
                sb.append(' ');
                sb.append(arrayStat.min);
                sb.append(' ');
                sb.append(arrayStat.max);
                sb.append(' ');
                sb.append(arrayStat.sum);
                sb.append(' ');
                sb.append(arrayStat.sumSq);
                sb.append(' ');
                sb.append(arrayStat.mean);
                sb.append(' ');
                sb.append(arrayStat.stdDev);
            }

            sb.append('\n');
        }

        return sb.toString();
    }

    public void writeStatisticFileFor(String _inFile)
    {
        String filename = _inFile + FILE_EXT;
        PrintStream ps = null;

        try
        {
            ps = new PrintStream(new FileOutputStream(filename));
            ps.println(this.toString());
            logger.info("Statistic for " + _inFile);
            logger.info("  written to " + filename);
        }
        catch (Exception ex)
        {
            logger.warn(ex.toString());
            logger.warn("Statistic not written for " + _inFile);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
