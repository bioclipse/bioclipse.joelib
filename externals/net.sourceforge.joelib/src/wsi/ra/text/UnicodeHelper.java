///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: UnicodeHelper.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Original Authors:  Copyright 2003 Sun Microsystems, Inc. All rights reserved.
//Version:  $Revision: 1.6 $
//          $Date: 2005/02/17 16:48:44 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package wsi.ra.text;

/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.6 $, $Date: 2005/02/17 16:48:44 $
 */
public class UnicodeHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String specialCharacters = "!=#:";
    private static final char DEFAULT_ESCAPE = '\\';
    private static final String HEX_DIGITS = "0123456789ABCDEF";

    //~ Methods ////////////////////////////////////////////////////////////////

    public static String decode(String original)
    {
        char character;
        int len = original.length();
        StringBuffer decoded = new StringBuffer(len);

        for (int x = 0; x < len;)
        {
            character = original.charAt(x++);

            if (character == DEFAULT_ESCAPE)
            {
                character = original.charAt(x++);

                switch (character)
                {
                case 'u':

                    int value = 0;

                    for (int i = 0; i < 4; i++)
                    {
                        value = (value << 4) + decodeChar(original.charAt(x++));
                    }

                    decoded.append((char) value);

                    break;

                case 't':
                    character = '\t';

                    break;

                case 'f':
                    character = '\f';

                    break;

                case 'r':
                    character = '\r';

                    break;

                case 'n':
                    character = '\n';

                    break;
                }
            }

            decoded.append(character);
        }

        return decoded.toString();
    }

    public static String encode(String original)
    {
        int len = original.length();
        StringBuffer encoded = new StringBuffer(len * 2);

        for (int x = 0; x < len; x++)
        {
            char character = original.charAt(x);

            if (character == DEFAULT_ESCAPE)
            {
                encoded.append(DEFAULT_ESCAPE);
                encoded.append(DEFAULT_ESCAPE);
            }

            switch (character)
            {
            case ' ':
                encoded.append(DEFAULT_ESCAPE);
                encoded.append(' ');

                break;

            case '\t':
                encoded.append(DEFAULT_ESCAPE);
                encoded.append('t');

                break;

            case '\f':
                encoded.append(DEFAULT_ESCAPE);
                encoded.append('f');

                break;

            case '\n':
                encoded.append(DEFAULT_ESCAPE);
                encoded.append('n');

                break;

            case '\r':
                encoded.append(DEFAULT_ESCAPE);
                encoded.append('r');

                break;

            default:

                if ((character < 0x0020) || (character > 0x007e))
                {
                    encoded.append(DEFAULT_ESCAPE);
                    encoded.append('u');
                    encoded.append(encodeChar(character));
                }
                else
                {
                    if (specialCharacters.indexOf(character) != -1)
                    {
                        encoded.append(DEFAULT_ESCAPE);
                    }

                    encoded.append(character);
                }
            }
        }

        return encoded.toString();
    }

    /**
     * @param char1
     * @return
     */
    private static int decodeChar(char hexCode)
    {
        int byteValue = 0;

        if ((hexCode >= '0') && (hexCode <= '9'))
        {
            byteValue = hexCode - '0';
        }
        else if ((hexCode >= 'a') && (hexCode <= 'f'))
        {
            byteValue = (hexCode + 10) - 'a';
        }
        else if ((hexCode >= 'A') && (hexCode <= 'F'))
        {
            byteValue = (hexCode + 10) - 'A';
        }
        else
        {
            throw new IllegalArgumentException(
                "Could not decode Unicode, because '" + hexCode +
                "' is not a valid HEX code.");
        }

        return hexCode;
    }

    private static String encodeChar(char character)
    {
        StringBuffer unicode = new StringBuffer(4);
        unicode.append(toHex((character >> 12)));
        unicode.append(toHex((character >> 8)));
        unicode.append(toHex((character >> 4)));
        unicode.append(toHex(character));

        return unicode.toString();
    }

    private static char toHex(int myByte)
    {
        return HEX_DIGITS.charAt(myByte & 0xF);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
