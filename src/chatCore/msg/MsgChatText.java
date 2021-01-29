package chatCore.msg;

public class MsgChatText extends MsgHead {
	private byte[] msgText;

	public byte[] getMsgText() {
		return msgText;
	}

	public void setMsgText(byte[] msgText) {
		this.msgText = msgText;
	}

}
