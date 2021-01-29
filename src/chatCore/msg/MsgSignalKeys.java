package chatCore.msg;

public class MsgSignalKeys extends MsgHead{
    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    int friend_id;
}
