package uz.abdulla.notes.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import uz.abdulla.notes.MainActivity
import uz.abdulla.notes.R
import uz.abdulla.notes.database.DatabaseHelper
import uz.abdulla.notes.model.DataNotes

class AlarmReceiver: BroadcastReceiver() {


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val databaseHelper = DatabaseHelper(context!!)
        val list: MutableList<DataNotes> = ArrayList()
        list.clear()
        val cursor = databaseHelper.readNote()
        cursor?.let {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val description = cursor.getString(2)
                    val color = cursor.getInt(3)
                    val createdDate = cursor.getString(4)
                    val notification = cursor.getInt(5)
                    val dataNotes = DataNotes(id, title, description,color,createdDate,notification)
                    list.add(dataNotes)
                }
            }
        }
        var title = list[0].title
        var description = list[0].description
        val id = intent?.getStringExtra("id")
        for (i in list.indices){
            if (list[i].id == id){
                title = list[i].title
                description = list[i].description
            }
        }
        databaseHelper.updateNote(id!!,0)
        Log.i("TTT", "onReceive: $id ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = "my_channel_id"
            val channel = NotificationChannel(channelId,"My channel name",NotificationManager.IMPORTANCE_DEFAULT)
            context!!.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)

            val startIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                100,
                startIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationBuilder = NotificationCompat.Builder(context,channelId)
            notificationBuilder
                .setSmallIcon(R.drawable.ic_notification_full)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent).priority = NotificationCompat.PRIORITY_DEFAULT
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(0,notificationBuilder.build())
        }
    }

}