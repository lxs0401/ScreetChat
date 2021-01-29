package chatCore.msg;

/*
 * 登陆消息体
 */
public class MsgLogin extends MsgHead {
	private String pwd;

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
