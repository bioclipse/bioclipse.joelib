package jmat.data;

import jmat.io.data.TextFile;
import jmat.io.data.fileTools.MatrixString;

import jmat.io.gui.FrameView;
import jmat.io.gui.TextWindow;

import javax.swing.JPanel;


/**
<P>
   The Text Class is just designed to provide easy-to-use string operations
   like building log files, displaying text in a window, converting matrix to String format...

@.author Yann RICHET
@version 2.0
*/
public class Text implements java.io.Serializable
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /** String for internal storage.
    @serial internal string storage.
    */
    private String string = "";

    //~ Constructors ///////////////////////////////////////////////////////////

    /** Construct a text.
    @param str  String of the text.
    */
    public Text(String str)
    {
        setString(str);
    }

    /** Construct a text.
    @param X  Matrix to convert in text.
    */
    public Text(Matrix X)
    {
        setString(X);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /** Load the text from a file.
    @param fileName    fileName.
    @return Text.
    */
    public static Text fromFile(String fileName)
    {
        TextFile mf = new TextFile(fileName);

        return mf.getText();
    }

    /** Save the text from a file.
    @param file    file.
    @return Text.
    */
    public static Text fromFile(java.io.File file)
    {
        TextFile mf = new TextFile(file);

        return mf.getText();
    }

    /** Provides access to the string of the text.
    @return String of the text.
    */
    public String getString()
    {
        return string.toString();
    }

    /** Merge the two Texts.
    @param text    text to merge.
    */
    public void merge(Text text)
    {
        string = new String(string + text.getString());
    }

    /** Merge the Matrix.
    @param X    Matrix to merge.
    */
    public void merge(Matrix X)
    {
        Text text = new Text(X);
        string = string + text.getString();
    }

    /** Merge the string.
    @param s    String to merge.
    */
    public void merge(String s)
    {
        string = string + s;
    }

    /** Provides access to the string of the text.
    @param str  String of the text.
    */
    public void setString(String str)
    {
        string = str;
    }

    public void setString(Matrix X)
    {
        string = MatrixString.printMatrix(X);
    }

    /** Print the Text in the Command Line.
    */
    public void toCommandLine()
    {
        System.out.println(string);
    }

    /** Save the Text in a file.
    @param fileName    fileName.
    */
    public void toFile(String fileName)
    {
        new TextFile(fileName, this);
    }

    /** Save the text in a file.
    @param file    file.
    */
    public void toFile(java.io.File file)
    {
        new TextFile(file, this);
    }

    /** Display the text in a Window in a Frame.
    @param title Title of the JFrame.
    */
    public void toFrame(String title)
    {
        new FrameView(title, toPanel());
    }

    /** Display the text in a Window.
    @return JPanel.
    */
    public JPanel toPanel()
    {
        return new TextWindow(this);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
