package eu.adventuria.chatsystem;

public class Messages {

    public static String prefix = ChatSystem.getInstance().getConfig().getString("prefix.global");
    public static String Warning = ChatSystem.getInstance().getConfig().getString("prefix.warning");
}
