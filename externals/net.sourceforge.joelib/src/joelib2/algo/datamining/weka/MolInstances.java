///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolInstances.java,v $
//  Purpose:  Molecule Weka instances.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:28 $
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
package joelib2.algo.datamining.weka;

import joelib2.feature.NativeValue;

import joelib2.feature.types.atomlabel.AtomIsHydrogen;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.molecule.types.PairData;

import joelib2.process.types.DescriptorBinning;

import joelib2.smiles.SMILESParser;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import java.text.ParseException;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Category;


/**
 * Molecule Weka instances.
 *
 * @.author    wegnerj
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:28 $
 */
public class MolInstances extends Instances
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "jcompchem.joelib2.algo.weka.MolInstances");

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Using <tt>true</tt> here is a not fully tested beta mode.
     * So this should be used carefully.
     */
    private boolean useCoords = false;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructor copying all instances and references to
     * the header information from the given set of instances.
     *
     * @param instances the set to be copied
     */
    public MolInstances(MolInstances dataset)
    {
        super(dataset);
    }

    /**
     * Reads a Descriptor ARFF file from a reader, and assigns a weight of
     * one to each instance. Lets the index of the class
     * attribute be undefined (negative).
     *
     * @param reader the reader
     * @exception IOException if the ARFF file is not read
     * successfully
     */
    public MolInstances(Reader reader) throws IOException
    {
        super(reader);
    }

    /**
     * Constructor creating an empty set of instances. Copies references
     * to the header information from the given set of instances. Sets
     * the capacity of the set of instances to 0 if its negative.
     *
     * @param instances the instances from which the header
     * information is to be taken
     * @param capacity the capacity of the new dataset
     */
    public MolInstances(MolInstances dataset, int capacity)
    {
        super(dataset, capacity);
    }

    /**
     * Reads the header of a Descriptor ARFF file from a reader and
     * reserves space for the given number of instances. Lets
     * the class index be undefined (negative).
     *
     * @param reader the reader
     * @param capacity the capacity
     * @exception IllegalArgumentException if the header is not read successfully
     * or the capacity is negative.
     * @exception IOException if there is a problem with the reader.
     */
    public MolInstances(Reader reader, int capacity) throws IOException
    {
        super(reader, capacity);
    }

    /**
     * Creates a new set of instances by copying a
     * subset of another set.
     *
     * @param source the set of instances from which a subset
     * is to be created
     * @param first the index of the first instance to be copied
     * @param toCopy the number of instances to be copied
     * @exception IllegalArgumentException if first and toCopy are out of range
     */
    public MolInstances(MolInstances source, int first, int toCopy)
    {
        super(source, first, toCopy);
    }

    /**
     * Creates an empty set of instances. Uses the given
     * attribute information. Sets the capacity of the set of
     * instances to 0 if its negative. Given attribute information
     * must not be changed after this constructor has been used.
     *
     * @param name the name of the relation
     * @param attInfo the attribute information
     * @param capacity the capacity of the set
     */
    public MolInstances(String name, FastVector attInfo, int capacity)
    {
        super(name, attInfo, capacity);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @param molecules
     * @param attributes
     * @param attributeTypes Weka attribute types can be {@link Attribute.NUMERIC} or {@link Attribute.NUMERIC}
     * @return
     */
    public static MolInstances createMolInstances(MoleculeVector molecules,
        String[] attributes, int[] attributeTypes)
    {
        // load descriptor binning
        DescriptorBinning binning = DescriptorBinning.getDescBinning(molecules);

        int length = molecules.getSize();

        if (attributes.length != attributeTypes.length)
        {
            logger.error("Different number of attributes and attribute types.");

            return null;
        }

        Enumeration enumeration = binning.getDescriptors();
        FastVector attributesV = new FastVector(binning.numberOfDescriptors());
        Molecule mol;
        PairData pairData;

        for (int i = 0; i < attributes.length; i++)
        {
            if (attributeTypes[i] == Attribute.NUMERIC)
            {
                // numeric
                attributesV.addElement(new Attribute(
                        (String) enumeration.nextElement(),
                        attributesV.size()));
            }
            else if (attributeTypes[i] == Attribute.NOMINAL)
            {
                // nominal
                // create a list with all nominal values
                Hashtable hashed = new Hashtable();

                for (int j = 0; j < length; j++)
                {
                    mol = molecules.getMol(j);

                    // get unparsed data
                    pairData = mol.getData(attributes[i], false);

                    if (pairData != null)
                    {
                        if (pairData.getKeyValue() instanceof String)
                        {
                            hashed.put(pairData.getKeyValue(), "");
                        }
                        else
                        {
                            hashed.put(pairData.toString(), "");
                        }
                    }
                }

                // store list of nominal values in the Weka data structure
                FastVector attributeValues = new FastVector(hashed.size());
                String tmp;

                for (Enumeration e = hashed.keys(); e.hasMoreElements();)
                {
                    tmp = (String) e.nextElement();
                    attributeValues.addElement(tmp);

                    //System.out.println("NOMINAL " + tmp);
                }

                attributesV.addElement(new Attribute(attributes[i],
                        attributeValues, attributesV.size()));
            }
        }

        int size = attributesV.size();
        Attribute attribute;

        // create molecule instances
        MolInstances instances = new MolInstances("MoleculeInstances",
                attributesV, attributesV.size());

        // iterate over all instances (to generate them)
        double[] instance;

        for (int i = 0; i < length; i++)
        {
            mol = molecules.getMol(i);
            instance = new double[size];

            for (int j = 0; j < size; j++)
            {
                attribute = (Attribute) attributesV.elementAt(j);

                // get parsed data
                pairData = mol.getData(attribute.name(), true);

                // add nominal or numeric or missing value
                if (pairData == null)
                {
                    instance[attribute.index()] = MolInstance.missingValue();
                }
                else
                {
                    if (attribute.isNominal())
                    {
                        // nominal
                        String tmpS = pairData.toString().trim();

                        if (tmpS.indexOf("\n") != -1)
                        {
                            logger.error("Descriptor " + attribute.name() +
                                " contains multiple lines and is not a valid nominal value.");
                        }
                        else
                        {
                            instance[attribute.index()] = attribute
                                .indexOfValue(pairData.toString());

                            if (instance[attribute.index()] == -1)
                            {
                                // invalid nominal value
                                logger.error("Invalid nominal value");

                                return null;
                            }
                        }
                    }
                    else
                    {
                        // numeric
                        if (pairData instanceof NativeValue)
                        {
                            double tmpD = ((NativeValue) pairData)
                                .getDoubleNV();

                            if (Double.isNaN(tmpD))
                            {
                                instance[attribute.index()] = MolInstance
                                    .missingValue();
                            }
                            else
                            {
                                instance[attribute.index()] = tmpD;
                            }
                        }
                        else
                        {
                            logger.error("Descriptor " + attribute.name() +
                                " is not a native value.");
                        }
                    }
                }

                attribute.index();
            }

            // add created molecule instance to molecule instances
            instances.add(new MolInstance(mol, 1, instance));
        }

        return instances;
    }

    /**
     * Adds one instance to the end of the set.
     * Shallow copies instance before it is added. Increases the
     * size of the dataset if it is not large enough. Does not
     * check if the instance is compatible with the dataset.
     *
     * @param instance the instance to be added
     */
    public final void add(MolInstance instance)
    {
        MolInstance newInstance = (MolInstance) instance.copy();

        newInstance.setDataset(this);
        m_Instances.addElement(newInstance);
    }

    /**
     * Returns the molinstance at the given position.
     *
     * @param index the instance's index
     * @return the instance at the given position
     */
    public final MolInstance molInstance(int index)
    {
        return (MolInstance) m_Instances.elementAt(index);
    }

    /**
     * @param showCoords The showCoords to set.
     */
    public void setUseCoords(boolean useCoords)
    {
        this.useCoords = useCoords;

        for (int i = 0; i < numInstances(); i++)
        {
            ((MolInstance) instance(i)).setShowCoords(useCoords);
        }
    }

    public String toNativeArff()
    {
        return super.toString();
    }

    /**
     * Reads a single instance using the tokenizer and appends it
     * to the dataset. Automatically expands the dataset if it
     * is not large enough to hold the instance.
     *
     * @param tokenizer the tokenizer to be used
     * @param flag if method should test for carriage return after
     * each instance
     * @return false if end of file has been reached
     * @exception IOException if the information is not read
     * successfully
     */
    protected boolean getInstanceFull(StreamTokenizer tokenizer, boolean flag)
        throws IOException
    {
        double[] instance = new double[numAttributes()];
        int index;
        Molecule mol = null;
        String unparsedSMILES = null;
        String molTitle = null;
        double x;
        double y;
        double z;

        for (int i = 0; i < 2; i++)
        {
            if (i > 0)
            {
                getNextToken(tokenizer);
                molTitle = tokenizer.sval;
            }
            else
            {
                unparsedSMILES = tokenizer.sval;
            }
        }

        mol = new BasicConformerMolecule(MolInstance.smiles,
                MolInstance.smiles);

        if (!SMILESParser.smiles2molecule(mol, unparsedSMILES, molTitle))
        {
            logger.error("SMILES entry \"" + unparsedSMILES +
                "\" could not be loaded.");
        }

        if (useCoords)
        {
            for (int i = 1; i <= mol.getAtomsSize(); i++)
            {
                if (!AtomIsHydrogen.isHydrogen(mol.getAtom(i)))
                {
                    getNextToken(tokenizer);

                    //                  System.out.println("x("+i+"):"+tokenizer.sval);
                    x = Double.parseDouble(tokenizer.sval);

                    getNextToken(tokenizer);

                    //                  System.out.println("y("+i+"):"+tokenizer.sval);
                    y = Double.parseDouble(tokenizer.sval);

                    getNextToken(tokenizer);

                    //                  System.out.println("z("+i+"):"+tokenizer.sval);
                    z = Double.parseDouble(tokenizer.sval);

                    mol.getAtom(i).setCoords3D(x, y, z);
                }
            }
        }

        //              System.out.println(mol.toString(IOTypeHolder.instance().getIOType("SDF")));
        // Get values for all attributes.
        for (int i = 0; i < numAttributes(); i++)
        {
            // Get next token
            getNextToken(tokenizer);

            // Check if value is missing.
            if (tokenizer.ttype == '?')
            {
                instance[i] = Instance.missingValue();
            }
            else
            {
                // Check if token is valid.
                if (tokenizer.ttype != StreamTokenizer.TT_WORD)
                {
                    errms(tokenizer, "not a valid value");
                }

                switch (attribute(i).type())
                {
                case Attribute.NOMINAL:

                    // Check if value appears in header.
                    index = attribute(i).indexOfValue(tokenizer.sval);

                    if (index == -1)
                    {
                        errms(tokenizer,
                            "nominal value not declared in header");
                    }

                    instance[i] = (double) index;

                    break;

                case Attribute.NUMERIC:

                    // Check if value is really a number.
                    try
                    {
                        instance[i] = Double.valueOf(tokenizer.sval)
                                            .doubleValue();
                    }
                    catch (NumberFormatException e)
                    {
                        errms(tokenizer, "number expected");
                    }

                    break;

                case Attribute.STRING:
                    instance[i] = attribute(i).addStringValue(tokenizer.sval);

                    break;

                case Attribute.DATE:

                    try
                    {
                        instance[i] = attribute(i).parseDate(tokenizer.sval);
                    }
                    catch (ParseException e)
                    {
                        errms(tokenizer, "unparseable date: " + tokenizer.sval);
                    }

                    break;

                default:
                    errms(tokenizer, "unknown attribute type in column " + i);
                }
            }
        }

        if (flag)
        {
            getLastToken(tokenizer, true);
        }

        // Add instance to dataset
        add(new MolInstance(mol, 1, instance));

        return true;
    }

    /**
     * Reads a single instance using the tokenizer and appends it
     * to the dataset. Automatically expands the dataset if it
     * is not large enough to hold the instance.
     *
     * @param tokenizer the tokenizer to be used
     * @param flag if method should test for carriage return after
     * each instance
     * @return false if end of file has been reached
     * @exception IOException if the information is not read
     * successfully
     */
    protected boolean getInstanceSparse(StreamTokenizer tokenizer, boolean flag)
        throws IOException
    {
        int valIndex;
        int numValues = 0;
        int maxIndex = -1;

        // Get values
        do
        {
            // Get index
            getIndex(tokenizer);

            if (tokenizer.ttype == '}')
            {
                break;
            }

            // Is index valid?
            try
            {
                m_IndicesBuffer[numValues] = Integer.valueOf(tokenizer.sval)
                                                    .intValue();
            }
            catch (NumberFormatException e)
            {
                errms(tokenizer, "index number expected");
            }

            if (m_IndicesBuffer[numValues] <= maxIndex)
            {
                errms(tokenizer, "indices have to be ordered");
            }

            if ((m_IndicesBuffer[numValues] < 0) ||
                    (m_IndicesBuffer[numValues] >= numAttributes()))
            {
                errms(tokenizer, "index out of bounds");
            }

            maxIndex = m_IndicesBuffer[numValues];

            // Get value;
            getNextToken(tokenizer);

            // Check if value is missing.
            if (tokenizer.ttype == '?')
            {
                m_ValueBuffer[numValues] = Instance.missingValue();
            }
            else
            {
                // Check if token is valid.
                if (tokenizer.ttype != StreamTokenizer.TT_WORD)
                {
                    errms(tokenizer, "not a valid value");
                }

                switch (attribute(m_IndicesBuffer[numValues]).type())
                {
                case Attribute.NOMINAL:

                    // Check if value appears in header.
                    valIndex = attribute(m_IndicesBuffer[numValues])
                        .indexOfValue(tokenizer.sval);

                    if (valIndex == -1)
                    {
                        errms(tokenizer,
                            "nominal value not declared in header");
                    }

                    m_ValueBuffer[numValues] = (double) valIndex;

                    break;

                case Attribute.NUMERIC:

                    // Check if value is really a number.
                    try
                    {
                        //System.out.println("should be a number:"+tokenizer.sval);
                        m_ValueBuffer[numValues] = Double.valueOf(
                                tokenizer.sval).doubleValue();
                    }
                    catch (NumberFormatException e)
                    {
                        errms(tokenizer, "number expected");
                    }

                    break;

                case Attribute.STRING:
                    m_ValueBuffer[numValues] = attribute(
                            m_IndicesBuffer[numValues]).addStringValue(
                            tokenizer.sval);

                    break;

                case Attribute.DATE:

                    try
                    {
                        m_ValueBuffer[numValues] = attribute(
                                m_IndicesBuffer[numValues]).parseDate(
                                tokenizer.sval);
                    }
                    catch (ParseException e)
                    {
                        errms(tokenizer, "unparseable date: " + tokenizer.sval);
                    }

                    break;

                default:
                    errms(tokenizer,
                        "unknown attribute type in column " +
                        m_IndicesBuffer[numValues]);
                }
            }

            numValues++;
        }
        while (true);

        if (flag)
        {
            getLastToken(tokenizer, true);
        }

        // Add instance to dataset
        double[] tempValues = new double[numValues];
        int[] tempIndices = new int[numValues];
        System.arraycopy(m_ValueBuffer, 0, tempValues, 0, numValues);
        System.arraycopy(m_IndicesBuffer, 0, tempIndices, 0, numValues);
        add(new SparseInstance(1, tempValues, tempIndices, numAttributes()));

        return true;
    }

    /**
     * Copies instances from one set to the end of another
     * one.
     *
     * @param source the source of the instances
     * @param from the position of the first instance to be copied
     * @param dest the destination for the instances
     * @param num the number of instances to be copied
     */
    private void copyInstances(int from, MolInstances dest, int num)
    {
        for (int i = 0; i < num; i++)
        {
            dest.add(instance(from + i));
        }
    }

    /**
     * Throws error message with line number and last token read.
     *
     * @param theMsg the error message to be thrown
     * @param tokenizer the stream tokenizer
     * @throws IOExcpetion containing the error message
     */
    private void errms(StreamTokenizer tokenizer, String theMsg)
        throws IOException
    {
        throw new IOException(theMsg + ", read " + tokenizer.toString());
    }

    /**
     * Gets next token, skipping empty lines.
     *
     * @param tokenizer the stream tokenizer
     * @exception IOException if reading the next token fails
     */
    private void getFirstToken(StreamTokenizer tokenizer) throws IOException
    {
        while (true)
        {
            if (tokenizer.nextToken() != StreamTokenizer.TT_EOL)
            {
                break;
            }
        }

        if ((tokenizer.ttype == '\'') || (tokenizer.ttype == '"'))
        {
            tokenizer.ttype = StreamTokenizer.TT_WORD;
        }
        else if ((tokenizer.ttype == StreamTokenizer.TT_WORD) &&
                (tokenizer.sval.equals("?")))
        {
            tokenizer.ttype = '?';
        }
    }

    /**
     * Gets index, checking for a premature and of line.
     *
     * @param tokenizer the stream tokenizer
     * @exception IOException if it finds a premature end of line
     */
    private void getIndex(StreamTokenizer tokenizer) throws IOException
    {
        if (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
        {
            errms(tokenizer, "premature end of line");
        }

        if (tokenizer.ttype == StreamTokenizer.TT_EOF)
        {
            errms(tokenizer, "premature end of file");
        }
    }

    /**
     * Gets token and checks if its end of line.
     *
     * @param tokenizer the stream tokenizer
     * @exception IOException if it doesn't find an end of line
     */
    private void getLastToken(StreamTokenizer tokenizer, boolean endOfFileOk)
        throws IOException
    {
        if ((tokenizer.nextToken() != StreamTokenizer.TT_EOL) &&
                ((tokenizer.ttype != StreamTokenizer.TT_EOF) || !endOfFileOk))
        {
            errms(tokenizer, "end of line expected");
        }
    }

    /**
     * Gets next token, checking for a premature and of line.
     *
     * @param tokenizer the stream tokenizer
     * @exception IOException if it finds a premature end of line
     */
    private void getNextToken(StreamTokenizer tokenizer) throws IOException
    {
        if (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
        {
            errms(tokenizer, "premature end of line");
        }

        if (tokenizer.ttype == StreamTokenizer.TT_EOF)
        {
            errms(tokenizer, "premature end of file");
        }
        else if ((tokenizer.ttype == '\'') || (tokenizer.ttype == '"'))
        {
            tokenizer.ttype = StreamTokenizer.TT_WORD;
        }
        else if ((tokenizer.ttype == StreamTokenizer.TT_WORD) &&
                (tokenizer.sval.equals("?")))
        {
            tokenizer.ttype = '?';
        }
    }

    /**
     * Initializes the StreamTokenizer used for reading the ARFF file.
     *
     * @param tokenizer the stream tokenizer
     */
    private void initTokenizer(StreamTokenizer tokenizer)
    {
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.wordChars(' ' + 1, '\u00FF');
        tokenizer.whitespaceChars(',', ',');
        tokenizer.commentChar('%');
        tokenizer.quoteChar('"');
        tokenizer.quoteChar('\'');
        tokenizer.ordinaryChar('{');
        tokenizer.ordinaryChar('}');
        tokenizer.eolIsSignificant(true);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
