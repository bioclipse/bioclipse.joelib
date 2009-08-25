package jmat.io.data.fileTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;


/**
 * <p>Titre : JAva MAtrix TOols</p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2002</p>
 * <p>Société : IRSN</p>
 * @.author Yann RICHET
 * @version 1.0
 */
public class XMLFile
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static Element fromFile(File file)
    {
        try
        {
            DOMBuilder b = new DOMBuilder();
            Document d = b.build(file);

            return d.getRootElement();
        }
        catch (Exception e)
        {
            System.out.println("File " + file.getName() + " is unreadable.");

            return null;
        }
    }

    public static void toFile(File file, Element e)
    {
        Document doc = new Document(e);
        XMLOutputter op = new XMLOutputter();

        try
        {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            op.output(doc, bw);
            bw.close();
        }
        catch (IOException ex)
        {
            System.out.println("File " + file.getName() + " is unwritable.");
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
