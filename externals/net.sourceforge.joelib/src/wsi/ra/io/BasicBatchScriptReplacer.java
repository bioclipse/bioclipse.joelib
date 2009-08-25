///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicBatchScriptReplacer.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:43 $
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
package wsi.ra.io;

import wsi.ra.tool.BasicResourceLoader;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.Writer;

import java.util.Hashtable;

import org.apache.log4j.Category;


/**
 *  Contains static methods for reading data from temporary files.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:43 $
 */
public class BasicBatchScriptReplacer implements BatchScriptReplacer
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "wsi.ra.io.BatchScriptReplacer");
    private static BasicBatchScriptReplacer scriptReplacer;
    private static char DEFAULT_QUOTING_CHARACTER = '?';

    //~ Instance fields ////////////////////////////////////////////////////////

    private char quotingCharacter = DEFAULT_QUOTING_CHARACTER;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the BatchScriptReplacer object
     */
    private BasicBatchScriptReplacer()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public static synchronized BasicBatchScriptReplacer instance()
    {
        if (scriptReplacer == null)
        {
            scriptReplacer = new BasicBatchScriptReplacer();
        }

        return scriptReplacer;
    }

    //  static public boolean fromFile(String in_file, String out_file, Hashtable variables)
    //  {
    //    // create a reader stream
    //    FileReader fileReader = null;
    //
    //    try{
    //      new FileReader(in_file);
    //    }
    //    catch(Exception e){
    //      logger.error(e, "Could not find batch file "+in_file+"...");
    //      return false;
    //    }
    //
    //    // create new file writer
    //    //FileWriter out = new FileWriter(writer);
    //
    //    // create the new batch file with replaced variables
    //    createBatchFile(fileReader, out_file, variables);
    //
    //    return true;
    //  }

    /**
     *  Description of the Method
     *
     *@param  reader     Description of the Parameter
     *@param  writer     Description of the Parameter
     *@param  variables  Description of the Parameter
     *@return            Description of the Return Value
     */
    public boolean createBatchFile(Reader reader, Writer writer,
        Hashtable variables)
    {
        if ((reader == null) || (writer == null) || (variables == null))
        {
            return false;
        }

        try
        {
            // create new buffered file reader
            BufferedReader in = new BufferedReader(reader);

            // create new stream tokenizer
            StreamTokenizer tin = new StreamTokenizer(in);
            tin.eolIsSignificant(false);

            // build new syntax table
            final int quoteChar = (int) quotingCharacter;

            tin.resetSyntax();
            tin.wordChars(' ', 255);
            tin.whitespaceChars(0, ' ');
            tin.quoteChar(quoteChar);
            tin.eolIsSignificant(true);

            int type;
            String outString = "";
            String eol = System.getProperty("line.separator");
            boolean quoted = false;
            boolean newLine = true;
            String space = " ";
            String noSpace = new String();
            String usedSpace = null;
            String variable;

            while ((type = tin.nextToken()) != StreamTokenizer.TT_EOF)
            {
                if (quoted || newLine)
                {
                    usedSpace = noSpace;
                    quoted = false;
                    newLine = false;
                }
                else
                {
                    usedSpace = space;
                }

                outString = "";

                switch (type)
                {
                case StreamTokenizer.TT_NUMBER:
                    writer.write(usedSpace);
                    writer.write(Double.toString(tin.nval));

                    break;

                case StreamTokenizer.TT_WORD:
                    writer.write(usedSpace);
                    writer.write(tin.sval);

                    break;

                case StreamTokenizer.TT_EOL:
                    writer.write(eol);
                    newLine = true;

                    break;
                }

                if (type == quoteChar)
                {
                    variable = outString = tin.sval;
                    quoted = true;

                    // replace the variable, if only one line
                    if (outString.lastIndexOf("\n") == -1)
                    {
                        outString = "" + (String) variables.get(variable);
                    }

                    // variable not defined
                    if (outString == null)
                    {
                        logger.error(
                            "Could not generate new batch file, the user variable '" +
                            "' " + variable + " is not defined.");

                        return false;
                    }

                    // write current data object item
                    writer.write(outString);
                }
            }

            reader.close();

            // close file and writer
            writer.close();
        }
        catch (Exception e)
        {
            logger.error("Could not generate new batch file...");

            return false;
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     *@param  resourceLocation  Description of the Parameter
     *@param  outputFile        Description of the Parameter
     *@param  variables         Description of the Parameter
     *@return                   Description of the Return Value
     */
    public boolean fromResource(String resourceLocation, String outputFile,
        Hashtable variables) throws IOException
    {
        String batchFile = null;

        // get the batch file from the resource path
        byte[] batchFileChars = null;

        try
        {
            batchFileChars = BasicResourceLoader.instance()
                                                .getBytesFromResourceLocation(
                    resourceLocation);
        }
        catch (Exception e)
        {
            logger.error("Could not find batch file " + resourceLocation +
                " in resource path...");

            return false;
        }

        if (batchFileChars == null)
        {
            return false;
        }
        else
        {
            batchFile = String.valueOf(batchFileChars);

            // create a reader stream
            StringReader sreader = new StringReader(batchFile);

            if (logger.isDebugEnabled())
            {
                // create new file writer
                logger.debug("Try to create batch file: " + outputFile);
            }

            FileWriter writer = new FileWriter(outputFile);

            // create the new batch file with replaced variables
            createBatchFile(sreader, writer, variables);
        }

        return true;
    }

    /**
     *  Gets the quoteCharacter attribute of the BatchScriptReplacer object
     *
     *@return    The quoteCharacter value
     */
    public char getQuoteCharacter()
    {
        return quotingCharacter;
    }

    /**
     *  Sets the quoteCharacter attribute of the BatchScriptReplacer object
     *
     *@param  _quotingCharacter  The new quoteCharacter value
     */
    public void setQuoteCharacter(char _quotingCharacter)
    {
        quotingCharacter = _quotingCharacter;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
