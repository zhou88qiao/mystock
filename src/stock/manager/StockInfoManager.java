package stock.manager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.PropertyConfigurator;

import common.ConstantsInfo;
import common.stockLogger;

import dao.StockDataDao;
import dao.StockInformation;
import dao.StockInformationDao;

import stockGUI.stocktree.StockObjectType;
import stockGUI.stocktree.StockTreeNodeData;
 
public class StockInfoManager {     
	static StockInformationDao sid =new StockInformationDao();
	static StockDataDao sdDao=new StockDataDao();
	static List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
	List<String> listIndustry = new ArrayList<String>(); 
	List<String> listConcept = new ArrayList<String>(); 	
	List<String> listRegional = new ArrayList<String>(); 
	List<String> listStockIndustry = new ArrayList<String>(); 
	List<String> listStockConcept = new ArrayList<String>(); 	
	List<String> listStockRegional = new ArrayList<String>(); 
	List<String> listStockOfYear = new ArrayList<String>(); 
	
	DefaultMutableTreeNode root=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.ROOT,"大智慧"));
	DefaultMutableTreeNode hs=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.MARKET,"沪深股市"));
	DefaultMutableTreeNode industry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"行业板块"));	
	DefaultMutableTreeNode concept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"概念板块"));
	DefaultMutableTreeNode regional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"地域板块"));
	DefaultMutableTreeNode market=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"大盘指数"));
	DefaultMutableTreeNode fund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.MARKET,"基金"));
	DefaultMutableTreeNode openFund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"开放式基金"));
	DefaultMutableTreeNode closeFund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"封闭式基金"));
	
	private JFrame jFrame=new JFrame("股票基本信息");
	JTree stockTree;
	JPanel operationPane;
	DefaultTreeModel stockModel;
	JScrollPane stockSP;
	JScrollPane stockTreeSP;
	
	JButton addBrotherButton=new JButton("添加兄弟结点");
	JButton addChildButton=new JButton("添加子结点");
	JButton deleteButton=new JButton("删除");
	JButton editButton=new JButton("编辑");
	
	TreePath movePath;
	String movePathName;

	public void init() throws IOException, ClassNotFoundException, SQLException
	{
		
		Iterator it,ie;
	
		listIndustry=sid.getAllIndustry(listStockInfo);		
		stockLogger.logger.debug("list industry size："+listIndustry.size());
		 for(it = listIndustry.iterator();it.hasNext();)
		 {
			String stockIndu = (String) it.next();		
			stockLogger.logger.debug("行业："+stockIndu);
			//System.out.println(stockIndu);
			DefaultMutableTreeNode stIndustry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockIndu));
		//	DefaultMutableTreeNode stIndustry=new DefaultMutableTreeNode(stockIndu);
			industry.add(stIndustry);
			if(stockIndu.equals("大盘"))
				market.add(stIndustry);
			listStockIndustry=sid.getStockIndustry(stockIndu);
			stockLogger.logger.debug("行业股票数："+listStockIndustry.size());
			for(ie=listStockIndustry.iterator();ie.hasNext();)
			{
				String stockInduFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockIndustry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockInduFullID));
				//DefaultMutableTreeNode stStockIndustry=new DefaultMutableTreeNode(stockInduFullID);
				stockLogger.logger.debug("股票："+stockInduFullID);
				stIndustry.add(stStockIndustry);
			}
		 }
		 
		 listConcept=sid.getAllConcept(listStockInfo);
		 stockLogger.logger.debug("list concept size："+listConcept.size());
		 for(it = listConcept.iterator();it.hasNext();)
		 {
			String stockCon = (String) it.next();	
			stockLogger.logger.debug("概念："+stockCon);
			//System.out.println(stockCon);
			DefaultMutableTreeNode stConcept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockCon));
			//DefaultMutableTreeNode stConcept=new DefaultMutableTreeNode(stockCon);
			concept.add(stConcept);
			listStockConcept=sid.getStockConcept(stockCon);
			stockLogger.logger.debug("概念股票数："+listStockConcept.size());
			for(ie=listStockConcept.iterator();ie.hasNext();)
			{
				String stockConFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockConcept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockConFullID));
			//	DefaultMutableTreeNode stStockConcept=new DefaultMutableTreeNode(stockConFullID);
				stockLogger.logger.debug("股票："+stockConFullID);
				stConcept.add(stStockConcept);
			}
		 }
		 
		 listRegional=sid.getAllRegional(listStockInfo);
		 stockLogger.logger.debug("list regional size："+listRegional.size());
		 for(it = listRegional.iterator();it.hasNext();)
		 {
			String stockReg = (String) it.next();	
			stockLogger.logger.debug("地域："+stockReg);
			//System.out.println(stockReg);
			DefaultMutableTreeNode stRegional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockReg));
			//DefaultMutableTreeNode stRegional=new DefaultMutableTreeNode(stockReg);
			regional.add(stRegional);
			listStockRegional=sid.getStockRegional(stockReg);
			stockLogger.logger.debug("地域股票数："+listStockRegional.size());
			for(ie=listStockRegional.iterator();ie.hasNext();)
			{
				String stockRegFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockRegional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockRegFullID));
				//DefaultMutableTreeNode stStockRegional=new DefaultMutableTreeNode(stockRegFullID);
				stockLogger.logger.debug("股票："+stockRegFullID);
				stRegional.add(stStockRegional);
			}
		 }
		
		hs.add(market);
		hs.add(industry);		
		hs.add(concept);
		hs.add(regional);
		fund.add(openFund);
		fund.add(closeFund);
		root.add(hs);
		root.add(fund);
		stockTree=new JTree(root);	
		stockModel=(DefaultTreeModel)stockTree.getModel();
		
		//可编辑
		stockTree.setEditable(true);		
		//使用自定义节点绘制器
		//stockTree.setCellRenderer(new StockTreeCellRenderer());		
		
	//	Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject(); 
	//	((MyClass)obj).setName(newValue.toString()); 
	//	super.valueForPathChanged(path, obj);
	//	http://stackoverflow.com/questions/11554583/jtree-node-rename-preserve-user-object
		
		stockTree.setShowsRootHandles(true);
		stockTree.setRootVisible(true);
		
		MouseListener mouseListener=new MouseAdapter()
		{
			//按下鼠标
			public void mousePressed(MouseEvent e)
			{
				TreePath tp=stockTree.getPathForLocation(e.getX(), e.getY());
				
				if(tp!=null)
				{	
					movePath=tp;
					
		            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tp.getLastPathComponent();
					movePathName=selectedNode.toString();
				}
			}
			
			//松开鼠标
			public void mouseReleased(MouseEvent e)
			{
				TreePath curPath=stockTree.getPathForLocation(e.getX(), e.getY());
				if(curPath!=null && movePath!=null)
				{				
					//阻止向子节点拖动
					if(movePath.isDescendant(curPath) && movePath!=curPath)
					{
						JOptionPane.showMessageDialog(jFrame,"该节点不能移动","非法操作",JOptionPane.ERROR_MESSAGE);
						return;
					}
					//不是向子节点移动，鼠标按下，松开的也不是同一个节点
					else if(movePath!=curPath)
					{
						System.out.println("原："+movePathName);						
						if(curPath.getPathCount()==4 && movePath.getPathCount()==5)
						{
							DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)curPath.getLastPathComponent();
							String curPathName=selectedNode.toString();
							System.out.println("新："+curPathName);
							//add 方法先将该节点从原父节点下删除，再添加到新父节点
							((DefaultMutableTreeNode)curPath.getLastPathComponent()).add((DefaultMutableTreeNode)movePath.getLastPathComponent());
							movePath=null;
							stockTree.updateUI();
							
							int updateType=0;
				            if(selectedNode.getParent().toString().contains("行业"))
				            	updateType=ConstantsInfo.StockIndustry;
				            else if(selectedNode.getParent().toString().contains("概念"))
				            	updateType=ConstantsInfo.StockConcept;
				            else if(selectedNode.getParent().toString().contains("地域"))
				            	updateType=ConstantsInfo.StockRegional;
							
							//更新
							try {
								sid.updateFromStockName(movePathName,curPathName,updateType);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}
						else
						{	
							JOptionPane.showMessageDialog(jFrame,"该节点不能移动","非法操作",JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					
				}
			}
			
		};
		//鼠标监听器
		stockTree.addMouseListener(mouseListener);
	
		operationPane=new JPanel();
		
		//添加兄弟节点
		addBrotherButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				//获取选中节点
				DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)stockTree.getLastSelectedPathComponent();
				if(selectedNode==null)
					return;
				//获取选中节点父节点
				DefaultMutableTreeNode parent=(DefaultMutableTreeNode)selectedNode.getParent();
				if(parent==null)
					return;
				//2级才能添加兄弟结点
				if(selectedNode.getLevel()==3)
				{
					/*
					//创建新节点
					DefaultMutableTreeNode newNode=new DefaultMutableTreeNode("新节点");
					//获取选中节点选中索引
					int selectedIndex=parent.getIndex(selectedNode)+1;
					//在选中位置插入新节点
					stockModel.insertNodeInto(newNode, parent, selectedIndex);
					selectedNode.add(newNode);
					//获取从根节点到新节点的所有节点
					TreeNode[] nodes=stockModel.getPathToRoot(newNode);
					TreePath path=new TreePath(nodes);
					//显示指定的treePath
					stockTree.scrollPathToVisible(path);
					//stockTree.updateUI();		
					*/
								
					int selectedIndex = parent.getIndex(selectedNode) + 1;
					String nodeName = (String)JOptionPane.showInputDialog(null,"请输入名称","新结点",JOptionPane.PLAIN_MESSAGE);
					DefaultMutableTreeNode newNode =new DefaultMutableTreeNode(nodeName);
					System.out.println("新增兄弟结点名称:"+nodeName);
					stockModel.insertNodeInto(newNode, parent, selectedIndex);
									
				}
				else
					JOptionPane.showMessageDialog(jFrame,"该节点是不能添加兄弟节点","非法操作",JOptionPane.ERROR_MESSAGE);
					
			}
		});
		operationPane.add(addBrotherButton);
		
		addChildButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)stockTree.getLastSelectedPathComponent();
				if(selectedNode==null)
					return;
				
				//2级才能添加子结点
				if(selectedNode.getLevel()==2)
				{
					//创建新节点
					String nodeName = (String)JOptionPane.showInputDialog(null,"请输入名称","新结点",JOptionPane.PLAIN_MESSAGE);
					DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(nodeName);
					selectedNode.add(newNode);
					TreeNode[] nodes=stockModel.getPathToRoot(newNode);
					TreePath path=new TreePath(nodes);
					stockTree.scrollPathToVisible(path);
					stockTree.updateUI();	
				}
				else
					JOptionPane.showMessageDialog(jFrame,"该节点是不能添加子节点","非法操作",JOptionPane.ERROR_MESSAGE);
					
			}
		});
		operationPane.add(addChildButton);
		
		
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode) stockTree.getLastSelectedPathComponent();
			///	StockTreeNodeData
				if(selectedNode==null)
					return;
				//System.out.println(selectedNode.getLevel());
				if(selectedNode.getParent()==null)
					return;
				//删除指定节点 第三级才可删除
				if(selectedNode!=null && selectedNode.getParent()!=null && selectedNode.getLevel()==3)
				{
					
					String nodeName=selectedNode.toString();					
					
					int updateType=0;
		            if(selectedNode.getParent().toString().contains("行业"))
		            	updateType=ConstantsInfo.StockIndustry;
		            else if(selectedNode.getParent().toString().contains("概念"))
		            	updateType=ConstantsInfo.StockConcept;
		            else if(selectedNode.getParent().toString().contains("地域"))
		            	updateType=ConstantsInfo.StockRegional;
		            
		            System.out.println("删除结点名称:"+nodeName);
		            System.out.println(updateType);
		            //更新
		            try {
						sid.deleteFromStockPlate(nodeName,updateType);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					stockModel.removeNodeFromParent(selectedNode);
				}
				else
					JOptionPane.showMessageDialog(jFrame,"该节点不能被删除","非法操作",JOptionPane.ERROR_MESSAGE);
			}
		});
		operationPane.add(deleteButton);
		
		editButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				TreePath selectedPath=stockTree.getSelectionPath();
				if(selectedPath!=null)
				{
					//股票可编辑选中的节点，其余不可编辑 第四级才可编辑
					if(selectedPath.getPathCount()==4)			
					{	
						//selectedNode.get
						stockTree.getCellEditor().addCellEditorListener(new CellEditorAction());
						stockTree.startEditingAtPath(selectedPath);
					}
					else
						JOptionPane.showMessageDialog(jFrame,"该节点不能编辑","非法操作",JOptionPane.ERROR_MESSAGE);
						//System.out.println(selectedPath.toString());
				}
			
			}
		});
		operationPane.add(editButton);
		//stockTree.setSize(100, 400);
		//stockTreeSP=new JScrollPane(stockTree);
		jFrame.add(new JScrollPane(stockTree));
		jFrame.add(operationPane,BorderLayout.SOUTH);		
		
		jFrame.pack();
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
		jFrame.setVisible(true);
	}
	
	private class CellEditorAction implements CellEditorListener{
		
	    public void editingCanceled(ChangeEvent e) {
	        System.out.println("==========编辑取消");
	    }
	    public void editingStopped(ChangeEvent e) {
	    	
	    	Object selectNode = stockTree.getLastSelectedPathComponent();
	    	String oldNodeName=selectNode.toString();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
            if(node.getLevel()==3)
            {
	            System.out.println("旧结点名称:"+oldNodeName);
	            CellEditor cellEditor = (CellEditor) e.getSource();
	            String newNodeName = (String) cellEditor.getCellEditorValue();
	            node.setUserObject(newNodeName);
	            System.out.println("新结点名称:"+newNodeName);
	            DefaultTreeModel model = (DefaultTreeModel) stockTree.getModel();
	            model.nodeStructureChanged(node);
	            
	            int updateType=0;
	            if(node.getParent().toString().contains("行业"))
	            	updateType=ConstantsInfo.StockIndustry;
	            else if(node.getParent().toString().contains("概念"))
	            	updateType=ConstantsInfo.StockConcept;
	            else if(node.getParent().toString().contains("地域"))
	            	updateType=ConstantsInfo.StockRegional;
	            //更新
	            try {
					sid.updateFromStockPlate(oldNodeName,newNodeName,updateType);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        System.out.println("=======编辑结束");
            }
	    }	  
	}
	 
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		StockInfoManager sif=new StockInfoManager();
		PropertyConfigurator.configure("log4j.properties");
		listStockInfo=sid.getStockDaoList();
		
		sif.init();	

	}

}


