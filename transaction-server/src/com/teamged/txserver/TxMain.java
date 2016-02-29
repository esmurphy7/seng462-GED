package com.teamged.txserver;

public class TxMain {

    private static boolean l2 = true;
    private static boolean rt = true;

    public static void main(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if (arg.equals("-L1")) {
                    l2 = false;
                    rt = false;
                    System.out.println("L2 and real time caches disabled");
                    break;
                } else if (arg.equals("-L2")) {
                    rt = false;
                    System.out.println("Real time cache disabled");
                    break;
                }
            }
        } else {
            System.out.println("Vanilla boot");
        }

        TransactionMonitor.runServer();
        System.out.println("Exiting server");
    }

    public static boolean l2Enabled() {
        return l2;
    }

    public static boolean rtEnabled() {
        return rt;
    }
}

