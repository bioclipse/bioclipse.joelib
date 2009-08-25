package jmat.io.data;

import jmat.data.Matrix;
import jmat.data.Text;

import jmat.io.data.fileTools.CharFile;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class TextFile
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private File file;
    private Text text = new Text("");

    //~ Constructors ///////////////////////////////////////////////////////////

    public TextFile(File f)
    {
        file = f;

        if (file.exists())
        {
            text = new Text(CharFile.fromFile(file));
        }
        else
        {
            text = new Text("");
        }
    }

    public TextFile(String fileName)
    {
        file = new File(fileName);

        if (file.exists())
        {
            text = new Text(CharFile.fromFile(file));
        }
        else
        {
            text = new Text("");
        }
    }

    public TextFile(File f, Text t)
    {
        text = t;
        file = f;
        CharFile.toFile(file, text.getString());
    }

    public TextFile(File f, String s)
    {
        text = new Text(s);
        file = f;
        CharFile.toFile(file, text.getString());
    }

    public TextFile(File f, Matrix X)
    {
        text = new Text(X);
        file = f;
        CharFile.toFile(file, text.getString());
    }

    public TextFile(String fileName, Text t)
    {
        text = t;
        file = new File(fileName);
        CharFile.toFile(file, text.getString());
    }

    public TextFile(String fileName, String s)
    {
        text = new Text(s);
        file = new File(fileName);
        CharFile.toFile(file, text.getString());
    }

    public TextFile(String fileName, Matrix X)
    {
        text = new Text(X);
        file = new File(fileName);
        CharFile.toFile(file, text.getString());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void append(Text t)
    {
        text = new Text(text.getString() + "\n" + t.getString());
        CharFile.toFile(file, text.getString());
    }

    public void append(String s)
    {
        text = new Text(text.getString() + "\n" + s);
        CharFile.toFile(file, text.getString());
    }

    public void append(Matrix X)
    {
        text = new Text(text.getString() + "\n" + new Text(X).getString());
        CharFile.toFile(file, text.getString());
    }

    public File getFile()
    {
        return file;
    }

    public String getFileName()
    {
        return file.getName();
    }

    public Text getText()
    {
        return text;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
