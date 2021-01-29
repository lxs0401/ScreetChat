package setting.protocol;


import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyStore;
import org.whispersystems.libsignal.util.KeyHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MySignedPreKeyStore implements SignedPreKeyStore
{
    Map<Integer,SignedPreKeyRecord> SignedPrekeys = new HashMap<Integer, SignedPreKeyRecord>();
    public MySignedPreKeyStore() throws InvalidKeyException {

    }
    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
        return this.SignedPrekeys.get(signedPreKeyId);
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return new LinkedList<>(this.SignedPrekeys.values());
    }

    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        this.SignedPrekeys.put(signedPreKeyId,record);
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        if(this.SignedPrekeys.containsKey(signedPreKeyId) == false){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        this.SignedPrekeys.remove(signedPreKeyId);
    }
}