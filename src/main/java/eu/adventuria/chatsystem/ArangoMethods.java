package eu.adventuria.chatsystem;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import org.bukkit.Bukkit;

public class ArangoMethods {

    public static String database = ArangoUtils.database;
    public static String Collection = "ChatSystem";

    public static void changeGlobalChatBoolean(String uuid){
        if(ArangoUtils.arangoDB.db(database).collection(Collection).getDocument(uuid, BaseDocument.class) == null){
            BaseDocument base = new BaseDocument();
            base.addAttribute("name", Bukkit.getOfflinePlayer(uuid).getName());
            base.addAttribute("boolean", true);
            base.setKey(uuid);
            ArangoUtils.createDocument(database, Collection, base, uuid);
        }else{
            BaseDocument base = new BaseDocument();
            if(getGlobalChatBoolean(uuid) == true){
                base.addAttribute("boolean", false);
            }else{
                base.addAttribute("boolean", true);
            }
            base.setKey(uuid);
            ArangoUtils.createDocument(database, Collection, base, uuid);
        }
    }

    public static Boolean getGlobalChatBoolean(String uuid){
        try {
            String query = "FOR doc in " + Collection + " SORT doc.name DESC RETURN doc";
            ArangoCursor<BaseDocument> cursor = ArangoUtils.arangoDB.db(database).query(query, null, null, BaseDocument.class);

            while (cursor.hasNext()) {
                return (Boolean) cursor.next().getAttribute("boolean");
            };
        } catch (ArangoDBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
