package stockGUI.stocktree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


public class StockTreeCellRenderer extends DefaultTreeCellRenderer
{
	
	//private ImageIcon 
	ImageIcon rootIcon=new ImageIcon("image/root.png");
	ImageIcon marketIcon=new ImageIcon("image/market.png");
	ImageIcon classIcon=new ImageIcon("image/class.png");
	ImageIcon plateIcon=new ImageIcon("image/plate.png");
	ImageIcon stockIcon=new ImageIcon("image/stock.png");
	public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;
		StockTreeNodeData data=(StockTreeNodeData)node.getUserObject();
		ImageIcon icon=null;
		switch(data.nodeType)
		{
		case StockObjectType.ROOT:
			icon=rootIcon;
			break;
		case StockObjectType.MARKET:
			icon=marketIcon;
			break;
		case StockObjectType.PLATE:
			icon=plateIcon;
			break;
		case StockObjectType.CLASS:
			icon=classIcon;
			break;
		case StockObjectType.STOCK:
			icon=stockIcon;
			break;
		
		}
		this.setIcon(icon);
		return this;
	}
	
} 
