package ru.jdev.qd.services;

public class TurnPageRightService extends TurnPageService {

    @Override
    protected int turnPage(int curPage, int pagesCount) {
        curPage++;
        if (curPage >= pagesCount) {
            curPage = 0;
        }

        return curPage;
    }
}
