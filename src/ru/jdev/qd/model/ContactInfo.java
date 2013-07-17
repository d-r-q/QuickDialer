/*
 *
 *  * Copyright (c) 2012 Aleksey Zhidkov. All Rights Reserved.
 *  
 */

package ru.jdev.qd.model;

import android.net.Uri;

/**
 * User: jdev
 * Date: 21.01.12
 */
public class ContactInfo {
    
    public final String name;
    public final String lookupId;
    public final Uri personUri;
    
    int usage;
    long lastCall;
    String lastDialedPhone;

    public ContactInfo(String name, String lastDialedPhone, String lookupId, Uri personUri) {
        this.name = name;
        this.lastDialedPhone = lastDialedPhone;
        this.lookupId = lookupId;
        this.personUri = personUri;
    }

    public String getName() {
        return name;
    }

    public String getLastDialedPhone() {
        return lastDialedPhone;
    }

    public String getLookupId() {
        return lookupId;
    }

    public int getUsage() {
        return usage;
    }

    public long getLastCall() {
        return lastCall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo that = (ContactInfo) o;

        return !(lookupId != null ? !lookupId.equals(that.lookupId) : that.lookupId != null);

    }

    @Override
    public int hashCode() {
        return lookupId != null ? lookupId.hashCode() : 0;
    }

}
