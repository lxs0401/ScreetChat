package setting.protocol;


import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SessionStore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MySessionStore implements SessionStore
{
    Map<SignalProtocolAddress, SessionRecord> address;
    Map<Integer,Boolean> visit = new HashMap<>();

    public boolean isFresh(int friend_id)
    {
        if (this.visit.containsKey(friend_id) == true)
        {
            return false;
        }
        else{
            visit.put(friend_id,true);
            return true;
        }
    }

    public List<SignalProtocolAddress> getAllAddress() {
        List<SignalProtocolAddress> list = new LinkedList<>();
        for (Map.Entry<SignalProtocolAddress, SessionRecord> entry : this.address.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    public void setAddress(Map<SignalProtocolAddress, SessionRecord> address)
    {
        this.address = address;
    }

    public MySessionStore()
    {
        this.address = new HashMap<>();
    }
    @Override
    public SessionRecord loadSession(SignalProtocolAddress address) {
        for(SignalProtocolAddress each:this.address.keySet())
        {
            if(each.getName().equals(address.getName()) && each.getDeviceId() == address.getDeviceId())
            {
                return this.address.get(each);
            }
        }
        return null;
    }

    @Override
    public List<Integer> getSubDeviceSessions(String name) {
        List<Integer> res = new LinkedList<>();
        for (Map.Entry<SignalProtocolAddress, SessionRecord> entry : this.address.entrySet()) {
            if(entry.getKey().getName() == name)
            {
                res.add(entry.getKey().getDeviceId());
            }

        }
        return res;
    }

    @Override
    public void storeSession(SignalProtocolAddress address, SessionRecord record) {
        this.address.put(address,record);
    }

    @Override
    public boolean containsSession(SignalProtocolAddress address) {
        if(this.address.containsKey(address))
        {
            return  true;
        }
        else {
            return false;
        }
    }

    @Override
    public void deleteSession(SignalProtocolAddress address) {
        this.address.remove(address);

    }

    @Override
    public void deleteAllSessions(String name) {
        this.address.clear();

    }
}