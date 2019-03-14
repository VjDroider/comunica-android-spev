package edu.campusvirtual.comunica.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.kaopiz.kprogresshud.KProgressHUD
import com.vamsi.customcalendar.CustomCalendar
import com.vamsi.customcalendar.Helpers.Badge
import com.vamsi.customcalendar.Helpers.CalenderDate
import com.vamsi.customcalendar.Helpers.ClickInterface
import edu.campusvirtual.comunica.activities.EventActivity
import edu.campusvirtual.comunica.adapters.CalendarAdapter
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.calendar.Event
import edu.campusvirtual.comunica.models.calendar.EventCOM

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getEvents
import edu.campusvirtual.comunica.services.getEventsCOM
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CalendarFragment : Fragment(), ClickInterface, SwipeRefreshLayout.OnRefreshListener {

    val events = ArrayList<EventCOM>()
    val eventsToShow = ArrayList<EventCOM>()
    var adapter: CalendarAdapter? = null
    var recycler : RecyclerView? = null
    val badges = ArrayList<Badge>()
    var calendar: CustomCalendar? = null
    var mutableMapEvents = mutableMapOf<Date, ArrayList<EventCOM>>()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var spin: KProgressHUD? = null
    var session:SessionManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_calendar, container, false)

        session = SessionManager(context!!)
        setupCalendar(v)
        setupRecyclerView(v)
        getEvents()
        setupSwipeRefresh(v)

        Answers.getInstance().logContentView(
            ContentViewEvent()
                .putContentName("Eventos")
                .putContentType("Calendar")
                .putContentId(session!!.getFullname())
        )

        return v
    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    fun getEvents() {
        spin = Util.loadingPage(context!!, null, spin)
        if(Constants.backend == Constants.COMUNICA) {
            Service.shared().getEventsCOM(context!!, completion = {
                this.events.clear()
                this.events.addAll(it!!)

                fillBadges()
                val currentDate = Date()
                val today = CalenderDate()
                today.day = Integer.parseInt(DateFormat.format("dd", currentDate).toString())
                today.month = Integer.parseInt(DateFormat.format("MM", currentDate).toString())
                today.year = Integer.parseInt(DateFormat.format("yyyy", currentDate).toString())

                fillEvents(today)
                swipeRefreshLayout?.isRefreshing = false
                stopLoading()
            }, failure = {

            })
        }

    }

    override fun onRefresh() {
        getEvents()
    }

    fun setupSwipeRefresh(v: View) {
        swipeRefreshLayout = v.findViewById<SwipeRefreshLayout>(R.id.swipeContainerCalendarId)
        swipeRefreshLayout!!.setOnRefreshListener(this)
    }

    fun setupRecyclerView(v: View) {
        recycler = v.findViewById<RecyclerView>(R.id.recyclerViewCalendarId)
        val orientation = LinearLayout.VERTICAL

        adapter = CalendarAdapter(eventsToShow) {
            val intent = Intent(context!!, EventActivity::class.java)
            intent.putExtra("Event", it)
            startActivity(intent)
        }

        recycler!!.layoutManager = LinearLayoutManager(context!!, orientation, false)

        recycler!!.adapter = adapter
        val dividerDecoration = DividerItemDecoration(recycler!!.context, orientation)

        recycler!!.addItemDecoration(dividerDecoration)
    }

    fun setupCalendar(v: View) {
        calendar = v.findViewById<CustomCalendar>(R.id.calendarId)
        calendar!!.setFullScreenWidth(true)
        calendar!!.setOnClickDate(this)
    }

    fun addBadge(count: Int, day: Int, month: Int) {
        badges.add(Badge(count, day, month))
    }

    override fun setDateClicked(date: CalenderDate?) {
        Log.d("CLICKED", "date clicked " + date.toString())
        fillEvents(date)
    }

    fun fillBadges() {
        mutableMapEvents.clear()
        badges.clear()

        for(event in events) {
            val dateEvent = getDate(event.startdate)

            if( mutableMapEvents[dateEvent!!] == null) {
                mutableMapEvents[dateEvent] = ArrayList()
            }

            mutableMapEvents[dateEvent]?.add(event)

        }

        for(b in mutableMapEvents) {
            val day = DateFormat.format("dd", b.key)
            val month = DateFormat.format("MM", b.key)
            addBadge(b.value.size, Integer.parseInt(day.toString()), Integer.parseInt(month.toString()))
        }

        calendar?.setBadgeDateList(badges)
    }

    fun fillEvents(date: CalenderDate?) {
        eventsToShow.clear()
        val currentDay = date?.day
        val currentMonth = date?.month
        val currentYear = date?.year

        for(event in events) {
            val dateEvent = getDate(event.startdate)
            val day = Integer.parseInt(DateFormat.format("dd", dateEvent).toString())
            val month = Integer.parseInt(DateFormat.format("MM", dateEvent).toString())
            val year = Integer.parseInt(DateFormat.format("yyyy", dateEvent).toString())

            if(currentDay == day && currentMonth == month && currentYear == year) {
                eventsToShow.add(event)
            }
        }

        adapter?.notifyDataSetChanged()
    }

    fun getDate(date: String) : Date? {
        val locale = Locale("es", "mx")
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale)

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
        val format = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", locale)

        try {
            var dateTime = format.format(date)

            return dateTime
        } catch(e: ParseException) {
            Log.d("ERROR", "message: " + e.message)
            return null
        }
    }


}
