/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *
 */

package ru.jdev.qd.services;

public class TurnPageLeftService extends TurnPageService {
    @Override
    protected int turnPage(int curPage, int pagesCount) {
        curPage--;

        if (curPage < 0) {
            curPage = pagesCount - 1;
        }

        return curPage;
    }
}
