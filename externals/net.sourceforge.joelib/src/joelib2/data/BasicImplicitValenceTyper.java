///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicImplicitValenceTyper.java,v $
//  Purpose:  Atom typer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:29 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
//
//  Copyright (c) Dept. Computer Architecture, University of Tuebingen,
//                Germany, 2001-2005
//, 2003-2005
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

import joelib2.feature.types.atomlabel.AtomHybridisation;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.smarts.types.BasicSMARTSPatternInt;
import joelib2.smarts.types.BasicSMARTSPatternString;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom typer based on structural expert rules.
 * The definition file can be defined in the
 * <tt>joelib2.data.JOEAtomTyper.resourceFile</tt> property in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * For assigning atom types using a geometry-based algorithm have a look at {@.cite ml91} and the dot
 * connecting method {@link joelib2.molecule.Molecule#connectTheDots()}.
 *
 * <p>
 * Default:<br>
 * joelib2.data.JOEAtomTyper.resourceFile=<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib/src/joelib2/data/plain/atomtype.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup">joelib2/data/plain/atomtype.txt</a>
 *
 * @.author     wegnerj
 * @.wikipedia  Valency
 * @.wikipedia Orbital hybridisation
 * @.wikipedia Molecule
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 * @see wsi.ra.tool.BasicPropertyHolder
 * @see wsi.ra.tool.BasicResourceLoader
 */
public class BasicImplicitValenceTyper extends AbstractDataHolder
    implements IdentifierHardDependencies, ImplicitValenceTyper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.7 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";

    private static Category logger = Category.getInstance(
            BasicImplicitValenceTyper.class.getName());
    private static BasicImplicitValenceTyper impValTyper;
    protected static final String DEFAULT_RESOURCE =
        "joelib2/data/plain/implicitValence.txt";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            BasicHybridisationTyper.class, BasicSMARTSPatternMatcher.class
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private List<BasicSMARTSPatternString> externalTypeRule;
    private List<BasicSMARTSPatternInt> hybridisation;
    private List<BasicSMARTSPatternInt> impliciteValence;

    //~ Constructors ///////////////////////////////////////////////////////////

    // of type SMARTSPatternString

    /**
     *  Constructor for the JOEAtomTyper object
     */
    private BasicImplicitValenceTyper()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        impliciteValence = new Vector<BasicSMARTSPatternInt>();

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);

        logger.info("Using implicit valence model: " + resourceFile);
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
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicImplicitValenceTyper instance()
    {
        if (impValTyper == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " + BasicAtomTyper.class.getName() +
                    " instance.");
            }

            impValTyper = new BasicImplicitValenceTyper();
        }

        return impValTyper;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    public void correctAromaticNitrogens(Molecule mol)
    {
        if (!initialized)
        {
            init();
        }

        if (mol.hasAromaticCorrected())
        {
            return;
        }

        mol.setAromaticCorrected();

        //    int j;
        //    Atom atom, nbr, a;
        //    JOEBitVec curr=new JOEBitVec();
        //    JOEBitVec used=new JOEBitVec();
        //    JOEBitVec next=new JOEBitVec();
        //    Vector v_N=new Vector();
        //    Vector v_OS=new Vector();    // of type Atom
        //
        //    Vector _aromNH = new Vector();     // of type boolean[1]
        //    boolean btmp[];
        //    _aromNH.clear();
        //    _aromNH.setSize(mol.numAtoms()+1);
        //
        //    AtomIterator ait1 = mol.atomIterator();
        //    while(ait1.hasNext())
        //    {
        //      atom = ait1.nextAtom();
        //      if (atom.isAromatic() && !atom.isCarbon() && !used.get(atom.getIdx()) )
        //      {
        //        Vector rsys=new Vector(); // of type Atom
        //        rsys.add(atom);
        //        curr.set( atom.getIdx() );
        //        used.set( atom.getIdx() );
        //
        //        while (curr.size()!=0)
        //          {
        //                next.clear();
        //                //for(j=curr.nextSetBit(0); j>=0; j=curr.nextSetBit(j+1))
        //                for (j = curr.nextBit(-1);j != curr.endBit();j = curr.nextBit(j))
        //                {
        //            atom = mol.getAtom(j);
        //
        //            NbrAtomIterator nait = atom.nbrAtomIterator();
        //            while(nait.hasNext())
        //            {
        //              nbr = nait.nextNbrAtom();
        //              if (!nait.actualBond().isAromatic()) continue;
        //                        if ( used.get(nbr.getIdx()) ) continue;
        //
        //                        rsys.add(nbr);
        //                        next.set( nbr.getIdx() );
        //                        used.set( nbr.getIdx() );
        //            }
        //            }
        //
        //            curr.set(next);
        //          }
        //
        //        //count number of electrons in the ring system
        //        v_N.clear();
        //        v_OS.clear();
        //        int nelectrons = 0;
        //
        //        for (int m=0; m< rsys.size(); m++)
        //              {
        //                a = (Atom) rsys.get(m);
        //
        //                boolean hasExoDoubleBond = false;
        //
        //                NbrAtomIterator nait = a.nbrAtomIterator();
        //          while(nait.hasNext())
        //          {
        //            nbr = nait.nextNbrAtom();
        //                  if (nait.actualBond().getBO() == 2 && !nait.actualBond().isInRing())
        //                    if (nbr.isOxygen() || nbr.isSulfur() || nbr.isNitrogen())
        //                            hasExoDoubleBond = true;
        //          }
        //
        //                if (a.isCarbon() && hasExoDoubleBond) continue;
        //
        //                if (a.isCarbon()) nelectrons++;
        //                else
        //                {
        //                  if (a.isOxygen() || a.isSulfur())
        //                  {
        //                      v_OS.add(a);
        //                      nelectrons += 2;
        //                  }
        //                  else
        //                  {
        //                    if (a.isNitrogen())
        //                      {
        //                        v_N.add(a); //store nitrogens
        //                        nelectrons++;
        //                      }
        //            }
        //          }
        //              }
        //
        //        //calculate what the number of electrons should be for aromaticity
        //        int naromatic = 2+4*((int)((double)(rsys.size()-2)*0.25+0.5));
        //
        //        if (nelectrons > naromatic) //try to give one electron back to O or S
        //        {
        //              for (int m=0; m< v_OS.size();m++)
        //          {
        //            a = (Atom) v_OS.get(m);
        //
        //            if (naromatic == nelectrons) break;
        //                if (a.getValence() == 2 && a.getHvyValence() == 2)
        //                {
        //                        nelectrons--;
        //                        a.setFormalCharge(1);
        //                }
        //              }
        //        }
        //
        //        if (v_N.size()==0) continue; //no nitrogens found in ring
        //
        //        //check for protonated nitrogens
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //
        //                if (naromatic == nelectrons) break;
        //                if (a.getValence() == 3 && a.getHvyValence() == 2)
        //                {
        //                  nelectrons++;
        //                  btmp = (boolean[])_aromNH.get(a.getIdx()); btmp[0] = true;
        //                }
        //                if (a.getFormalCharge() == -1)
        //                {
        //                  nelectrons++;
        //                  btmp = (boolean[])_aromNH.get(a.getIdx()); btmp[0] = false;
        //                }
        //              }
        //
        //         //charge up tert nitrogens
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //              if (a.getHvyValence() == 3 && a.BOSum() < 5) a.setFormalCharge(1);
        //        }
        //
        //        //try to uncharge nitrogens first
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //
        //                if (a.BOSum() > 4) continue; //skip n=O
        //                if (naromatic == nelectrons) break;
        //                if (a.getHvyValence() == 3)
        //                {
        //                  nelectrons++;
        //                  a.setFormalCharge(0);
        //                }
        //              }
        //
        //        if (naromatic == nelectrons) continue;
        //
        //        //try to protonate amides next
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //
        //                if (naromatic == nelectrons) break;
        //
        //          btmp = (boolean[])_aromNH.get(a.getIdx());
        //                if (a.isAmideNitrogen() && a.getValence() == 2 &&
        //                    a.getHvyValence() == 2 && !btmp[0])
        //                {
        //                  nelectrons++;
        //                  btmp[0] = true;
        //                }
        //              }
        //
        //        if (naromatic == nelectrons) continue;
        //
        //        //protonate amines in 5 membered rings first - try to match kekule
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //
        //                if (naromatic == nelectrons) break;
        //
        //          btmp = (boolean[])_aromNH.get(a.getIdx());
        //                if (a.getValence() == 2 && !btmp[0] &&
        //                    a.isInRingSize(5) && a.BOSum() == 2)
        //                {
        //                  nelectrons++;
        //                  btmp[0] = true;
        //                }
        //              }
        //
        //        if (naromatic == nelectrons) continue;
        //
        //        //protonate amines in 5 membered rings first - no kekule restriction
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //
        //                if (naromatic == nelectrons) break;
        //
        //          btmp = (boolean[])_aromNH.get(a.getIdx());
        //                if (a.getValence() == 2 && !btmp[0] &&
        //                    a.isInRingSize(5))
        //                {
        //                  nelectrons++;
        //                  btmp[0] = true;
        //                }
        //              }
        //
        //        if (naromatic == nelectrons) continue;
        //
        //        //then others -- try to find an atom w/o a double bond first
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //
        //                if (naromatic == nelectrons) break;
        //
        //          btmp = (boolean[])_aromNH.get(a.getIdx());
        //                if (a.getHvyValence() == 2 && !btmp[0] &&
        //                    !a.hasDoubleBond())
        //                {
        //                  nelectrons++;
        //                  btmp[0] = true;
        //                }
        //              }
        //
        //        for (int m=0; m< v_N.size();m++)
        //              {
        //                a = (Atom) v_N.get(m);
        //
        //                if (naromatic == nelectrons) break;
        //
        //          btmp = (boolean[])_aromNH.get(a.getIdx());
        //                if (a.getHvyValence() == 2 && !btmp[0])
        //                {
        //                  nelectrons++;
        //                  btmp[0] = true;
        //                }
        //              }
        //      }
        //    }
        //
        //    ait1.reset();
        //    while(ait1.hasNext())
        //    {
        //      atom = ait1.nextAtom();
        //          btmp = (boolean[])_aromNH.get(atom.getIdx());
        //      if (btmp[0] && atom.getValence() == 2) atom.setImplicitValence(3);
        //    }
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    public void getImplicitValence(Molecule mol, int[] impVal)
    {
        int[] itmp;

        if (!initialized)
        {
            init();
        }

        // ensure that hybridisations are already assigned
        if (mol.getAtomsSize() > 1)
        {
            //System.out.println("Calculate hybridisations");
            AtomHybridisation.getIntValue(mol.getAtom(1));
        }

        Atom atom;
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            impVal[atom.getIndex() - 1] = atom.getValence();
        }

        List<int[]> matchList;

        for (int i = 0; i < impliciteValence.size(); i++)
        {
            BasicSMARTSPatternInt pi = impliciteValence.get(i);

            if (pi.smartsValue.match(mol))
            {
                matchList = pi.smartsValue.getMatches();

                //System.out.println("assign imp. val.:"+pi.smartsValue.getSmarts()+" "+matchList.size()+" "+pi.intValue);
                for (int j = 0; j < matchList.size(); j++)
                {
                    itmp = matchList.get(j);
                    impVal[mol.getAtom(itmp[0]).getIndex() - 1] = pi.intValue;
                    //System.out.println("a:"+mol.getAtom(itmp[0]).getIndex()+" h:"+pi.intValue);
                }
            }
        }

        if (!mol.hasAromaticCorrected())
        {
            correctAromaticNitrogens(mol);
        }

        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (impVal[atom.getIndex() - 1] < atom.getValence())
            {
                impVal[atom.getIndex() - 1] = atom.getValence();
            }

            //System.out.println("impVal "+atom.getIndex()+" "+impVal[atom.getIndex() - 1]);
        }
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return BasicImplicitValenceTyper.getReleaseDate();
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return BasicImplicitValenceTyper.getReleaseVersion();
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return BasicImplicitValenceTyper.getVendor();
    }

    public boolean isValidType(String type)
    {
        return BasicAtomTypeConversionHolder.instance().isValidInternalType(
                type);
    }

    /**
     *  Description of the Method
     *
     * @param  buffer  Description of the Parameter
     */
    protected void parseLine(String buffer)
    {
        List<String> vs = new Vector<String>();

        HelperMethods.tokenize(vs, buffer);

        if ((vs.size() != 0) && (vs.size() >= 2))
        {
            if (vs.get(0).charAt(0) != '#')
            {
                SMARTSPatternMatcher sp = new BasicSMARTSPatternMatcher();

                if (sp.init((String) vs.get(0)))
                {
                    impliciteValence.add(new BasicSMARTSPatternInt(sp,
                            Integer.parseInt((String) vs.get(1))));
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
