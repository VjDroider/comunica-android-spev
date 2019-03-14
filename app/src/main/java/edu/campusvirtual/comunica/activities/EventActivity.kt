package edu.campusvirtual.comunica.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.text.format.DateFormat
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.models.calendar.Event
import edu.campusvirtual.comunica.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.content.ContentUris
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.ShareEvent
import com.kaopiz.kprogresshud.KProgressHUD
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.calendar.EventCOM
import edu.campusvirtual.comunica.models.configuration.Configuration
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.confirmAssistance
import java.io.File


class EventActivity : AppCompatActivity(), View.OnTouchListener {

    var event: EventCOM? = null
    var spin: KProgressHUD? = null

    var session: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        session = SessionManager(this)

        Answers.getInstance().logContentView(
            ContentViewEvent()
                .putContentName("VerEvento")
                .putContentType(event!!.title)
                .putContentId(session!!.getFullname())
        )
        event = intent.getSerializableExtra("Event") as EventCOM
        setupView()
    }

    fun setupView() {
        val imageView = findViewById<ImageView>(R.id.imageViewHeader)
        // Picasso.with(this).load(this.applicationContext.resources.getIdentifier("city", "drawable", packageName)).fit().into(imageView)

        Util.downloadAsset(this, Configuration.ImagenEventos, Configuration.ImagenEventos, Configuration.ImagenEventos, completion = {
            Picasso.with(this).load(File(it)).fit().into(imageView)
        }, failure = {
            Picasso.with(this).load(this.applicationContext.resources.getIdentifier("city", "drawable", packageName)).fit().into(imageView)
        })

        val title = findViewById<TextView>(R.id.textViewTitle)
        val place = findViewById<TextView>(R.id.textViewPlace)
        val start = findViewById<TextView>(R.id.textViewInitial)
        val end = findViewById<TextView>(R.id.textViewEnd)
        val description = findViewById<TextView>(R.id.textViewDescription)
        val buttonScheduling = findViewById<Button>(R.id.buttonSchedule)

        title.setText(event?.title)
        place.setText(event?.notes)
        start.setText(getDateString(getDate(event?.startdate!!)))
        end.setText(getDateString(getDate(event?.enddate!!)))
        description.setText(event?.description)
        buttonScheduling.setOnTouchListener(this)
    }

    fun getDate(date: String) : Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        try {
            return format.parse(date)
        } catch(e: ParseException) {
            Log.d("ERROR", "error: " + e.message)
            return null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateString(date: Date?) : String? {
        val locale = Locale("es", "mx")
        val format = SimpleDateFormat("EEEE, dd 'de' MMMM 'del' yyyy 'a las' HH:mm", locale)

        try {
            var dateTime = format.format(date)

            return dateTime
        } catch(e: ParseException) {
            Log.d("ERROR", "message: " + e.message)
            return null
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when(v?.id) {
            R.id.buttonSchedule -> {
                schedulingEvent()
            }
        }

        return true
    }

    fun schedulingEvent() {
        val beginTime = Calendar.getInstance()
        var beginEvent = getDate(event?.startdate!!)

        beginTime.set(
                Integer.parseInt(DateFormat.format("yyyy", beginEvent).toString()),
                Integer.parseInt(DateFormat.format("MM", beginEvent).toString()),
                Integer.parseInt(DateFormat.format("dd", beginEvent).toString()),
                Integer.parseInt(DateFormat.format("HH", beginEvent).toString()),
                Integer.parseInt(DateFormat.format("mm", beginEvent).toString())
        )

        val endTime = Calendar.getInstance()
        var endEvent = getDate(event?.enddate!!)

        endTime.set(
                Integer.parseInt(DateFormat.format("yyyy", endEvent).toString()),
                Integer.parseInt(DateFormat.format("MM", endEvent).toString()),
                Integer.parseInt(DateFormat.format("dd", endEvent).toString()),
                Integer.parseInt(DateFormat.format("HH", endEvent).toString()),
                Integer.parseInt(DateFormat.format("mm", endEvent).toString())
        )

        Service.shared().confirmAssistance(this, event?.id_evento!!, completion = {
            Log.d("YESSS", "si se pudo")
        }, failure = {
            Log.d("FAILLL", "No se pudo")
        })

        Answers.getInstance().logShare(ShareEvent()
            .putMethod("Calendar")
            .putContentName(event!!.title)
            .putContentType("event")
            .putContentId(event!!.id_evento.toString()));

        val intent = Intent(Intent.ACTION_INSERT)

        intent.setData(CalendarContract.Events.CONTENT_URI)
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
        intent.putExtra(CalendarContract.Events.TITLE, event?.title)
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event?.description)
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event?.notes)

        startActivity(intent)
    }

    private fun pushAppointmentsToCalender(cr: ContentResolver, title: String, addInfo: String, place: String, startDate: Long, endDate: Long): Long {

        val eventUriString = Uri.parse("content://com.android.calendar/events")

        val eventValues = ContentValues()

        eventValues.put("calendar_id", 1) // id, We need to choose from our mobile for primary its 1
        eventValues.put("title", title)
        eventValues.put("description", addInfo)
        eventValues.put("eventLocation", place)
        eventValues.put("dtstart", startDate)
        eventValues.put("dtend", endDate)
        eventValues.put("eventTimezone", TimeZone.getDefault().toString())
        eventValues.put("eventStatus", 1) // This information is sufficient for most entries tentative (0), confirmed (1) or canceled (2):
        eventValues.put("hasAlarm", 0) // 0 for false, 1 for true

        val eventUri = cr.insert(eventUriString, eventValues)
        //System.out.println("event id is::"+eventID);
        return java.lang.Long.parseLong(eventUri!!.lastPathSegment)

    }


    /** user defined method to get the next event id based on max_id+1  */
    private fun getNewEventId(cr: ContentResolver): Long {
        val local_uri = Uri.parse("content://com.android.calendar/events")
        val cursor = cr.query(local_uri, arrayOf("MAX(_id) as max_id"), null, null, "_id")
        cursor!!.moveToFirst()
        val max_val = cursor.getLong(cursor.getColumnIndex("max_id"))
        return max_val + 1
    }


    /** user defined method to delete the event based on id */
    private fun deleteEventFromCalendar(cr: ContentResolver, id: Long): Int {
        val eventUri = Uri.parse("content://com.android.calendar/events")  // or
        var deleteUri: Uri?
        deleteUri = ContentUris.withAppendedId(eventUri, id)
// System.out.println("Rows deleted: " + rows);
        return cr.delete(deleteUri!!, null, null)
    }

}
