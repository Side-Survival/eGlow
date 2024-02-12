package me.MrGraycat.eglow.Util;

import lv.side.lang.api.LangAPI;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangMessages {

    public static String getPlayerLanguage(Player player) {
        if (player == null)
            return LangAPI.getDefaultLang();

        String lang = LangAPI.getPlayerSelected(player.getName());
        if (lang == null)
            lang = LangAPI.getDefaultLang();

        return lang;
    }

    public static String get(Player player, String key) {
        key = "s-eglow." + key;
        String r = LangAPI.localize(getPlayerLanguage(player), key);
        if (r.equalsIgnoreCase("???"))
            System.out.println("Missing LangAPI key " + key);

        return ChatUtil.translateColors(r);
    }

    public static List<String> getList(Player player, String key) {
        return ChatUtil.translateColors(
                new ArrayList<>(Arrays.asList(
                        get(player, key).split("\n")
                ))
        );
    }

    public static String paramsReplace(String msg, String[] paramsName, String[] paramsValue) {
        for (int i = 0; i < paramsValue.length; i++) {
            if (i < paramsName.length)
                msg = msg.replace(paramsName[i], paramsValue[i]);
        }

        return msg;
    }

    public static List<String> paramsReplace(List<String> msgList, String[] paramsName, String[] paramsValue) {
        for (int i = 0; i < msgList.size(); i++) {
            msgList.set(i, paramsReplace(msgList.get(i), paramsName, paramsValue));
        }

        return msgList;
    }

    public static String getParam(Player player, String key, String paramName, String paramValue) {
        return ChatUtil.translateColors(get(player, key).replace(paramName, paramValue));
    }

    public static String getParam(Player player, String key, String[] paramsName, String[] paramsValue) {
        return ChatUtil.translateColors(paramsReplace(get(player, key), paramsName, paramsValue));
    }

    public static List<String> getListParam(Player player, String key, String paramName, String paramValue) {
        return ChatUtil.translateColors(
                new ArrayList<>(Arrays.asList(
                        get(player, key).replace(paramName, paramValue).split("\n")
                ))
        );
    }

    public static List<String> getListParam(Player player, String key, String[] paramsName, String[] paramsValue) {
        return ChatUtil.translateColors(
                new ArrayList<>(Arrays.asList(
                        paramsReplace(get(player, key), paramsName, paramsValue).split("\n")
                ))
        );
    }
}