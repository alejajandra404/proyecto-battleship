/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 *
 * @author PC WHITE WOLF
 */
public class TransferableColor implements Transferable {
    protected static final DataFlavor colorFlavor =
            new DataFlavor(Color.class, "A Color Object");

    protected static final DataFlavor[] supportedFlavors = {
            colorFlavor,
            DataFlavor.stringFlavor,
    };

    private final Color color;

    public TransferableColor(Color color) {

        this.color = color;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {

        return supportedFlavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {

        return flavor.equals(colorFlavor) ||
                flavor.equals(DataFlavor.stringFlavor);
    }


    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {

        if (flavor.equals(colorFlavor)) {
            return color;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
            return color.toString();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
