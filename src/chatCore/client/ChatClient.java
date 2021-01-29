package chatCore.client;

import java.io.*;
import java.net.Socket;
import java.security.interfaces.ECPrivateKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import chatCore.chatUI.DialogUI;
import chatCore.dataBase.Figures;
import chatCore.dataBase.ListInfo;
import chatCore.msg.*;
import chatCore.tools.DialogTool;
import chatCore.tools.PackageTool;
import chatCore.tools.ParseTool;
import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.ecc.DjbECPublicKey;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.state.PreKeyRecord;
import setting.protocol.signal;
import org.whispersystems.libsignal.ecc.*;
/**
 * ChatClient 该类用于与服务器进行通信
 * 
 * @author He11o_Liu
 */
public class 	ChatClient extends Thread {
	private String ServerIP;
	private int port;
	private Socket client;
	private static int OwnJKNum;// 当登陆成功后，就该ChatClient的唯一JK号
	private InputStream ins;
	private OutputStream ous;
	public signal sig;
	/**
	 * ChatClient的构建函数
	 * 
	 * @param ServerIP
	 *            服务器的IP
	 * @param port
	 *            端口
	 */
	public ChatClient(String ServerIP, int port,signal sig) {
		this.ServerIP = ServerIP;
		this.port = port;
		this.sig = sig;
	}

	/**
	 * 不断的处理从服务器发来的信息
	 */
	public void run() {
		while (true) {
			try {
				processMsg();
			} catch (IOException | UntrustedIdentityException | InvalidKeyException e) {
				e.printStackTrace();
				System.out.println("脱离主进程与服务器断开连接");
				JOptionPane.showMessageDialog(null, "与服务器断开连接", "ERROR", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			} catch (DuplicateMessageException e) {
				e.printStackTrace();
			} catch (InvalidMessageException e) {
				e.printStackTrace();
			} catch (NoSessionException e) {
				e.printStackTrace();
			} catch (InvalidKeyIdException e) {
				e.printStackTrace();
			} catch (InvalidVersionException e) {
				e.printStackTrace();
			} catch (LegacyMessageException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 连接到服务器
	 * 
	 * @return 是否连接到服务器
	 */
	public boolean ConnectServer() {
		try {
			client = new Socket(ServerIP, port);
			System.out.println("服务器已连接");
			ins = client.getInputStream();
			ous = client.getOutputStream();// 获取该连接的输入输出流
			return true;
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return false;
	}



	/**
	 * Register 注册用户
	 * 
	 * @param NikeName
	 *            昵称
	 * @param PassWord
	 *            密码
	 * @return 注册状态
	 */
	public boolean Reg(String NikeName, String PassWord) throws NoSessionException, DuplicateMessageException, UnsupportedEncodingException, LegacyMessageException, InvalidKeyIdException, InvalidMessageException, InvalidKeyException, UntrustedIdentityException {

		try {

			MsgReg mr = new MsgReg();
			int len = 33+3434; // MsgReg的长度为固定的33
			byte type = 0x01; // MsgReg类型为0x01

			// 设置MsgReg的参数
			mr.setTotalLen(len);
			mr.setType(type);
			mr.setDest(Figures.ServerJK); // 服务器的JK号
			mr.setSrc(Figures.LoginJK);
			mr.setNikeName(NikeName);
			mr.setPwd(PassWord);
			mr.setIdentityKey(sig.identityKeyPair.getPublicKey().serialize());
			mr.setReistrationId(sig.registrationId);
			mr.setSignedPreKey(sig.signedPreKey.getKeyPair().getPublicKey().serialize());
			mr.setSignedPreKeySig(sig.signedPreKey.getSignature());
			LinkedList<byte[]> prekeys = new LinkedList<>();
			for (PreKeyRecord each : sig.preKeys)
			{
				prekeys.add(each.getKeyPair().getPublicKey().serialize());
			}
			mr.setPreKeys(prekeys.toArray(new byte[100][]));

			// 打包MsgReg
			byte[] sendMsg = PackageTool.packMsg(mr);
			ous.write(sendMsg);
//			System.out.print("qqq");
			// 接收服务器的反馈信息
			byte[] data = receiveMsg();

			// 将数组转换为类
			MsgHead recMsg = ParseTool.parseMsg(data);

			if (recMsg.getType() != 0x11) {// 不是回应注册消息
				System.out.println("通讯协议错误");
				return false;
			}

			MsgRegResp mrr = (MsgRegResp) recMsg;
			// System.out.println("TestHere"+recMsg.getDest());
			if (mrr.getState() == 0) {
				/*
				 * 注册成功
				 */
				//				// System.out.println("注册的JK号为" + mrr.getDest());
				JOptionPane.showMessageDialog(null, "注册成功\nJK码为" + mrr.getDest());
				sig.username = String.valueOf(mrr.getDest());
				byte[] serialize = sig.serialize();
				DataWR.getFile(serialize);

				return true;
			} else {
				/*
				 * 注册失败
				 */
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("与服务器断开连接");

		return false;
	}

	/**
	 * Login 向服务器发送登陆请求
	 * 
	 * @param id
	 * @param pwd
	 * @return 能否登陆
	 */
	public int Login(int id, String pwd) {
		try {

			DataInputStream dis = new DataInputStream(ins);

			MsgLogin ml = new MsgLogin();
			int len = 23;
			byte type = 0x02;

			// 设置MsgLogin的各种东西
			ml.setTotalLen(len);
			ml.setType(type);
			ml.setDest(Figures.ServerJK);
			ml.setSrc(id);
			ml.setPwd(pwd);
			// 打包MsgLogin
			byte[] sendmsg = PackageTool.packMsg(ml);
			ous.write(sendmsg);
			// 接收服务器的反馈信息
			byte[] data = receiveMsg();
			// 将数组转换为类
			MsgHead recMsg = ParseTool.parseMsg(data);
			if (recMsg.getType() != 0x22) {// 不是登陆反馈信息
				System.out.println("通讯协议错误");
				return 5;
			}
			MsgLoginResp mlr = (MsgLoginResp) recMsg;
			byte resp = mlr.getState();
			if (resp == 0) {
				System.out.println("登陆成功");
				OwnJKNum = id;
				return 0;
			} else if (resp == 1) {
				System.out.println("JK号或密码错误");
				return 1;
			} else if(resp == 2){
				System.out.println("这个账号已经登陆");
				return 2;
			} else {
				System.out.println("未知错误");
				return 3;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("与服务器断开连接");
		return 4;
	}

	/**
	 * ListInfo 
	 * 接收好友列表
	 * 
	 * @return
	 * @throws IOException
	 */
	public ListInfo getlist() throws IOException {
		byte[] data = receiveMsg();
		MsgHead recMsg = ParseTool.parseMsg(data);
		if (recMsg.getType() != 0x03) {
			System.out.println("通讯协议错误");
			System.exit(0);
		}
		return packlist(recMsg);
	}

	public ListInfo packlist(MsgHead recMsg){
		ListInfo list = new ListInfo();
		MsgTeamList mtl = (MsgTeamList) recMsg;
		list.setNickName(mtl.getUserName());
		list.setJKNum(mtl.getDest());
		list.setPic(mtl.getPic());
		list.setListCount(mtl.getListCount());
		list.setListName(mtl.getListName());
		list.setBodyCount(mtl.getBodyCount());
		list.setBodyNum(mtl.getBodyNum());
		list.setBodypic(mtl.getBodyPic());
		list.setNikeName(mtl.getNikeName());
		list.setBodyState(mtl.getBodyState());
		return list;
	}
	
	/**
	 * 这个方法用于添加好友
	 * @param add_id
	 * @param list_name
	 * @return 0  成功
	 * @return 1 查无此人
	 * @return 2 已有此人
	 * @return 3 创建错误
	 * @return 4 通讯协议错误
	 * @throws IOException
	 */
	public void SendaddFriend(int add_id, String list_name) throws IOException {
		MsgAddFriend maf = new MsgAddFriend();
		byte data[] = list_name.getBytes();
		int TotalLen = 17;
		TotalLen += data.length;
		byte type = 0x05;
		maf.setTotalLen(TotalLen);
		maf.setType(type);
		maf.setDest(Figures.ServerJK);
		maf.setSrc(OwnJKNum);
		maf.setAdd_ID(add_id);
		maf.setList_name(list_name);
		byte[] sendMsg = PackageTool.packMsg(maf);
		ous.write(sendMsg);
		ous.flush();

	}

//		System.out.println("Before");
//		// 接收服务器的反馈信息
//		byte[] resp = receiveMsg();
//		
//		System.out.println("After");
//		// 将数组转换为类
//		MsgHead recMsg = ParseTool.parseMsg(resp);
//		if (recMsg.getType() != 0x55) {// 不是登陆反馈信息
//			System.out.println("通讯协议错误");
//			return 4;
//		}
//		MsgAddFriendResp mafr = (MsgAddFriendResp) recMsg;
//		byte resp_state = mafr.getState();
//		if (resp_state == 0x00) {
//			System.out.println("添加成功");
//			return 0;
//		} else if (resp_state == 0x01) {
//			System.out.println("查无此人");
//			return 1;
//		} else if (resp_state == 0x02) {
//			System.out.println("已有此人");
//			return 2;
//		} else if (resp_state == 0x03) {
//			System.out.println("创建通讯列表错误");
//			return 3;
//		} else {
//			System.out.println("通讯协议错误");
//		}
//		return 4;

	/**
	 * sendMsg 发送信息
	 * 
	 * @param to
	 * @param Msg
	 * @throws IOException
	 */
	public void sendMsg(int to, String Msg) throws IOException, UntrustedIdentityException, InvalidKeyException {
		MsgChatText mct = new MsgChatText();
		byte data[] = Msg.getBytes();
		int TotalLen = 13;

		if(sig.mySessionStroe.isFresh(to))
		{
			mct.setMsgText(sig.encrypt(
							Msg.getBytes("UTF-8"),
							new SignalProtocolAddress(String.valueOf(to),0),
							true
					)
			);
		}
		else {
			mct.setMsgText(sig.encrypt(
							Msg.getBytes("UTF-8"),
							new SignalProtocolAddress(String.valueOf(to),0),
							false
					)
			);
		}
		TotalLen += mct.getMsgText().length;
		System.out.println(mct.getMsgText().length);
		byte type = 0x04;
		mct.setTotalLen(TotalLen);
		mct.setType(type);
		mct.setDest(to);
		mct.setSrc(OwnJKNum);
		byte[] sendMsg = PackageTool.packMsg(mct);
		ous.write(sendMsg);
		ous.flush();
	}

	/*
	 * 这个方法用于从输入流中间读取一定长度的信息 信息长度为最前面的一个整数
	 * 
	 * @return byte[]读出的长度信息
	 */
	public byte[] receiveMsg() throws IOException {
		DataInputStream dis = new DataInputStream(ins);
		int totalLen = dis.readInt();
		System.out.println("TotalLen"+totalLen);
		// 读取totalLen长度的数据
		byte[] data = new byte[totalLen - 4];
		dis.readFully(data);
		return data;
	}

	private void getFirendsKeys(ListInfo list) throws IOException {
		List<SignalProtocolAddress> allAd = sig.mySessionStroe.getAllAddress();
		HashMap<Integer,Boolean> visit = new HashMap<>();
		for(int[] x : list.getBodyNum())
		{
			for(int y : x)
			{
				visit.put(y,true);
			}
		}
		for(SignalProtocolAddress each : allAd)
		{
			visit.remove(Integer.parseInt(each.getName()));
		}
		for(int each : visit.keySet())
		{
			MsgSignalKeys msk = new MsgSignalKeys();
			msk.setFriend_id(each);
			msk.setDest(Figures.ServerJK);
			msk.setSrc(Figures.JKNum);
			msk.setType((byte) 0x06);
			msk.setTotalLen(17);
			byte[] sendMsg = PackageTool.packMsg(msk);
			ous.write(sendMsg);
			ous.flush();
			return;
		}
	}
	/**
	 * processMsg 接受服务器传来的消息
	 * 
	 * @throws IOException
	 */
	public void processMsg() throws IOException, UntrustedIdentityException, InvalidKeyException, NoSessionException, LegacyMessageException, InvalidVersionException, InvalidMessageException, DuplicateMessageException, InvalidKeyIdException {
		byte[] data = receiveMsg();
		// 将数组转换为类
		MsgHead recMsg = ParseTool.parseMsg(data);
		byte MsgType = recMsg.getType();

		// 根据不同的信息进行处理
		if (MsgType == 0x04) {
			MsgChatText mct = (MsgChatText) recMsg;
			int from = mct.getSrc();
			String Msg;
			if(sig.mySessionStroe.isFresh(mct.getSrc()))
			{
				Msg = new String(sig.decrypt(
						mct.getMsgText(),
						new SignalProtocolAddress(String.valueOf(mct.getSrc()),0),
						true),"UTF-8"
				);
				sendMsg(mct.getSrc(),"聊天初始化完成");
			}
			else{
				Msg = new String(sig.decrypt(
						mct.getMsgText(),
						new SignalProtocolAddress(String.valueOf(mct.getSrc()),0),
						false),"UTF-8"
				);
			}

			DialogTool.ShowMessage(from, Msg);
		}
		else if(MsgType == 0x03){//更新好友列表
			System.out.println("Refresh list");
			ListInfo list = packlist(recMsg);
			Figures.list.Refresh_List(list);
			getFirendsKeys(list);
		}
		else if (MsgType == 0x55){
//			System.out.println("Here");
			MsgAddFriendResp mafr = (MsgAddFriendResp) recMsg;
			byte result = mafr.getState();
			System.out.println("Add Friend Result "+result);
			if(Figures.afu != null){
//				System.out.println("To show Result");
				Figures.afu.showResult(result);
			}
		}
		else if(MsgType == 0x66)
		{
			MsgSignalKeysResp mskr = (MsgSignalKeysResp) recMsg;

			sig.createSession(new SignalProtocolAddress((String.valueOf(mskr.getFirend_id())), 0),
					mskr.getReistrationId(),
					Curve.decodePoint(mskr.getPreKeys(),0),
					new IdentityKey(mskr.getIdentityKey(),0),
					Curve.decodePoint(mskr.getSignedPreKey(),0),
					mskr.getSignedPreKeySig()
			);
		}
	}

	/*
	 * 下列代码均为测试代码
	 */
	/*
	 * 用于测试是否成功接收列表
	 */
	public void printList(ListInfo list) {
		System.out.println("Nick " + list.getNickName());
		System.out.print(" JK " + list.getJKNum());
		System.out.print(" Pic " + list.getPic());
		byte listCount = list.getListCount();
		System.out.println("listCount " + listCount);
		String[] listName = list.getListName();
		byte[] bodyCount = list.getBodyCount();
		int[][] bodyNum = list.getBodyNum();
		int[][] bodyPic = list.getBodypic();
		String[][] nikeName = list.getNikeName();
		byte[][] state = list.getBodyState();
		int i, j;
		for (i = 0; i < listCount; i++) {
			System.out.print("ListName " + listName[i]);
			System.out.println(" bodyCount " + bodyCount[i]);
			for (j = 0; j < bodyCount[i]; j++) {
				System.out.print(" JK " + bodyNum[i][j]);
				System.out.print(" Pic " + bodyPic[i][j]);
				System.out.print(" nikeName " + nikeName[i][j]);
				System.out.print(" State " + state[i][j]);
			}
			System.out.print("\n");
		}
	}

	/*
	 * 用来测试客户端
	 */
//
//	public static void main(String[] args) throws IOException {
//		System.out.println("测试客户端开启");
//		ChatClient cc = new ChatClient("localhost", 9090);
//		if (cc.ConnectServer()) {
//			System.out.println("连接服务器完成");
//			int result = cc.Login(0, "123");
//			if (result==0){
//				System.out.println("登陆测试成功");
//				cc.printList(cc.getlist());
//				//cc.start();
//				result = cc.addFriend(5, "新的列表");
//				System.out.println(result);
//			} else {
//				System.out.println("登陆失败");
//			}
//		} else {
//			System.out.println("无法连接服务器");
//		}
//
//	}

}
