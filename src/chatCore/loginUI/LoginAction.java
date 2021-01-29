package chatCore.loginUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import chatCore.client.ChatClient;
import chatCore.dataBase.Figures;
import chatCore.friendListUI.FriendListUI;


/**
 * LoginAction
 * 登陆期间按键监听器
 * 登陆和注册都公用此监听器
 * 用以监听三个按钮
 * 实现登陆、注册、提交注册信息的功能
 * @author He11o_Liu
 *
 */
public class LoginAction implements ActionListener {

	private JTextField userid_field;// Login界面的ID号
	private JPasswordField password_field;// Login界面的密码
//	private JTextField NikeName;// Register界面的昵称
//	private JTextField Rpassword;// Register界面的密码
	private ChatClient cc; //LoginUI传过来的用于连接服务器的ChatClient
	private boolean is_Registering = false;// 这个参数用于判断是否已经打开了注册界面
	public static JFrame LoginJF;//登陆界面以及注册界面的JF 用于关闭窗口

	public ChatClient getCc() {
		return cc;
	}

	public void setCc(ChatClient cc) {
		this.cc = cc;
	}

	public JTextField getUsername() {
		return userid_field;
	}

	public void setUsername(JTextField username) {
		this.userid_field = username;
	}

	public JPasswordField getPassword() {
		return password_field;
	}

	public void setPassword(JPasswordField password) {
		this.password_field = password;
	}

	public boolean isIs_Registering() {
		return is_Registering;
	}

	public void setIs_Registering(boolean is_Registering) {
		this.is_Registering = is_Registering;
	}

	/**
	 * 按键监听器
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton jb = (JButton) e.getSource();
		//若按下的按键为登陆
		if (jb.getText().equals("Login")) {
			int userid = Integer.valueOf(userid_field.getText());
			if (userid_field.getText().equals("")) {//若输入ID号为空
				JOptionPane.showMessageDialog(null, "ID不为空", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				String password = new String(password_field.getPassword());
				if (password.equals(""))
					JOptionPane.showMessageDialog(null, "密码不为空", "Error", JOptionPane.ERROR_MESSAGE);
				else{
					int result = cc.Login(userid, password) ;
					if(result == 0){//若密码正确
						Figures.cc = cc;
						new FriendListUI();
						LoginJF.dispose();
					}
					else if (result == 1){//若密码错误
						JOptionPane.showMessageDialog(null, "用户名或密码错误", "Error", JOptionPane.ERROR_MESSAGE);
						password_field.setText("");
					}
					else if(result == 2){
						JOptionPane.showMessageDialog(null, "该用户已经登陆", "Error", JOptionPane.ERROR_MESSAGE);
						password_field.setText("");
						userid_field.setText("");
					}
				}
			}
		} 
		//若按下的按键为注册按键
		else if (jb.getText().equals("Register")) {
			//System.out.println("One click");
			if (!is_Registering) {
				new RegisterUI(this,cc); // 将当前监听器传过去
				is_Registering = true;
			}
		}
	}

}
