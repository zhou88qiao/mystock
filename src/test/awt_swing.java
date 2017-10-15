package test;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class awt_swing {

	private Frame f=new Frame("测试");
	private TextArea ta=new TextArea(10,100);
	private Button b1=new Button("按钮1");
	private Button b2=new Button("按钮2");
	private MenuBar mb=new MenuBar();
	Menu file =new Menu("文件");
	Menu edit =new Menu("编辑");
	PopupMenu pop=new PopupMenu();
	MenuItem newItem=new MenuItem("新建");
	MenuItem saveItem=new MenuItem("保存");
	MenuItem exitItem=new MenuItem("退出",new MenuShortcut(KeyEvent.VK_X));
	CheckboxMenuItem autoWrap=new CheckboxMenuItem("自动换行");
	MenuItem copyItem=new MenuItem("复制");
	MenuItem pasteItem=new MenuItem("粘贴");
	Menu format=new Menu("格式");
	MenuItem commentItem=new MenuItem("注释",new MenuShortcut(KeyEvent.VK_SLASH,true));
	MenuItem cancelItem=new MenuItem("取消注释");
	public void init()
	{
		FirstListener f1=new FirstListener();
		b1.addActionListener(f1);
		b1.addActionListener(new SecondListener());
		b2.addActionListener(f1);
		
		final Panel p=new Panel();
		p.add(b1);
		p.add(b2);
	//	f.addWindowListener(new MyListener());//内部类
		
		//以匿名内部类的形式创建菜单监听器
		ActionListener menuListener=new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				String cmd=e.getActionCommand();
				ta.append("单击"+cmd+"菜单"+"\n");
				if(cmd.equals("退出"))
				{
					System.exit(0);
				}
				
			}
			
		};
		
		////以匿名内部类的形式创建菜单监听器
		saveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ta.append("单击"+"save菜单"+"\n");	
			}
		}); 
		
		commentItem.addActionListener(menuListener);
		exitItem.addActionListener(menuListener);
		newItem.addActionListener(menuListener);
		file.add(newItem);
		file.add(saveItem);
		file.add(exitItem);
		
		edit.add(autoWrap);
		edit.addSeparator();
		edit.add(copyItem);
		edit.add(pasteItem);
		//右键菜单
		pop.add(autoWrap);
		pop.addSeparator();
		pop.add(copyItem);
		pop.add(pasteItem);
		
		format.add(commentItem);
		format.add(cancelItem);
		
		
		edit.add(new MenuItem("-"));
		//将format组合到edit菜单 二级菜单
		edit.add(format);
		
		pop.add(new MenuItem("-"));
		//将format组合到edit菜单 二级菜单
		pop.add(format);
		
		p.setPreferredSize(new Dimension(300,160));
		p.add(pop);
		

		
		
		//将file edit菜单添加到mb菜单条中
		mb.add(file);
		mb.add(edit);
		//为f窗口设置菜单条
		f.setMenuBar(mb);
		
		////以匿名内部类的形式创建菜单监听器
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.out.println("用户关闭窗口");
				System.exit(0);
				
			}
		}); 
		
		////以匿名内部类的形式创建菜单监听器
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.out.println("用户关闭窗口");
				System.exit(0);
				
			}
		}); 
		
		f.add(ta);
		f.add(p,BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);	
		
	}
	class MyListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			System.out.println("用户关闭窗口");
			System.exit(0);
			
		}
	}
	class FirstListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			ta.append("第一个事件监听器被触发，事件源是"+e.getActionCommand()+"\n");
		}
		
	}
	
	class SecondListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			ta.append("单击了"+e.getActionCommand()+"按钮\n");
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		awt_swing as=new awt_swing();
		as.init();
	}

}
