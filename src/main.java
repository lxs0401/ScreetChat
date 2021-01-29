import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.DjbECPublicKey;
import setting.protocol.*;
import java.io.*;

public class main
{
    public static void main(String []args) throws InvalidKeyIdException, UntrustedIdentityException, InvalidKeyException, IOException, NoSessionException, DuplicateMessageException, InvalidMessageException, LegacyMessageException, InvalidVersionException {
//        signal a,b,c;
//        SignalProtocolAddress deviceId = new SignalProtocolAddress("fuck",0),deviceId2 = new SignalProtocolAddress("op",0);
//        a = new signal();
//        b = new signal();
//        a.createSession(deviceId,
//                b.registrationId,
//                Curve.decodePoint(b.preKeys.get(0).getKeyPair().getPublicKey().serialize(),0)
//                ,new IdentityKey(b.identityKeyPair.getPublicKey().serialize(),0),
//                Curve.decodePoint(b.signedPreKey.getKeyPair().getPublicKey().serialize(),0),
//                b.signedPreKey.getSignature());
//        b.createSession(deviceId2,a.registrationId,a.preKeys.get(0).getKeyPair().getPublicKey(),a.identityKeyPair.getPublicKey(),a.signedPreKey.getKeyPair().getPublicKey(),a.signedPreKey.getSignature());
//
//
//        byte[] fuck;
//        byte[] aa;
//        fuck = b.encrypt("1234".getBytes("UTF-8"),deviceId2,true);
//        aa = a.decrypt(fuck,deviceId,true);
//
//        System.out.print(new String(aa));

        chatCore.loginUI.LoginUI frame = new chatCore.loginUI.LoginUI();
        frame.setVisible(true);
        chatCore.loginUI.LoginAction.LoginJF = frame;
//        signal sig = new signal();
//        System.out.println(sig.identityKeyPair.getPublicKey().serialize().length);
//        System.out.println(sig.signedPreKey.getKeyPair().getPublicKey().serialize().length);
//        System.out.println(sig.signedPreKey.getSignature().length);
//        System.out.println(sig.preKeys.get(0).getKeyPair().getPublicKey().serialize().length*100);
//






    }

}
