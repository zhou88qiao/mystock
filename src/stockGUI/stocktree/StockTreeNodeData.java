package stockGUI.stocktree;

public class StockTreeNodeData {
	public int nodeType;
	public String nodeData;
	public StockTreeNodeData(int nodeType,String nodeData)
	{
		this.nodeType=nodeType;
		this.nodeData=nodeData;
	}
	
	public String toString()
	{
		return nodeData;
	}
}
