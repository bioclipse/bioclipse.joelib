///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: JCAMPParser.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.jcamp;

import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.log4j.Category;


/**
 *  A class to interpret JCAMP-DX data (JCAMP-CS is not implemented yet). The
 *  supported data types are XYPAIRS, XYDATA=(X++(Y..Y)), PEAK TABLE and LINK.
 *  <br>
 *  If you want load a file with multiple blocks or inner blocks you must use
 *  <code>JCampMultipleFile</code>.<br>
 *  This class can only load separated single blocks with one TITLE and END
 *  label !<br>
 *  <br>
 *
 *  <ul>
 *    <li> ... The International Union of Pure and Applied Chemistry (IUPAC)
 *    took over responsibility from the Joint Commitee on Atomic and Molecular
 *    Physical Data (JCAMP) in 1995 ...<br>
 *    <a href="http://jcamp.isas-dortmund.de/">I U P A C<br>
 *    Committee on Printed and Electronic Publications <br>
 *    Working Party on Spectroscopic Data Standards (JCAMP-DX)</a> <br>
 *    <br>
 *
 *    <li> <a href="http://wwwchem.uwimona.edu.jm:1104/software/jcampdx.html">
 *    The Department of Chemistry at the University of the West Indies</a>
 *  </ul>
 *
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 * @.cite dl93
 * @.cite dw88
 * @.cite ghhjs91
 * @.cite lhdl94
 * @.cite dhl90
 */
public class JCAMPParser
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.jcamp.JCAMPParser");

    /**
     *  Description of the Field
     */
    public final static int DATA_TYPE_SPECTRUM = 1;

    /**
     *  Description of the Field
     */
    public final static int DATA_TYPE_PEAKS = 1;

    /**
     *  data representation: <a href="#DEFINITIONS">XYPAIRS definition</a>
     */
    public final static int XYPAIRS = 0;

    /**
     *  data representation: <a href="#DEFINITIONS">XYDATA=(X++(Y..Y))
     *  definition</a>
     */
    public final static int XYDATA_X_YY = 10;

    /**
     *  data representation: <a href="#DEFINITIONS">definition for the parameter
     *  of a chemical structure</a>
     */
    public final static int CHEMICAL_STRUCTURE = 20;

    /**
     *  data representation: <a href="#DEFINITIONS">XYPOINTS definition</a>
     */
    public final static int XYPOINTS = 30;

    /**
     *  data representation: <a href="#DEFINITIONS">PEAK TABLE definition</a>
     */
    public final static int PEAK_TABLE = 40;

    /**
     *  data representation: <a href="#DEFINITIONS">PEAK ASSIGNMENTS definition
     *  </a>
     */
    public final static int PEAK_ASSIGNMENTS = 50;

    /**
     *  data representation: <a href="#DEFINITIONS">RADATA definition</a>
     */
    public final static int RADATA = 60;

    /**
     *  data representation: <a href="#DEFINITIONS">LINK definition</a>
     */
    public final static int LINK = 70;

    //    private final int dataBlockSize = 5200;

    /**
     *  An array of the commonly used LDR's (Label Data Records). <br>
     *  <br>
     *  For more information see:<BR>
     *
     *  <UL>
     *    <LI> Robert S. Mc Donald, Paul A. Wilks, ''JCAMP-DX: A Standard Form
     *    for Exchange of Infrared Spectra in Computer Readable Form'', <EM>
     *    Appl. Spec.</EM> , <EM> 42</EM> , (1988), 151-162. <BR>
     *    URL<TT> <A HREF="http://jcamp.isas-dortmund.de/protocols/dxir01.pdf">
     *    http://jcamp.isas-dortmund.de/protocols/dxir01.pdf</A> </TT> </LI>
     *
     *    <LI> J. Gasteiger, B. M. P. Hendriks, P. Hoever, C. Jochum, H.
     *    Somberg, ''JCAMP-CS: A Standard Form for Chemical Structure
     *    Information in Computer Readable Form'', <EM> Appl. Spec.</EM> , <EM>
     *    45</EM> , (1991), 4-11. <BR>
     *    URL<TT> <A HREF="http://jcamp.isas-dortmund.de/protocols/dxcs01.pdf">
     *    http://jcamp.isas-dortmund.de/protocols/dxcs01.pdf</A> </TT> </LI>
     *
     *    <LI> Peter Lampen, Heinrich Hillig, Antony N. Davies, Michael
     *    Linscheid, ''JCAMP-DX for Mass Spectrometry'', <EM> Appl. Spec.</EM> ,
     *    <EM> 48</EM> , (1994), 1545-1552. <BR>
     *    URL<TT> <A HREF="http://jcamp.isas-dortmund.de/protocols/dxms01.pdf">
     *    http://jcamp.isas-dortmund.de/protocols/dxms01.pdf</A> </TT> </LI>
     *
     *    <LI> Antony N. Davies, Peter Lampen, ''JCAMP-DX for NMR'', <EM> Appl.
     *    Spec.</EM> , <EM> 47</EM> , (1993), 1093-1099. <BR>
     *    URL<TT> <A HREF="http://jcamp.isas-dortmund.de/protocols/dxnmr01.pdf">
     *    http://jcamp.isas-dortmund.de/protocols/dxnmr01.pdf</A> </TT> </LI>
     *
     *  </UL>
     *
     */
    public final static String[] commonLDRs =
        {
            ".ACCELERATING VOLTAGE", ".ACQUISITION MODE", ".ACQUISITION RANGE",
            ".ACQUISITION TIME", ".AVERAGES", ".BASE PEAK",
            ".BASE PEAK INTENSITY", ".COUPLING CONSTANTS", ".DECOUPLER",
            ".DELAY", ".DETECTOR", ".DIGITISER RES", ".FIELD", ".FILTER WIDTH",
            ".INLET", ".IONISATION ENERGY", ".IONISATION MODE",
            ".MIN INTENSITY", ".MAX INTENSITY", ".NOMINAL MASS", ".OBSERVE 90",
            ".OBSERVE FREQUENCY", ".OBSERVE NUCLEUS", ".PHASE 0", ".PHASE 1",
            ".RELAXATION TIMES", ".RIC", ".SCAN NUMBER", ".SCAN RATE",
            ".SOLVENT REFERENCE", ".SOURCE TEMPERATURE", ".SPECTROMETER TYPE",
            ".SPINNING RATE", ".TOTAL ION CURRENT", ".ZERO FILL", "AFACTOR",
            "ALIAS", "ATOMLIST", "AUNITS", "BEILSTEIN LAWSON NO", "BLOCK_ID",
            "BLOCKS", "BONDLIST", "BP", "CAS NAME", "CAS REGISTRY NO",
            "CHARGE", "CLASS", "CONCENTRATIONS", "CROSS REFERENCE",
            "DATA CLASS", "DATA PROCESSING", "DATA TABLE", "DATA TYPE", "DATE",
            "DELTAR", "DELTAX", "DENSITY", "END", "END NTUPLES", "FACTOR",
            "FIRST", "FIRSTA", "FIRSTR", "FIRSTX", "FIRSTY",
            "INSTRUMENT PARAMETERS", "JCAMP-CS", "JCAMP-DX", "LAST", "LASTR",
            "LASTX", "MAX", "MAX_RASTER", "MAX_XYZ", "MAXA", "MAXX", "MAXY",
            "MIN", "MINA", "MINX", "MINY", "MOLFORM", "MP", "MW", "NPOINTS",
            "NTUPLES", "ORIGIN", "OWNER", "PAGE", "PATH LENGTH",
            "PEAK ASSIGNMENTS", "PEAK TABLE", "PRESSURE", "RADATA", "RADICAL",
            "REFRACTIVE INDEX", "RESOLUTION", "RFACTOR", "RUNITS",
            "SAMPLE DESCRIPTION", "SAMPLING PROCEDURE", "SOURCE REFERENCE",
            "SPECTROMETER/DATA SYSTEM", "STATE", "STEREOCENTER",
            "STEREOMOLECULE", "STEREOPAIR", "TEMPERATURE", "TIME", "TITLE",
            "UNITS", "VAR_DIM", "VAR_NAME", "VAR_FORM", "WISWESSER", "XFACTOR",
            "XLABEL", "XUNITS", "XYDATA", "XY_RASTER", "XYPOINTS", "XYZ",
            "XYZ_FACTOR", "XYZ_SOURCE", "YFACTOR", "YLABEL", "YUNITS", "ZPD"
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  state if the <a href="#decodeData()"><code>decodeData()</code></a> -
     *  routine must estimate the DELTAX label
     *
     *@see    #decodeData()
     */
    private boolean calculatedDeltaXneeded = false;

    /**
     *  the state if this data was decoded. Only available after decoding the
     *  data with <a href="#decodeData()"><code>decodeData()</code></a>
     *
     *@see    #decodeData()
     */
    private boolean dataDecoded = false;

    /**
     *  the JCAMP data type of this data set. If the data type was not evaluated
     *  by <a href="#decodeData()"><code>decodeData()</code></a> it is -1
     */
    private int dataType = -1;

    /**
     *  DELTAX label from this data set
     */
    private double deltaX = 0;

    /**
     *  this defines the valid difference of the original DELTAX label and the
     *  calculated DELTAX label
     *
     *@see    #decodeData()
     */
    private final double DELTAX_DIFFERENCE_WARNING_VALUE = 0.001;

    /**
     *  state if the <a href="#decodeData()"><code>decodeData()</code></a> -
     *  routine must estimate the NPOINTS label
     *
     *@see    #decodeData()
     */
    private boolean estimatedNPointsNeeded = false;

    /**
     *  FIRSTX label from this data set
     */
    private double firstX = 0;

    /**
     *  LASTX label from this data set
     */
    private double lastX = 0;

    /**
     *  NPOINTS label from this data set
     */
    private double nPoints = 0;

    /**
     *  here are the LDR's (label data records) stored
     */
    private Hashtable parameterTable = new Hashtable();

    /**
     *  state if the separator standard is already checked
     */
    private boolean separatorStandardChecked = false;

    /**
     *  standard separator is "," and ";", values are represanteted with ".",
     *  e.g. "123.456". Spaces can be used for a nicer look.<br>
     *  <br>
     *  Another possibility for a separator is " ", values are represanteted
     *  with ",", e.g. "123,456". That's not a standard definition !!!
     */
    private boolean standardSeparator = true;

    /**
     *  temporarily used when data is decoded
     */
    private LinkedList xData;
    private double[] xDoubleData;

    /**
     *  XFACTOR label from this data set
     */
    private double xFactor = 0;

    /**
     *  temporarily used when data is decoded
     */
    private LinkedList yData;
    private double[] yDoubleData;

    /**
     *  YFACTOR label from this data set
     */
    private double yFactor = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Initializes this <code>JCampInterpreter</code> and gets the JCAMP data
     *  in the <code>String s</code>. <code>file</code> is stored, but not used.
     *  Data is decoded if <code>state</code> is true. Messages are printed to
     *  the <code>JTextComponent messages</code>
     *
     *@param  s                   Description of the Parameter
     *@param  state               Description of the Parameter
     *@exception  IOException     Description of the Exception
     *@exception  JCAMPException  Description of the Exception
     */
    public JCAMPParser(String s) throws IOException, JCAMPException
    {
        interpretLDRs(s);
        decodeData();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the label <code>infoType</code>. It's equivalent to <code>getParameter</code>
     *  (<code>infoType</code>). If the LDR is not available an empty <code>String</code>
     *  is returned.
     *
     *@param  infoType  Description of the Parameter
     *@return           the data for the requested label.
     */
    public String getAdditionalInformation(String infoType)
    {
        LabelData set = new LabelData();

        set = getParameter(infoType);

        if (set == null)
        {
            return "";
        }

        return set.getData();
    }

    /**
     *  Gets the data type of this data set. That's important for the
     *  visualisation routines.
     *
     *@return    The dataType value
     */
    public final int getDataType()
    {
        if ((dataType == XYPAIRS) || (dataType == XYDATA_X_YY))
        {
            return DATA_TYPE_SPECTRUM;
        }

        if (dataType == PEAK_TABLE)
        {
            return DATA_TYPE_PEAKS;
        }

        return -1;
    }

    /**
     *  Gets the JCAMP data type of this data set.
     *
     *@return    the JCAMP data type
     */
    public final int getJCampDataType()
    {
        return dataType;
    }

    /**
     *  Gets the length of the X and Y array.
     *
     *@return    the length of the X and Y data array. If the data was not
     *      decoded it returns -1.
     */
    public final int getLength()
    {
        if (dataDecoded)
        {
            return xDoubleData.length;
        }
        else
        {
            return -1;
        }
    }

    /**
     *  Get all LDR's (data label records) that are stored.
     *
     *@return    all labels and their corresponding data
     */
    public final LabelData[] getParameter()
    {
        Enumeration e;
        LabelData[] back;
        int size = parameterTable.size();
        back = new LabelData[2 * size];

        int i = 0;

        for (e = parameterTable.keys(); e.hasMoreElements();)
        {
            back[i] = new LabelData();
            back[i].setLabel((String) e.nextElement());
            back[i].setData((String) parameterTable.get(back[i].getLabel()));
            i++;
        }

        return back;
    }

    public final LabelData[] getParameter(String[] wanted)
    {
        if (wanted == null)
        {
            return null;
        }

        LabelData[] back = new LabelData[2 * wanted.length];

        for (int i = 0; i < wanted.length; i++)
        {
            if (wanted[i] != null)
            {
                back[i] = new LabelData();
                back[i].label = wanted[i];
                back[i].data = (String) parameterTable.get(wanted[i]);

                //                if (back[i].data == null) {
                //                  logger.warn("JCAMP label \"" + wanted + "\" does not exist");
                //                }
            }
            else
            {
                back[i] = new LabelData();
            }
        }

        return back;
    }

    public final LabelData getParameter(String wanted)
    {
        LabelData back = new LabelData();

        back.setLabel(wanted);
        back.setData((String) parameterTable.get(wanted));

        if (back.getData() == null)
        {
            //            logger.warn("JCAMP label \"" + wanted + "\" does not exist");
            return null;
        }

        return back;
    }

    /**
     *  Gets the X array.
     *
     *@return    the X data or <code>null</code> if the data was not decoded.
     */
    public final double[] getXData()
    {
        if (dataDecoded)
        {
            return xDoubleData;
        }
        else
        {
            if (dataDecoded)
            {
                return xDoubleData;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     *  Gets the label for the X axis. It's equivalent to <code>getParameter</code>
     *  ("XUNITS"). If the LDR is not available an empty <code>String</code> is
     *  returned.
     *
     *@return    the label for the X axis.
     */
    public final String getXLabel()
    {
        LabelData set = new LabelData();

        if (dataDecoded)
        {
            set = getParameter("XUNITS");

            if (set == null)
            {
                return "";
            }

            return set.getData();
        }
        else
        {
            logger.warn("Can't get JCAMP XUNITS label. Data not decoded.");

            return null;
        }
    }

    /**
     *  Gets the Y array.
     *
     *@return    the Y data or <code>null</code> if the data was not decoded.
     */
    public final double[] getYData()
    {
        if (dataDecoded)
        {
            return yDoubleData;
        }
        else
        {
            if (dataDecoded)
            {
                return yDoubleData;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     *  Gets the label for the Y axis. It's equivalent to <code>getParameter</code>
     *  ("YUNITS"). If the LDR is not available an empty <code>String</code> is
     *  returned.
     *
     *@return    the label for the Y axis.
     */
    public final String getYLabel()
    {
        LabelData set = new LabelData();

        if (dataDecoded)
        {
            set = getParameter("YUNITS");

            if (set == null)
            {
                return "";
            }

            return set.getData();
        }
        else
        {
            logger.warn("Can't get JCAMP YUNITS label. Data not decoded.");

            return null;
        }
    }

    /**
     *  Set some LDR's (data label records).
     *
     *@param  newLDR  the new labels and the new data
     */
    public final void setParameter(LabelData[] newLDR)
    {
        for (int i = 0; i < newLDR.length; i++)
        {
            if (newLDR[i] != null)
            {
                parameterTable.put(newLDR[i].getLabel(), newLDR[i].getData());
            }
        }
    }

    /**
     *  Set one LDR (data label record).
     *
     *@param  newLDR  the new label and the new data
     */
    public final void setParameter(LabelData newLDR)
    {
        if (newLDR != null)
        {
            parameterTable.put(newLDR.getLabel(), newLDR.getData());
        }
    }

    /**
     *  Returns a string of all LDR's (label data records.
     *
     *@return    a string of all LDR's (label data records
     */
    public String toString()
    {
        // HashTable in String umwandeln;
        String s = "";
        Enumeration en;

        //int i=0;
        String dummy;

        LabelData dummy2 = null;
        dummy2 = getParameter("TITLE");

        if (dummy2 == null)
        {
            dummy2 = new LabelData();
            dummy2.label = "TITLE";
            dummy2.data = "";
        }

        s = s.concat("##" + dummy2.label + "=" + dummy2.data + "\r\n");

        for (en = parameterTable.keys(); en.hasMoreElements();)
        {
            dummy = (String) en.nextElement();

            if (!dummy.equals("TITLE") && !dummy.equals("END"))
            {
                s = s.concat("##" + dummy + "=" +
                        (String) parameterTable.get(dummy) + "\r\n");
            }

            //i=i+2;
        }

        dummy2 = getParameter("END");

        if (dummy2 == null)
        {
            dummy2 = new LabelData();
            dummy2.label = "END";
            dummy2.data = "";
        }

        s = s.concat("##" + dummy2.label + "=" + dummy2.data + "\r\n");

        return s;
    }

    /**
     *  Gets the <code>double</code> from <code>ld.data</code>.
     *
     *@param  ld                         Description of the Parameter
     *@return                            The double value
     *@exception  NumberFormatException  Description of the Exception
     */
    protected final double getDouble(LabelData ld) throws NumberFormatException
    {
        if (ld == null)
        {
            return 0.0;
        }

        double dummy = 0;

        try
        {
            //alle ',' gleich durch '.' ersetzen, sonst gibts eine NumberFormatException
            dummy = (new Double(ld.getData().replace(',', '.'))).doubleValue();
        }
        catch (NumberFormatException e)
        {
            if (e.toString().indexOf("empty") >= 0)
            {
                return 0.0;
            }
            else
            {
                throw new NumberFormatException("The Label " + ld.getLabel() +
                    " contains not a valid double value: " + ld.getData());
            }
        }

        return dummy;
    }

    /**
     *  Adds a Data Line to the yData- and the xData - puffer
     *
     *@param  s                          The feature to be added to the
     *      XYDATADataLine attribute
     *@exception  NumberFormatException  Description of the Exception
     */
    private final void addXYDATADataLine(String s) throws NumberFormatException
    {
        double xDataDummy = 0;
        double yDataDummy = 0;

        if (!separatorStandardChecked)
        {
            //entscheide ob , als Trennung oder fuer FlieSkommazahl verwendet wird !;-((
            int kommaIndex = s.indexOf(",", 0);
            int pointIndex = s.indexOf(".", 0);
            int spaceIndex = s.indexOf(" ", 0);
            int tabulatorIndex = s.indexOf("\t", 0);

            if (pointIndex == -1)
            {
                //bei schlechtem Standard, kann auch Komma Fuer FlieSkommazahlen
                //vorkommen und darf NICHT als Trennzeichen interpretiert werden
                // Was fuer ein Mist, gibts, aber !!!
                if ((kommaIndex != -1) && (spaceIndex != -1))
                {
                    if (kommaIndex < spaceIndex)
                    {
                        if ((s.charAt(kommaIndex + 1) >= '0') &&
                                (s.charAt(kommaIndex + 1) <= '9'))
                        {
                            //s=s.replace(',','.');
                            standardSeparator = false;
                        }
                    }
                }
                else if ((kommaIndex != -1) && (tabulatorIndex != -1))
                {
                    if (kommaIndex < tabulatorIndex)
                    {
                        if ((s.charAt(kommaIndex + 1) >= '0') &&
                                (s.charAt(kommaIndex + 1) <= '9'))
                        {
                            //s=s.replace(',','.');
                            standardSeparator = false;
                        }
                    }
                }
            }

            separatorStandardChecked = true;
        }

        if (!standardSeparator)
        {
            s = s.replace(',', '.');
        }

        StringTokenizer dataLine = new StringTokenizer(s, " \t,");
        int yDataPerLine = dataLine.countTokens() - 1;
        int dummyIndex = 0;
        String s1 = "";
        String s2 = "";

        yDataPerLine = 0;

        do
        {
            try
            {
                s1 = dataLine.nextToken();
            }
            catch (NoSuchElementException e)
            {
                break;
            }

            int signIndex = s1.indexOf("-", 1);

            //X Wert gar nicht erst auf - ueberpruefen
            String sign = "-";

            if (signIndex != -1)
            {
                int posSignIndex = s1.indexOf("+", 1);

                if ((posSignIndex != -1) && (posSignIndex < signIndex))
                {
                    signIndex = posSignIndex;
                    sign = "+";
                }
            }
            else
            {
                int posSignIndex = s1.indexOf("+", 1);

                if (posSignIndex != -1)
                {
                    signIndex = posSignIndex;
                    sign = "+";
                }
            }

            if (signIndex > 0)
            {
                if (signIndex > 0)
                {
                    //                    println("sign in "+s1+ "  "+signIndex);
                    s2 = s1.substring(0, signIndex);
                    yDataPerLine++;

                    if (yDataPerLine == 1)
                    {
                        try
                        {
                            xDataDummy = (new Double(s2)).doubleValue();
                        }
                        catch (NumberFormatException e)
                        {
                            //                      logger.error(s2);
                            logger.error("The data line \"" + s + "\"" +
                                " is skipped. It contains" +
                                " not a valid X value.");

                            return;
                        }

                        xDataDummy = xDataDummy * xFactor;

                        //                    println("x="+s2);
                    }
                    else
                    {
                        try
                        {
                            yDataDummy = (new Double(s2)).doubleValue();
                        }
                        catch (NumberFormatException e)
                        {
                            logger.error("The data line \"" + s + "\"" +
                                " is skipped. It contains" +
                                " not a valid Y value.");

                            return;
                        }

                        yDataDummy = yDataDummy * yFactor;
                        xData.add(new Double(xDataDummy));
                        yData.add(new Double(yDataDummy));
                        xDataDummy = xDataDummy + deltaX;

                        //                           println("y="+s2);
                    }

                    do
                    {
                        dummyIndex = s1.indexOf(sign, signIndex + 1);

                        if (dummyIndex == -1)
                        {
                            dummyIndex = s1.length();
                        }

                        s2 = s1.substring(signIndex, dummyIndex);
                        yDataPerLine++;

                        try
                        {
                            yDataDummy = (new Double(s2)).doubleValue();
                        }
                        catch (NumberFormatException e)
                        {
                            logger.error("The data line \"" + s + "\"" +
                                " is skipped. It contains" +
                                " not a valid Y value.");

                            return;
                        }

                        yDataDummy = yDataDummy * yFactor;
                        xData.add(new Double(xDataDummy));
                        yData.add(new Double(yDataDummy));
                        xDataDummy = xDataDummy + deltaX;

                        //                           println("y="+s2);
                        signIndex = s1.indexOf(sign, signIndex + 1);
                    }
                    while (dummyIndex != s1.length());
                }
            }
            else
            {
                yDataPerLine++;

                if (yDataPerLine == 1)
                {
                    try
                    {
                        //println(s1);
                        xDataDummy = (new Double(s1)).doubleValue();
                    }
                    catch (NumberFormatException e)
                    {
                        logger.error("The data line \"" + s + "\"" +
                            " is skipped. It contains" +
                            " not a valid X value.");

                        return;
                    }

                    xDataDummy = xDataDummy * xFactor;

                    //println("x: "+xDataDummy);
                }
                else
                {
                    try
                    {
                        yDataDummy = (new Double(s1)).doubleValue();
                    }
                    catch (NumberFormatException e)
                    {
                        logger.error("The data line \"" + s + "\"" +
                            " is skipped. It contains" +
                            " not a valid Y value.");

                        return;
                    }

                    yDataDummy = yDataDummy * yFactor;
                    xData.add(new Double(xDataDummy));
                    yData.add(new Double(yDataDummy));

                    //println(xDataDummy+" "+yDataDummy);
                    xDataDummy = xDataDummy + deltaX;
                }
            }
        }
        while (true);
    }

    /**
     *  Adds a Data Line to the yData- and the xData - puffer
     *
     *@param  s                          The feature to be added to the
     *      XYPAIRSandPEAKTABLEdataLine attribute
     *@exception  NumberFormatException  Description of the Exception
     */
    private final void addXYPAIRSandPEAKTABLEdataLine(String s)
        throws NumberFormatException
    {
        double xDataDummy = 0;
        double yDataDummy = 0;

        if (!separatorStandardChecked)
        {
            //entscheide ob , als Trennung oder fuer FlieSkommazahl verwendet wird !;-((
            int kommaIndex = s.indexOf(",", 0);
            int pointIndex = s.indexOf(".", 0);
            int spaceIndex = s.indexOf(" ", 0);
            int tabulatorIndex = s.indexOf("\t", 0);

            if (pointIndex == -1)
            {
                //bei schlechtem Standard, kann auch Komma Fuer FlieSkommazahlen
                //vorkommen und darf NICHT als Trennzeichen interpretiert werden
                // Was fuer ein Mist, gibts, aber !!!
                if ((kommaIndex != -1) && (spaceIndex != -1))
                {
                    if (kommaIndex < spaceIndex)
                    {
                        if ((s.charAt(kommaIndex + 1) >= '0') &&
                                (s.charAt(kommaIndex + 1) <= '9'))
                        {
                            //s=s.replace(',','.');
                            standardSeparator = false;
                        }
                    }
                }
                else if ((kommaIndex != -1) && (tabulatorIndex != -1))
                {
                    if (kommaIndex < tabulatorIndex)
                    {
                        if ((s.charAt(kommaIndex + 1) >= '0') &&
                                (s.charAt(kommaIndex + 1) <= '9'))
                        {
                            //s=s.replace(',','.');
                            standardSeparator = false;
                        }
                    }
                }
            }

            separatorStandardChecked = true;
        }

        if (!standardSeparator)
        {
            s = s.replace(',', '.');
        }

        StringTokenizer dataLine = new StringTokenizer(s, " \t,");
        String s1 = "";

        try
        {
            s1 = dataLine.nextToken();
        }
        catch (NoSuchElementException e)
        {
            logger.error("The data line \"" + s + "\" is skipped. It contains" +
                " no valid data.");

            return;
        }

        try
        {
            //println("x "+s1);
            xDataDummy = (new Double(s1)).doubleValue();
        }
        catch (NumberFormatException e)
        {
            logger.error("The data line \"" + s + "\" is skipped. It contains" +
                " not a valid X value.");

            return;
        }

        xDataDummy = xDataDummy * xFactor;
        xData.add(new Double(xDataDummy));

        try
        {
            s1 = dataLine.nextToken();
        }
        catch (NoSuchElementException e)
        {
            logger.error(e.getMessage());
        }

        try
        {
            //println("y "+s1);
            yDataDummy = (new Double(s1)).doubleValue();
        }
        catch (NumberFormatException e)
        {
            logger.error("The data line \"" + s + "\" is skipped. It contains" +
                " not a valid Y value.");

            return;
        }

        yDataDummy = yDataDummy * yFactor;
        yData.add(new Double(yDataDummy));
    }

    /**
     *  Decodes the chemical data.
     *
     *@exception  JCAMPException         Description of the Exception
     *@exception  NumberFormatException  Description of the Exception
     */
    private final void decodeChemicalStructure() throws JCAMPException,
        NumberFormatException
    {
    }

    /**
     *  Decide which data type this JCAMP data set contains and decodes this
     *  data. After decoding the data is available with <code>getXData()</code>
     *  and <code>getYData()</code>, if the data contains a spectra.<br>
     *  Chemical structures are not supported, until now !
     *
     *@param  decodeDataNow              if <code>true</code> the data type is
     *      estimated and the data is decoded. if <code>false</code> only the
     *      data type is estimated.
     *@exception  JCAMPException         Description of the Exception
     *@exception  NumberFormatException  Description of the Exception
     */
    private void decodeData() throws JCAMPException, NumberFormatException
    {
        if (dataDecoded)
        {
            return;
        }

        LabelData set;
        int end = 0;
        String s1 = "";

        //which data type ?
        boolean xypairsState = true;
        boolean xydataState = true;
        set = getParameter("XYPAIRS");

        if (set == null)
        {
            xypairsState = false;
        }

        if (xypairsState)
        {
            end = set.getData().indexOf("\n", 0);

            if (end == -1)
            {
                throw new JCAMPException(
                    "Carriage return after \"=\" is not allowed");
            }

            s1 = set.getData().substring(0, end).trim();

            if (!s1.equals("(XY..XY)"))
            {
                xypairsState = false;
            }
            else
            {
                dataType = XYPAIRS;
                decodeXYPAIRSandPEAKTABLE();
                dataDecoded = true;

                return;
            }
        }

        if (!xypairsState)
        {
            set = getParameter("XYDATA");

            if (set == null)
            {
                xydataState = false;
            }

            if (xydataState)
            {
                end = set.getData().indexOf("\n", 0);

                if (end == -1)
                {
                    throw new JCAMPException(
                        "Carriage return after \"=\" is not allowed");
                }

                s1 = set.getData().substring(0, end).trim();

                if (!s1.equals("(X++(Y..Y))"))
                {
                    xydataState = false;
                }
                else
                {
                    dataType = XYDATA_X_YY;
                    decodeXYDATA();
                    dataDecoded = true;

                    return;
                }
            }
        }

        /*
         *  if(dataType==XYPAIRS || dataType==XYDATA_X_YY){
         *  if(decodeDataNow){
         *  decodeXYDATAorXYPAIRS();
         *  }
         *  dataDecoded=true;
         *  return;
         *  }
         */
        boolean chemicalStructureState = true;
        set = getParameter("MOLFORM");

        if (set == null)
        {
            chemicalStructureState = false;
        }

        set = getParameter("ATOMLIST");

        if (set == null)
        {
            chemicalStructureState = false;
        }

        set = getParameter("BONDLIST");

        if (set == null)
        {
            chemicalStructureState = false;
        }

        if (chemicalStructureState)
        {
            dataType = CHEMICAL_STRUCTURE;

            //if(state)decodeChemicalStructure();
            //dataDecoded=true;
            return;
        }

        set = getParameter("XYPOINTS");

        if (set != null)
        {
            throw new JCAMPException("Data XYPOINTS is not supported ");

            //dataType=XYPOINTS;
            //dataDecoded=true;
            //return;
        }

        set = getParameter("PEAK TABLE");

        if (set != null)
        {
            end = set.getData().indexOf("\n", 0);

            if (end == -1)
            {
                throw new JCAMPException(
                    "Carriage return after \"=\" is not allowed");
            }

            s1 = set.getData().substring(0, end).trim();

            if (s1.equals("(XY..XY)"))
            {
                dataType = PEAK_TABLE;
                decodeXYPAIRSandPEAKTABLE();
                dataDecoded = true;

                return;
            }
        }

        set = getParameter("PEAK ASSIGNMENTS");

        if (set != null)
        {
            throw new JCAMPException("Data PEAK ASSIGNMENTS is not supported ");

            //dataType=PEAK_ASSIGNMENTS;
            //dataDecoded=true;
            //return;
        }

        set = getParameter("RADATA");

        if (set != null)
        {
            throw new JCAMPException("Data RADATA is not supported ");

            //dataType=RADATA;
            //dataDecoded=true;
            //return;
        }

        set = getParameter("DATA TYPE");

        if (set != null)
        {
            if (set.getData().trim().equals("LINK"))
            {
                dataType = LINK;

                //dataDecoded=true;
                return;
            }
        }

        //irgend ein anderer Datentyp
        throw new JCAMPException("Data type not supported ");
    }

    /*
     *  -------------------------------------------------------------------------*
     *  private methods
     *  -------------------------------------------------------------------------
     */

    /**
     *  Decodes XYDATA. Only the uncompressed ASDF format is supported. The
     *  compressed AFFN format is not supported.
     *
     *@exception  JCAMPException         Description of the Exception
     *@exception  NumberFormatException  Description of the Exception
     */
    private void decodeXYDATA() throws JCAMPException, NumberFormatException
    {
        LabelData set = new LabelData();
        int begin;
        int end = 0;
        String s1 = "";
        String s2 = "";

        set = getParameter("DELTAX");

        if (set == null)
        {
            logger.warn("The label DELTAX is missing. Now its calculated !;-)");
            calculatedDeltaXneeded = true;
        }

        deltaX = getDouble(set);

        set = getParameter("LASTX");

        if (set == null)
        {
            String add = "";

            if (!calculatedDeltaXneeded)
            {
                add = ". The label DELTAX is missing and " +
                    "can not be calculated";
            }

            throw new JCAMPException("The label LASTX is missing" + add);
        }

        lastX = getDouble(set);

        set = getParameter("FIRSTX");

        if (set == null)
        {
            String add = "";

            if (!calculatedDeltaXneeded)
            {
                add = ". The label DELTAX is missing and " +
                    "can not be calculated";
            }

            throw new JCAMPException("The label FIRSTX is missing" + add);
        }

        firstX = getDouble(set);

        set = getParameter("NPOINTS");

        if (set == null)
        {
            String add = "";

            if (!calculatedDeltaXneeded)
            {
                add = ". The label NPOINTS is missing and DELTAX" +
                    "can not be calculated";
                throw new JCAMPException("The label FIRSTX is missing" + add);
            }

            logger.warn("The label NPOINTS is missing. Now its estimated");
            estimatedNPointsNeeded = true;
        }

        nPoints = getDouble(set);

        //Berechnung von deltaX;
        if (calculatedDeltaXneeded)
        {
            deltaX = (lastX - firstX) / (nPoints - 1);
        }
        else
        {
            double dummy = 0;

            if (!estimatedNPointsNeeded && (nPoints != 0))
            {
                //einfach nochmals zur Kontrolle von deltaX. Eigentlich unnoetig !
                dummy = (lastX - firstX) / (nPoints - 1);

                if (Math.abs(dummy - deltaX) > DELTAX_DIFFERENCE_WARNING_VALUE)
                {
                    logger.warn("The calculated DELTAX=" + dummy +
                        " (original DELTAX=" + deltaX +
                        ") shows a difference above " +
                        DELTAX_DIFFERENCE_WARNING_VALUE);
                }
            }
        }

        set = getParameter("XFACTOR");

        if (set == null)
        {
            throw new JCAMPException("The label XFACTOR is missing");
        }

        xFactor = getDouble(set);

        set = getParameter("YFACTOR");

        if (set == null)
        {
            throw new JCAMPException("The label YFACTOR is missing");
        }

        yFactor = getDouble(set);

        //beginne Dekodierung
        set = getParameter("XYPAIRS");

        //if (set == null)
        //{
        //}
        //    if(dataType==XYPAIRS){
        //      end=set.getData().indexOf("\n",0);
        //      if (end==-1)throw new JCAMPException("Carriage return after \"=\" is not allowed");
        //      s1=set.getData().substring(0, end).trim();
        //    }
        //    else
        if (dataType == XYDATA_X_YY)
        {
            set = getParameter("XYDATA");

            //            if (set == null)
            //            {
            //            }
            end = set.getData().indexOf("\n", 0);

            if (end == -1)
            {
                throw new JCAMPException("Carriage return" +
                    " after \"=\" is not allowed");
            }

            s1 = set.getData().substring(0, end).trim();
        }

        s1 = set.getData().substring(end + 1,
                set.getData().indexOf("\n", end + 1));

        //s1=s1.trim();
        //hier findet keine Unterscheidung zwischen AFFN (ungepackt) oder ASDF (gepackt) statt
        //Pseudo-digits or ASDF forms
        //ASCII digits          0 1 2 3 4 5 6 7 8 9
        //Positive SQZ digits   @ A B C D E F G H I
        //Negative SQZ digits     a b c d e f g h i
        //Positive DIF digits   % J K L M N O P Q R
        //Negative DIF digits     j k l m n o p q r
        //Positive DUP digits     S T U V W X Y Z s
        StringTokenizer isAFFN = new StringTokenizer(s1,
                "@ABCDEFGHIabcdefghi%JKLMNOPQRjklmnopqrSTUVWXYZs");

        try
        {
            s2 = isAFFN.nextToken();

            if (s1.equals(s2))
            {
                //ist ein Token enthalten, so ist s1 ungleich s2
                //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                //nur AFFN !!!
                //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                //wieviele Datenpunkte sind es denn etwa ?
                //                StringTokenizer dataValue = new StringTokenizer(s1, " \t");
                //ruhig groS waehlen, da sie nachher eh geloescht werden.
                //            int dataPerLine=100;
                //Anzahl der Datenwerte ist NPOINTS/countTokens
                //dataPerLine=Math.max(dataValue.countTokens(), 100);
                //            yData= new Vector((int)nPoints, dataPerLine);
                //            xData= new Vector((int)nPoints, dataPerLine);
                xData = new LinkedList();
                yData = new LinkedList();

                begin = 0;
                begin = set.getData().indexOf("\n", begin);
                begin++;

                do
                {
                    end = set.getData().indexOf("\n", begin);

                    if (set.getData().indexOf(";", begin) != -1)
                    {
                        end = Math.min(end, set.getData().indexOf(";", begin));
                    }

                    if (end == -1)
                    {
                        break;
                    }

                    s1 = set.getData().substring(begin, end).trim();

                    //dataLine
                    //println("s1:"+parameterSaetze[1].substring(begin,end));
                    addXYDATADataLine(s1);
                    begin = end + 1;
                }
                while (true);

                sendDataToDoubleArray();

                return;
            }
            else
            {
                throw new JCAMPException(
                    "Compressed data in ASDF format is not supported ");
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error(e.getMessage());
        }
    }

    /**
     *  Decodes XYPAIRS.
     *
     *@exception  JCAMPException         Description of the Exception
     *@exception  NumberFormatException  Description of the Exception
     */
    private void decodeXYPAIRSandPEAKTABLE() throws JCAMPException,
        NumberFormatException
    {
        LabelData set = new LabelData();
        int begin;
        int end = 0;
        String s1 = "";

        if (dataType == PEAK_TABLE)
        {
            xFactor = 1.0;
            yFactor = 1.0;
            set = getParameter("PEAK TABLE");

            //            if (set == null)
            //            {
            //            }
        }
        else
        {
            set = getParameter("DELTAX");

            if (set == null)
            {
                calculatedDeltaXneeded = true;
            }

            deltaX = getDouble(set);

            set = getParameter("LASTX");

            if (set == null)
            {
                String add = "";

                if (!calculatedDeltaXneeded)
                {
                    add = ". The label DELTAX is missing and " +
                        "can not be calculated";
                }

                throw new JCAMPException("The label LASTX is missing" + add);
            }

            lastX = getDouble(set);

            set = getParameter("FIRSTX");

            if (set == null)
            {
                String add = "";

                if (!calculatedDeltaXneeded)
                {
                    add = ". The label DELTAX is missing and " +
                        "can not be calculated";
                }

                throw new JCAMPException("The label FIRSTX is missing" + add);
            }

            firstX = getDouble(set);

            set = getParameter("NPOINTS");

            if (set == null)
            {
                String add = "";

                if (!calculatedDeltaXneeded)
                {
                    add = ". The label NPOINTS is missing and DELTAX" +
                        "can not be calculated";
                    throw new JCAMPException("The label FIRSTX is missing" +
                        add);
                }

                logger.warn("The label NPOINTS is missing. Now its estimated");
                estimatedNPointsNeeded = true;
            }

            nPoints = getDouble(set);

            //Berechnung von deltaX;
            if (calculatedDeltaXneeded)
            {
                deltaX = (lastX - firstX) / (nPoints - 1);
            }
            else
            {
                double dummy = 0;

                if (!estimatedNPointsNeeded && (nPoints != 0))
                {
                    //einfach nochmals zur Kontrolle von deltaX. Eigentlich unnoetig !
                    dummy = (lastX - firstX) / (nPoints - 1);

                    if (Math.abs(dummy - deltaX) >
                            DELTAX_DIFFERENCE_WARNING_VALUE)
                    {
                        logger.warn("The calculated DELTAX=" + dummy +
                            " (original DELTAX=" + deltaX +
                            ") shows a difference above " +
                            DELTAX_DIFFERENCE_WARNING_VALUE);
                    }
                }
            }

            set = getParameter("XFACTOR");

            if (set == null)
            {
                throw new JCAMPException("The label XFACTOR is missing");
            }

            xFactor = getDouble(set);

            set = getParameter("YFACTOR");

            if (set == null)
            {
                throw new JCAMPException("The label YFACTOR is missing");
            }

            yFactor = getDouble(set);

            set = getParameter("XYPAIRS");

            //            if (set == null)
            //            {
            //            }
        }

        end = set.getData().indexOf("\n", 0);

        if (end == -1)
        {
            throw new JCAMPException("Carriage return" +
                " after \"=\" is not allowed");
        }

        s1 = set.getData().substring(0, end).trim();
        s1 = set.getData().substring(end + 1,
                set.getData().indexOf("\n", end + 1));

        //        StringTokenizer dataValue = new StringTokenizer(s1, " \t");
        xData = new LinkedList();
        yData = new LinkedList();

        begin = 0;
        begin = set.getData().indexOf("\n", begin);
        begin++;

        do
        {
            end = set.getData().indexOf("\n", begin);

            if (set.getData().indexOf(";", begin) != -1)
            {
                end = Math.min(end, set.getData().indexOf(";", begin));
            }

            if (end == -1)
            {
                break;
            }

            s1 = set.getData().substring(begin, end).trim();

            //dataLine
            //println("s1:"+parameterSaetze[1].substring(begin,end));
            addXYPAIRSandPEAKTABLEdataLine(s1);
            begin = end + 1;
        }
        while (true);

        sendDataToDoubleArray();
    }

    /**
     *  Interprets the data in <code>s</code>. The interpreted LDR's (label data
     *  record's) are stored in the <code>parameterTable</code>.
     *
     *@param  s                      the JCAMP data to decode
     *@exception  IOException        by trouble with the <code>file</code>
     *@exception  JCAMPException     Description of the Exception
     *@see                           #parameterTable
     *@see                           #decodeData()
     */
    private void interpretLDRs(String s) throws JCAMPException, IOException
    {
        //String s="";
        int begin_s = 0;
        int end_s = 0;
        int numberOfDataset = 0;
        String s1 = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        int begin = 0;
        int end = 0;

        //s=getStringOfFile(file);
        //s=s.replace(',','.');
        //println(s);
        do
        {
            //hole den ersten Datensatz
            s1 = "";
            begin_s = s.indexOf("##", begin_s);

            if (begin_s == -1)
            {
                if (numberOfDataset == 0)
                {
                    throw new IOException("File contains no data");

                    //Wenn kein ## enthalten ist, dann abbrechen, evtl. falsches Dateiformat
                }
                else
                {
                    break;
                }
            }

            ++numberOfDataset;
            begin_s = begin_s + 2;
            end_s = s.indexOf("##", begin_s);

            if (end_s == -1)
            {
                end_s = s.length();
            }

            //                println("begin "+begin_s+" end "+end_s);
            s1 = (new String(s1 + s.substring(begin_s, end_s)));

            //.trim();
            begin_s = end_s;

            //Erstelle den Datensatz ohne Kommentare
            begin = 0;
            s2 = "";

            for (;;)
            {
                end = s1.indexOf("$$", begin);

                //ist ein Kommentar enthalten ?
                if (end == -1)
                {
                    s2 = s2 + s1.substring(begin, s1.length());

                    break;
                }

                //Nein, dann verlasse die Schleife
                //denn nichtkommentierten Teil dieser Zeile an s4 anhängen
                s2 = s2 + ' ' + s1.substring(begin, end);

                //.trim();
                begin = end + 2;

                //das "Short-Comment"-Token nicht mitkopieren
                begin = s1.indexOf("\n", begin);

                //"Short-Comment" nur bis zum Ende der Zeile betrachten
                if (begin == -1)
                {
                    break;
                }
            }

            //                println("unkommentiert: \""+s2+"\"\n");
            //"Data Label Name", also Variablenname bestimmen
            //s2=s2.trim();
            StringTokenizer dataLabelTerminator = new StringTokenizer(s2, "=");

            //Sucht nach = oder allen Zeichen die hier angegeben werden, leider
            //ist nur ein Token zulässig, also ## geht nicht, da er 2mal # findet !
            try
            {
                s3 = dataLabelTerminator.nextToken();

                //                        println("Data Label Name: \"" + s3 +"\" "+s3.length()+"\"" + s2 +"\" "+s2.length());
                if (s2.length() == s3.length())
                {
                    throw new JCAMPException("in \2" + s2 +
                        "\"\r\nshould be \"" + s2 + "=\"");
                }
                else
                {
                    s3 = s3.trim();
                }
            }
            catch (NoSuchElementException e)
            {
                throw new JCAMPException("in \2" + s2 + "\"\r\nshould be \"" +
                    s2 + "=\"");
            }

            //"Data Set", also Variablenwert bestimmen
            begin = s2.indexOf("=");
            end = s2.length();
            s4 = s2.substring(++begin, end);
            s4 = s4.trim();

            //s4=s4.replace('\n',' ');
            //s4=s4.replace('\r',' ')
            //                println("Data Set: \"" + s4 +"\"\n");
            //                println("----------------------------------------------------------------------------------------------------\n");
            if (s3.compareTo("END") != 0)
            {
                parameterTable.put(s3, s4);

                //list.add(new LabelData(s3,s4));
            }
        }
        //while(dataLabelFlag.hasMoreTokens());
        while (true);
    }

    /**
     *  Sends the data from the vectors to the X and Y array.
     *
     *@see    #getXData()
     *@see    #getYData()
     */
    private void sendDataToDoubleArray()
    {
        xDoubleData = new double[xData.size()];

        ListIterator iterator = xData.listIterator();
        Double next = null;
        int counter = 0;

        while (iterator.hasNext())
        {
            next = (Double) iterator.next();
            xDoubleData[counter] = next.doubleValue();
            counter++;
        }

        yDoubleData = new double[yData.size()];
        iterator = yData.listIterator();
        counter = 0;

        while (iterator.hasNext())
        {
            next = (Double) iterator.next();
            yDoubleData[counter] = next.doubleValue();
            counter++;
        }

        xData = yData = null;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
