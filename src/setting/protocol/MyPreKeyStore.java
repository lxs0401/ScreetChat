package setting.protocol;


import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.PreKeyStore;
import org.whispersystems.libsignal.util.KeyHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPreKeyStore implements PreKeyStore
{
    Map<Integer, PreKeyRecord> preKeys;

    public MyPreKeyStore()
    {
        preKeys = new HashMap<Integer, PreKeyRecord>();
    }
    @Override
    public PreKeyRecord loadPreKey(int preKeyId) throws InvalidKeyIdException {
        return this.preKeys.get(preKeyId);
    }

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        this.preKeys.put(preKeyId,record);
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        if(this.preKeys.containsKey(preKeyId) == true)
        {
            return  true;
        }
        else{
            return false;
        }

    }

    @Override
    public void removePreKey(int preKeyId) {
        this.preKeys.remove(preKeyId);
    }
}