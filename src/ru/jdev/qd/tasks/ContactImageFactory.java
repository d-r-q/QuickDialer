package ru.jdev.qd.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import ru.jdev.qd.model.ContactInfo;

import java.io.InputStream;
import java.util.LinkedHashMap;

public class ContactImageFactory {

    private LinkedHashMap<ContactInfo, Bitmap> imagesCache = new LinkedHashMap<ContactInfo, Bitmap>();

    public Bitmap createBitmap(Context context, ContactInfo contactInfo) {

        if (!imagesCache.containsKey(contactInfo)) {
            imagesCache.put(contactInfo, getContactImageBitmap(context, contactInfo));
            if (imagesCache.size() > 10) {
                imagesCache.remove(imagesCache.keySet().iterator().next());
            }
        }

        return imagesCache.get(contactInfo);
    }

    private Bitmap getContactImageBitmap(Context context, ContactInfo contactInfo) {
        if (contactInfo.getPersonUri() != null) {
            final InputStream input = ContactsContract.Contacts
                    .openContactPhotoInputStream(context.getContentResolver(), contactInfo.getPersonUri());

            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        }

        return new FallbackImageFactory(contactInfo).getFallbackImage();
    }

}
