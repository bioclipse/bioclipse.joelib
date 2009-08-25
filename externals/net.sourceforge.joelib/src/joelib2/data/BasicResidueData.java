///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicResidueData.java,v $
//  Purpose:  Type table.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
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

import joelib2.molecule.Molecule;

import joelib2.util.BitVector;

import joelib2.util.types.BasicStringInt;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Type table.
 *
 * @.author     wegnerj
 * @.wikipedia  Amino acid
 * @.wikipedia  Residue
 * @.wikipedia Molecule
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:29 $
 */
public class BasicResidueData extends AbstractDataHolder
    implements IdentifierHardDependencies, ResidueData
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(BasicResidueData.class
            .getName());
    private static BasicResidueData residueData;
    private static final String DEFAULT_RESOURCE =
        "joelib2/data/plain/residue.txt";
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.3 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";

    //~ Instance fields ////////////////////////////////////////////////////////

    private List _resatoms;
    private List _resbonds;
    private List _resname;
    private List _vatmtmp;
    private List _vtmp;
    private String releaseDate;
    private String releaseVersion;
    private String vendor;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOETypeTable object
     */
    private BasicResidueData()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        _resname = new Vector();
        _resatoms = new Vector();
        _resbonds = new Vector();

        _vatmtmp = new Vector();
        _vtmp = new Vector();

        vendor = VENDOR;
        releaseVersion = IdentifierExpertSystem.transformCVStag(
                RELEASE_VERSION);
        releaseDate = IdentifierExpertSystem.transformCVStag(RELEASE_DATE);

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicResidueData instance()
    {
        if (residueData == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " + BasicResidueData.class.getName() +
                    " instance.");
            }

            residueData = new BasicResidueData();
        }

        return residueData;
    }

    public boolean assignBonds(Molecule mol, BitVector bv)
    {
        //  Atom a1,a2;
        //  JOEResidue r1,r2;
        //  vector<OEAtom*>::iterator i,j;
        //
        //  //assign alpha peptide bonds
        //  for (a1 = mol.BeginAtom(i);a1;a1 = mol.NextAtom(i))
        //    if (bv.bitIsOn(a1.getIdx()))
        //      {
        //      r1 = a1.getResidue();
        //      if (!(r1.getAtomID(a1) == "C")) continue;
        //      for (j=i,a2 = mol.NextAtom(j);a2;a2 = mol.NextAtom(j))
        //        {
        //          r2 = a2.getResidue();
        //          if (!(r2.getAtomID(a2) == "N")) continue;
        //          if (r1.getNum() < r2.getNum()-1) break;
        //          if (r1.getChainNum() == r2.getChainNum())
        //            {
        //              mol.AddBond(a1.getIdx(),a2.getIdx(),1);
        //              break;
        //            }
        //        }
        //      }
        //
        //  Vector v;
        //  int bo,skipres=0;
        //  string rname = "";
        //  //assign other residue bonds
        //  for (a1 = mol.BeginAtom(i);a1;a1 = mol.NextAtom(i))
        //    {
        //      r1 = a1.getResidue();
        //      if (skipres && r1.getNum() == skipres) continue;
        //
        //      if (r1.getName() != rname)
        //      {
        //        skipres = setResName(r1.getName()) ? 0 : r1.getNum();
        //        rname = r1.getName();
        //      }
        //      //assign bonds for each atom
        //      for (j=i,a2 = mol.NextAtom(j);a2;a2 = mol.NextAtom(j))
        //      {
        //        r2 = a2.getResidue();
        //        if (r1.getNum() != r2.getNum()) break;
        //        if (r1.getName() != r2.getName()) break;
        //
        //        if ((bo = lookupBO(r1.getAtomID(a1),r2.getAtomID(a2))))
        //          {
        //            v = a1.getVector() - a2.getVector();
        //            if (v.length_2() < 3.5) //float check by distance
        //                mol.addBond(a1.getIdx(),a2.getIdx(),bo);
        //          }
        //      }
        //    }
        //
        //  int hyb;
        //  string type;
        //
        //  //types and hybridization
        //  for (a1 = mol.BeginAtom(i);a1;a1 = mol.NextAtom(i))
        //    {
        //      if (a1.isOxygen() && !a1.getValence())
        //      {
        //        a1.setType("O3");
        //        continue;
        //      }
        //
        //      if (a1.isHydrogen())
        //      {
        //        a1.setType("H");
        //        continue;
        //      }
        //
        //      r1 = a1.getResidue();
        //      if (skipres && r1.getNum() == skipres) continue;
        //
        //      if (r1.getName() != rname)
        //      {
        //        skipres = setResName(r1.getName()) ? 0 : r1.getNum();
        //        rname = r1.getName();
        //      }
        //
        //      //***valence rule for O-
        //      if (a1.isOxygen() && a1.getValence() == 1)
        //      {
        //        Bond bond;
        //        bond = *(a1.beginBonds());
        //        if (bond.getBO() == 2)
        //          {
        //            a1.setType("O2"); a1.setHyb(2);
        //          }
        //        if (bond.getBO() == 1)
        //          {
        //            a1.setType("O-"); a1.setHyb(3);
        //            a1.setFormalCharge(-1);
        //          }
        //      }
        //      else
        //      if (lookupType(r1.getAtomID(a1),type,hyb))
        //        {
        //          a1.setType(type);
        //          a1.setHyb(hyb);
        //        }
        //      else // try to figure it out by bond order ???
        //        {
        //        }
        //    }
        return (true);
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return releaseDate;
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return releaseVersion;
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return vendor;
    }

    public int lookupBO(String s)
    {
        //    if (_resnum == -1) return(0);
        //
        //     int i;
        //    for (i = 0;i < _resbonds[_resnum].size();i++)
        //      if (_resbonds[_resnum][i].first == s)
        //          return(_resbonds[_resnum][i].second);
        return (0);
    }

    public int lookupBO(String s1, String s2)
    {
        //    if (_resnum == -1) return(0);
        //    string s;
        //
        //    s = (s1 < s2) ? s1 + " " + s2 : s2 + " " + s1;
        //
        //     int i;
        //    for (i = 0;i < _resbonds[_resnum].size();i++)
        //      if (_resbonds[_resnum][i].first == s)
        //          return(_resbonds[_resnum][i].second);
        return (0);
    }

    public BasicStringInt lookupType(String atmid)
    {
        //    if (_resnum == -1) return(false);
        //
        //    String s;
        //    //vector<string>::iterator i;
        //    String type;
        //    int hyb;
        //    for (i = _resatoms[_resnum].begin();i != _resatoms[_resnum].end();i+=3)
        //      if (atmid == i)
        //      {
        //        i++;    type = i;
        //        i++;    hyb = atoi((i).c_str());
        //
        //        return new StringInt(type, hyb);
        //      }
        return null;
    }

    public boolean setResName(String s)
    {
        //   int i;
        //    for (i = 0;i < _resname.size();i++)
        //      if (_resname[i] == s)
        //      {
        //          _resnum = i;
        //          return(true);
        //      }
        //
        //    _resnum = -1;
        return (false);
    }

    protected void parseLine(String buffer)
    {
        //   int bo;
        //   String s;
        //
        //
        //    if (!buffer.trim().equals("") && buffer.charAt(0) != '#')
        //    {
        //      Vector      vs   = new Vector();
        //      // of type String
        //      JHM.instance().tokenize(vs, buffer);
        //      if (vs.size()!=0)
        //       {
        //          s = (String) vs.get(0);
        //          if ( s.equals("BOND") && vs.size() == 4 )
        //            {
        //                      s = (vs[1] < vs[2]) ? vs[1] + " " + vs[2] :
        //                      vs[2] + " " + vs[1];
        //                      bo = atoi(vs[3].c_str());
        //                      _vtmp.add(pair<string,int> (s,bo));
        //            }
        //
        //          if ( s.equals("ATOM") && vs.size() == 4)
        //            {
        //                      _vatmtmp.add(vs[1]);
        //                      _vatmtmp.add(vs[2]);
        //                      _vatmtmp.add(vs[3]);
        //            }
        //
        //          if ( s.equals("RES") )
        //            { _resname.add(vs[1]);
        //            }
        //
        //          if ( s.equals("END") )
        //            {
        //                      _resatoms.add(_vatmtmp);
        //                      _resbonds.add(_vtmp);
        //                      _vtmp.clear();
        //                      _vatmtmp.clear();
        //            }
        //        }
        //    }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
