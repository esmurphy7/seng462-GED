package com.teamged.txserver;

import com.teamged.ServerConstants;
import com.teamged.txserver.transactions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by DanielF on 2016-01-30.
 */
public class TransactionMonitor {

    /**
     * List for keeping track of the various running thread queues.
     */
    //private static final ArrayList<TransactionServerThread> threadQueues = new ArrayList<>();

    private static final Object syncObject = new Object();

    private static final ArrayList<TransactionServerThread> reqProcThreads = new ArrayList<>();
    private static final ArrayList<TransactionServerThread> txProcThreads = new ArrayList<>();
    private static final ArrayList<TransactionServerThread> respProcThreads = new ArrayList<>();

    private static final BlockingQueue<String> requestTxQueue =
            new LinkedBlockingQueue<>(ServerConstants.TRANSACTION_QUEUE_SIZE);
    private static final BlockingQueue<String> responseTxQueue =
            new LinkedBlockingQueue<>(ServerConstants.TRANSACTION_QUEUE_SIZE);

    private static final ConcurrentHashMap<String, UserQueueObject> requestTxUserMap =
            new ConcurrentHashMap<>(/* TODO: args */);

    /**
     * Launches the transaction server, spinning up the specified number of threads for client bound and quote server
     * bound operations.
     */
    public static void runServer() {
        InternalLog.Log("Launching transaction server socket listeners.");
        for (int i = 0; i < ServerConstants.TX_PORT_RANGE.length; i++) {
            int portNum = ServerConstants.TX_PORT_RANGE[i];
            TransactionServerThread rpThread;
            try {
                rpThread = new RequestProcessingThread(portNum, ServerConstants.COMM_THREAD_COUNT, syncObject);
                reqProcThreads.add(rpThread);
                new Thread(rpThread).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < ServerConstants.PROCESSING_THREAD_COUNT; i++) {
            TransactionServerThread txpThread;
            try {
                txpThread = new TransactionProcessingThread(ServerConstants.THREAD_POOL_SIZE, syncObject);
                txProcThreads.add(txpThread);
                new Thread(txpThread).start();
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
        }

        do {
            synchronized (syncObject) {
                try {
                    syncObject.wait();

                    /*
                    check thread statuses, restart threads if necessary
                     */
                } catch (InterruptedException e) {
                    // Close threads?
                    e.printStackTrace();
                    break;
                }
            }
        } while (!reqProcThreads.isEmpty());
    }

    public static int CountRequestQueue() {
        return requestTxQueue.size();
    }

    public static void PutRequestQueue(String user) {
        try {
            requestTxQueue.put(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
            InternalLog.Log("Operation was interrupted: request for user \"" + user + "\" will not be queued");
        } catch (NullPointerException e) {
            e.printStackTrace();
            InternalLog.Log("Attempted to add null user to request queue");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            InternalLog.Log("User \"" + user + "\" experienced an unexpected error while queueing");
        }
    }

    public static String TakeRequestQueue() {
        String request = null;
        try {
            request = requestTxQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            InternalLog.Log("Operation was interrupted while attempting to dequeue a request");
        }

        return request;
    }

    /**
     * Adds the given transaction object to a processing set. The transaction object will be associated with the
     * user it specifies.
     * @param txObject  The transaction object to prep for processing.
     */
    public static void AddTransactionObject(TransactionObject txObject) {
        if (txObject != null && txObject.getErrorString().isEmpty()) {
            String user = txObject.getUserName();
            UserQueueObject userObj;
            if (!requestTxUserMap.containsKey(user)) {
                // Only adds if absent to avoid a race condition of multiple threads creating and overwriting at once
                requestTxUserMap.putIfAbsent(user, new UserQueueObject());
            }

            userObj = requestTxUserMap.get(user);

            if (userObj != null) {
                userObj.addTransactionObject(txObject);
            } else {
                InternalLog.Log("Could not add request " + txObject.toString() + " for user " + user);
            }
        }
    }

    /**
     * Gets the user object associated with the user name. This will be null if no user exists.
     * @param user  The user name to get the UserQueueObject for.
     * @return  The user object for the given user name, if it exists.
     */
    public static UserQueueObject GetUserObject(String user) {
        return requestTxUserMap.get(user);
    }
}
