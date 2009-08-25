///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ProgrammableAtomTyper.java,v $
//  Purpose:  Pattern assignment of SMARTS pattern.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner, Badreddin Abolmaali
//  Version:  $Revision: 1.12 $
//            $Date: 2005/02/17 16:48:39 $
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
package joelib2.smarts;

import joelib2.data.IdentifierExpertSystem;

import joelib2.molecule.Molecule;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * PATtern TYper (PATTY) using SMARTS patterns.
 * This object finds PATTY rules and assigns them to atoms or groups.
 * All following rules will overwrite an already assigned rule.
 *
 * @.author     wegnerj
 * @.author     abolmaal
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:39 $
 * @.cite bs93
 * @.cite smarts
 */
public class ProgrammableAtomTyper implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.12 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:39 $";

    private static Category logger = Category.getInstance(
            ProgrammableAtomTyper.class.getName());
    public final static int TYPE_UNKNOWN = -1;
    private static final Class[] DEPENDENCIES =
        new Class[]{BasicSMARTSPatternMatcher.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean initialized = false;

    /**
     *  Holds the SMARTS pattern as {@link String}.
     */
    private List<String> smarts;

    /**
     *  Holds the SMARTS pattern for type assignment.
     */
    private List<SMARTSPatternMatcher> smartsPattern;

    /**
     *  Holds the types to assign.
     */
    private List<String> type;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the PATTY object.
     */
    public ProgrammableAtomTyper()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    /**
     * Adds a new PATTY rule.
     *
     * @param  rule        The rule as SMARTS pattern
     * @param  assignment  The identifier for this rule
     * @return             <tt>true</tt> if sucessfull
     * @.cite smarts
     * @see #addStringRule(String)
     * @see #addRule(JOESmartsPattern, String)
     * @see #addRules(Vector)
     * @see #readRules(String)
     * @see #readRules(InputStream)
     */
    public SMARTSPatternMatcher addRule(String rule, String assignment)
    {
        if (!initialized)
        {
            init(100);
        }

        SMARTSPatternMatcher tmpSP = new BasicSMARTSPatternMatcher();

        if (tmpSP.init(rule) == true)
        {
            addRule(tmpSP, assignment);

            return tmpSP;
        }
        else
        {
            logger.warn("Patty rule '" + rule + "' -> '" + assignment +
                "' can't be generated.");

            return null;
        }
    }

    /**
     * Adds a new PATTY rule.
     *
     * @param  pattern     The parsed SMARTS rule
     * @param  assignment  The identifier for this PATTY rule
     * @return             <tt>true</tt> if successfull
     * @see #addStringRule(String)
     * @see #addRule(JOESmartsPattern, String)
     * @see #addRules(Vector)
     * @see #readRules(String)
     * @see #readRules(InputStream)
     */
    public boolean addRule(SMARTSPatternMatcher pattern, String assignment)
    {
        if (!initialized)
        {
            init(100);
        }

        //    System.out.println("add:::"+pattern);
        smartsPattern.add(pattern);
        smarts.add(pattern.getSmarts());
        type.add(assignment);

        //System.out.println("SPSPSPSP:"+" "+sp.size());
        return true;
    }

    /**
     * Adds a new rules to the pattern typer.
     * The {@link String}'s in the {@link Vector} must have the format
     * <blockquote><pre>
     * 'SMARTS identifier'
     * </pre></blockquote>
     * for e.g.
     * <blockquote><pre>
     * a             aromaticAtoms
     * c-c           biphenyl
     * [NX3]C=[G6]   amideN
     * [OD2]C=O      esterO
     * C=O[NX3][NX3] ureaC
     * </pre></blockquote>
     *
     * @param  rules  {@link Vector} of SMARTS as {@link String}
     * @return           <tt>true</tt> if successfull
     * @.cite smarts
     * @see #addStringRule(String)
     * @see #addRule(String, String)
     * @see #addRule(JOESmartsPattern, String)
     * @see #addRules(List)
     * @see #readRules(String)
     * @see #readRules(InputStream)
     */
    public boolean addRules(List rules)
    {
        if (!initialized)
        {
            init(rules.size());
        }

        for (int i = 0; i < rules.size(); i++)
        {
            if (!addStringRule((String) rules.get(i)))
            {
                logger.error("Error in patty rule \"" + rules.get(i) +
                    "\" in entry " + i + ".");

                return false;
            }
        }

        return true;
    }

    /**
     * Adds a new rule to the pattern typer.
     * The {@link String} must have the format
     * <blockquote><pre>
     * SMARTS identifier
     * </pre></blockquote>
     * for e.g.
     * <blockquote><pre>
     * a             aromaticAtoms
     * c-c           biphenyl
     * [NX3]C=[G6]   amideN
     * [OD2]C=O      esterO
     * C=O[NX3][NX3] ureaC
     * </pre></blockquote>
     *
     * @param  ruleLine  The new PATTY rule
     * @return           <tt>true</tt> if successfull
     * @.cite smarts
     * @see #addRule(String, String)
     * @see #addRule(JOESmartsPattern, String)
     * @see #addRules(List)
     * @see #readRules(String)
     * @see #readRules(InputStream)
     */
    public boolean addStringRule(String ruleLine)
    {
        if (!initialized)
        {
            init(100);
        }

        if (ruleLine.trim().equals("") || (ruleLine.charAt(0) == '#'))
        {
            return true;
        }

        List<String> vs = new Vector<String>();
        HelperMethods.tokenize(vs, ruleLine, " \t\n");

        if (vs.size() >= 2)
        {
            if (addRule((String) vs.get(0), (String) vs.get(1)) == null)
            {
                return false;
            }

            // dump atom expression
            //      System.out.println(((String) vs.get(1))+" "+tmpSP.toString());
        }
        else
        {
            return false;
        }

        return true;
    }

    /**
     *  Assigns identifier's to the <b>first</b> matching atom in the SMARTS pattern.
     *
     * @param  mol  The molcule
     * @return      An array of the indentifier indices
     */
    public int[] assignTypes(Molecule mol)
    {
        int[] ia = new int[mol.getAtomsSize()];

        boolean sucessfull = assignTypes(mol, ia);

        if (sucessfull)
        {
            return ia;
        }
        else
        {
            return null;
        }
    }

    /**
     *  Assigns identifier's to the <b>first</b> matching atom in the SMARTS pattern.
     *
     * @param  mol       The molecule
     * @param  atomType  An array to store the identifier indices
     * @return           <tt>true</tt> if successfull
     */
    public boolean assignTypes(Molecule mol, int[] atomType)
    {
        //    System.out.println("SPSPSPSP:"+sp);
        if (smartsPattern == null)
        {
            logger.error("No patty rules available.");

            return false;
        }

        if (atomType.length != mol.getAtomsSize())
        {
            logger.error("Patty atom type array must have size of #atoms.");

            return true;
        }

        List matchList;
        SMARTSPatternMatcher tmpSP;
        int[] iaTmp;
        Arrays.fill(atomType, TYPE_UNKNOWN);

        for (int i = 0; i < smartsPattern.size(); i++)
        {
            tmpSP = (SMARTSPatternMatcher) smartsPattern.get(i);
            tmpSP.match(mol);
            matchList = tmpSP.getMatches();

            if (matchList.size() != 0)
            {
                //logger.debug(typ[i]+" "+smarts[i]+" matched ");
                for (int j = 0; j < matchList.size(); j++)
                {
                    iaTmp = (int[]) matchList.get(j);

                    //logger.debug(iaTmp[0] << " ");
                    atomType[iaTmp[0] - 1] = i;
                }
            }
        }

        return true;
    }

    /**
     *  Assigns identifier's to <b>all</b> matching atoms in the SMARTS pattern.
     *
     * @param  mol  The molcule
     * @return      An array of the indentifier indices
     */
    public int[] assignTypes2All(Molecule mol)
    {
        int[] ia = new int[mol.getAtomsSize()];

        boolean sucessfull = assignTypes2All(mol, ia);

        if (sucessfull)
        {
            return ia;
        }
        else
        {
            return null;
        }
    }

    /**
     *  Assigns identifier's to <b>all</b> matching atoms in the SMARTS pattern.
     *
     * @param  mol       The molecule
     * @param  atomType  An array to store the identifier indices
     * @return           <tt>true</tt> if successfull
     */
    public boolean assignTypes2All(Molecule mol, int[] atomType)
    {
        if (smartsPattern == null)
        {
            logger.error("No patty rules available.");

            return false;
        }

        if (atomType.length != mol.getAtomsSize())
        {
            logger.error("Patty atom type array must have size of #atoms.");

            return true;
        }

        List matchList;
        SMARTSPatternMatcher tmpSP;
        int[] iaTmp;

        Arrays.fill(atomType, TYPE_UNKNOWN);

        for (int i = 0; i < smartsPattern.size(); i++)
        {
            tmpSP = smartsPattern.get(i);
            tmpSP.match(mol);
            matchList = tmpSP.getMatches();

            if (matchList.size() != 0)
            {
                //logger.debug(typ[i]+" "+smarts[i]+" matched ");
                for (int j = 0; j < matchList.size(); j++)
                {
                    iaTmp = (int[]) matchList.get(j);

                    for (int match = 0; match < iaTmp.length; match++)
                    {
                        //logger.debug(iaTmp[0] << " ");
                        atomType[iaTmp[match] - 1] = i;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Assigns identifier's to <b>all</b> matching atoms in the SMARTS pattern and returns all unique matching lists.
     *
     * @param  mol       The molecule
     * @param  atomType  An array to store the identifier indices
     * @param  allMatchLists  A {@link List} to store all matching lists
     * @return           <tt>true</tt> if successfull
     */
    public boolean assignTypes2Groups(Molecule mol, int[] atomType,
        List<List<int[]>> allMatchLists)
    {
        if (smartsPattern == null)
        {
            logger.error("No patty rules available.");

            return false;
        }

        if (atomType.length != mol.getAtomsSize())
        {
            logger.error("Patty atom type array must have size of #atoms.");

            return false;
        }

        List<int[]> matchList;
        SMARTSPatternMatcher tmpSP;
        int[] iaTmp;

        Arrays.fill(atomType, TYPE_UNKNOWN);

        for (int i = 0; i < smartsPattern.size(); i++)
        {
            tmpSP = smartsPattern.get(i);
            tmpSP.match(mol);
            matchList = tmpSP.getMatchesUnique();

            if (matchList.size() != 0)
            {
                allMatchLists.add(matchList);

                //logger.debug(typ[i]+" "+smarts[i]+" matched ");
                for (int j = 0; j < matchList.size(); j++)
                {
                    iaTmp = matchList.get(j);

                    for (int match = 0; match < iaTmp.length; match++)
                    {
                        //logger.debug(iaTmp[0] << " ");
                        atomType[iaTmp[match] - 1] = i;
                    }
                }
            }
        }

        return true;
    }

    //  /**
    //   *  Description of the Method
    //   *
    //   * @param  type             Description of the Parameter
    //   * @param  failOnUndefined  Description of the Parameter
    //   * @return                  Description of the Return Value
    //   */
    //  public int typeToInt(final String type, boolean failOnUndefined)
    //  {
    //    int  result;
    //
    //    switch (Character.toUpperCase(type.charAt(0)))
    //    {
    //        case 'C':
    //          // CAT - CATION
    //          result = PT_CATION;
    //          break;
    //        case 'A':
    //          if (Character.toUpperCase(type.charAt(1)) == 'N')
    //          {
    //            // ANI - ANION
    //            result = PT_ANION;
    //          }
    //          else
    //          {
    //            result = PT_ACCEPTOR;
    //          }
    //          break;
    //        case 'P':
    //          // POL - POLAR
    //          result = PT_POLAR;
    //          break;
    //        case 'D':
    //          // DON - DONOR
    //          result = PT_DONOR;
    //          break;
    //        case 'H':
    //          // HYD - HYDROPHOBIC
    //          result = PT_HYDROPHOBIC;
    //          break;
    //        case 'M':
    //          // Metal
    //          result = PT_METAL;
    //          break;
    //        case 'O':
    //          // OTH - OTHER
    //          result = PT_OTHER;
    //          break;
    //        default:
    //          // This was added by Brian,
    //          // Behavior will fail if type is undefined
    //          if (failOnUndefined)
    //          {
    //            logger.error("Unable to find type of feature passed in ");
    //            logger.error("Feature passed in is " + type);
    //          }
    //          result = -1;
    //    }
    //    return (result);
    //  }
    public boolean equals(Object obj)
    {
        if (obj instanceof ProgrammableAtomTyper)
        {
            ProgrammableAtomTyper patty = (ProgrammableAtomTyper) obj;

            if (this.smarts.size() != patty.smarts.size())
            {
                return false;
            }

            for (int i = 0; i < smarts.size(); i++)
            {
                if (!smarts.get(i).equals(patty.smarts.get(i)))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Returns <tt>true</tt> if one ore more rules can be assigned.
     *
     * @param  mol       The molecule
     * @return           <tt>true</tt> if one ore more rules can be assigned.
     */
    public boolean fitsAnyRule(Molecule mol)
    {
        if (smartsPattern == null)
        {
            logger.error("No patty rules available.");

            return false;
        }

        List matchList;
        SMARTSPatternMatcher tmpSP;

        //int iaTmp[];
        for (int i = 0; i < smartsPattern.size(); i++)
        {
            tmpSP = smartsPattern.get(i);
            tmpSP.match(mol);
            matchList = tmpSP.getMatches();

            if (matchList.size() != 0)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns {@link java.util.Vector} of all assigned rules.
     *
     * @param  mol       The molecule
     * @return           {@link java.util.List} of all assigned rules.
     */
    public List<String> getAllFittingRules(Molecule mol)
    {
        if (smartsPattern == null)
        {
            logger.error("No patty rules available.");

            return null;
        }

        Vector<String> fits = new Vector<String>();

        List matchList;
        SMARTSPatternMatcher tmpSP;

        //int iaTmp[];
        for (int i = 0; i < smartsPattern.size(); i++)
        {
            tmpSP = smartsPattern.get(i);
            tmpSP.match(mol);
            matchList = tmpSP.getMatches();

            if (matchList.size() != 0)
            {
                fits.add(type.get(i));
            }

            /*matchList = tmpSP.getMatchesUnique();
            System.out.println(matchList.size() + " ");
            for (int nn = 0; nn < matchList.size(); nn++)
            {
                    iaTmp = (int[]) matchList.get(nn);
                    System.out.print(nn + ": ");
                    for (int j = 0; j < iaTmp.length; j++)
                    {
                            System.out.print(iaTmp[j] + " ");
                    }
                    System.out.println();
            }*/
        }

        return fits;
    }

    public List getRulesAssignment()
    {
        return type;
    }

    public List<String> getRulesSMARTS()
    {
        return smarts;
    }

    /**
     * Gets the identifier name from the given identifier index.
     *
     * @param  type  The identifier index
     * @return       The identifier name
     */
    public String getStringFromType(int typeNumber)
    {
        if ((typeNumber == TYPE_UNKNOWN) || (typeNumber >= type.size()))
        {
            return null;
        }

        return (String) type.get(typeNumber);
    }

    public int hashCode()
    {
        if (smarts == null)
        {
            return 0;
        }
        else
        {
            return smarts.size();
        }
    }

    /**
     * Loads patty rules from file at resource loacation <tt>resourceURL</tt>.
     *
     * The file must have the format
     * <blockquote><pre>
     * SMARTS1 identifier1
     * SMARTS2 identifier2
     * ...
     * </pre></blockquote>
     * for e.g.
     * <blockquote><pre>
     * a             aromaticAtoms
     * c-c           biphenyl
     * [NX3]C=[G6]   amideN
     * [OD2]C=O      esterO
     * C=O[NX3][NX3] ureaC
     * </pre></blockquote>
     *
     * @param  resourceURL  patty file location in resource path
     * @return              <tt>true</tt> if the patty rules were loaded
     *      succesfully
     * @.cite smarts
     * @see #readRules(InputStream)
     * @see #addStringRule(String)
     * @see #addRule(String, String)
     * @see #addRule(JOESmartsPattern, String)
     * @see #addRules(Vector)
     * @see BasicResourceLoader
     */
    public boolean readRules(final String resourceURL)
    {
        byte[] bytes = BasicResourceLoader.instance()
                                          .getBytesFromResourceLocation(
                resourceURL);

        if (bytes == null)
        {
            logger.error("Patty rule file " + resourceURL +
                " can't be loaded.");

            return false;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        return readRules(bais);
    }

    /**
     * Loads patty rules from input stream.
     *
     * The data must have the format
     * <blockquote><pre>
     * SMARTS1 identifier1
     * SMARTS2 identifier2
     * ...
     * </pre></blockquote>
     * for e.g.
     * <blockquote><pre>
     * a             aromaticAtoms
     * c-c           biphenyl
     * [NX3]C=[G6]   amideN
     * [OD2]C=O      esterO
     * C=O[NX3][NX3] ureaC
     * </pre></blockquote>
     *
     * @param  resourceURL  patty file location in resource path
     * @return              <tt>true</tt> if the patty rules was loaded
     *      succesfully
     * @.cite smarts
     * @see #readRules(String)
     * @see #addStringRule(String)
     * @see #addRule(String, String)
     * @see #addRule(JOESmartsPattern, String)
     * @see #addRules(Vector)
     */
    public boolean readRules(final InputStream is)
    {
        InputStreamReader isr = new InputStreamReader(is);
        LineNumberReader lnr = new LineNumberReader(isr);

        String nextLine = null;

        if (!initialized)
        {
            init(1000);
        }

        for (;;)
        {
            try
            {
                nextLine = lnr.readLine();

                if (nextLine == null)
                {
                    break;
                }

                if (!addStringRule(nextLine))
                {
                    logger.error("Error in patty rule \"" + nextLine +
                        "\" in line " + lnr.getLineNumber() + ".");

                    return false;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  capacity  Description of the Parameter
     */
    private void init(int capacity)
    {
        initialized = true;
        smartsPattern = new Vector<SMARTSPatternMatcher>(capacity);
        smarts = new Vector<String>(capacity);
        type = new Vector<String>(capacity);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
