package ru.jdev.qd.model;

import java.util.*;

import static java.lang.Long.signum;

public class Pager {

    public static final ContactInfo FAKE_CONTACT = new ContactInfo("No Data", null, null, null);
    private final Page page = new Page();

    private final ContactInfoDao contactInfoDao;

    public Pager(ContactInfoDao contactInfoDao) {
        this.contactInfoDao = contactInfoDao;
    }

    public void fillPage() {
        final List<ContactInfo> mostUsed = new ArrayList<ContactInfo>(contactInfoDao.getContactInfoList());
        Collections.sort(mostUsed, new ByUsageCmp());
        final Iterator<ContactInfo> mostUsedIterator = mostUsed.iterator();
        final List<ContactInfo> lastCalled = new ArrayList<ContactInfo>(contactInfoDao.getContactInfoList());
        Collections.sort(lastCalled, new ByLastCallCmp());
        final Iterator<ContactInfo> lastCalledIterator = lastCalled.iterator();

        final Set<String> usedKeys = new HashSet<String>();
        final Set<String> usedPhones = new HashSet<String>();

        fillRow(lastCalledIterator, usedKeys, usedPhones, page.lastCalled);
        fillRow(mostUsedIterator, usedKeys, usedPhones, page.mostUsed);
    }

    private void fillRow(Iterator<ContactInfo> iterator, Set<String> usedKeys, Set<String> usedPhones, ContactInfo[] row) {
        for (int i = 0; i < row.length; i++) {
            final ContactInfo ci = iterator.hasNext() ? iterator.next() : FAKE_CONTACT;
            if (usedKeys.contains(ci.getLookupId()) ||
                    usedPhones.contains(ci.getLastDialedPhone())) {
                i--;
                continue;
            }
            row[i] = ci;
            if (ci != FAKE_CONTACT) {
                usedKeys.add(ci.getLookupId());
                usedPhones.add(ci.getLastDialedPhone());
            }
        }
    }

    public Page getPage() {
        return page;
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
