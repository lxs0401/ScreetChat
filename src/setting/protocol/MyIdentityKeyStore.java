package setting.protocol;


import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.util.KeyHelper;

public class MyIdentityKeyStore implements IdentityKeyStore
{
    SignalProtocolAddress address;
    IdentityKey identityKey;

    IdentityKeyPair identityKeyPair;
    int                registrationId;

    public MyIdentityKeyStore(IdentityKeyPair identityKeyPair,int registrationId) throws InvalidKeyException {
        this.identityKeyPair = identityKeyPair;
        this.registrationId = registrationId;
    }


    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        return this.identityKeyPair;
    }

    @Override
    public int getLocalRegistrationId() {
        return this.registrationId;
    }

    @Override
    public void saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
        this.address = address;
        this.identityKey = identityKey;
    }

    @Override
    public boolean isTrustedIdentity(SignalProtocolAddress address, IdentityKey identityKey) {


        if(this.address.equals(address) && this.identityKey.equals(identityKey)){
            return true;
        }
        else{
            return false;
        }
    }
}