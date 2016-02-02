package com.teamged.txserver;

public class TxMain {

    public static void main(String[] args) {
        TransactionMonitor.runServer();
        System.out.println("Exiting server");
    }
}
