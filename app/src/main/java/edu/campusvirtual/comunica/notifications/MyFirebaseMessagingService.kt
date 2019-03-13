package edu.campusvirtual.comunica.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.campusvirtual.comunica.activities.HomeActivity
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.activities.MainActivity
import me.leolin.shortcutbadger.ShortcutBadger
import java.util.*

/**
 * Created by jonathan on 3/9/18.
 */
class MyFirebaseMessagingService: FirebaseMessagingService() {


    val TAG = "Service"
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        sendNotification(remoteMessage!!)

    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val r = Random()
        val random = r.nextInt(800 - 1) + 1
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 233, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val number = Integer.parseInt(remoteMessage.data["badge"])
        val tema = remoteMessage.data["message"]
        var notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = resources.getString(R.string.default_notification_channel_id)
            var mChannel:NotificationChannel = NotificationChannel(channel, tema, NotificationManager.IMPORTANCE_HIGH)

            mChannel.enableLights(true)
            mChannel.lightColor = resources.getColor(R.color.colorPrimary)
            mChannel.enableVibration(true)
            mChannel.description = ""

            notificationManager.createNotificationChannel(mChannel)
        }

        ShortcutBadger.applyCount(this, number)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setContentText(tema)
                .setContentTitle(getString(R.string.app_name))
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_notification)
                .setSound(defaultSoundUri)
                .setNumber(number)
                .setChannelId(resources.getString(R.string.default_notification_channel_id))
                .setColor(resources.getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)
        // val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(random /* ID of notification */, notificationBuilder.build())

    }

    // override fun zzd(p0: Intent?) { }

}