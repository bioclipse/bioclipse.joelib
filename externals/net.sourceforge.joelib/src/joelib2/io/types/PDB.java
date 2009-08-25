///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: PDB.java,v $
//  Purpose:  Reader/Writer for Undefined files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2006/07/24 22:29:16 $
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
package joelib2.io.types;

import cformat.PrintfFormat;
import cformat.PrintfStream;

import joelib2.data.BasicElementHolder;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicResidue;
import joelib2.molecule.types.Residue;

import joelib2.util.iterator.NbrAtomIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 *  Reader/Writer for Protein DataBank (PDB) files.
 *
 * @.author     wegnerj
 * @.wikipedia  Protein Data Bank
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2006/07/24 22:29:16 $
 * @.cite pdbFormat
 */
public class PDB implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            PDB.class.getName());
    private final static String description = "Protein Data Bank";
    private final static String[] extensions = new String[]{"pdb","ent"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private LineNumberReader lnr;
    private PrintfStream ps;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Undefined object
     */
    public PDB()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    public void closeReader() throws IOException
    {
    }

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    public void closeWriter() throws IOException
    {
    }

    /**
     *  Description of the Method
     *
     * @param  is               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public void initReader(InputStream is) throws IOException
    {
        lnr = new LineNumberReader(new InputStreamReader(is));
    }

    /**
     *  Description of the Method
     *
     * @param  os               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException
    {
        ps = new PrintfStream(os);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String inputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return extensions;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String outputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String[] outputFileExtensions()
    {
        return extensions;
    }

    /**
     *  Reads an molecule entry as (unparsed) <tt>String</tt> representation.
     *
     * @return                  <tt>null</tt> if the reader contains no more
     *      relevant data. Otherwise the <tt>String</tt> representation of the
     *      whole molecule entry is returned.
     * @exception  IOException  typical IOException
     */
    public String read() throws IOException
    {
    	// if ENDMOL available, use that
    	// otherwise use last TER
        logger.error(
            "Reading protein database entry as String representation is not implemented yet !!!");

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException, MoleculeIOException
    {
        return read(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  title                    Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public boolean read(Molecule mol, String title) throws IOException,
        MoleculeIOException
    {    	
        //          res->SetChainNum(chainNum);
        //          res->SetName(resname);
        //          res->SetNum(rnum);
    	
        //  resdat.Init();
        //  int chainNum = 1;
        //  char buffer[BUFF_SIZE];
        //  OEBitVec bs;
        //
        //  mol.BeginModify();
        //  while (ifs.getline(buffer,BUFF_SIZE) && !EQn(buffer,"END",3))
        //  {
        //    if (EQn(buffer,"TER",3)) chainNum++;
        //    if (EQn(buffer,"ATOM",4) || EQn(buffer,"HETATM",6))
        //      {
        //      ParseAtomRecord(buffer,mol,chainNum);
        //      if (EQn(buffer,"ATOM",4))
        //        bs.SetBitOn(mol.NumAtoms());
        //      }
        //
        //    if (EQn(buffer,"CONECT",6))
        //      ParseConectRecord(buffer,mol);
        //  }
        //
        //  resdat.AssignBonds(mol,bs);
        //  /*assign hetatm bonds based on distance*/
        //  mol.ConnectTheDots();
        //
        //  mol.EndModify();
        //  mol.SetAtomTypesPerceived();
        //  atomtyper.AssignImplicitValence(mol);
        //
        //  if (!mol.NumAtoms()) return(false);
        //  return(true);
        return (false);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean readable()
    {
        return false;
    }

    /**
     *  Description of the Method
     *
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean skipReaderEntry() throws IOException
    {
        String line;

        while ((line = lnr.readLine()) != null)
        {
            if ((line.length() > 0) && (line.charAt(0) == 'E') &&
                    (line.indexOf("END") != -1))
            {
                break;
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException, MoleculeIOException
    {
        return write(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  title                    Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException,
        MoleculeIOException
    {
        // write header
        ps.println("HEADER    PROTEIN");

        // write title
        String tmpTitle = null;

        if (title == null)
        {
            tmpTitle = mol.getTitle();

            if ((tmpTitle == null) || (tmpTitle.trim().length() == 0))
            {
                ps.println("COMPND    UNNAMED");
            }
        }
        else
        {
            tmpTitle = title;
        }

        if (tmpTitle != null)
        {
            ps.print("COMPND    ");
            ps.print(tmpTitle);
            ps.println(' ');
        }

        // write author
        ps.println("AUTHOR    GENERATED BY JOELIB " /*+JHM.version() */);

        // initialize helper classes
        BasicElementHolder etab = BasicElementHolder.instance();
        PrintfFormat d3 = new PrintfFormat("%3d");
        PrintfFormat d4 = new PrintfFormat("%4d");
        PrintfFormat d5 = new PrintfFormat("%5d");
        PrintfFormat s3 = new PrintfFormat("%3s");
        PrintfFormat s4 = new PrintfFormat("%-4s");
        PrintfFormat f9_3 = new PrintfFormat("%9.3f");
        PrintfFormat f8_3 = new PrintfFormat("%8.3f");

        // write atoms
        Atom atom;
        Residue res;
        String typeName;
        String paddedName = null;
        int resNumber;
        String resName;

        for (int i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);
            typeName = etab.getSymbol(atom.getAtomicNumber());

            if (typeName.length() > 1)
            {
                typeName = typeName.toUpperCase();
            }

            // has nothing to do with PDB, only used for a qick and dirty autodock export
            //      else
            //      {
            //        if(typeName.charAt(0)='C' && atom.isAromatic())typeName="A";
            //      }
            if (atom.hasResidue())
            {
                res = atom.getResidue();
                resName = res.getName();
                typeName = res.getAtomID(atom);
                resNumber = res.getNumber();
            }
            else
            {
                resName = "UNK";

                if (typeName.length() == 2)
                {
                    paddedName = typeName;
                }
                else if (typeName.length() == 1)
                {
                    paddedName = " " + typeName;
                }
                else if (typeName.length() == 0)
                {
                    paddedName = "  ";
                }
                else
                {
                    paddedName = typeName.substring(0, 2);
                }

                typeName = paddedName;
                resNumber = 1;
            }

            ps.print("ATOM   ");
            ps.printf(d4, i);
            ps.print("  ");
            ps.printf(s4, typeName);
            ps.printf(s3, resName);
            ps.printf(s3, "");
            ps.printf(d3, resNumber);
            ps.print("   ");
            ps.printf(f9_3, atom.get3Dx());
            ps.printf(f8_3, atom.get3Dy());
            ps.printf(f8_3, atom.get3Dz());
            ps.print("  ");
            ps.print("1.00");
            ps.print("  ");
            ps.print("0.00");
            ps.println(" ");
        }

        // write bonds
        Atom nbr;
        NbrAtomIterator nait;

        for (int i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);

            if (atom.getValence() != 0)
            {
                ps.print("CONECT ");
                ps.printf(d5, i);
                nait = atom.nbrAtomIterator();

                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();
                    ps.printf(d5, nbr.getIndex());
                }

                ps.println();
            }
        }

        ps.print("MASTER        0    0    0    0    0    0    0    0 ");
        ps.printf(d4, mol.getAtomsSize());
        ps.print("    0 ");
        ps.printf(d4, mol.getAtomsSize());
        ps.println("    0");

        ps.println("END");

        return (true);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean writeable()
    {
        return true;
    }

    static boolean parseAtomRecord(String line, Molecule mol, int chainNum)
    {
        //ATOMFORMAT "(i5,1x,a4,a1,a3,1x,a1,i4,a1,3x,3f8.3,2f6.2,1x,i3)"
        //  string sbuf = &buffer[6];
        //  if (sbuf.size() < 48) return(false);
        //
        //  bool hetatm = (EQn(buffer,"HETATM",6)) ? true : false;
        //
        //  /* serial number */
        //  string serno = sbuf.substr(0,5);
        //  //SerialNum(the_atom) = atoi(tmp_str);
        //
        //  /* atom name */
        //  string atmid = sbuf.substr(6,4);
        //
        //  //trim spaces on the right and left sides
        //  while (!atmid.empty() && atmid[0] == ' ')
        //    atmid = atmid.substr(1,atmid.size()-1);
        //
        //  while (!atmid.empty() && atmid[atmid.size()-1] == ' ')
        //    atmid = atmid.substr(0,atmid.size()-1);
        //
        //  /* residue name */
        //
        //  string resname = sbuf.substr(11,3);
        //  if (resname == "   ")
        //    resname = "UNK";
        //  else
        //  {
        //    while (!resname.empty() && resname[0] == ' ')
        //      resname = resname.substr(1,resname.size()-1);
        //
        //    while (!resname.empty() && resname[resname.size()-1] == ' ')
        //      resname = resname.substr(0,resname.size()-1);
        //  }
        //
        //  /* residue sequence number */
        //
        //  string resnum = sbuf.substr(16,4);
        //
        //  /* X, Y, Z */
        //  string xstr = sbuf.substr(24,8);
        //  string ystr = sbuf.substr(32,8);
        //  string zstr = sbuf.substr(40,8);
        //
        //  string type;
        //
        //  if (EQn(buffer,"ATOM",4))
        //  {
        //    type = atmid.substr(0,1);
        //    if (isdigit(type[0]))
        //       type = atmid.substr(1,1);
        //
        //    if (resname.substr(0,2) == "AS" || resname[0] == 'N')
        //    {
        //      if (atmid == "AD1") type = "O";
        //      if (atmid == "AD2") type = "N";
        //    }
        //    if (resname.substr(0,3) == "HIS" || resname[0] == 'H')
        //    {
        //      if (atmid == "AD1" || atmid == "AE2") type = "N";
        //      if (atmid == "AE1" || atmid == "AD2") type = "C";
        //    }
        //    if (resname.substr(0,2) == "GL" || resname[0] == 'Q')
        //    {
        //      if (atmid == "AE1") type = "O";
        //      if (atmid == "AE2") type = "N";
        //    }
        //  }
        //  else //must be hetatm record
        //  {
        //    if (isalpha(atmid[0])) type = atmid.substr(0,1);
        //    else                   type = atmid.substr(1,1);
        //    if (atmid == resname)
        //      {
        //      type = atmid;
        //      if (type.size() == 2) type[1] = tolower(type[1]);
        //      }
        //    else
        //    if (resname == "ADR" || resname == "COA" || resname == "FAD" ||
        //      resname == "GPG" || resname == "NAD" || resname == "NAL" ||
        //      resname == "NDP")
        //      {
        //      if (type.size() > 1)
        //        type = type.substr(0,1);
        //      //type.erase(1,type.size()-1);
        //      }
        //    else
        //      if (isdigit(type[0]))
        //      {
        //        type = type.substr(1,1);
        //        //type.erase(0,1);
        //        //if (type.size() > 1) type.erase(1,type.size()-1);
        //      }
        //      else
        //      if (type.size() > 1 && isdigit(type[1]))
        //        type = type.substr(0,1);
        //    //type.erase(1,1);
        //      else
        //        if (type.size() > 1 && isalpha(type[1]) && isupper(type[1]))
        //          type[1] = tolower(type[1]);
        //
        //  }
        //
        //  OEAtom atom;
        //  Vector v(atof(xstr.c_str()),atof(ystr.c_str()),atof(zstr.c_str()));
        //  atom.SetVector(v);
        //
        //  atom.SetAtomicNum(etab.GetAtomicNum(type.c_str()));
        //  atom.SetType(type);
        //
        //  int        rnum = atoi(resnum.c_str());
        //  OEResidue *res  = (mol.NumResidues() > 0) ? mol.GetResidue(mol.NumResidues()-1) : NULL;
        //  if (res == NULL || res->GetName() != resname || res->GetNum() != rnum)
        //  {
        //      vector<OEResidue*>::iterator ri;
        //      for (res = mol.BeginResidue(ri) ; res ; res = mol.NextResidue(ri))
        //          if (res->GetName() == resname && res->GetNum() == rnum)
        //              break;
        //
        //      if (res == NULL)
        //      {
        //          res = mol.NewResidue();
        //          res->SetChainNum(chainNum);
        //          res->SetName(resname);
        //          res->SetNum(rnum);
        //      }
        //  }
        //
        //  if (!mol.AddAtom(atom))
        //      return(false);
        //  else
        //  {
        //      OEAtom *atom = mol.GetAtom(mol.NumAtoms());
        //
        //      res->AddAtom(atom);
        //      res->SetSerialNum(atom, atoi(serno.c_str()));
        //      res->SetAtomID(atom, atmid);
        //      res->SetHetAtom(atom, hetatm);
        //
        //      return(true);
        //  }
        return false;
    }

    //
    //static bool ParseConectRecord(char *buffer,OEMol &mol)
    //{
    //  vector<string> vs;
    //
    //  buffer[70] = '\0';
    //  tokenize(vs,buffer);
    //  if (vs.empty()) return(false);
    //  vs.erase(vs.begin());
    //  int con1,con2,con3,con4;
    //  con1 = con2 = con3 = con4 = 0;
    //  int start = atoi(vs[0].c_str());
    //
    //  if (vs.size() > 1) con1 = atoi(vs[1].c_str());
    //  if (vs.size() > 2) con2 = atoi(vs[2].c_str());
    //  if (vs.size() > 3) con3 = atoi(vs[3].c_str());
    //  if (vs.size() > 4) con4 = atoi(vs[4].c_str());
    //  if (!con1) return(false);
    //
    //  OEAtom *a1,*a2;
    //  OEResidue *r1,*r2;
    //  vector<OEAtom*>::iterator i,j;
    //  for (a1 = mol.BeginAtom(i);a1;a1 = mol.NextAtom(i))
    //    {
    //      r1 = a1->GetResidue();
    //      if (r1->GetSerialNum(a1) == start)
    //    for (a2 = mol.BeginAtom(j);a2;a2 = mol.NextAtom(j))
    //      {
    //        r2 = a2->GetResidue();
    //        if (con1 && r2->GetSerialNum(a2) == con1)
    //          mol.AddBond(a1->GetIdx(),a2->GetIdx(),1);
    //        if (con2 && r2->GetSerialNum(a2) == con2)
    //          mol.AddBond(a1->GetIdx(),a2->GetIdx(),1);
    //        if (con3 && r2->GetSerialNum(a2) == con3)
    //          mol.AddBond(a1->GetIdx(),a2->GetIdx(),1);
    //        if (con4 && r2->GetSerialNum(a2) == con4)
    //          mol.AddBond(a1->GetIdx(),a2->GetIdx(),1);
    //      }
    //    }
    //
    //  return(true);
    //}
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
