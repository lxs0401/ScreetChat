package chatCore.dataBase;

import chatCore.client.ChatClient;
import chatCore.friendListUI.AddFriendUI;
import chatCore.friendListUI.FriendListUI;
import chatCore.friendListUI.ListPane;
import org.whispersystems.libsignal.state.SessionRecord;

public class Figures {
	public static final int ServerJK = 2000000000;// 服务器的JK号
	public static final int LoginJK = 2000000001;// 登陆界面的JK号
	public static ChatClient cc;
	public static int JKNum;
	public static int Pic;
	public static String NickName;
	public static ListPane list;
	public static AddFriendUI afu;
	public static FriendListUI flu;
	public static SessionRecord nul = new SessionRecord();
}
