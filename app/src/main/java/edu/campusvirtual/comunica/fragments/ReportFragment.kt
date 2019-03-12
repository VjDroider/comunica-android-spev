package edu.campusvirtual.comunica.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kaopiz.kprogresshud.KProgressHUD
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.vamsi.customcalendar.CustomCalendar
import com.vamsi.customcalendar.Helpers.Badge
import com.vamsi.customcalendar.Helpers.CalenderDate
import com.vamsi.customcalendar.Helpers.ClickInterface

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.activities.EventActivity
import edu.campusvirtual.comunica.activities.ReportActivity
import edu.campusvirtual.comunica.adapters.CalendarAdapter
import edu.campusvirtual.comunica.adapters.ReportAdapter
import edu.campusvirtual.comunica.decorators.EventDecorator
import edu.campusvirtual.comunica.decorators.MySelectorDecorator
import edu.campusvirtual.comunica.decorators.OneDayDecorator
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.models.calendar.EventCOM
import edu.campusvirtual.comunica.models.inbox.Report
import edu.campusvirtual.comunica.services.Service
import edu.campusvirtual.comunica.services.getEventsCOM
import edu.campusvirtual.comunica.services.getReportsCOM
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ReportFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReportFragment : Fragment(), ClickInterface, SwipeRefreshLayout.OnRefreshListener, OnDateSelectedListener, OnMonthChangedListener {

    var eventss:ArrayList<CalendarDay> = arrayListOf();
    val events = ArrayList<Report>()
    val eventsToShow = ArrayList<Report>()
    var adapter: ReportAdapter? = null
    var recycler : RecyclerView? = null
    val badges = ArrayList<Badge>()
    var calendar: MaterialCalendarView? = null
    var mutableMapEvents = mutableMapOf<Date, ArrayList<Report>>()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var spin: KProgressHUD? = null

    var start:String = ""
    var end:String = ""
    var oneDayDecorator = OneDayDecorator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_report, container, false)

        setupCalendar(v)
        setupRecyclerView(v)
        getEvents()
        setupSwipeRefresh(v)

        return v
    }

    fun stopLoading() {
        Util.stopLoadingPage(spin)
    }

    fun getEvents() {
        // calendar!!.removeDecorators()
        eventss.clear()
        spin = Util.loadingPage(context!!, null, spin)
        if(Constants.backend == Constants.COMUNICA) {

            val calendar = Calendar.getInstance();
            // eventss.add(EventDay(calendar, R.drawable.ic_arrow_back));

            Service.shared().getReportsCOM(context!!, start, end, completion = {
                events.clear()
                events.addAll(it!!)

                val currentDate = Date()
                val today = CalenderDate()
                today.day = Integer.parseInt(DateFormat.format("dd", currentDate).toString())
                today.month = Integer.parseInt(DateFormat.format("MM", currentDate).toString())
                today.year = Integer.parseInt(DateFormat.format("yyyy", currentDate).toString())

                fillEvents(null)
                swipeRefreshLayout?.isRefreshing = false
                stopLoading()
                fillBadges()
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

        adapter = ReportAdapter(eventsToShow) {
            val intent = Intent(context!!, ReportActivity::class.java)
            intent.putExtra("report", it)
            startActivity(intent)
        }

        recycler!!.layoutManager = LinearLayoutManager(context!!, orientation, false)

        recycler!!.adapter = adapter
        val dividerDecoration = DividerItemDecoration(recycler!!.context, orientation)

        recycler!!.addItemDecoration(dividerDecoration)
    }

    fun setupCalendar(v: View) {
        calendar = v.findViewById<MaterialCalendarView>(R.id.calendarId)
        calendar!!.setOnMonthChangedListener(this)
        calendar!!.setOnDateChangedListener(this)
        calendar!!.showOtherDates = MaterialCalendarView.SHOW_ALL
        // calendar!!.selectionColor = R.color.colorPrimary
        getLastFirstDays(Calendar.getInstance())
    }

    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        // calendar = widget
        // oneDayDecorator.setDate(date.date)
        // calendar!!.invalidateDecorators()
        // widget.selectionColor = R.color.white
        widget.setCurrentDate(date, false)
        val selected = CalenderDate()
        var d = Calendar.getInstance()
        d.set(date.year, date.month, date.day)

        selected.day = Integer.parseInt(DateFormat.format("dd", d).toString())
        selected.month = Integer.parseInt(DateFormat.format("MM", d).toString()) -1
        selected.year = Integer.parseInt(DateFormat.format("yyyy", d).toString())

        fillEvents(selected)


    }

    override fun onMonthChanged(widget: MaterialCalendarView?, date: CalendarDay?) {
        // calendar = widget
        // oneDayDecorator.setDate(date?.date)
        widget?.setCurrentDate(date, false)
        widget?.invalidateDecorators()
        // calendar!!.setCurrentDate(date)
        // calendar!!.removeDecorators()

        var d = Calendar.getInstance()
        d.set(date?.year!!, date?.month!! - 1, date?.day!!)

        getLastFirstDays(d)
        getEvents()

        // calendar!!.addDecorators(oneDayDecorator)
        //
        // calendar!!.setCurrentDate(date, true)
    }

    fun addBadge(count: Int, day: Int, month: Int) {
        badges.add(Badge(count, day, month))
    }

    fun getLastFirstDays(date: Calendar) {
        val cmonth = date.get(Calendar.MONTH)

        date.set(Calendar.MONTH, date.get(Calendar.MONTH) - 1)

        start = date.get(Calendar.YEAR).toString() + "-" + (date.get(Calendar.MONTH) + 1) + "-01"

        // date.set(Calendar.MONTH, cmonth)
        date.set(Calendar.MONTH, date.get(Calendar.MONTH) + 3)

        var lastDate = date.getActualMaximum(Calendar.DATE)

        end = date.get(Calendar.YEAR).toString() + "-" + (date.get(Calendar.MONTH) + 1) + "-" + lastDate

    }

    override fun setDateClicked(date: CalenderDate?) {
        Log.d("CLICKED", "date clicked " + date.toString())
        fillEvents(date)
    }

    fun fillBadges() {
        calendar!!.addDecorator(EventDecorator(Color.RED, eventss))
    }

    fun fillEvents(date: CalenderDate?) {
        eventsToShow.clear()
        val currentDay = date?.day
        val currentMonth = date?.month
        val currentYear = date?.year

        for(event in events) {
            val dateEvent = getLocalDate(event._Fecha)
            val da = getDate(event._Fecha)
            val day = Integer.parseInt(DateFormat.format("dd", da).toString())
            val month = Integer.parseInt(DateFormat.format("MM", da).toString())
            val year = Integer.parseInt(DateFormat.format("yyyy", da).toString())

            if(currentDay == day && currentMonth == month && currentYear == year) {
                eventsToShow.add(event)
            }
            var d:CalendarDay = CalendarDay.from(dateEvent)
            eventss.add(d)
        }
        fillBadges()
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

    fun getLocalDate(date: String) : LocalDate? {
        var instance = Instant.parse(date + "Z")
        var result = LocalDateTime.ofInstant(instance, ZoneId.of(ZoneOffset.UTC.id))

        return result.toLocalDate()
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateString(date: Date?) : String? {

        try {
            val locale = Locale("es", "mx")
            val format = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", locale)

            var dateTime = format.format(date)

            return dateTime
        } catch(e: ParseException) {
            Log.d("ERROR", "message: " + e.message)
            return null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getHourString(date: Date?) : String? {
        val locale = Locale("es", "mx")
        val format = SimpleDateFormat("HH:mm", locale)

        try {
            var dateTime = format.format(date)

            return dateTime
        } catch(e: ParseException) {
            Log.d("ERROR", "message: " + e.message)
            return null
        }
    }

}
