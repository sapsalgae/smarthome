package com.example.user.sweethome.SocketConnection;

import static com.example.user.sweethome.SocketConnection.ConnectionInfo.*;
/**
 * Created by USER on 2018-05-01.
 */

public class SendThread extends Thread {

    private int msg;

    public SendThread(int msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        mOut.println(this.msg);
        mOut.flush();
    }
}
