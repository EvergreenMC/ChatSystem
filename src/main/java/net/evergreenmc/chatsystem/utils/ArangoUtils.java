package net.evergreenmc.chatsystem.utils;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import net.evergreenmc.chatsystem.ChatSystem;

import java.util.logging.Level;

public class ArangoUtils {

    public static ConfigManager cf = ChatSystem.getInstance().cm;
    private static ChatSystem pl = ChatSystem.getInstance();

    public static int port = cf.getInt("database.port");
    public static String host = cf.getString("database.host");
    public static String user = cf.getString("database.user");
    public static String password = cf.getString("database.password");
    public static String database = cf.getString("database.database");

    public static ArangoDB arangoDB = new ArangoDB.Builder().host(host, port).user(user).password(password).build();

    public static boolean isConnected(){
        try{
            ArangoDatabase db = arangoDB.db(database);
            db.exists();
            return true;
        }catch (ArangoDBException e){
            pl.getLogger().log(Level.WARNING, "");
            pl.getLogger().log(Level.WARNING, "§8==================== §4WARNUNG §8====================");
            pl.getLogger().log(Level.WARNING, "§cDie Verbindung zur Datenbank konnte nicht hergestellt werden!");
            pl.getLogger().log(Level.WARNING, "§8==================== §4WARNUNG §8====================");
            pl.getLogger().log(Level.WARNING, "");

            ChatSystem.getInstance().getPluginLoader().disablePlugin(ChatSystem.getInstance());
            return false;
        }
    }

    public static void createDatabase(String database){
        if(isConnected()){
            ArangoDatabase db = arangoDB.db(database);

            if(!db.exists()){
                db.create();
                System.out.println(ChatSystem.getPrefix() + "§7Die Datenbank §8'§a" + database +"§8' §7wurde erstellt.");
            }
        }
    }

    public static void createCollection(String database, String collection){
        if(isConnected() && !arangoDB.db(database).collection(collection).exists()){
            try {
                CollectionEntity myArangoCollection = arangoDB.db(database).createCollection(collection);
                pl.getLogger().log(Level.INFO, "Collection erstellt: " + myArangoCollection.getName());
            } catch (ArangoDBException e) {
                pl.getLogger().log(Level.WARNING, "Fehler beim erstellen einer collection: " + collection + "; " + e.getMessage());
            }
        }
    }

    public static void createDocument(String database, String collection, BaseDocument document, String key){
        if(isConnected() && arangoDB.db(database).collection(collection).getDocument(key, BaseDocument.class) == null){
            try {
                arangoDB.db(database).collection(collection).insertDocument(document);
            } catch (ArangoDBException e) {
                pl.getLogger().log(Level.WARNING, "Fehler beim erstellen eines Dokuments. " + e.getMessage());
            }
        }else{
            try {
                arangoDB.db(database).collection(collection).updateDocument(key, document);
            } catch (ArangoDBException e) {
                pl.getLogger().log(Level.WARNING, "Fehler beim updaten eines Dokuments. " + e.getMessage());
            }
        }
    }

    public static void deleteDocument(String database, String collection, String key){
        if(isConnected() && arangoDB.db(database).collection(collection).getDocument(key, BaseDocument.class) != null){
            try {
                arangoDB.db(database).collection(collection).deleteDocument(key);
            } catch (ArangoDBException e) {
                pl.getLogger().log(Level.WARNING,"Fehler beim Löschen eines Dokuments. " + e.getMessage());
            }
        }else{
            pl.getLogger().log(Level.WARNING,"Diese Collection existiert nicht!");
        }
    }

}
