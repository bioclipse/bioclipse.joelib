///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicMoleculeVector.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:36 $
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
package joelib2.molecule;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.BasicReader;
import joelib2.io.IOType;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Molecule vector to load single and multiple molecule files.
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:36 $
 *
 * @see #joelib2.io.IOHelper
 */
public class BasicMoleculeVector implements java.io.Serializable, MoleculeVector
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;
    private static Category logger = Category.getInstance(MoleculeVector.class
            .getName());
    private static final int DEFAULT_SIZE = 100;
    private static final String DEFAULT_IO = "SDF";

    //~ Instance fields ////////////////////////////////////////////////////////

    private List<Molecule> molvec;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Creates an empty molecule vector.
     */
    public BasicMoleculeVector()
    {
        molvec = new Vector<Molecule>(DEFAULT_SIZE);
    }

    /**
     *  Constructor for the JOEMolVector object
     *
     * @param  ifs                        Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public BasicMoleculeVector(InputStream ifs) throws IOException
    {
        read(ifs, BasicIOTypeHolder.instance().getIOType(DEFAULT_IO),
            BasicIOTypeHolder.instance().getIOType(DEFAULT_IO), -1);
    }

    /**
     *  Constructor for the JOEMolVector object
     *
     * @param  ifs                        Description of the Parameter
     * @param  nToRead                    Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public BasicMoleculeVector(InputStream ifs, int nToRead) throws IOException
    {
        read(ifs, BasicIOTypeHolder.instance().getIOType(DEFAULT_IO),
            BasicIOTypeHolder.instance().getIOType(DEFAULT_IO), nToRead);
    }

    /**
     *  Constructor for the JOEMolVector object
     *
     * @param  ifs                        Description of the Parameter
     * @param  in_type                    Description of the Parameter
     * @param  out_type                   Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public BasicMoleculeVector(InputStream ifs, final IOType in_type,
        final IOType out_type) throws IOException
    {
        read(ifs, in_type, out_type, -1);
    }

    /**
     *  Constructor for the JOEMolVector object
     *
     * @param  ifs                        Description of the Parameter
     * @param  in_type                    Description of the Parameter
     * @param  out_type                   Description of the Parameter
     * @param  nToRead                    Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public BasicMoleculeVector(InputStream ifs, final IOType in_type,
        final IOType out_type, int nToRead) throws IOException,
        MoleculeIOException
    {
        read(ifs, in_type, out_type, nToRead);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Add molecule to this molecule vector.
     *
     * @param  i    The new mol value
     * @param  mol  The new mol value
     */
    public void addMol(Molecule mol)
    {
        molvec.add(mol);
    }

    /**
     *  Functions for dealing with groups of molecules. MolVec will read either
     *  all molecules from a file or a set of conformers.
     */
    public void finalize() throws Throwable
    {
        //        for (int i = 0; i < _molvec.size(); i++)
        //        {
        //            Object obj = _molvec.get(i);
        //            obj = null;
        //        }
        molvec.clear();
        super.finalize();
    }

    /**
     *  Get a specific molecule from a OEMolVector. Index starts at zero.
     *
     * @param  index  Description of the Parameter
     * @return    The mol value
     */
    public Molecule getMol(int index)
    {
        Molecule mol = null;

        if (molvec != null)
        {
            if ((index >= 0) && (index < molvec.size()))
            {
                mol = (Molecule) molvec.get(index);
            }
            else
            {
                logger.error("Index " + index +
                    " out of range in JOEMolVector.getMol ");
            }
        }

        return mol;
    }

    /**
     *  Gets the size attribute of the JOEMolVector object
     *
     * @return    The size value
     */
    public int getSize()
    {
        if (molvec == null)
        {
            return -1;
        }

        return (molvec.size());
    }

    /**
     * @return    {@link java.util.List} of <tt>Molecule</tt>
     */
    public List getVector()
    {
        return (molvec);
    }

    /**
     *  Read all molecules from a file into a <code>Vector</code> of <code>Molecule</code>. Input and output
     *  types default to SDF.
     *
     * @param  ifs                        Description of the Parameter
     * @param  in_type                    Description of the Parameter
     * @param  out_type                   Description of the Parameter
     * @param  nToRead                    Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public void read(InputStream ifs, final IOType in_type,
        final IOType out_type, int nToRead) throws IOException
    {
        if (nToRead == -1)
        {
            molvec = new Vector<Molecule>(DEFAULT_SIZE);
        }
        else
        {
            molvec = new Vector<Molecule>(nToRead);
        }

        int nRead = 0;
        BasicReader reader = null;

        //        try
        //        {
        reader = new BasicReader(ifs, in_type);

        //        }
        //        catch (IOException ex)
        //        {
        //            throw ex;
        //        }
        Molecule mol;

        while (true)
        {
            // load only nToRead molecules
            if (nRead == nToRead)
            {
                break;
            }

            // create empty molecule
            mol = new BasicConformerMolecule(in_type, out_type);

            // load next molecule
            try
            {
                if (!reader.readNext(mol))
                {
                    break;
                }
            }
            catch (IOException ex)
            {
                throw ex;
            }
            catch (MoleculeIOException mex)
            {
                logger.warn("Skipping molecule entry. " + mex.getMessage());
            }

            // increment molecule counter and load next molecule
            nRead++;
            molvec.add(mol);
        }
    }

    /**
     *  Sets the molecule at position <tt>i</tt> of this molecule vector.
     *
     * @param  i    The new mol value
     * @param  mol  The new mol value
     */
    public Object setMol(int i, Molecule mol)
    {
        return molvec.set(i, mol);
    }

    /**
     *  Write a OEMolVector to a file. Output type defaults to SDF.
     *
     * @param  ofs                        Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public void write(OutputStream ofs) throws IOException, MoleculeIOException
    {
        BasicIOType type = BasicIOTypeHolder.instance().getIOType("SDF");
        write(ofs, type);
    }

    /**
     *  Write a OEMolVector to a file. Output type defaults to SDF.
     *
     * @param  ofs                        Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public void write(OutputStream ofs, IOType type) throws IOException,
        MoleculeIOException
    {
        MoleculeFileIO writer = null;

        writer = MoleculeFileHelper.getMolWriter(ofs, type);

        if (!writer.writeable())
        {
            logger.warn(type.getRepresentation() + " is not writeable.");

            return;
        }

        Molecule mol;

        for (int i = 0; i < molvec.size(); i++)
        {
            mol = (Molecule) molvec.get(i);

            //System.out.println("Store "+mol);
            writer.write(mol, mol.getTitle());
        }
    }

    //  public boolean readConfs(InputStream ifs, final IOType in_type, final IOType out_type)
    //  {
    //  Molecule mol;
    //  JOEFileFormat ff;
    //  String title,master;
    //
    //  _molvec.resize(0);
    //
    //  int i = 1;
    //  while (1)
    //    {
    //      mol = new OEMol;
    //      mol.setInputType(in_type);
    //      mol.setOutputType(out_type);
    //      streampos sp = ifs.tellg();
    //      ff.readMolecule(ifs,*mol);
    //      if (mol.numAtoms() == 0)
    //        {
    //          mol=null;
    //    return(false);
    //        }
    //
    //      title = mol.getTitle();
    //      if (i == 1)
    //        {
    //          master = title;
    //          _molvec.put(mol);
    //        }
    //      else
    //        {
    //          if (title == master)
    //      _molvec.put(mol);
    //          else
    //            {
    //              ifs.seekg(sp);
    //              mol = mol;
    //              break;
    //            }
    //        }
    //      i++;
    //    }
    //  return(true);
    //  }
    //
    //  public boolean readConfs(InputStream ifs)
    //
    //  {
    //    boolean retval = readConfs(ifs, IOType.SDF, IOType.SDF);
    //    return retval;
    //  }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
