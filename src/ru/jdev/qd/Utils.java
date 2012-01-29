/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *
 */

package ru.jdev.qd;

public class Utils {

    public static final int PAGES_COUNT = 4;

    private Utils() {
    }

    public static String getWidgetPageProperty(int appWidgetId) {
        return appWidgetId + ".page";
    }

    public static int getPagesCount() {
        return PAGES_COUNT;
    }
}
