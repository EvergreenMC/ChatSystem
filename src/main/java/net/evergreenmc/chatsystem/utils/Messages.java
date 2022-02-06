package net.evergreenmc.chatsystem.utils;

import net.evergreenmc.chatsystem.ChatSystem;

public class Messages {

    public static ConfigManager cm = ChatSystem.getInstance().cm;


    public static String prefix = cm.getString("prefix.global");
    public static String Warning = cm.getString("prefix.warning");
}
