///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: FeatureHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.14 $
//            $Date: 2006/07/24 22:29:15 $
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

import joelib2.feature.types.atomlabel.AtomType;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;

import joelib2.util.iterator.PairDataIterator;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Some methods to faciliate the work with descriptors.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.14 $, $Date: 2006/07/24 22:29:15 $
 */
public final class FeatureHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(FeatureHelper.class
            .getName());
    private static FeatureHelper instance;
    private static final int EXP_DESC_NUM = 100;
    public static final String COMMENT_IDENTIFIER = "comment";
    public static final String COORDS_3D_X_IDENTIFIER = "coordinates3Dx";
    public static final String COORDS_3D_Y_IDENTIFIER = "coordinates3Dy";
    public static final String COORDS_3D_Z_IDENTIFIER = "coordinates3Dz";
    public static final String COORDS_2D_X_IDENTIFIER = "coordinates2Dx";
    public static final String COORDS_2D_Y_IDENTIFIER = "coordinates2Dy";
    public static final String VERSION_IDENTIFIER = "versionID";

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean addDesc = true;

    private Hashtable featureHolder;
    private BasicPropertyHolder propertyHolder;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Initializes the descriptor helper factory.
     */
    private FeatureHelper()
    {
        propertyHolder = BasicPropertyHolder.instance();

        // initialize descriptor holder
        featureHolder = new Hashtable(EXP_DESC_NUM);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Getting descriptor values from a molecule and try to calculate a descriptor
     * value if it is not already available. This method should NOT be used for getting
     * common {@link JOEGenericData} elements of molecules.
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
     * @param  mol           The molecule
     * @param  descName      The name of the descriptor
     * @param  add If <tt>true</tt> the descriptor value will be added to the molecule
     * @return     The descriptor result
     * @see #featureFrom(Molecule, String)
     * @see #featureFrom(Molecule, String, DescResult)
     * @see Molecule#getData(JOEDataType)
     * @see Molecule#getData(String)
     * @see Molecule#genericDataIterator()
     * @see Molecule#addData(JOEGenericData)
     * @see Molecule#addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureResult
     * @see joelib2.molecule.BasicDataHolder#getData(String, boolean)
     */
    public static synchronized FeatureResult featureFrom(Molecule mol,
        String descName, boolean add) throws FeatureException
    {
        // it's more efficient to solve this in the calculation calling method
        FeatureResult result = null;

        //ResultFactory.instance().getDescResult(descName);
        return featureFrom(mol, descName, result, add);
    }

    /**
     * Getting descriptor values from a molecule and try to calculate a descriptor
     * value if it is not already available. This method should NOT be used for getting
     * common {@link JOEGenericData} elements of molecules.
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
     * The given descriptor result is only used if this descriptor exists not already.
     * If this descriptor exists already, then this object is returned without cloning.
     * So you should create your own clone if you plan to modify this object.
     *
     * @param  mol           The molecule
     * @param  featureName      The name of the descriptor
     * @param  featureResult    The descriptor result
     * @param  addIfNotExist If <tt>true</tt> the descriptor value will be added to the molecule
     * @return     The descriptor result
     * @see #featureFrom(Molecule, String)
     * @see #featureFrom(Molecule, String, DescResult)
     * @see Molecule#getData(JOEDataType)
     * @see Molecule#getData(String)
     * @see Molecule#genericDataIterator()
     * @see Molecule#addData(JOEGenericData)
     * @see Molecule#addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureResult
     * @see joelib2.molecule.BasicDataHolder#getData(String, boolean)
     */
    public static synchronized FeatureResult featureFrom(Molecule mol,
        String featureName, FeatureResult featureResult, boolean add)
        throws FeatureException
    {
        FeatureResult actResult = null;

        // get descriptor or calculate if not already available
        //    System.out.println(mol);
        //    System.out.println(descName+": "+mol.hasData(descName));
        if (mol.hasData(featureName))
        {
            // get calculated descriptor
            BasicPairData data = (BasicPairData) mol.getData(featureName, true);

            //      System.out.println("has '"+descName+"' "+data);
            //type checking by class name
            //      if(data.getValue().getClass().getName().equals(descResult.getClass().getName()))
            //      {
            // not really correct
            // more correct would be:
            // ((DescResult) data.getValue()).clone(descResult);
            Object obj = data.getKeyValue();

            if (!(obj instanceof FeatureResult))
            {
                logger.error("Result '" + featureName + "' must implement " +
                    FeatureResult.class.getName() + ". " +
                    obj.getClass().getName() + " does not.");
            }
            else
            {
                actResult = (FeatureResult) data.getKeyValue();
            }

            //          System.out.println(obj.getClass().getName()+":"+obj);
            //          System.out.println("descResult="+descResult);
            //          System.out.println("data="+data);
            //          System.out.println(data.getValue().getClass().getName()+" compared to ");
            //          System.out.println(descResult.getClass().getName());
            //      }
        }
        else
        {
            // calculate distance matrix
            Feature descriptor;

            try
            {
                descriptor = FeatureFactory.getFeature(featureName);

                if (descriptor == null)
                {
                    logger.error(
                        "Can not find calculation class for feature: '" +
                        featureName + "'.");
                }
                else
                {
                    if (featureResult == null)
                    {
                        actResult = ResultFactory.instance().getFeatureResult(
                                featureName);
                    }
                    else
                    {
                        actResult = featureResult;
                    }

                    actResult = descriptor.calculate(mol, actResult);

                    if (actResult == null)
                    {
                        logger.error("Feature '" + featureName +
                            "' can not be calculated for " + mol.getTitle());
                    }
                    else if (add)
                    {
                        // add calculated descriptor to molecule (if it do not exist)
                        BasicPairData pairData = (BasicPairData) actResult; // = new PairData();
                        pairData.setKey(featureName);
                        pairData.setKeyValue(actResult);
                        mol.addData(pairData);
                    }
                }
            }
            catch (FeatureException ex)
            {
                // more user friendly
                //              ex.printStackTrace();
                //              return null;
                // more developer like
                throw ex;
            }
        }

        return actResult;
    }

    /**
     * Faciliates the generation of a descriptor information.
     *
     * @param name             the name of the descriptor
     * @param descClass        the descriptor calculation class
     * @param type             the descriptor type
     * @param dimension        the descriptor dimension, which is needed for calculation
     * @param init   the initialization representation (DEPRECATED)
     * @param result           the name of the result class
     * @return DescriptorInfo  the descriptor information
     * @todo remove initialization
     */
    public static BasicFeatureInfo generateFeatureInfo(Class descClass,
        String type, String init, String result)
    {
        String repr = descClass.getName();
        int index = repr.lastIndexOf(".");
        String docs = "docs/feature/" + repr.substring(index + 1);

        return new BasicFeatureInfo(repr, type, repr, docs, init, result);
    }

    /**
     * Gets the instance of the descriptor helper factory.
     *
     * @return the instance of the descriptor helper factory
     */
    public static synchronized FeatureHelper instance()
    {
        if (instance == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    FeatureHelper.class.getClass().getName() + " instance.");
            }

            instance = new FeatureHelper();
            instance.loadInfos();
        }

        return instance;
    }

    /**
     * Getting descriptor values from a molecule and try to calculate a descriptor
     * value if it is not already available. This method should NOT be used for getting
     * common {@link JOEGenericData} elements of molecules. Not existing descriptor
     * values will be added to the molecule, if the
     * <tt>joelib2.feature.addIfNotExist</tt> property in the {@link BasicPropertyHolder} is
     * <tt>true</tt>.
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
     * @param  mol        The molecule
     * @param  descName   The name of the descriptor
     * @return     The descriptor result
     * @see #featureFrom(Molecule, String, DescResult)
     * @see #featureFrom(Molecule, String, DescResult, boolean)
     * @see Molecule#getData(JOEDataType)
     * @see Molecule#getData(String)
     * @see Molecule#genericDataIterator()
     * @see Molecule#addData(JOEGenericData)
     * @see Molecule#addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureResult
     * @see joelib2.molecule.BasicDataHolder#getData(String, boolean)
     * @see BasicPropertyHolder
     */
    public synchronized FeatureResult featureFrom(Molecule mol, String descName)
        throws FeatureException
    {
        // it's more efficient to solve this in the calculation calling method
        FeatureResult result = null;

        //ResultFactory.instance().getDescResult(descName);
        return featureFrom(mol, descName, result, addDesc);
    }

    /**
     * Getting descriptor values from a molecule and try to calculate a descriptor
     * value if it is not already available. This method should NOT be used for getting
     * common {@link JOEGenericData} elements of molecules. Not existing descriptor
     * values will be added to the molecule, if the
     * <tt>joelib2.feature.addIfNotExist</tt> property in the {@link BasicPropertyHolder} is
     * <tt>true</tt>.
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
     * @param  mol        The molecule
     * @param  descName   The name of the descriptor
     * @param  descResult The descriptor result, where the result should be stored
     * @return     The descriptor result
     * @see #featureFrom(Molecule, String)
     * @see #featureFrom(Molecule, String, DescResult, boolean)
     * @see Molecule#getData(JOEDataType)
     * @see Molecule#getData(String)
     * @see Molecule#genericDataIterator()
     * @see Molecule#addData(JOEGenericData)
     * @see Molecule#addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see BasicPairData
     * @see FeatureResult
     * @see joelib2.molecule.BasicDataHolder#getData(String, boolean)
     * @see BasicPropertyHolder
     */
    public synchronized FeatureResult featureFrom(Molecule mol, String descName,
        FeatureResult descResult) throws FeatureException
    {
        return featureFrom(mol, descName, descResult, addDesc);
    }

    /**
     * @return
     */
    public boolean getAddingPolicy()
    {
        return addDesc;
    }

    /**
     * Gets the names of all available {@link joelib2.molecule.types.AtomProperties} descriptors.
     *
     * @return the names of all available {@link joelib2.molecule.types.AtomProperties} descriptors
     */
    public List getAtomLabelFeatures()
    {
        return getAtomLabelFeatures(true);
    }

    /**
     * Gets the names of all available {@link joelib2.molecule.types.AtomProperties} descriptors.
     *
     * @return the names of all available {@link joelib2.molecule.types.AtomProperties} descriptors
     */
    public List getAtomLabelFeatures(boolean onlyNumeric)
    {
        Enumeration atomProps = featureHolder.keys();
        String name;
        Vector descs = new Vector(20);

        while (atomProps.hasMoreElements())
        {
            name = (String) atomProps.nextElement();

            if (isAtomLabelFeature(name))
            {
                descs.add(name);
            }
        }

        // we are only interested in numeric atom properties
        // TODO: change atom property interface and add
        // isNominal, isNumeric methods
        // TODO: remove this hard coded workaround
        if (onlyNumeric)
        {
            for (int i = 0; i < descs.size(); i++)
            {
                if (((String) descs.get(i)).equals(AtomType.getName()))
                {
                    descs.remove(i);
                }
            }
        }

        return descs;
    }

    /**
     * Returns all descriptor values in the molecule as {@link JOEGenericData} elements.
     *
     * @param  mol        The molecule
     * @return     All descriptors in this molecule as {@link JOEGenericData} elements
     * @see Molecule#genericDataIterator()
     * @see JOEGenericData
     */
    public synchronized List getAvailableFeatures(Molecule mol)
    {
        Vector descriptors = new Vector(mol.getDataSize());

        // List of all available descriptors in molecule
        PairDataIterator gdit = mol.genericDataIterator();

        while (gdit.hasNext())
        {
            descriptors.add(gdit.nextPairData());
        }

        return descriptors;
    }

    /**
     * Gets the names of all available {@link joelib2.molecule.types.BondProperties} descriptors.
     *
     * @return the names of all available {@link joelib2.molecule.types.BondProperties} descriptors
     */
    public List getBondLabelFeatures()
    {
        Enumeration bondProps = featureHolder.keys();
        String name;
        Vector descs = new Vector(20);

        while (bondProps.hasMoreElements())
        {
            name = (String) bondProps.nextElement();

            if (isBondLabelFeature(name))
            {
                descs.add(name);
            }
        }

        return descs;
    }

    /**
    * Returns a descriptor information for the given descriptor name.
    *
    * @param  name  the name of the descriptor
    * @return       the descriptor information
    */
    public BasicFeatureInfo getFeatureInfo(String name)
    {
        BasicFeatureInfo info = null;

        if (name != null)
        {
            info = (BasicFeatureInfo) featureHolder.get(name);
        }

        return info;
    }

    /**
     * Returns a {@link Enumeration} of all available descriptor names
     * that could be calculated by using JOELib.
     *
     * @return  The {@link Enumeration} of all available descriptor names
     * @see Feature
     */
    public Enumeration getFeatureNames()
    {
        return featureHolder.keys();
    }

    /**
     * Gets the number of descriptors that are available.
     *
     * @return the number of descriptors that are available
     */
    public int getFeaturesSize()
    {
        return featureHolder.size();
    }

    /**
     * Gets the names of all available {@link joelib2.feature.NativeValue} descriptors.
     *
     * @return the names of all available {@link joelib2.feature.NativeValue} descriptors
     */
    public List getNativeFeatures()
    {
        Enumeration nativeFeat = featureHolder.keys();
        String name;
        Vector descs = new Vector(20);

        while (nativeFeat.hasMoreElements())
        {
            name = (String) nativeFeat.nextElement();

            if (isNativeFeature(name))
            {
                descs.add(name);
            }
        }

        return descs;
    }

    /**
     * Checks if this is a {@link joelib2.molecule.types.AtomProperties} descriptor.
     *
     * @param name the name of the descriptor to check
     * @return <tt>true</tt> if this is an atom property descriptor
     */
    public boolean isAtomLabelFeature(String name)
    {
        FeatureResult result = null;
        boolean isAP = false;

        try
        {
            result = ResultFactory.instance().getFeatureResult(name);
        }
        catch (FeatureException ex)
        {
            logger.warn(ex.toString());
        }

        if (result instanceof joelib2.molecule.types.AtomProperties)
        //if (JOEHelper.hasInterface(result, "AtomProperties"))
        {
            isAP = true;
        }

        return isAP;
    }

    /**
     * Checks if this is a {@link joelib2.molecule.types.BondProperties} descriptor.
     *
     * @param name the name of the descriptor to check
     * @return <tt>true</tt> if this is an bond property descriptor
     */
    public boolean isBondLabelFeature(String name)
    {
        FeatureResult result = null;
        boolean isBP = false;

        try
        {
            result = ResultFactory.instance().getFeatureResult(name);
        }
        catch (FeatureException ex)
        {
            logger.warn(ex.toString());
        }

        if (result instanceof joelib2.molecule.types.BondProperties)
        //if (JOEHelper.hasInterface(result, "BondProperties"))
        {
            isBP = true;
        }

        return isBP;
    }

    /**
     * Checks if this is a {@link joelib2.feature.NativeValue} descriptor.
     *
     * @param name the name of the descriptor to check
     * @return <tt>true</tt> if this is a native value descriptor
     */
    public boolean isNativeFeature(String name)
    {
        FeatureResult result = null;
        boolean isNative = false;

        try
        {
            result = ResultFactory.instance().getFeatureResult(name);
        }
        catch (FeatureException ex)
        {
            logger.warn(ex.toString());
        }

        if (result instanceof joelib2.feature.NativeValue)
        //if (JOEHelper.hasInterface(result, "NativeValue"))
        {
            isNative = true;
        }

        return isNative;
    }

    /**
     * @param add
     */
    public void setAddingPolicy(boolean add)
    {
        this.addDesc = add;
    }

    private synchronized BasicFeatureInfo loadFeatureInfo(String repr)
        throws FeatureException
    {
        Feature descBase;

        // try to load Feature representation class
        try
        {
            descBase = (Feature) Class.forName(repr).newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            throw new FeatureException(repr + " not found.");
        }
        catch (InstantiationException ex)
        {
            throw new FeatureException(repr + " can not be instantiated.");
        }
        catch (IllegalAccessException ex)
        {
            throw new FeatureException(repr + " can't be accessed.");
        }

        if (descBase == null)
        {
            throw new FeatureException("Feature class " + repr +
                " does'nt exist.");
        }

        FeatureDescription description = descBase.getDescription();
        boolean missing = false;
        StringBuffer sbuffer = new StringBuffer(20);

        //        if(!description.hasXMLDescription())
        //        {
        //          sb.append("XML ");
        //          descriptionMissing=true;
        //        }
        //if (!description.hasTextDescription())
        //{
        //      sb.append("TXT ");
        //      descriptionMissing = true;
        //}
        if (!description.hasHtml())
        {
            sbuffer.append("HTML ");
            missing = true;
        }

        if (missing)
        {
            sbuffer.append(" description is missing for feature ");
            sbuffer.append(repr);
            sbuffer.append(" in ");
            sbuffer.append(description.getBasePath());

            logger.warn(sbuffer.toString());
        }

        return descBase.getDescInfo();
    }

    /**
     * Load all descriptor informations from the property file.
     *
     * @return if the informations were loaded successfully
     */
    private synchronized boolean loadInfos()
    {
        String repr;
        Properties prop = propertyHolder.getProperties();
        BasicFeatureInfo descInfo = null;

        int index = 0;
        String descriptor;
        boolean infosLoaded = true;

        while (true)
        {
            descriptor = "joelib2.feature." + index;
            repr = prop.getProperty(descriptor + ".representation");

            if (logger.isDebugEnabled())
            {
                logger.debug("load " + index + " " + repr);
            }

            if (repr == null)
            {
                logger.info("" + featureHolder.size() +
                    " feature informations loaded.");

                break;
            }

            try
            {
                repr = repr.trim();
                descInfo = loadFeatureInfo(repr);

                if (descInfo != null)
                {
                    if (featureHolder.contains(descInfo.getName()))
                    {
                        logger.warn("Feature entry #" + index + ": " + repr +
                            " is skipped, " +
                            "because name for this feature (" +
                            descInfo.getName() + ") exists already.");
                    }
                    else
                    {
                        featureHolder.put(descInfo.getName(), descInfo);
                    }
                }
                else
                {
                    logger.warn("Feature info #" + index + " is empty.");
                }
            }
            catch (FeatureException ex)
            {
                logger.warn(ex.toString());
                logger.warn("Error in feature entry #" + index + ": " + repr);
                infosLoaded = false;
            }

            index++;
        }

        String flagString = propertyHolder.getProperties().getProperty(
                "joelib2.feature.addIfNotExist");
        addDesc = Boolean.valueOf(flagString).booleanValue();

        return infosLoaded;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
