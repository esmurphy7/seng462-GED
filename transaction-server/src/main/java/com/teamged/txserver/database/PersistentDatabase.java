package com.teamged.txserver.database;

import com.mongodb.*;
import com.teamged.txserver.TxMain;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;
import java.util.Calendar;

public class PersistentDatabase {

    public static MongoClient mongoClient;

    static {

        try {

            String databaseServer = TxMain.Deployment.getDatabaseServer().getServer();
            Integer databasePort = TxMain.Deployment.getDatabaseServer().getPort();

            System.out.println(String.format("Connecting to database %s:%s...", databaseServer, databasePort));

            mongoClient = new MongoClient(
                    new ServerAddress(databaseServer, databasePort),
                    new MongoClientOptions.Builder().build());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserDatabaseObject(UserDatabaseObject userDatabaseObject, int workloadSequenceNumber) {

        Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(mongoClient, "daytrade");

        datastore.save(userDatabaseObject);

        System.out.println(String.format("Backed up user object for transaction [%d] at %d",
                workloadSequenceNumber,
                Calendar.getInstance().getTimeInMillis()));
    }
}
