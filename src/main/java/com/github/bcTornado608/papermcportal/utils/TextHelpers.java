package com.github.bcTornado608.papermcportal.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class TextHelpers {
    

    public static Component normalText(String text, TextColor color){
        return Component.text(text).color(color).decoration(TextDecoration.ITALIC, false);
    }
    public static Component normalText(String text){
        return normalText(text, NamedTextColor.WHITE);
    }

    public static Component italicText(String text, TextColor color){
        return Component.text(text).color(color).decorate(TextDecoration.ITALIC);
    }
}
