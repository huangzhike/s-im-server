package mmp.im.gate;


import mmp.im.gate.ack.AckTimeoutScanner;

public class Starter {

    public static void main(String[] args) {


        Thread t = new Thread(new AckTimeoutScanner(), "ack.timeout.scanner");
        t.setDaemon(true);
        t.start();


    }
}
