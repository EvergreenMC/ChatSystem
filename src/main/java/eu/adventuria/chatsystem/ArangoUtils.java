package eu.adventuria.chatsystem;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class ArangoUtils {

    static @NotNull FileConfiguration cf = ChatSystem.getInstance().getConfig();

    public static int port = cf.getInt("arango.port");
    public static String host = cf.getString("arango.host");
    public static String user = cf.getString("arango.user");
    public static String password = cf.getString("arango.password");
    public static String database = cf.getString("arango.database");

    public static ArangoDB arangoDB = new ArangoDB.Builder().host(host, port).user(user).password(password).build();

    public static boolean isConnected(){
        try{
            ArangoDatabase db = arangoDB.db(database);
            db.exists();
            return true;
        }catch (ArangoDBException e){
            System.out.println(Messages.Warning + "");
            System.out.println(Messages.Warning + "§8==================== §4WARNUNG §8====================");
            System.out.println(Messages.Warning + "§cDie Verbindung zur Datenbank konnte nicht hergestellt werden!");
            System.out.println(Messages.Warning + "§8==================== §4WARNUNG §8====================");
            System.out.println(Messages.Warning + "");
            ChatSystem.getInstance().getPluginLoader().disablePlugin(ChatSystem.getInstance());
            return false;
        }
    }

    public static void createDatabase(String database){
        if(isConnected()){
            ArangoDatabase db = arangoDB.db(database);

            if(!db.exists()){
                db.create();
                System.out.println(Messages.prefix + "§7Die Datenbank §8'§a" + database +"§8' §7wurde erstellt.");
            }
        }
    }

    public static void createCollection(String database, String collection){
        if(isConnected() && !arangoDB.db(database).collection(collection).exists()){
            try {
                CollectionEntity myArangoCollection = arangoDB.db(database).createCollection(collection);
                System.out.println(Messages.prefix + "Collection erstellt: " + myArangoCollection.getName());
            } catch (ArangoDBException e) {
                System.err.println(Messages.prefix + "Fehler beim erstellen einer collection: " + collection + "; " + e.getMessage());
            }
        }
    }

    public static void createDocument(String database, String collection, BaseDocument document, String key){
        if(isConnected() && arangoDB.db(database).collection(collection).getDocument(key, BaseDocument.class) == null){
            try {
                arangoDB.db(database).collection(collection).insertDocument(document);
            } catch (ArangoDBException e) {
                System.err.println(Messages.prefix + "Fehler beim erstellen eines Dokuments. " + e.getMessage());
            }
        }else{
            try {
                arangoDB.db(database).collection(collection).updateDocument(key, document);
            } catch (ArangoDBException e) {
                System.err.println(Messages.prefix + "Fehler beim updaten eines Dokuments. " + e.getMessage());
            }
        }
    }

    public static void deleteDocument(String database, String collection, String key){
        if(isConnected() && arangoDB.db(database).collection(collection).getDocument(key, BaseDocument.class) != null){
            try {
                arangoDB.db(database).collection(collection).deleteDocument(key);
            } catch (ArangoDBException e) {
                System.err.println("Fehler beim Löschen eines Dokuments. " + e.getMessage());
            }
        }else{
            System.err.println("Diese Collection existiert nicht!");
        }
    }

}
