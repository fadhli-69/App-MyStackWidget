package com.example.mystackwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri

class ImagesBannerWidget : AppWidgetProvider() {

    companion object {

        private const val TOAST_ACTION = "com.example.mystackwidget.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.mystackwidget.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.image_banner_widget).apply {
                setRemoteAdapter(R.id.stack_view, intent)
                setEmptyView(R.id.stack_view, R.id.empty_view)

                // Intent untuk menangani klik pada StackView
                val toastIntent = Intent(context, ImagesBannerWidget::class.java).apply {
                    action = TOAST_ACTION
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }

                val toastPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    toastIntent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    else PendingIntent.FLAG_UPDATE_CURRENT
                )

                setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
            }

            // Perbarui widget dengan RemoteViews
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update semua widget aktif
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == TOAST_ACTION) {
            val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
            Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
        }
    }
}
