/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *
 */

package ru.jdev.qd.model;

import java.util.*;

import static java.lang.Long.signum;

public class Pager {
    
    private final ContactInfoDao contactInfoDao;
    private final Page[] pages;

    public Pager(ContactInfoDao contactInfoDao, int pagesCount) {
        this.contactInfoDao = contactInfoDao;
        pages = new Page[pagesCount];
    }
    
    public void fillPages() {
        final List<ContactInfo> mostUsed = new ArrayList<ContactInfo>(contactInfoDao.getContactInfoList());
        Collections.sort(mostUsed, new ByUsageCmp());
        final Iterator<ContactInfo> mostUsedIterator = mostUsed.iterator();
        final List<ContactInfo> lastCalled = new ArrayList<ContactInfo>(contactInfoDao.getContactInfoList());
        Collections.sort(lastCalled, new ByLastCallCmp());
        final Iterator<ContactInfo> lastCalledIterator = lastCalled.iterator();
        
        final Set<String> usedKeys = new HashSet<String>();
        final Set<String> usedPhones = new HashSet<String>();

        for (int i = 0; i < pages.length; i++) {
            final Page page = new Page();
            pages[i] = page;
            fillRow(lastCalledIterator, usedKeys, usedPhones, page.lastCalled);
            fillRow(mostUsedIterator, usedKeys, usedPhones, page.mostUsed);
        }
    }

    private void fillRow(Iterator<ContactInfo> iterator, Set<String> usedKeys, Set<String> usedPhones, ContactInfo[] row) {
        for (int i = 0; i < row.length && iterator.hasNext(); i++) {
            ContactInfo ci = iterator.next();
            if (usedKeys.contains(ci.getLookupId()) ||
                    usedPhones.contains(ci.getLastDialedPhone())) {
                i--;
                continue;
            }
            row[i] = ci;
            usedKeys.add(ci.getLookupId());
            usedPhones.add(ci.getLastDialedPhone());
        }
    }
    
    public Page getPage(int idx) {
        if (idx >= pages.length) {
            idx = 0;
        } else if (idx < 0) {
            idx = pages.length - 1;
        }

        return pages[idx];
    }

    private final static class ByUsageCmp implements Comparator<ContactInfo> {

        public int compare(ContactInfo lhs, ContactInfo rhs) {
            return rhs.usage - lhs.usage;
        }
    }

    private final static class ByLastCallCmp implements Comparator<ContactInfo> {

        public int compare(ContactInfo lhs, ContactInfo rhs) {
            return signum(rhs.lastCall - lhs.lastCall);
        }
    }
    
}
