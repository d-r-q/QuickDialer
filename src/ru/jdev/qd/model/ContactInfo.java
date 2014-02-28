package ru.jdev.qd.model;

import android.graphics.Color;
import android.net.Uri;

public class ContactInfo {

    private final String name;
    private final String lookupId;
    private final Uri personUri;

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

    public Uri getPersonUri() {
        return personUri;
    }

    public String getContactLabel() {
        if (name != null) {
            return name;
        } else {
            return lastDialedPhone;
        }
    }

    public int contactColor() {
        long hashCode = ((long) getContactLabel().hashCode()) - Integer.MIN_VALUE;
        return Color.HSVToColor(new float[]{(int) (hashCode % 360), 0.84F, 0.8F});
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
