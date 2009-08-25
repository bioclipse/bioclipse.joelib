///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ResultFactory.java,v $
//  Purpose:  Factory class to get loader/writer classes.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2006/02/22 02:18:22 $
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
package joelib2.feature;

import joelib2.data.AbstractDataHolder;

import joelib2.feature.result.StringResult;

import joelib2.io.types.cml.ResultCMLProperties;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.types.BasicStringPattern;
import joelib2.util.types.StringPattern;
import joelib2.util.types.StringString;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Category;


/**
 *  Factory class to get descriptor results and faciliate the parsing of descriptor entries.
 * The definition file can be defined in the
 * <tt>joelib2.data.ResultFactory.resourceFile</tt> property in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * Let's have a look at a <tt>knownResults.txt</tt> example file:
 * <blockquote><pre>
 * $JOELIB_RESULT$ joelib2.feature.result.IntResult
 * #
 * # PETRA descriptors
 * #
 * E_CHARGE
 * E_DELTAHF
 * E_HASH
 * E_POLARIZABILITY
 * #
 * # Molconn Z 350
 * #
 * id
 * nvx
 * nrings
 * ncirc
 * nelem
 * $REGEXP$ nas\p{Upper}\p{Lower}*
 * $REGEXP$ nd\d
 * $REGEXP$ ne\d+
 * </pre></blockquote>
 * This means all descriptors of the type nd1, nd2, nd3, ..., nd9 are {@link joelib2.feature.result.IntResult}
 * descriptors. Also the E_CHARGE, E_DELTAHF, E_HASH, E_POLARIZABILITY, id, nvx, ... descriptors.<br>
 * For a detailed description for regular expressions patterns have a look at the
 * {@link java.util.regex.Pattern} class.
 *
 * <p>
 * Speed optimization (for external descriptors):
 * <ul>
 * <li> Use as much explicit descriptor name entries as possible. They will be stored in a look up
 * table ({@link java.util.Hashtable})  with fast access predicates.
 * <li> Use regular expressions only if there can be a lot of descriptors described with these expressions.
 * Still, there will be all regular expressions checked, until the first one, which matches the given descriptor,
 * will be found. But this will be always much more expensier than getting descriptor result
 * (representation classes for descriptor values) directly from the {@link java.util.Hashtable}.
 * </ul>
 * External descriptors will be descriptors, which are known in JOELib, but can not be calculated. All
 * internal descriptor result classes will be always stored explicitly.
 *
 * <p>
 * Default:<br>
 * joelib2.feature.ResultFactory.resourceFile=<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib/src/joelib2/data/plain/knownResults.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup">joelib2/data/plain/knownResults.txt</a>
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2006/02/22 02:18:22 $
 * @see wsi.ra.tool.BasicPropertyHolder
 * @see wsi.ra.tool.BasicResourceLoader
 */
public class ResultFactory extends AbstractDataHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(ResultFactory.class
            .getName());
    private final static String RESOURCE =
        "joelib2/data/plain/knownResults.txt";
    private final static String IDENTIFIER = "$JOELIB_RESULT$";
    private final static String REGEXP = "$REGEXP$";
    private static ResultFactory instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    private String actRep;
    private List<StringPattern> regExp;

    private Map<String, String> repr;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the ResultFactory.
     */
    private ResultFactory()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", RESOURCE);

        repr = new Hashtable<String, String>(100, 50);
        regExp = new Vector<StringPattern>(30);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public static synchronized ResultFactory instance()
    {
        if (instance == null)
        {
            instance = new ResultFactory();
        }

        return instance;
    }

    /**
     *  Gets the descResult attribute of the ResultFactory class
     *
     * @param featureName                 Description of the Parameter
     * @return                         The descResult value
     * @exception FeatureException  Description of the Exception
     */
    public FeatureResult getFeatureResult(String featureName)
        throws FeatureException
    {
        if (!initialized)
        {
            init();
        }

        String resultRepr = null;
        FeatureResult featureResult = null;
        BasicFeatureInfo featureInfo = FeatureHelper.instance().getFeatureInfo(
                featureName);

        //System.out.println("INFOTIP:::"+featureInfo+" "+resultRepr);
        if (featureInfo == null)
        {
            resultRepr = (String) repr.get(featureName);
            
            //System.out.println("TIP:::"+featureName+" "+resultRepr);
            if (resultRepr == null)
            {
                // o.k., now there is no result type defined for this descriptor
                // let's try to guess one
                resultRepr = guessFeatureResult(featureName);
            }

            if (resultRepr != null)
            {
                featureInfo = new BasicFeatureInfo(featureName,
                        BasicFeatureInfo.TYPE_UNKNOWN, "", "", null,
                        resultRepr);
            }

            //          throw new DescriptorException("Descriptor '"+name+"' is not defined");
        }
        else
        {
            resultRepr = featureInfo.getResult();
        }

        if (resultRepr != null)
        {
            try
            {
                // works only for construtor without arguments
                featureResult = (FeatureResult) Class.forName(resultRepr)
                                                     .newInstance();

                // for descriptor with arguments
                //      Class        cls          = Class.forName(resultRepr);
                //      Constructor  constructor[]  = cls.getDeclaredConstructors();
                //      for (int i = 0; i < constructor.length; i++)
                //      {
                //        Class[]  params  = constructor[i].getParameterTypes();
                //        if (params.length == 1)
                //        {
                //          Object[]  inputs  = {descInfo};
                //          descResult = (DescResult) constructor[i].newInstance(inputs);
                //        }
                //      }
            }
            catch (ClassNotFoundException ex)
            {
                throw new FeatureException(featureInfo.getResult() +
                    " not found.");
            }
            catch (InstantiationException ex)
            {
                throw new FeatureException(featureInfo.getResult() +
                    " can not be instantiated.");
            }
            catch (IllegalAccessException ex)
            {
                throw new FeatureException(featureInfo.getResult() +
                    " can't be accessed.");
            }

            //        catch (InvocationTargetException ex)
            //        {
            //            ex.printStackTrace();
            //            throw new DescriptorException("InvocationTargetException.");
            //        }
            if (featureResult == null)
            {
                throw new FeatureException("FeatureResult class " + resultRepr +
                    " does'nt exist.");
            }
        }

        return featureResult;
    }

    /**
     *  Description of the Method
     *
     * @param buffer  Description of the Parameter
     */
    public void parseLine(String buffer)
    {
        // skip command lines
        String trimmed = buffer.trim();

        if (!trimmed.equals("") && (buffer.charAt(0) != '#'))
        {
            if (trimmed.charAt(0) == '$')
            {
                int index = trimmed.indexOf(IDENTIFIER);

                if (index != -1)
                {
                    actRep = trimmed.substring(index + 1 + IDENTIFIER.length())
                                    .trim();
                }
                else
                {
                    index = trimmed.indexOf(REGEXP);

                    if (index != -1)
                    {
                        String tmp = trimmed.substring(index + 1 +
                                REGEXP.length()).trim();
                        regExp.add(new BasicStringPattern(actRep,
                                Pattern.compile(tmp)));
                    }
                }
            }
            else if (actRep != null)
            {
                //              System.out.println(buffer.trim()+" is of type "+actRep);
                repr.put(trimmed, actRep);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param mol       Description of the Parameter
     * @param descName  Description of the Parameter
     * @param data      Description of the Parameter
     * @return          Description of the Return Value
     */
    public PairData parsePairData(Molecule mol, String descName,
        PairData pairData)
    {
        // check if descriptor has already been calculated
        // and if it's not a unparsed descriptor entry in
        // the StringResult
        // If the StringResult descriptor contains CML element attributes
        // copy these attributes
        if ((pairData.getKeyValue() instanceof FeatureResult) &&
                !(pairData.getKeyValue() instanceof StringResult))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Feature '" + descName + "' is (parsed) PairData");
            }
        }
        else
        {
            FeatureResult result = null;

            try
            {
                result = getFeatureResult(descName);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            if (result == null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("No feature result found for '" + descName +
                        "'. Suppose String value.");
                }
            }
            else
            {
                // StringResult descriptor
                // convert StringReult descriptor to Int, Double, ...
                // descriptor, if data type is known
                if (parseData(result, mol, pairData, descName))
                {
                    // replace old data
                    mol.deleteData(descName);
                    mol.addData((BasicPairData) result);
                }

                pairData = (BasicPairData) result;
            }
        }

        return pairData;
    }

    /**
     * Get descriptor result ype by using regular expressions.
     *
     * @param descName
     * @return String
     */
    protected String guessFeatureResult(String descName)
    {
        BasicStringPattern reg;
        Matcher matcher;

        String match = null;

        for (int i = 0; i < regExp.size(); i++)
        {
            reg = (BasicStringPattern) regExp.get(i);
            matcher = reg.pattern.matcher(descName);

            //System.out.println(descName+" matches "+reg.pattern.pattern()+"="+matcher.matches());
            if (matcher.matches())
            {
                ///System.out.println(reg.s+" guessed from "+descName+" with "+reg.p.pattern());
                match = reg.string;

                break;
            }
        }

        return match;
    }

    /**
     * @param pairData
     */
    private boolean parseData(FeatureResult result, Molecule mol,
        PairData pairData, String descName)
    {
        boolean allFine = true;

        if ((pairData.getKeyValue() instanceof StringResult))
        {
            allFine = parseStringResult(result, mol, pairData, descName);
        }
        else
        {
            allFine = parseString(result, mol, pairData, descName);
        }

        return allFine;
    }

    /**
     * @param mol
     * @param pairData
     * @param descName
     * @return
     */
    private boolean parseString(FeatureResult result, Molecule mol,
        PairData pairData, String descName)
    {
        boolean parsed = false;

        if (logger.isDebugEnabled())
        {
            logger.debug("Feature '" + descName + "'is (unparsed) String");
            logger.debug("Feature '" + descName + "'will forced to be " +
                result.getClass().getName());
            logger.debug(descName + "=" + pairData);
        }

        try
        {
            //System.out.println("INPUT:"+IOTypeHolder.instance().getIOType("CML")+" data:"+ pairData);
            //System.out.println("Forceed to be:"+result.getClass().getName());
            if (!result.fromPairData(mol.getInputType(), pairData))
            {
                logger.error("Feature '" + descName + "' could not be parsed.");
            }

            //result.fromPairData(IOTypeHolder.instance().getIOType("CML"), pairData);
            parsed = true;
        }
        catch (NumberFormatException ex)
        {
            logger.error(ex.toString());
            ex.printStackTrace();
            logger.error("At feature '" + descName + "' in molecule: " +
                mol.getTitle());

            parsed = false;
        }

        return parsed;
    }

    /**
     * @param mol
     * @param pairData
     * @param descName
     */
    private boolean parseStringResult(FeatureResult result, Molecule mol,
        PairData pairData, String descName)
    {
        StringResult sResult = ((StringResult) pairData.getKeyValue());

        if (logger.isDebugEnabled())
        {
            logger.debug("Feature '" + descName +
                "'is (unparsed) StringResult");
            logger.debug("Feature '" + descName + "'will forced to be " +
                result.getClass().getName());
            logger.debug(descName + "=" + sResult.value);
        }

        pairData.setKeyValue(sResult.value);

        boolean parsed = false;

        try
        {
            //System.out.println("INPUT:"+IOTypeHolder.instance().getIOType("CML")+" data:"+ pairData);
            if (!result.fromPairData(mol.getInputType(), pairData))
            {
                logger.error("Feature '" + descName +
                    "' could not be parsed for " + mol.getTitle());
            }

            //result.fromPairData(IOTypeHolder.instance().getIOType("CML"), pairData);
            // copy CML element attributes
            if ((result instanceof ResultCMLProperties) &&
                    (sResult instanceof ResultCMLProperties))
            {
                Enumeration enumeration = sResult.getCMLProperties();

                if (enumeration != null)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Copy CML attribute properties");
                    }

                    StringString cmlProp;
                    ResultCMLProperties cmlProps = ((ResultCMLProperties) result);

                    while (enumeration.hasMoreElements())
                    {
                        cmlProp = (StringString) enumeration.nextElement();
                        cmlProps.addCMLProperty(cmlProp);
                    }
                }

                //                                              else
                //                                              {
                //                                                      if (logger.isDebugEnabled())
                //                                                              logger.debug("No CML attribute properties defined");
                //                                              }
            }

            //                                  else
            //                                  {
            //                                          if (logger.isDebugEnabled())
            //                                                  logger.debug("No CML attribute property acceptor");
            //                                  }
            parsed = true;
        }
        catch (Exception ex)
        {
            logger.error("Parsing error: " + ex.getMessage());

            //ex.printStackTrace();
            logger.error(" at feature '" + descName + "' in molecule: " +
                mol.getTitle());

            parsed = false;
        }

        return parsed;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
