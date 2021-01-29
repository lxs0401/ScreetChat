package chatCore.friendListUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import chatCore.dataBase.Figures;
import chatCore.dataBase.ListInfo;
import chatCore.object.AddButton;
import chatCore.object.ExitButton;
import chatCore.object.MinimizeButton;
import chatCore.object.ScrollBarUI;

public class FriendListUI extends JFrame {

	private boolean isAdding = false;
	private ListPane list;
	private int xx, yy;
	private boolean isDraging = false;
	private FriendListUI flu;
	private JScrollPane scrollPane;
	private JPanel panel;
	
	public boolean isAdding() {
		return isAdding;
	}

	public void setAdding(boolean isAdding) {
		this.isAdding = isAdding;
	}
	
	private ListInfo user;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public FriendListUI() {
		
		flu = this;
		Figures.flu = this;
		setBackground(Color.DARK_GRAY);

		// 接收列表信息

		try {
			user = Figures.cc.getlist();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//设置参数
		Figures.JKNum = user.getJKNum();
		Figures.NickName = user.getNickName();
		
		
		

		// 设置无标题栏
		setUndecorated(true);

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				isDraging = true;
				xx = e.getX();
				yy = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				isDraging = false;
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (isDraging) {
					int left = getLocation().x;
					int top = getLocation().y;
					setLocation(left + e.getX() - xx, top + e.getY() - yy);
				}
			}
		});

		
		
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setResizable(false);
		contentPane.setLayout(null);

		// 设置自制按钮

		ExitButton eb = new ExitButton();
		int windowWeith = this.getWidth();
		eb.setBounds(windowWeith - 4 - 40, 0, 40, 30);
		contentPane.add(eb);
		MinimizeButton mb = new MinimizeButton(this);
		mb.setBounds(windowWeith - 4 - 80, 0, 40, 30);
		contentPane.add(mb);

		JPanel OwnInfo = new JPanel();
		OwnInfo.setBounds(15, 15, 272, 70);
		OwnInfo.setBackground(Color.DARK_GRAY);
		OwnInfo.setPreferredSize(new Dimension(200, 50));
		contentPane.add(OwnInfo);
		OwnInfo.setLayout(null);


		AddButton button = new AddButton();
		button.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 36));
		button.setBounds(436, 36,40, 40);
		contentPane.add(button);

		JLabel UserInfo = new JLabel(user.getNickName());
		UserInfo.setForeground(Color.WHITE);
		UserInfo.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 35));
		UserInfo.setBounds(1, 16, 202, 47);
		OwnInfo.add(UserInfo);


		panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBounds(15, 88, 470, 290);
		panel.setBorder(null);
		contentPane.add(panel);
		panel.setLayout(null);

		list = new ListPane(user);
		scrollPane = new JScrollPane(list);
		Figures.list = list;//设置list
		scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI()); 
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);// 不显示水平滚动条；
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(null);
		scrollPane.setBounds(0, 0, 470, 290);
		panel.add(scrollPane);
		

		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(isAdding != true){
					isAdding = true;
					AddFriendUI afu = new AddFriendUI(flu, Figures.cc);
				}
				
			}
		});
		
		setVisible(true);
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高
		int height = this.getHeight();
		setLocation(screenWidth * 3 / 4, (screenHeight - height) / 2);

		
		/*
		 * 开启从服务器不间断获取信息
		 */
		Figures.cc.start();
		
		
	}
	
	public void updatelist(ListPane new_list){
//		scrollPane.updateUI();
//		scrollPane.setBorder(null);
//		scrollPane.setBounds(0, 0, 272, 420);
//		scrollPane = new JScrollPane(new_list);
//		Figures.list = list;//设置list
//		scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI()); 
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);// 不显示水平滚动条；
//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		scrollPane.setBorder(null);
//		scrollPane.setBounds(0, 0, 272, 420);
//		panel.add(scrollPane);
	}
	
	
}
