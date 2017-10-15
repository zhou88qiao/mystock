package stockGUI.stocktableheader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;



/**
 * Created at 2006-9-5 16:02:46<br>
 * ±íÍ·ÃèÊöÆ÷
 * 
 * @author Brad.Wu
 * @version 1.0
 */
@SuppressWarnings("serial")
public class TableHeaderRenderer extends JLabel  implements TableCellRenderer, Serializable {
    
	int TABLE_ROW_HEIGHT=25;
	/**
     * Creates a default table cell renderer.
     */
    public TableHeaderRenderer() {
        setOpaque(true);
        setHorizontalAlignment(JLabel.CENTER);
    }

    // implements javax.swing.table.TableCellRenderer
    /**
     * Returns the default table cell renderer.
     * 
     * @param table the <code>JTable</code>
     * @param value the value to assign to the cell at <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }

        setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        setValue(value);

        Dimension dim = getPreferredSize();
        if (dim.height < TABLE_ROW_HEIGHT)
            setPreferredSize(new Dimension(getPreferredSize().width, TABLE_ROW_HEIGHT));
        return this;
    }

    /*
     * The following methods are overridden as a performance measure to to prune code-paths are
     * often called in the case of renders but which we know are unnecessary. Great care should be
     * taken when writing your own renderer to weigh the benefits and drawbacks of overriding
     * methods like these.
     */

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     */
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        // p should now be the JTable.
        boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground())
                && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     * 
     * @since 1.5
     */
    public void invalidate() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     */
    public void validate() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     */
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     */
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     */
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     * 
     * @since 1.5
     */
    public void repaint() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Strings get interned...
        if (propertyName == "text") {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

    /**
     * Sets the <code>String</code> object for the cell being rendered to <code>value</code>.
     * 
     * @param value the string value for this cell; if value is <code>null</code> it sets the text
     *            value to an empty string
     * @see JLabel#setText
     */
    protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }

    /**
     * A subclass of <code>DefaultTableCellRenderer</code> that implements <code>UIResource</code>.
     * <code>DefaultTableCellRenderer</code> doesn't implement <code>UIResource</code> directly
     * so that applications can safely override the <code>cellRenderer</code> property with
     * <code>DefaultTableCellRenderer</code> subclasses.
     * <p>
     * <strong>Warning:</strong> Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is appropriate for short term
     * storage or RMI between applications running the same version of Swing. As of 1.4, support for
     * long term storage of all JavaBeans<sup><font size="-2">TM</font></sup> has been added to
     * the <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
     */
    public static class UIResource extends DefaultTableCellRenderer implements
            javax.swing.plaf.UIResource {
    }

}
