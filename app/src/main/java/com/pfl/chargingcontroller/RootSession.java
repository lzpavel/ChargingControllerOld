package com.pfl.chargingcontroller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class RootSession {

    private static final String LOG_TAG = "RootSession";
    private static final String LOG_TAG_ERROR = "RootSessionError";

    Process process;

    BufferedReader reader;
    BufferedReader readerError;
    DataOutputStream writer;


    public RootSession() throws Exception {
        //process = Runtime.getRuntime().exec("echo 1");
        process = Runtime.getRuntime().exec("su");
        //reader = new DataInputStream(process.getInputStream());
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        writer = new DataOutputStream(process.getOutputStream());
    }



    public String readValue(String path) throws Exception {
        int wait = 0;
        exec("cat " + path);
        while (!reader.ready()) {
            Thread.sleep(100);
            if (wait++ >= 10) {
                throw new Exception("Timeout");
            }
        }
        String out = reader.readLine();
        while (reader.ready()) {
            Log.d(LOG_TAG, "Buffer trash:" + reader.readLine());
        }
        return out;
    }

    public void writeValue(String path, String value) throws Exception {
        exec("echo " + value + " > " + path);
    }

    public void allowWriting(String path) throws Exception {
        int wait = 0;
        exec("ls -l " + path);
        while (!reader.ready()) {
            Thread.sleep(100);
            if (wait++ >= 10) {
                throw new Exception("Timeout");
            }
        }

        String out = reader.readLine();

        boolean isWritable = out.matches("-rw.*");
        if(!isWritable) {
            exec("chmod u+w " + path);
        }
    }

    public void writeValueForce(String path, String value) throws Exception {
        allowWriting(path);
        writeValue(path, value);
    }

    public static void disableCharging() throws Exception {
        RootSession rootSession = new RootSession();
        String path = "/sys/class/power_supply/battery/charging_enabled";
        rootSession.exec("echo 0 > " + path);
        rootSession.close();
    }

    public static void enableCharging() throws Exception {
        RootSession rootSession = new RootSession();
        String path = "/sys/class/power_supply/battery/charging_enabled";
        rootSession.exec("echo 1 > " + path);
        rootSession.close();
    }

    public static void resetBatteryStats() throws Exception {
        RootSession rootSession = new RootSession();
        rootSession.exec("dumpsys batterystats --reset");
        rootSession.close();
    }

    /*public static String setCurrentLimit() throws Exception {
        RootSession rootSession = new RootSession();
        String path = "/sys/class/power_supply/main/current_max";
        int wait = 0;

        rootSession.exec("ls -l " + path);
        while (!rootSession.reader.ready()) {
            Thread.sleep(100);
            if (wait++ >= 10) {
                throw new Exception("Timeout");
            }
        }
        String out = rootSession.reader.readLine();

        Log.d(LOG_TAG, out);

        boolean isWritable = out.matches("-rw.*");
        if(!isWritable) {
            rootSession.exec("chmod u+w " + path);
        }
        //rootSession.exec("echo " + Config.currentLimit + "000 > " + path);

        wait = 0;
        rootSession.exec("cat " + path);
        while (!rootSession.reader.ready()) {
            Thread.sleep(100);
            if (wait++ >= 10) {
                throw new Exception("Timeout");
            }
        }
        out = rootSession.reader.readLine();

        while (rootSession.reader.ready()) {
            Log.d(LOG_TAG, rootSession.reader.readLine());
        }
        while (rootSession.readerError.ready()) {
            Log.d(LOG_TAG_ERROR, rootSession.readerError.readLine());
        }
        rootSession.close();

        String curVal = out.substring(0, out.length() - 3);
        return curVal;
    }*/

    /*public static void execSimple(String cmd) {
        RootSession rootSession = new RootSession();
        rootSession.exec(cmd);
        Log.d(LOG_TAG, rootSession.readOut());
        rootSession.close();
    }*/

    public void exec(String cmd) throws Exception {
        writer.writeBytes(cmd + "\n");
        writer.flush();
        //process.waitFor();
    }


    public void close() throws Exception {
        writer.writeBytes("exit\n");
        writer.flush();
        process.waitFor();
    }

}
