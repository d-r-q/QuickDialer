package ru.jdev.qd.tasks;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import ru.jdev.qd.QdWidgetProvider;
import ru.jdev.qd.R;
import ru.jdev.qd.model.ContactInfo;
import ru.jdev.qd.model.Page;
import ru.jdev.qd.model.Pager;
import ru.jdev.qd.services.TapListenerService;

public class UpdateWidgetsTask implements Runnable {

    private static final String TAG = "QD.UW";

    private static final int[][] mostUsedLabelIds = {
            {R.id.mu_1_label, R.id.mu_2_label, R.id.mu_3_label, R.id.mu_4_label},
            {R.id.mu_1_photo, R.id.mu_2_photo, R.id.mu_3_photo, R.id.mu_4_photo}};

    private static final int[][] lastCalledLabelIds = {
            {R.id.lc_1_label, R.id.lc_2_label, R.id.lc_3_label, R.id.lc_4_label},
            {R.id.lc_1_photo, R.id.lc_2_photo, R.id.lc_3_photo, R.id.lc_4_photo}};

    private final ContactImageFactory contactImageFactory;

    private final Context context;
    private final Pager pager;

    private int[] appWidgetIds;

    public UpdateWidgetsTask(ContactImageFactory contactImageFactory, Context context, Pager pager, int[] appWidgetIds) {
        this.contactImageFactory = contactImageFactory;
        this.context = context;
        this.pager = pager;
        this.appWidgetIds = appWidgetIds;
    }

    @Override
    public void run() {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        for (int appWidgetId : appWidgetIds) {
            updateWidget(appWidgetManager, appWidgetId, pager);
        }
    }

    private void updateWidget(AppWidgetManager appWidgetManager, int appWidgetId, Pager pager) {
        Log.v(TAG, "update widget: " + appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);

        final Page page = pager.getPage();
        Log.v(TAG, "Page: " + page);
        setRow(views, page.lastCalled, lastCalledLabelIds, appWidgetId, 0);
        setRow(views, page.mostUsed, mostUsedLabelIds, appWidgetId, lastCalledLabelIds.length * 2);

        Log.v(TAG, "Pending intents setted");

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setRow(RemoteViews views, ContactInfo[] contactInfos, int[][] labels, int appWidgetId, int idx) {
        for (int i = 0; i < contactInfos.length; i++) {

            final ContactInfo contactInfo = contactInfos[i];
            final String lastDialedPhone = contactInfo.getLastDialedPhone();
            final String contactLabel = getContactLabel(contactInfo, lastDialedPhone);
            final int contactColor = getContactColor(contactInfo);
            final int labelColor = getLabelColor(appWidgetId, lastDialedPhone, contactColor);

            views.setTextViewText(labels[0][i], contactLabel);
            views.setInt(labels[0][i], "setTextColor", labelColor);
            addIntent(views, labels[0][i], idx++, appWidgetId, lastDialedPhone);

            views.setImageViewBitmap(labels[1][i], contactImageFactory.createBitmap(context, contactInfo));
            addIntent(views, labels[1][i], idx++, appWidgetId, lastDialedPhone);
        }
    }

    private int getLabelColor(int appWidgetId, String contactPhone, int contactColor) {
        final boolean isActive = contactPhone != null && contactPhone.equals(QdWidgetProvider.getActivePhone(context, appWidgetId));
        return isActive
                ? contactColor
                : context.getResources().getColor(R.color.nonActiveLabelColor);
    }

    private int getContactColor(ContactInfo contactInfo) {
        final boolean hasPhoto = contactInfo.getPersonUri() != null;
        return hasPhoto
                ? context.getResources().getColor(R.color.activeLabelColor)
                : contactInfo.contactColor();
    }

    private String getContactLabel(ContactInfo contactInfo, String lastDialedPhone) {
        final String contactLabel;
        if (contactInfo.getName() != null) {
            contactLabel = contactInfo.getName();
        } else if (lastDialedPhone != null) {
            contactLabel = lastDialedPhone;
        } else {
            contactLabel = context.getResources().getString(R.string.no_data);
        }
        return contactLabel;
    }

    private void addIntent(RemoteViews views, int viewId, int idx, int appWidgetId, String phoneToCall) {
        final Intent handleTapIntent = TapListenerService.createIntent(context, appWidgetId, phoneToCall);
        handleTapIntent.setData(Uri.parse("qd://" + viewId));
        final PendingIntent pi = PendingIntent.getService(context, idx, handleTapIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(viewId, pi);
    }

}
