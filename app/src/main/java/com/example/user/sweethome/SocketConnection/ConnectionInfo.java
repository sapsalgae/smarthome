package com.example.user.sweethome.SocketConnection;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by USER on 2018-04-24.
 */

public class ConnectionInfo {

    final static String serverIP = "192.168.10.105";
    final static int serverPort = 8090;

    static boolean isConnected = false;
    static Socket mSocket;
    static PrintWriter mOut;
    static BufferedReader mIn;
}
