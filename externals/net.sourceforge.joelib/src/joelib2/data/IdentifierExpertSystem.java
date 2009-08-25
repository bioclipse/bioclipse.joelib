///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IdentifierExpertSystem.java,v $
//  Purpose:  Atom typer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.data;

import joelib2.ext.ExternalFactory;

import joelib2.feature.FeatureFactory;
import joelib2.feature.FeatureHelper;
import joelib2.feature.ResultFactory;

import joelib2.feature.types.atompair.AtomPairTypeHolder;

import joelib2.io.BasicIOTypeHolder;

import joelib2.process.ProcessFactory;

import joelib2.process.filter.FilterFactory;

import joelib2.smarts.SMARTSParser;

import joelib2.util.BasicArrayHelper;
import joelib2.util.BasicLineArrayHelper;
import joelib2.util.BasicLineMatrixHelper;
import joelib2.util.BasicMatrixHelper;
import joelib2.util.BasicMoleculeCacheHolder;
import joelib2.util.HelperMethods;

import joelib2.util.types.BasicStringString;
import joelib2.util.types.StringString;

import jtt.latex.bibtex.BibitemHolder;

import wsi.ra.io.BasicBatchFileUtilities;
import wsi.ra.io.BasicBatchScriptReplacer;

import wsi.ra.text.DecimalFormatHelper;

import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Category;

import com.vladium.utils.ClassScope;


/**
 * Chemistry kernel informations unsing a classloader hack to build the
 * dependency tree.
 *
 * @.author wegnerj
 * @.author (C) <a
 *         href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad
 *         Roubtsov </a>, 2003
 * @.license GPL
 * @.cvsversion $Revision: 1.12 $, $Date: 2005/02/17 16:48:29 $
 */
public class IdentifierExpertSystem
{
    // /////////////////////////////////////////////

    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            IdentifierExpertSystem.class.getName());

    private static final String DELIMITER = "  ";

    public static final String CML_KERNEL_REFERENCE = "dictRef";

    public static final String CML_KERNEL_REFERENCE_PREFIX = "jk:k";

    public static final String CML_SOFT_KERNEL = "softDependencies";

    public static final String CML_HARD_KERNEL = "hardDependencies";

    private static IdentifierExpertSystem chemistryKernel;

    private static String[] dependencyTree;

    private static Hashtable<String, int[]> dependencyVersionHash;

    //~ Instance fields ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////

    private Hashtable<String, String> hardInfos = new Hashtable<String, String>(
            30);

    private String[] infos;

    private StringString kernelID;

    private Hashtable<String, String> softInfos = new Hashtable<String, String>(
            30);

    private String[] titles;

    //~ Methods ////////////////////////////////////////////////////////////////

    public static String getDependencyTree(String className)
    {
        int index = getDependencyTreeIndex(className);

        if (index != -1)
        {
            return dependencyTree[index];
        }
        else
        {
            return null;
        }
    }

    public static String[] getDependencyTreeClassNames()
    {
        if (dependencyTree == null)
        {
            dependencyTree = buildDependencyTree();
        }

        Vector<String> classNames = new Vector<String>(200);
        Enumeration enumeration = dependencyVersionHash.keys();

        for (int i = 0; enumeration.hasMoreElements(); i++)
        {
            classNames.add((String) enumeration.nextElement());
        }

        String[] classNamesArr = new String[classNames.size()];

        for (int i = 0; i < classNamesArr.length; i++)
        {
            classNamesArr[i] = classNames.get(i);
        }

        return classNamesArr;
    }

    public static int getDependencyTreeComplexity(String className)
    {
        if (dependencyTree == null)
        {
            dependencyTree = buildDependencyTree();
        }

        int[] hashIndex = dependencyVersionHash.get(className);

        if (hashIndex == null)
        {
            return 0;
        }
        else
        {
            return hashIndex[2];
        }
    }

    public static int getDependencyTreeHash(String className)
    {
        if (dependencyTree == null)
        {
            dependencyTree = buildDependencyTree();
        }

        int[] hashIndex = dependencyVersionHash.get(className);

        if (hashIndex == null)
        {
            return 0;
        }
        else
        {
            return hashIndex[0];
        }
    }

    public static int getDependencyTreeIndex(String className)
    {
        if (dependencyTree == null)
        {
            dependencyTree = buildDependencyTree();
        }

        int[] hashIndex = dependencyVersionHash.get(className);

        if (hashIndex == null)
        {
            return -1;
        }
        else
        {
            return hashIndex[1];
        }
    }

    // ////////////////////////////////////////////////////////////////

    public static synchronized IdentifierExpertSystem instance()
    {
        if (chemistryKernel == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    IdentifierExpertSystem.class.getName() + " instance.");
            }

            chemistryKernel = new IdentifierExpertSystem();
        }

        return chemistryKernel;
    }

    public static void main(String[] args)
    {
        String[] infos = IdentifierExpertSystem.instance()
                                               .getKernelInformations();
        String[] titles = IdentifierExpertSystem.instance().getKernelTitles();
        Hashtable attributes = new Hashtable();
        int kernelHash = IdentifierExpertSystem.instance().getKernelHash();
        StringBuffer buffer = new StringBuffer(10000);
        String title;
        String delimiterHuge =
            "===========================================================================\n";
        String delimiterSmall =
            "---------------------------------------------------------------------------\n";

        buffer.append(delimiterHuge);
        buffer.append(("Date: " + (new Date()).toGMTString()) + "\n");
        buffer.append("JOELib2 chemistry kernel (expert systems) ID: " +
            kernelHash + "\n");
        buffer.append("The following expert systems are used:\n");
        buffer.append(delimiterSmall);

        for (int i = 0; i < infos.length; i++)
        {
            attributes.clear();
            title = IdentifierExpertSystem.CML_KERNEL_REFERENCE_PREFIX +
                kernelHash + ":" + titles[i];

            //            if (i < (infos.length - 1))
            //            {
            //                buffer.append(", ");
            //            }

            buffer.append(title + " (" + infos[i].hashCode() + ") \n");
            buffer.append(infos[i] + "\n");
        }

        buffer.append(delimiterHuge);

        String[] asList = IdentifierExpertSystem.getDependencyTreeClassNames();

        for (int i = 0; i < asList.length; i++)
        {
            buffer.append("dependency class is: \t\t\t\t" + asList[i] + "\n");
            buffer.append("dependency version hash code is: \t\t" +
                IdentifierExpertSystem.getDependencyTreeHash(asList[i]) +
                " (including chemistry kernel hash: " + kernelHash + ")\n");
            buffer.append("dependency algorithm complexity is at least: \t" +
                IdentifierExpertSystem.getDependencyTreeComplexity(asList[i]) +
                " (+ basic user input graph, + cyclic dependencies, + data structure , + forgotten dependencies)\n");
            buffer.append("\n" +
                IdentifierExpertSystem.getDependencyTree(asList[i]) + "\n");

            if (i < (asList.length - 1))
            {
                buffer.append(delimiterSmall);
            }
        }

        buffer.append(delimiterHuge);

        System.out.println(buffer.toString());
    }

    public static String transformCVStag(String tag)
    {
        String tmp = tag.replace('$', ' ');
        tmp = tmp.trim();

        if (tmp.startsWith("Revision:"))
        {
            tmp = tmp.substring("Revision:".length());
        }

        if (tmp.startsWith("Date:"))
        {
            tmp = tmp.substring("Date:".length());
        }

        tmp = tmp.replace('/', '-');
        tmp = tmp.replace(':', '-');
        tmp = tmp.trim();

        return tmp.replace(' ', '_');
    }

    public void addHardCodedKernel(IdentifierHardDependencies hardCodedKernel)
    {
        String kernelString = hardCodedKernel.getClass().getName() + " " +
            hardCodedKernel.getVendorInternal() + " " +
            hardCodedKernel.getReleaseVersionInternal() + " " +
            hardCodedKernel.getReleaseDateInternal();

        if (logger.isDebugEnabled())
        {
            logger.debug("hard dependencies: " + kernelString);
        }

        hardInfos.put(CML_HARD_KERNEL + ":" +
            hardCodedKernel.getClass().getName(), kernelString);
    }

    public void addSoftCodedKernel(IdentifierSoftDependencies softCodedKernel)
    {
        String kernelString = softCodedKernel.getClass().getName() + " " +
            softCodedKernel.getVendorExternal() + " " +
            softCodedKernel.getResourceExternal() + " " +
            softCodedKernel.getReleaseVersionExternal() + " " +
            softCodedKernel.getReleaseDateExternal();

        if (logger.isDebugEnabled())
        {
            logger.debug("soft dependencies : " + kernelString);
        }

        softInfos.put(CML_SOFT_KERNEL + ":" +
            softCodedKernel.getClass().getName(), kernelString);
    }

    /**
     * @return
     */
    public int getKernelHash()
    {
        getKernelInformations();

        int kernelHash = 0;

        for (int i = 0; i < infos.length; i++)
        {
            kernelHash = (31 * kernelHash) + infos[i].hashCode();
        }

        return kernelHash;
    }

    public StringString getKernelID()
    {
        if (kernelID == null)
        {
            kernelID = new BasicStringString(CML_KERNEL_REFERENCE,
                    CML_KERNEL_REFERENCE_PREFIX +
                    Integer.toString(getKernelHash()));
        }

        return kernelID;
    }

    public String[] getKernelInformations()
    {
        if (infos == null)
        {
            getKernelTitles();
            infos = new String[titles.length];
            infos[0] = (String) softInfos.get(titles[0]);
            infos[1] = (String) softInfos.get(titles[1]);
            infos[2] = (String) softInfos.get(titles[2]);
            infos[3] = (String) softInfos.get(titles[3]);
            infos[4] = (String) softInfos.get(titles[4]);
            infos[5] = (String) softInfos.get(titles[5]);
            infos[6] = (String) softInfos.get(titles[6]);
            infos[7] = (String) softInfos.get(titles[7]);
            infos[8] = (String) softInfos.get(titles[8]);
            infos[9] = (String) softInfos.get(titles[9]);
            infos[10] = (String) hardInfos.get(titles[10]);
            infos[11] = (String) hardInfos.get(titles[11]);
            infos[12] = (String) hardInfos.get(titles[12]);
            infos[13] = (String) hardInfos.get(titles[13]);
            infos[14] = (String) hardInfos.get(titles[14]);
            infos[15] = (String) hardInfos.get(titles[15]);
            infos[16] = (String) hardInfos.get(titles[16]);
            infos[17] = (String) hardInfos.get(titles[17]);
            infos[18] = (String) hardInfos.get(titles[18]);
            infos[19] = (String) hardInfos.get(titles[19]);
        }

        return infos;
    }

    public String[] getKernelTitles()
    {
        if (titles == null)
        {
            // guarantee that all chemistry kernels are initialized
            IdentifierSoftDefaultSystem hybTyper = BasicHybridisationTyper
                .instance();
            hybTyper.init();

            IdentifierSoftDefaultSystem impValTyper = BasicImplicitValenceTyper
                .instance();
            impValTyper.init();

            IdentifierSoftDefaultSystem aromaticTyper = BasicAromaticityTyper
                .instance();
            aromaticTyper.init();

            IdentifierSoftDefaultSystem atomTyper = BasicAtomTyper.instance();
            atomTyper.init();

            IdentifierSoftDefaultSystem element = BasicElementHolder.instance();
            element.init();

            IdentifierSoftDefaultSystem gc = BasicGroupContributionHolder
                .instance();
            gc.init();

            IdentifierSoftDefaultSystem isotope = BasicIsotopeHolder.instance();
            isotope.init();

            IdentifierSoftDefaultSystem phModel = BasicProtonationModel
                .instance();
            phModel.init();

            IdentifierSoftDefaultSystem residue = BasicResidueData.instance();
            residue.init();

            IdentifierSoftDefaultSystem typeTable =
                BasicAtomTypeConversionHolder.instance();
            typeTable.init();

            titles = new String[20];

            String kernelType = CML_SOFT_KERNEL + ":";
            titles[0] = kernelType + aromaticTyper.getClass().getName();
            titles[1] = kernelType + atomTyper.getClass().getName();
            titles[2] = kernelType + element.getClass().getName();
            titles[3] = kernelType + gc.getClass().getName();
            titles[4] = kernelType + isotope.getClass().getName();
            titles[5] = kernelType + phModel.getClass().getName();
            titles[6] = kernelType + typeTable.getClass().getName();
            titles[7] = kernelType + residue.getClass().getName();
            titles[8] = kernelType + impValTyper.getClass().getName();
            titles[9] = kernelType + hybTyper.getClass().getName();
            kernelType = CML_HARD_KERNEL + ":";
            titles[10] = kernelType + aromaticTyper.getClass().getName();
            titles[11] = kernelType + atomTyper.getClass().getName();
            titles[12] = kernelType + element.getClass().getName();
            titles[13] = kernelType + gc.getClass().getName();
            titles[14] = kernelType + isotope.getClass().getName();
            titles[15] = kernelType + phModel.getClass().getName();
            titles[16] = kernelType + typeTable.getClass().getName();
            titles[17] = kernelType + residue.getClass().getName();
            titles[18] = kernelType + impValTyper.getClass().getName();
            titles[19] = kernelType + hybTyper.getClass().getName();
        }

        return titles;
    }

    /**
     *
     */
    private static String[] buildDependencyTree()
    {
        //      ensure, that the class loader has all relevant classes loaded
        BasicAromaticityTyper.instance();
        BasicArrayHelper.instance();
        AtomPairTypeHolder.instance();
        BasicAtomTypeConversionHolder.instance();
        BasicAtomTyper.instance();
        BasicBatchScriptReplacer.instance();
        BibitemHolder.instance();

        //DatabaseConnection.instance();
        DecimalFormatHelper.instance();
        BasicElementHolder.instance();
        ExternalFactory.instance();
        FeatureFactory.instance();
        FeatureHelper.instance();
        BasicBatchFileUtilities.instance();
        FilterFactory.instance();

        //        try
        //        {
        //            GhemicalInterface.instance();
        //        }
        //        catch(Exception e){
        //
        //        }
        BasicGroupContributionHolder.instance();
        BasicIOTypeHolder.instance();
        BasicIsotopeHolder.instance();
        HelperMethods.instance();
        BasicLineArrayHelper.instance();
        BasicLineMatrixHelper.instance();
        BasicMatrixHelper.instance();

        // Avoid an instance, because this will add a X-server dependency for linux systems
        //Mol2Image.instance();
        // Avoid an instance, because this will add a X-server dependency for linux systems
        //MolFileChooser.instance();

        try
        {
            BasicMoleculeCacheHolder.instance();
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        ProcessFactory.instance();
        BasicPropertyHolder.instance();
        BasicProtonationModel.instance();
        BasicResidueData.instance();
        BasicResourceLoader.instance();
        ResultFactory.instance();
        BasicRotorRulesHolder.instance();
        //Viewer.instance();

        // show all loaded classes in the class loader
        final ClassLoader[] loaders = ClassScope.getCallerClassLoaderTree();
        final Class[] classes = ClassScope.getLoadedClasses(loaders);
        String[] cnames = new String[classes.length];
        Vector<String> results = new Vector<String>(classes.length);
        StringBuffer buffer;
        dependencyVersionHash = new Hashtable<String, int[]>(classes.length);

        for (int c = 0; c < classes.length; c++)
        {
            buffer = new StringBuffer(300);
            cnames[c] = classes[c].getName();
            //System.out.println ("[" + classes[c].getName () + "]:");
            //System.out.println (" loaded by [" + classes[c].getClassLoader
            // ().getClass ().getName () + "]");
            //System.out.println (" from [" + ClassScope.getClassLocation
            // (classes[c])
            // + "]");

            recurseInto(classes[c], buffer, 0, results);
        }

        String[] resultsArr = new String[results.size()];

        for (int i = 0; i < resultsArr.length; i++)
        {
            resultsArr[i] = results.get(i);
        }

        return resultsArr;
    }

    /**
     * @param class1
     * @param buffer
     * @param i
     */
    private static int recurseInto(Class class1, StringBuffer buffer, int tabs,
        Vector<String> results)
    {
        Method dependencies = null;
        Method releaseVersion = null;

        try
        {
            dependencies = class1.getMethod("getDependencies", null);
            releaseVersion = class1.getMethod("getReleaseVersion", null);
        }
        catch (SecurityException e)
        {
            // ignore all exceptions
        }
        catch (NoSuchMethodException e)
        {
            // ignore all exceptions
        }

        int dependsOn = 0;

        if (dependencies != null)
        {
            Object dependsOnVal = null;
            Object releaseVal = null;

            //            if (releaseVersion != null) {
            //                System.out.println(classes[c].getName() + " "
            //                        + releaseVersion + " isAccessible="
            //                        + releaseVersion.isAccessible() + " isStatic="
            //                        + Modifier.isStatic(releaseVersion.getModifiers())
            //                        + " isPublic="
            //                        + Modifier.isPublic(releaseVersion.getModifiers()));
            //            }
            try
            {
                int modifier = dependencies.getModifiers();

                if (Modifier.isStatic(modifier) && Modifier.isPublic(modifier))
                {
                    dependsOnVal = dependencies.invoke(null, null);
                }

                if (releaseVersion != null)
                {
                    modifier = releaseVersion.getModifiers();

                    if (Modifier.isStatic(modifier) &&
                            Modifier.isPublic(modifier))
                    {
                        releaseVal = releaseVersion.invoke(null, null);
                    }
                }
            }
            catch (IllegalArgumentException e2)
            {
                // ignore all exceptions
            }
            catch (IllegalAccessException e2)
            {
                // ignore all exceptions
            }
            catch (InvocationTargetException e2)
            {
                // ignore all exceptions
            }
            catch (NullPointerException e2)
            {
                // ignore all exceptions
            }

            if ((dependsOnVal != null) && (dependsOnVal instanceof Class[]))
            {
                Class[] dependson = (Class[]) dependsOnVal;

                for (int j = 0; j < tabs; j++)
                {
                    buffer.append(DELIMITER);
                }

                buffer.append(class1 + "(");

                if ((releaseVal != null) && (releaseVal instanceof String))
                {
                    String release = (String) releaseVal;
                    buffer.append("version ");
                    buffer.append(release);
                }
                else
                {
                    buffer.append("no version available");
                }

                buffer.append(")");

                if (dependson.length != 0)
                {
                    if (class1 != SMARTSParser.class)
                    {
                        buffer.append(" depends on");
                    }
                    else
                    {
                        buffer.append(
                            " depends on (WARNING: cyclic dependencies)");
                    }

                    for (int i = 0; i < dependson.length; i++)
                    {
                        if (class1 != SMARTSParser.class)
                        {
                            buffer.append('\n');

                            //System.out.println("dependson["+i+"]:"+dependson[i]);
                            dependsOn += recurseInto(dependson[i], buffer,
                                    tabs + 1, results);
                            dependsOn++;
                        }
                        else
                        {
                            if ((i % 7) == 0)
                            {
                                buffer.append('\n');

                                for (int j = 0; j < (tabs + 1); j++)
                                {
                                    buffer.append(DELIMITER);
                                }
                            }

                            int index = dependson[i].getName().lastIndexOf(".");

                            if (index == -1)
                            {
                                index = 0;
                            }
                            else
                            {
                                index = index + 1;
                            }

                            buffer.append(dependson[i].getName().substring(
                                    index));

                            if (i < (dependson.length - 1))
                            {
                                buffer.append(", ");
                            }
                        }
                    }
                }

                if (tabs == 0)
                {
                    String finalDT = buffer.toString();
                    results.add(finalDT);

                    int dthash = IdentifierExpertSystem.instance()
                                                       .getKernelHash();
                    dthash = (31 * dthash) + finalDT.hashCode();
                    dependencyVersionHash.put(class1.getName(),
                        new int[]{dthash, results.size() - 1, dependsOn});
                }
            }
        }

        return dependsOn;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
