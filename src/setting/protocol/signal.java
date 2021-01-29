package setting.protocol;//import com.google.gson.Gson;
import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.protocol.PreKeySignalMessage;
import org.whispersystems.libsignal.protocol.SignalMessage;
import org.whispersystems.libsignal.state.*;
import org.whispersystems.libsignal.util.KeyHelper;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tool.*;
class data implements Serializable
{
    String username;
    Map<SignalProtocolAddress,byte[]> session;
    Map<Integer,Boolean> visit = new HashMap<>();
    byte[] identityKeyPair;
    int registrationId;
    List<byte[]> preKeys;
    byte[] signedPreKey;
}
public class signal {
    public String username;
    public IdentityKeyPair    identityKeyPair;
    public int                registrationId;
    public List<PreKeyRecord> preKeys;
    public SignedPreKeyRecord signedPreKey;




    private SessionStore      sessionStore;
    public MySessionStore mySessionStroe;
    private PreKeyStore       preKeyStore;
    private SignedPreKeyStore signedPreKeyStore;
    private SessionCipher sessionCipher;
    private SessionBuilder sessionBuilder;
    private Map<SignalProtocolAddress,IdentityKeyStore>  identityStores = new HashMap<>();




    public void createSession(SignalProtocolAddress deviceId, int _reistrationId, ECPublicKey _preKeys, IdentityKey _identityKeyPair, ECPublicKey _signedPreKey,byte[] _signedPreKeySig) throws UntrustedIdentityException, InvalidKeyException {


        IdentityKeyStore  identityStore  = new MyIdentityKeyStore(identityKeyPair,registrationId);
        sessionStore.storeSession(deviceId,new SessionRecord());
        identityStores.put(deviceId,identityStore);
        identityStore.saveIdentity(deviceId,_identityKeyPair);
        // Instantiate a SessionBuilder for a remote recipientId + deviceId tuple.


        sessionBuilder = new SessionBuilder(sessionStore, preKeyStore, signedPreKeyStore,
                identityStore, deviceId);

        PreKeyBundle retrievedPreKey = new PreKeyBundle(
                _reistrationId,
                deviceId.getDeviceId(),
                0,
                _preKeys,
                0
                ,_signedPreKey
                ,_signedPreKeySig,
                _identityKeyPair);
        sessionBuilder.process(retrievedPreKey);


    }
    public byte[] serialize()
    {
        List<SignalProtocolAddress> users = mySessionStroe.getAllAddress();
//        class data
//        {
//            Map<SignalProtocolAddress,byte[]> session;
//            byte[] identityKeyPair;
//            int registrationId;
//            List<byte[]> preKeys;
//            byte[] signedPreKey;
//        }
        ObjectToBytes bytes = new ObjectToBytes();
        data serialize = new data();
        Map<SignalProtocolAddress,byte[]> session = new HashMap<>();
        for (SignalProtocolAddress each : users)
        {
            SessionRecord record = sessionStore.loadSession(each);
            session.put(each,record.serialize());
        }
        serialize.visit = mySessionStroe.visit;
        serialize.session = session;
        serialize.identityKeyPair = identityKeyPair.serialize();
        serialize.registrationId = registrationId;
        serialize.signedPreKey = signedPreKey.serialize();
        serialize.preKeys = new LinkedList<>();
        serialize.username = username;
        for(PreKeyRecord each : preKeys)
        {
            serialize.preKeys.add(each.serialize());
        }
        return bytes.toByteArray(serialize);
    }
    public signal() throws InvalidKeyException, UnsupportedEncodingException, InvalidKeyIdException, UntrustedIdentityException, LegacyMessageException, InvalidMessageException, DuplicateMessageException, NoSessionException {
        identityKeyPair = KeyHelper.generateIdentityKeyPair();
        registrationId  = KeyHelper.generateRegistrationId(true);
        preKeys         = KeyHelper.generatePreKeys(0, 100);
        signedPreKey    = KeyHelper.generateSignedPreKey(identityKeyPair, 0);

// Store identityKeyPair somewhere durable and safe.
// Store registrationId somewhere durable and safe.

// Store preKeys in PreKeyStore.
// Store signed prekey in SignedPreKeyStore.
        sessionStore      =  mySessionStroe  = new MySessionStore();
        preKeyStore       =                    new MyPreKeyStore();
        signedPreKeyStore =                    new MySignedPreKeyStore();


        signedPreKeyStore.storeSignedPreKey(0,signedPreKey);
        preKeyStore      .storePreKey(0,preKeys.get(0));
    }
    public void updatePrekeys()
    {
        preKeys = KeyHelper.generatePreKeys(0, 100);
        preKeyStore.storePreKey(0,preKeys.get(0));
    }
    public  void updateSignedPreKey() throws InvalidKeyException {
        signedPreKey    = KeyHelper.generateSignedPreKey(identityKeyPair, 0);
        signedPreKeyStore.storeSignedPreKey(0,signedPreKey);
    }
    public signal(byte[] serialize) throws InvalidKeyException, IOException {
        //        class data
//        {
//            Map<SignalProtocolAddress,byte[]> session;
//            byte[] identityKeyPair;
//            int registrationId;
//            List<byte[]> preKeys;
//            byte[] signedPreKey;
//        }
        ObjectToBytes object = new ObjectToBytes();
        data record = (data) object.toObject(serialize);
        Map<SignalProtocolAddress, SessionRecord> address = new HashMap<>();

        this.identityKeyPair = new IdentityKeyPair(record.identityKeyPair);
        this.registrationId  = record.registrationId;
        this.preKeys         = new LinkedList<>();
        this.signedPreKey    = new SignedPreKeyRecord(record.signedPreKey);

        sessionStore      = mySessionStroe  = new MySessionStore();
        preKeyStore       = new MyPreKeyStore();
        signedPreKeyStore = new MySignedPreKeyStore();

        signedPreKeyStore.storeSignedPreKey(0,signedPreKey);

        for(byte[] each : record.preKeys)
        {
            this.preKeys.add(new PreKeyRecord(each));
        }

        preKeyStore.storePreKey(0,preKeys.get(0));
        for (Map.Entry<SignalProtocolAddress, byte[]> entry : record.session.entrySet()) {
            address.put(entry.getKey(),new SessionRecord(entry.getValue()));
        }
        mySessionStroe.visit = record.visit;
        mySessionStroe.setAddress(address);
        username = record.username;
    }


    public byte[] decrypt(byte[] ser,SignalProtocolAddress deviceId,boolean first) throws InvalidVersionException, InvalidMessageException, InvalidKeyException, DuplicateMessageException, InvalidKeyIdException, UntrustedIdentityException, LegacyMessageException, NoSessionException {

// Build a session with a PreKey retrieved from the server.
        byte[] message;
        if(first == true)
        {
            sessionCipher = new SessionCipher(sessionStore, preKeyStore,signedPreKeyStore, identityStores.get(deviceId), deviceId);
            message   =  sessionCipher.decrypt(new PreKeySignalMessage(ser));
            preKeys.remove(0);
            preKeyStore.storePreKey(0,preKeys.get(0));
        }
        else
        {
            sessionCipher = new SessionCipher(sessionStore, null,null, null, deviceId);
            message = sessionCipher.decrypt(new SignalMessage(ser));
        }

        return message;
    }
    public byte[] encrypt(byte[] msg,SignalProtocolAddress deviceId,boolean first) throws UntrustedIdentityException, InvalidKeyException {
        // Build a session with a PreKey retrieved from the server.
        sessionCipher = new SessionCipher(sessionStore, null,null, null, deviceId);
        if (first == true)
        {
            preKeys.remove(0);
            preKeyStore.storePreKey(0,preKeys.get(0));
        }
        return sessionCipher.encrypt(msg).serialize();
    }
}
