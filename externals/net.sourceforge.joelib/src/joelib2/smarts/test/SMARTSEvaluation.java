///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMARTSEvaluation.java,v $
//  Purpose:  Test SMARTS matching in batch mode allowing different rules, molecules, etc.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2005/02/17 16:48:40 $
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
package joelib2.smarts.test;

import cformat.PrintfStream;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.HelperMethods;

import joelib2.util.types.BasicIntInt;

import wsi.ra.io.BasicRegExpFilenameFilter;

import wsi.ra.tool.BasicResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Test SMARTS matching in batch mode allowing different rules, molecules, etc.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:40 $
 */
public class SMARTSEvaluation
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(SMARTSEvaluation.class
            .getName());
    private static String delimiter =
        "----------------------------------------------";

    //~ Instance fields ////////////////////////////////////////////////////////

    private List matchesAtomIdx;
    private List matchesMols;

    private Hashtable molecules;
    private List moleculesV;
    private boolean printAllMatches = false;
    private List smarts;
    private List smartsDescription;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SMARTSEvaluation()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
      *  The main program for the TestSmarts class
      *
      * @param  args  The command line arguments
      */
    public static void main(String[] args)
    {
        SMARTSEvaluation smartsEval = new SMARTSEvaluation();

        if (args.length != 4)
        {
            smartsEval.usage();
            System.exit(0);
        }
        else
        {
            smartsEval.test(args[0], args[1], args[2], args[3]);
        }

        System.exit(0);
    }

    /**
     *  A unit test for JUnit
     *
     * @param  molURL   Description of the Parameter
     * @param  inType   Description of the Parameter
     * @param  outType  Description of the Parameter
     */
    public void test(String molDirectory, String evaluationFile,
        String outputFile, String showType)
    {
        if (showType.equalsIgnoreCase("all"))
        {
            printAllMatches = true;
        }
        else
        {
            printAllMatches = false;
        }

        loadMolecules(molDirectory);
        loadEvaluationFile(evaluationFile);

        // start evaluation
        evaluateSMARTS(outputFile);
    }

    /**
     *  Description of the Method
     */
    public void usage()
    {
        StringBuffer sb = new StringBuffer();
        String programName = this.getClass().getName();

        //String programPackage = this.getClass().getPackage().getName();
        sb.append("Usage is : ");
        sb.append("java -cp . ");
        sb.append(programName);
        sb.append(" <directory with SDF files>");
        sb.append(" <evaluationFile>");
        sb.append(" <outputFile>");
        sb.append(" <show: 'all' or 'errors'>");
        sb.append("\n\n where the evaluation file has the form:\n");
        sb.append(
            "SMARTS description molName_1#i_1#i_2#i_n molName_m#i_1#i_n ...");

        System.out.println(sb.toString());

        System.exit(0);
    }

    /**
     *
     */
    private void evaluateSMARTS(String outputFile)
    {
        FileOutputStream out = null;
        PrintfStream ps = null;

        try
        {
            out = new FileOutputStream(outputFile);
            ps = new PrintfStream(out);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Molecule mol;
        String molName;
        String description;
        Vector matchesMolsSingle = null;
        Vector matchesAtomIdxSingle = null;
        SMARTSPatternMatcher pSMARTS = null;
        List matchList;
        int[] iaTmp;
        int[] firstAtomsExpected;
        int firstAtomIdx;
        boolean oneFound;
        Vector wronglyRecognized = new Vector();
        Vector notRecognized = new Vector();
        int ok = 0;
        int mismatches = 0;
        int singleMismatches;
        ps.println("Checking " + smarts.size() + " SMARTS patterns.");

        for (int i = 0; i < smarts.size(); i++)
        {
            //System.out.println(smarts.get(i).getClass().getName()+": "+smarts);
            pSMARTS = (SMARTSPatternMatcher) smarts.get(i);
            description = (String) smartsDescription.get(i);

            //System.out.println(matchesMols.get(i).getClass().getName()+": "+matchesMols);
            matchesMolsSingle = (Vector) matchesMols.get(i);

            //System.out.println(matchesAtomIdx.get(i).getClass().getName()+": "+matchesAtomIdx);
            matchesAtomIdxSingle = (Vector) matchesAtomIdx.get(i);

            for (int j = 0; j < matchesMolsSingle.size(); j++)
            {
                molName = (String) matchesMolsSingle.get(j);
                firstAtomsExpected = (int[]) matchesAtomIdxSingle.get(j);
                mol = (Molecule) molecules.get(molName);

                //System.out.println("Empty: " + mol.empty());
                if ((mol == null) || mol.isEmpty())
                {
                    logger.error("No molecular structure available for " +
                        molName);

                    continue;
                }
                else
                {
                    wronglyRecognized.clear();
                    notRecognized.clear();

                    // find substructures
                    pSMARTS.match(mol);
                    matchList = pSMARTS.getMatches();
                    singleMismatches = 0;

                    for (int nn = 0; nn < matchList.size(); nn++)
                    {
                        iaTmp = (int[]) matchList.get(nn);

                        firstAtomIdx = iaTmp[0];
                        oneFound = false;

                        if (firstAtomsExpected != null)
                        {
                            if (firstAtomsExpected[0] != -1)
                            {
                                for (int k = 0; k < firstAtomsExpected.length;
                                        k++)
                                {
                                    if (firstAtomsExpected[k] == firstAtomIdx)
                                    {
                                        oneFound = true;
                                    }
                                }
                            }
                        }

                        if (firstAtomsExpected == null)
                        {
                            if (!oneFound || (firstAtomsExpected == null))
                            {
                                wronglyRecognized.add(new BasicIntInt(nn,
                                        firstAtomIdx));
                                singleMismatches++;
                            }
                        }
                        else
                        {
                            if (firstAtomsExpected[0] != -1)
                            {
                                if (!oneFound)
                                {
                                    wronglyRecognized.add(new BasicIntInt(nn,
                                            firstAtomIdx));
                                    singleMismatches++;
                                }
                            }
                        }
                    }

                    if (firstAtomsExpected != null)
                    {
                        if (firstAtomsExpected[0] == -1)
                        {
                            if (matchList.size() == 0)
                            {
                                notRecognized.add(new Integer(-1));
                                singleMismatches++;
                            }
                        }
                        else
                        {
                            if (matchList.size() < firstAtomsExpected.length)
                            {
                                for (int k = 0; k < firstAtomsExpected.length;
                                        k++)
                                {
                                    oneFound = false;

                                    for (int nn = 0; nn < matchList.size();
                                            nn++)
                                    {
                                        iaTmp = (int[]) matchList.get(nn);
                                        firstAtomIdx = iaTmp[0];

                                        if (firstAtomsExpected[k] ==
                                                firstAtomIdx)
                                        {
                                            oneFound = true;
                                        }
                                    }

                                    if (!oneFound)
                                    {
                                        notRecognized.add(new Integer(
                                                firstAtomsExpected[k]));
                                        singleMismatches++;
                                    }
                                }
                            }
                        }
                    }

                    if (firstAtomsExpected == null)
                    {
                        ok++;
                        mismatches += singleMismatches;
                        ok += (matchList.size() - singleMismatches);
                    }
                    else
                    {
                        if (firstAtomsExpected[0] == -1)
                        {
                            mismatches += singleMismatches;
                            ok++;
                        }
                        else
                        {
                            mismatches += singleMismatches;
                            ok += (matchList.size() - singleMismatches);
                        }
                    }

                    if ((wronglyRecognized.size() != 0) ||
                            (notRecognized.size() != 0) || printAllMatches)
                    {
                        ps.println(delimiter);
                        ps.println("check " + description + ": " +
                            pSMARTS.getSmarts());
                        ps.println(" check molecule: " + molName);

                        if (printAllMatches)
                        {
                            for (int nn = 0; nn < matchList.size(); nn++)
                            {
                                iaTmp = (int[]) matchList.get(nn);
                                printMatch(ps, iaTmp);
                            }
                        }
                    }

                    if (wronglyRecognized.size() != 0)
                    {
                        ps.println("    Too many matches.");

                        BasicIntInt ii;

                        for (int k = 0; k < wronglyRecognized.size(); k++)
                        {
                            ii = (BasicIntInt) wronglyRecognized.get(k);
                            ps.println("    Match at atom " + ii.intValue2 +
                                " should not occur.");

                            iaTmp = (int[]) matchList.get(ii.intValue1);
                            printMatch(ps, iaTmp);
                        }
                    }

                    if (notRecognized.size() != 0)
                    {
                        ps.println("    Missing matches.");

                        int tmp;

                        for (int k = 0; k < notRecognized.size(); k++)
                        {
                            tmp = ((Integer) notRecognized.get(k)).intValue();

                            if (tmp == -1)
                            {
                                ps.println("    Expected matches are missing.");
                            }
                            else
                            {
                                ps.println("    Expected match at atom " + tmp +
                                    " is missing.");
                            }

                            if (!printAllMatches)
                            {
                                for (int nn = 0; nn < matchList.size(); nn++)
                                {
                                    iaTmp = (int[]) matchList.get(nn);
                                    printMatch(ps, iaTmp);
                                }
                            }
                        }
                    }
                }
            }
        }

        ps.println(delimiter);
        ps.println("Correct matches: " + ok);
        ps.println("Mismatches:      " + mismatches);
        ps.println(delimiter);
        ps.println("Parsed SMARTS patterns:\n");

        for (int i = 0; i < smarts.size(); i++)
        {
            pSMARTS = (SMARTSPatternMatcher) smarts.get(i);

            ps.println(pSMARTS.getSmarts() + " " + smartsDescription.get(i));
            ps.println(pSMARTS);
            ps.println(delimiter);
        }
    }

    /**
    * @param evaluationFile
    */
    private void loadEvaluationFile(String evaluationFile)
    {
        List evalLines = BasicResourceLoader.readLines(evaluationFile);

        smarts = new Vector(evalLines.size());
        smartsDescription = new Vector(evalLines.size());
        matchesMols = new Vector(evalLines.size());
        matchesAtomIdx = new Vector(evalLines.size());

        String line;
        Vector lineV = new Vector();
        String smartsPattern;
        String matches;

        //Vector matchesV = new Vector();
        String singleMatch;
        Vector singleMatchV = new Vector();

        for (int i = 0; i < evalLines.size(); i++)
        {
            line = (String) evalLines.get(i);

            //System.out.println("------------------------------");
            HelperMethods.tokenize(lineV, line, " \t\r\n");

            // parse, initialize and generate SMARTS pattern
            // to allow fast pattern matching
            smartsPattern = (String) lineV.get(0);

            SMARTSPatternMatcher parsedSmarts = new BasicSMARTSPatternMatcher();

            if (!parsedSmarts.init(smartsPattern))
            {
                logger.error("Invalid SMARTS pattern :" + smartsPattern);

                continue;
            }

            if (lineV.size() > 2)
            {
                Vector atomIdxList = null;
                Vector matchMolNames = null;
                int size = lineV.size() - 2;
                atomIdxList = new Vector(size);
                matchMolNames = new Vector(size);

                // parse SMART matches
                for (int j = 2; j < lineV.size(); j++)
                {
                    matches = (String) lineV.get(j);
                    singleMatch = matches;
                    HelperMethods.tokenize(singleMatchV, singleMatch, "#");

                    String moleculeName = ((String) singleMatchV.get(0)).trim();

                    //System.out.println("molName:" + moleculeName);
                    if (singleMatchV.size() > 1)
                    {
                        int[] ia = new int[singleMatchV.size() - 1];

                        for (int index = 1; index < singleMatchV.size();
                                index++)
                        {
                            //System.out.print(singleMatchV.get(index) + " ");
                            ia[index - 1] = Integer.parseInt(
                                    ((String) singleMatchV.get(index)).trim());
                        }

                        //System.out.println();
                        if (molecules.containsKey(moleculeName))
                        {
                            atomIdxList.add(ia);
                            matchMolNames.add(moleculeName);
                        }
                        else
                        {
                            logger.error("Molecule " + moleculeName +
                                " is not available in the SDF files.");

                            continue;
                        }
                    }
                    else
                    {
                        //logger.warn(
                        //      "No atom matching indices defined for molecule: '"
                        //              + moleculeName
                        //              + "'");
                        matchMolNames.add(moleculeName);
                        atomIdxList.add(null);
                    }
                }

                matchesMols.add(matchMolNames);
                matchesAtomIdx.add(atomIdxList);

                smarts.add(parsedSmarts);

                //System.out.println("parsed SMARTS: " + parsedSmarts);
                smartsDescription.add(lineV.get(1));

                //                              System.out.println("----------------------------------");
                //                              System.out.println(
                //                                      parsedSmarts.getSMARTS()
                //                                              + " "
                //                                              + lineV.get(1)
                //                                              + " "
                //                                              + matchMolNames
                //                                              + " "
                //                                              + atomIdxList);
            }
            else
            {
                logger.error("No matching molecule defined for: " +
                    smartsPattern);

                continue;
            }
        }
    }

    /**
     * @param molDirectory
     */
    private void loadMolecules(String molDirectory)
    {
        String FILTER = ".*mol";
        File dir = new File(molDirectory);
        File[] files = dir.listFiles(new BasicRegExpFilenameFilter(FILTER));

        if (files == null)
        {
            logger.error("No files where found in: " + dir);
            System.exit(1);
        }

        logger.info("" + files.length + " files where found in: " + dir);
        molecules = new Hashtable(files.length);

        BasicIOType inType = BasicIOTypeHolder.instance().getIOType("SDF");
        String filename = null;
        FileInputStream in = null;
        MoleculeFileIO loader = null;
        Molecule mol = null;

        for (int i = 0; i < files.length; i++)
        {
            filename = files[i].toString();

            try
            {
                in = new FileInputStream(filename);
                loader = MoleculeFileHelper.getMolReader(in, inType);
            }
            catch (FileNotFoundException ex)
            {
                logger.error("Can not find input file: " + filename);
                System.exit(1);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            if (!loader.readable())
            {
                logger.error(inType.getRepresentation() + " is not readable.");
                logger.error("You're invited to write one !;-)");
                System.exit(1);
            }

            boolean success = true;

            for (;;)
            {
                mol = new BasicConformerMolecule(inType, inType);
                mol.clear();

                try
                {
                    success = loader.read(mol);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(1);
                }
                catch (MoleculeIOException ex)
                {
                    ex.printStackTrace();
                    logger.info("Molecule was skipped: " + mol.getTitle());

                    continue;
                }

                if (!success)
                {
                    break;
                }
                else
                {
                    //System.out.println("Loaded " +mol.getTitle()+": "+success + " Empty: " + mol.empty());
                    molecules.put(mol.getTitle().trim(), mol);
                }
            }
        }

        // store all loaded molecules in a vector
        moleculesV = new Vector(molecules.size());

        for (Enumeration e = molecules.keys(); e.hasMoreElements();)
        {
            moleculesV.add(e.nextElement());
        }
    }

    /**
     * @param ps
     * @param iaTmp
     */
    private final void printMatch(PrintfStream ps, int[] iaTmp)
    {
        ps.print("    match : ");

        for (int jj = 0; jj < iaTmp.length; jj++)
        {
            ps.print(iaTmp[jj] + " ");
        }

        ps.println();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
