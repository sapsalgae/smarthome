package com.example.user.sweethome;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by USER on 2018-01-24.
 */

public class ConnectionManager {
    static boolean isConnected;
    static Socket mSocket;
    final static String serverIP = "192.168.10.104";
    final static int port = 8090;
    static String mServerIP = null;
    static PrintWriter mOut;
    static BufferedReader mIn;
    static String room;
}