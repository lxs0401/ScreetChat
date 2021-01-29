package chatCore.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import chatCore.msg.*;

/*
 * 此类用于打包信息
 */
public class PackageTool {

	/*
	 * 向流对象中写入固定长度的字符串
	 * 
	 * @param dous 流对象
	 * 
	 * @param len 字节的长度
	 * 
	 * @param s 写入的字符串
	 */
	private static void writeString(DataOutputStream dous, int len, String s) throws IOException {
		byte[] data = s.getBytes();
		if (data.length > len) {
			throw new IOException("写入长度超长");
		}
		dous.write(data);
		while (data.length < len) {
			dous.writeByte('\0');
			len--;
		}
	}
	private static void writebyte(DataOutputStream dous, int len, byte[] data) throws IOException {
		if (data.length > len) {
			throw new IOException("写入长度超长");
		}
		dous.write(data);
		while (data.length < len) {
			dous.writeByte('\0');
			len--;
		}
	}
	private static byte[] intToByteArr(int x){

		byte[] arr = new byte[4];
		arr[3]= (byte)(x & 0xff);
		arr[2]= (byte)(x>>8 & 0xff);
		arr[1]= (byte)(x>>16 & 0xff);
		arr[0]= (byte)(x>>24 & 0xff);
		return arr;
	}
	private static byte[] bytesstobytes(byte[][] data)
	{
		byte[] res = new byte[3300];
		int index = 0;
		for(byte[] each : data)
		{
			for (byte each2 : each)
			{
				res[index] = each2;
				index += 1;
			}
		}

		return  res;
	}
	public static byte[] packMsg(MsgHead msg) throws IOException {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		writeHead(dous, msg);
		int msgType = msg.getType();
		if (msgType == 0x01) {
			MsgReg mr = (MsgReg) msg;
			writeString(dous, 10, mr.getNikeName());
			writeString(dous, 10, mr.getPwd());
			dous.write(mr.getIdentityKey());
			dous.write(mr.getSignedPreKey());
			dous.write(mr.getSignedPreKeySig());
			dous.writeInt(mr.getReistrationId());
			dous.write(bytesstobytes(mr.getPreKeys()));

		} else if (msgType == 0x11) {
			MsgRegResp mrr = (MsgRegResp) msg;
			dous.write(mrr.getState());
		} else if (msgType == 0x02) {
			MsgLogin mli = (MsgLogin) msg;
			writeString(dous, 10, mli.getPwd());
		} else if (msgType == 0x22) {
			MsgLoginResp mlr = (MsgLoginResp) msg;
			dous.write(mlr.getState());
		} else if (msgType == 0x03) {
			MsgTeamList mtl = (MsgTeamList) msg;

			// 从mtl中获取信息
			String userName = mtl.getUserName();
			int pic = mtl.getPic();
			byte listCount = mtl.getListCount();
			String listName[] = mtl.getListName();
			byte bodyCount[] = mtl.getBodyCount();
			int bodyNum[][] = mtl.getBodyNum();
			int bodyPic[][] = mtl.getBodyPic();
			String nikeName[][] = mtl.getNikeName();
			byte bodyState[][] = mtl.getBodyState();

			// 开始写入流中
			int i, j;
			writeString(dous, 10, userName);
			dous.writeInt(pic);
			dous.write(listCount);// 分组个数
			for (i = 0; i < listCount; i++) {
				writeString(dous, 10, listName[i]);
				dous.write(bodyCount[i]);
				for (j = 0; j < bodyCount[i]; j++) {// 每个组里面
					dous.writeInt(bodyNum[i][j]);
					dous.writeInt(bodyPic[i][j]);
					writeString(dous, 10, nikeName[i][j]);
					dous.write(bodyState[i][j]);
				}
			}

		} else if (msgType == 0x04) {
			MsgChatText mct = (MsgChatText) msg;
			dous.write(mct.getMsgText());

		} else if (msgType == 0x05) {
			MsgAddFriend maf = (MsgAddFriend) msg;
			dous.writeInt(maf.getAdd_ID());
			writeString(dous,maf.getTotalLen() -  17,maf.getList_name());
		} else if (msgType == 0x55) {
			MsgAddFriendResp mafr = (MsgAddFriendResp) msg;
			dous.write(mafr.getState());
		} else if(msgType == 0x06){
			MsgSignalKeys msk = (MsgSignalKeys) msg;
			dous.writeInt(msk.getFriend_id());
		}

		dous.flush();
		byte[] data = bous.toByteArray();
		return data;
	}

	private static void writeHead(DataOutputStream dous, MsgHead msg) throws IOException {
		dous.writeInt(msg.getTotalLen());
		dous.writeByte(msg.getType());
		dous.writeInt(msg.getDest());
		dous.writeInt(msg.getSrc());
	}

}
