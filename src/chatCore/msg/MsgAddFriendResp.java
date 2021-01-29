package chatCore.msg;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.ecc.ECPublicKey;

public class MsgAddFriendResp extends MsgHead{
	private byte state;

	int reistrationId;
	ECPublicKey[] preKeys;
	IdentityKey identityKey;
	ECPublicKey signedPreKey;
	byte[] signedPreKeySig;

	public int getReistrationId() {
		return reistrationId;
	}

	public void setReistrationId(int reistrationId) {
		this.reistrationId = reistrationId;
	}

	public ECPublicKey[] getPreKeys() {
		return preKeys;
	}

	public void setPreKeys(ECPublicKey[] preKeys) {
		this.preKeys = preKeys;
	}

	public IdentityKey getIdentityKey() {
		return identityKey;
	}

	public void setIdentityKey(IdentityKey identityKey) {
		this.identityKey = identityKey;
	}

	public ECPublicKey getSignedPreKey() {
		return signedPreKey;
	}

	public void setSignedPreKey(ECPublicKey signedPreKey) {
		this.signedPreKey = signedPreKey;
	}

	public byte[] getSignedPreKeySig() {
		return signedPreKeySig;
	}

	public void setSignedPreKeySig(byte[] signedPreKeySig) {
		this.signedPreKeySig = signedPreKeySig;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
}
