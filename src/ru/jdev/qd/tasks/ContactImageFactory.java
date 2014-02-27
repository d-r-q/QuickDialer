package ru.jdev.qd.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import ru.jdev.qd.model.ContactInfo;

import java.io.InputStream;

public class ContactImageFactory {

    public Bitmap createBitmap(Context context, ContactInfo contactInfo) {

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
