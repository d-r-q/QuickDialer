/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *  
 */

package ru.jdev.qd.tasks;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import ru.jdev.qd.QdWidgetProvider;
import ru.jdev.qd.R;
import ru.jdev.qd.Utils;
import ru.jdev.qd.model.ContactInfo;
import ru.jdev.qd.model.Page;
import ru.jdev.qd.model.Pager;
import ru.jdev.qd.services.TurnPageLeftService;
import ru.jdev.qd.services.TurnPageRightService;
import ru.jdev.qd.services.TurnPageService;

/**
 * User: jdev
 * Date: 28.01.12
 */
public class UpdateWidgetsTask implements Runnable {

    private static final String TAG = "QD.UW";

    private static final int[][] mostUsedLabelIds = {
            {R.id.mu_1_label, R.id.mu_2_label, R.id.mu_3_label, R.id.mu_4_label},
            {R.id.mu_1_photo, R.id.mu_2_photo, R.id.mu_3_photo, R.id.mu_4_photo}};

    private static final int[][] lastCalledLabelIds = {
            {R.id.lc_1_label, R.id.lc_2_label, R.id.lc_3_label, R.id.lc_4_label},
            {R.id.lc_1_photo, R.id.lc_2_photo, R.id.lc_3_photo, R.id.lc_4_photo}};

    private final Context context;
    private final Pager pager;

    private int[] appWidgetIds;

    public UpdateWidgetsTask(Context context, Pager pager, int[] appWidgetIds) {
        this.context = context;
        this.pager = pager;
        this.appWidgetIds = appWidgetIds;
    }

    @Override
    public void run() {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetIds == null) {
            appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, QdWidgetProvider.class));
        }
        for (int appWidgetId : appWidgetIds) {
            updateWidget(appWidgetManager, appWidgetId, pager);
        }
    }

    private void updateWidget(AppWidgetManager appWidgetManager, int appWidgetId, Pager pager) {
        Log.v(TAG, "update widget: " + appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
        views.setViewVisibility(R.id.loading_layout, View.GONE);
        views.setViewVisibility(R.id.data_layout, View.VISIBLE);

        final int currentPage = context.getSharedPreferences(QdWidgetProvider.PREFS_FILE_NAME, Context.MODE_PRIVATE).getInt(Utils.getWidgetPageProperty(appWidgetId), 0);
        Log.v(TAG, "Current page: " + String.valueOf(currentPage));
        final Page page = pager.getPage(currentPage);
        Log.v(TAG, "Page: " + page);
        setRow(views, page.lastCalled, lastCalledLabelIds);
        setRow(views, page.mostUsed, mostUsedLabelIds);

        views.setTextViewText(R.id.page, String.format("%d/%d", currentPage + 1, Utils.getPagesCount()));

        final Intent nextPage = new Intent(context, TurnPageRightService.class);
        nextPage.putExtra(TurnPageService.EXTRA_APP_WIDGET_ID, appWidgetId);
        views.setOnClickPendingIntent(R.id.next, PendingIntent.getService(context, 0, nextPage, PendingIntent.FLAG_UPDATE_CURRENT));

        final Intent prevPage = new Intent(context, TurnPageLeftService.class);
        prevPage.putExtra(TurnPageService.EXTRA_APP_WIDGET_ID, appWidgetId);
        views.setOnClickPendingIntent(R.id.prev, PendingIntent.getService(context, 0, prevPage, PendingIntent.FLAG_UPDATE_CURRENT));
        Log.v(TAG, "Pending intents setted");

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setRow(RemoteViews views, ContactInfo[] contactInfos, int[][] labels) {
        for (int i = 0; i < contactInfos.length; i++) {
            final ContactInfo contactInfo = contactInfos[i];
            if (contactInfo != null) {
                Log.v(TAG, contactInfo.getName() + " : " + contactInfo.getLastDialedPhone() + " : " + contactInfo.getLookupId() + " : " +
                        contactInfo.getUsage());
                views.setTextViewText(labels[0][i], contactInfo.name);

                final Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactInfo.getLastDialedPhone()));
                final PendingIntent pi = PendingIntent.getActivity(context, 0, callIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
                views.setOnClickPendingIntent(labels[0][i], pi);

                if (contactInfo.photoURI != null) {
                    views.setImageViewUri(labels[1][i], contactInfo.photoURI);
                } else {
                    views.setImageViewResource(labels[1][i], R.drawable.ic_contact_picture);
                }
            } else {
                views.setTextViewText(labels[0][i], "No Data");
                views.setImageViewResource(labels[1][i], R.drawable.ic_contact_picture);
            }
        }
    }

}
