package chatCore.loginUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import chatCore.client.ChatClient;
import chatCore.client.DataWR;
import chatCore.dataBase.Figures;
import chatCore.object.ExitButton;
import chatCore.object.MinimizeButton;
import chatCore.object.RecButton;
import org.whispersystems.libsignal.*;
import setting.protocol.*;
/**
 * LoginUI
 * 客户端启动登陆界面
 * 客户端程序从这里开始
 * 利用ChatClient连接服务器
 * 利用LoginAction监听按键
 * @author He11o_Liu
 */
public class LoginUI extends JFrame {

	private int wx, wy;
	private boolean isDraging = false;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField JKArea;
	private JPasswordField passwordField;
	private  signal sig;

	/**
	 * 客户端程序入口
	 * @param args
	 */
//	public static void main(String[] args) throws UntrustedIdentityException, InvalidKeyException, IOException, LegacyMessageException, InvalidKeyIdException, DuplicateMessageException, NoSessionException, InvalidMessageException {
//
//
//
//		LoginUI frame = new LoginUI();
//		frame.setVisible(true);
//		LoginAction.LoginJF = frame;
//	}
//


	/**
	 * 客户端登陆(启动界面)
	 */
	public LoginUI() throws NoSessionException, DuplicateMessageException, IOException, LegacyMessageException, InvalidKeyIdException, InvalidMessageException, InvalidKeyException, UntrustedIdentityException {
		// 设置无标题栏
		setUndecorated(true);
		// 监听鼠标 确保窗体能够拖拽
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				isDraging = true;
				wx = e.getX();
				wy = e.getY();
			}
			public void mouseReleased(MouseEvent e) {
				isDraging = false;
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (isDraging) {
					int left = getLocation().x;
					int top = getLocation().y;
					setLocation(left + e.getX() - wx, top + e.getY() - wy);
				}
			}
		});

		setBounds(100, 100, 439, 369);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setResizable(false);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblUserName = new JLabel("JKNumber");
		lblUserName.setForeground(Color.WHITE);
		lblUserName.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 25));
		lblUserName.setBounds(37, 170, 126, 27);
		contentPane.add(lblUserName);

		// 设置自制按钮
		ExitButton eb = new ExitButton();
		int windowWeith = this.getWidth();
		eb.setBounds(windowWeith - 4 - 40, 0, 40, 30);
		contentPane.add(eb);
		MinimizeButton mb = new MinimizeButton(this);
		mb.setBounds(windowWeith - 4 - 80, 0, 40, 30);
		contentPane.add(mb);

		JLabel lblPassword = new JLabel("PassWord");
		lblPassword.setForeground(Color.WHITE);
		lblPassword.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 25));
		lblPassword.setBounds(37, 215, 126, 27);
		contentPane.add(lblPassword);

		//	设置监听器，用于监听按键事件
		LoginAction la = new LoginAction();

		JKArea = new JTextField();
		JKArea.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 25));
		JKArea.setBounds(170, 167, 219, 35);
		JKArea.setBorder(null);
		contentPane.add(JKArea);
		JKArea.setColumns(10);
		la.setUsername(JKArea);
		// 限制只能输入数字
		JKArea.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				int keyChar = e.getKeyChar();
				if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {
				} else {
					e.consume(); // 关键，屏蔽掉非法输入
				}
			}
		});

		JLabel lblv = new JLabel("Login System");
		lblv.setForeground(Color.WHITE);
		lblv.setBackground(Color.WHITE);
		lblv.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 58));
		lblv.setBounds(37, 60, 357, 80);
		contentPane.add(lblv);

		RecButton Login = new RecButton("Login");
		Login.setBounds(225, 285, 170, 40);
		contentPane.add(Login);
		Login.setFocusPainted(false);// 设置不要焦点（文字的边框）
		Login.addActionListener(la);

		passwordField = new JPasswordField();
		passwordField.setBounds(170, 212, 219, 35);
		passwordField.setBorder(null);
		passwordField.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 30));
		passwordField.setColumns(10);
		contentPane.add(passwordField);
		la.setPassword(passwordField);

		RecButton register = new RecButton("Register");
		register.setBounds(37, 285, 170, 40);
		contentPane.add(register);
		register.addActionListener(la);

		JLabel label = new JLabel("Chat Room");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 20));
		label.setBounds(37, 20, 165, 35);
		contentPane.add(label);

		//利用ChatClient连接服务器
		File file = new File(".\\signal.core");
		DataWR.setPath("signal.core",".");
		// 判断文件是否存在
		ChatClient cc;

		if (!file.exists()) {
			sig = new signal();
			cc = new ChatClient("localhost", 9090,sig);

			la.setCc(cc);
			register.doClick();
		}
		else{
			sig = new signal(DataWR.getBytes());
			cc = new ChatClient("localhost", 9090,sig);
			JKArea.setText(String.valueOf(cc.sig.username));
			JKArea.setEnabled(false);
			la.setCc(cc);
		}

		if (!cc.ConnectServer()) {
			JOptionPane.showMessageDialog(null,"无法连接服务器", "错误", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}


	}
}
