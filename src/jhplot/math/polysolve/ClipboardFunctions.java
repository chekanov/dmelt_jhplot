/***************************************************************************
 *   Copyright (C) 2012 by Paul Lutus                                      *
 *   lutusp@arachnoid.com                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package jhplot.math.polysolve;

import java.awt.datatransfer.*;
import java.awt.*;

/**
 *
 * @author lutusp
 */
public final class ClipboardFunctions {

    public static void clipCopy(String s) {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //String s = resultText.getText();
        StringSelection contents = new StringSelection(s);
        clipboard.setContents(contents, contents);
    }

    public static String clipPaste(PolySolve parent) {
        String data = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipboardContent = clipboard.getContents(parent);
        String s = "";
        StringSelection contents = new StringSelection(s);
        if ((clipboardContent != null) &&
                (clipboardContent.isDataFlavorSupported(contents.getTransferDataFlavors()[0]))) {
            try {
                data = (String) clipboardContent.getTransferData(contents.getTransferDataFlavors()[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
