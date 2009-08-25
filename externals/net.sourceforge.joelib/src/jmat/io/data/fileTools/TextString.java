package jmat.io.data.fileTools;

import jmat.data.Text;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class TextString
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private String S;

    //private static int decimalSize = 10;
    private Text T;

    //~ Constructors ///////////////////////////////////////////////////////////

    public TextString(Text t)
    {
        T = t;
        S = TextString.printText(T);
    }

    public TextString(String s)
    {
        S = s;
        T = readText(S);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static String printText(Text t)
    {
        return t.getString();
    }

    public static Text readText(String s)
    {
        return new Text(s);
    }

    public String getString()
    {
        return S;
    }

    public Text getText()
    {
        return T;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
