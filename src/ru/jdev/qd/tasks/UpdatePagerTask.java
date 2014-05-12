package ru.jdev.qd.tasks;

import ru.jdev.qd.model.ContactInfoDao;
import ru.jdev.qd.model.Pager;

public class UpdatePagerTask implements Runnable {

    private final ContactInfoDao contactInfoDao;
    private final Pager pager;

    public UpdatePagerTask(ContactInfoDao contactInfoDao, Pager pager) {
        this.contactInfoDao = contactInfoDao;
        this.pager = pager;
    }

    @Override
    public void run() {
        contactInfoDao.update();
        pager.fillPage();
    }
}
