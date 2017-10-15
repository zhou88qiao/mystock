package stockGUI.stocktableheader;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * 合并列头测试
 * 
 * @author Brad.Wu
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GroupableTableHeaderTest extends JFrame {

	GroupableTableHeader tableHeader;
    /**
     * @param args
     */
    public static void main(String[] args) {
        GroupableTableHeaderTest test = new GroupableTableHeaderTest();
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      test.setSize(800, 600);
      //  test.setExtendedState(JFrame.MAXIMIZED_BOTH);
        test.setVisible(true);
    }

    private DefaultTableModel tableModel = new DefaultTableModel() {

        public int getColumnCount() {
       // 	return 6;
            return 17;
        }

        public int getRowCount() {
            return 4;
        }

    };

    private JTable table = new JTable(tableModel);

    private JScrollPane scroll = new JScrollPane(table);

    /**
     * @throws HeadlessException
     */
    
   
    public GroupableTableHeaderTest() throws HeadlessException {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        GroupableTableHeader tableHeader = new GroupableTableHeader();
        table.setTableHeader(tableHeader);
        
      
        DefaultGroup group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(0);
        group.setColumnSpan(9);
        group.setHeaderValue("前一周期");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(9);
        group.setColumnSpan(8);
        group.setHeaderValue("当前");
        tableHeader.addGroup(group);
        
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(0);
        group.setColumnSpan(2);
        group.setHeaderValue("起点");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(2);
        group.setColumnSpan(2);
        group.setHeaderValue("终点");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(4);
        group.setColumnSpan(2);
        group.setHeaderValue("交叉");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(6);
        group.setColumnSpan(3);
        group.setHeaderValue("幅度");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(9);
      //  group.setColumnSpan(1);
        group.setHeaderValue("方向");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(10);
        group.setColumnSpan(2);
        group.setHeaderValue("起点");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(12);
        group.setColumnSpan(2);
        group.setHeaderValue("当前");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(14);
        group.setColumnSpan(3);
        group.setHeaderValue("幅度");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(0);
        group.setHeaderValue("时间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(1);
        group.setHeaderValue("点位");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(2);
        group.setHeaderValue("时间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(3);
        group.setHeaderValue("点位");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(4);
        group.setHeaderValue("时间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(5);
        group.setHeaderValue("点位");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(6);
        group.setHeaderValue("时间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(7);
        group.setHeaderValue("空间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(8);
        group.setHeaderValue("斜率");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(9);
        group.setHeaderValue("方向");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(10);
        group.setHeaderValue("时间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(11);
        group.setHeaderValue("点位");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(12);
        group.setHeaderValue("时间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(13);
        group.setHeaderValue("点位");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(14);
        group.setHeaderValue("时间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(15);
        group.setHeaderValue("空间");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(16);
        group.setHeaderValue("斜率");
        tableHeader.addGroup(group);
        
    
        /*
        DefaultGroup group = new DefaultGroup();
        group.setRow(0);
        group.setRowSpan(2);
        group.setColumn(0);
        group.setHeaderValue("楼层");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setRowSpan(2);
        group.setColumn(1);
        group.setHeaderValue("水平/垂直系数");
        tableHeader.addGroup(group);

        
        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(2);
        group.setColumnSpan(2);
        group.setHeaderValue("A & B");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(2);
        group.setHeaderValue("Column A");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(3);
        group.setHeaderValue("Column B");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(4);
        group.setColumnSpan(2);
        group.setHeaderValue("C & D");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(4);
        group.setHeaderValue("Column C");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(5);
        group.setHeaderValue("Column D");
        tableHeader.addGroup(group);
*/
        getContentPane().add(scroll, BorderLayout.CENTER);
    }
    

	void addTableHeader()
	{
		DefaultGroup group = new DefaultGroup();
        group.setRow(0);
        group.setRowSpan(3);//
        group.setColumn(0);
        group.setColumnSpan(1);
        group.setHeaderValue("类型"); //位于第1行1列 ，占3行
        tableHeader.addGroup(group);
        
		group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(1);
        group.setColumnSpan(6);
        group.setHeaderValue("技术");//位于第1行第2列，占6列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(7);
        group.setColumnSpan(5);
        group.setHeaderValue("基本面");//位于第1行第8列，占5列
        tableHeader.addGroup(group);
      
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(1);
   //    group.setRowSpan(2);
        group.setHeaderValue("时间");//位于第2行第2列，占2行
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(4);
   //     group.setRowSpan(3);
        group.setHeaderValue("点位");//位于第2行第2列，占2行
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(3);
        group.setColumnSpan(2);
        group.setHeaderValue("交叉点");//位于第2行第4列，占2列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(5);
        group.setColumnSpan(2);
        group.setHeaderValue("统计");//位于第2行第6列，占2列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(7);
        group.setColumnSpan(2);
        group.setHeaderValue("地区");//位于第2行第8列，占2列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(9);
      //  group.setColumnSpan(2);
        group.setHeaderValue("概念");//位于第2行第10列，占1列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(10);
        group.setColumnSpan(2);
        group.setHeaderValue("行业");//位于第2行第11列，占2列
        tableHeader.addGroup(group);
        
     
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(3);
        group.setHeaderValue("前");//位于第3行第4列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(4);
        group.setHeaderValue("后");//位于第3行第5列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(5);
        group.setHeaderValue("幅度");//位于第3行第6列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(6);
        group.setHeaderValue("时间");//位于第3行第7列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(7);
        group.setHeaderValue("省");//位于第3行第8列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(8);
        group.setHeaderValue("市");//位于第3行第9列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(9);
        group.setHeaderValue("概念");//位于第3行第10列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(10);
        group.setHeaderValue("行业");//位于第3行第11列
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(11);
        group.setHeaderValue("细分");//位于第3行第12列
        tableHeader.addGroup(group);
	}

    
}
