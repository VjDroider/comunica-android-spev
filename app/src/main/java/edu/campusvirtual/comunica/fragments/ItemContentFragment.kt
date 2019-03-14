package edu.campusvirtual.comunica.fragments

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import kotlinx.android.synthetic.main.fragment_item_content.*
import edu.campusvirtual.comunica.models.item.Item
import edu.campusvirtual.comunica.models.item.ItemCOM

import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.library.SessionManager
import org.jsoup.Jsoup

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ItemContentFragmentFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ItemContentFragmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemContentFragment : Fragment() {

    lateinit var item: ItemCOM
    lateinit var contentTextView: WebView
    var session:SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val x = inflater.inflate(R.layout.fragment_item_content, container, false)

        session = SessionManager(context!!)
        contentTextView = x.findViewById(R.id.textHtmlId)

        var msg:String = item.contenido

        var doc = Jsoup.parse(msg)
        var body = doc.getElementsByTag("body")
        var head = doc.getElementsByTag("head")

        var myHead = head.toString()

        if(myHead == "<head></head>") {
            myHead = "<head>" +
                       "<style>" +
                          "img {left: 50%;position: relative;max-width: 100%;-webkit-transform: translateX(-50%);-moz-transform: translateX(-50%);-ms-transform: translateX(-50%);-o-transform: translateX(-50%);transform: translateX(-50%);}" +
                       "</style>" +
                       "<meta name=\"viewport\" content=\"initial-scale=1, width=device-width, height=device-height, viewport-fit=cover\">" +
                       "<meta charset=\"UTF-8\">"+
                     "</head>\n"
        }

        var html = "<html>\n" +
                myHead.toString() +
                body.toString() +
                "</html>"

        Answers.getInstance().logContentView(ContentViewEvent().putContentName(item.menuItemTexto).putContentType("Item").putContentId(session!!.getFullname()))
        contentTextView.settings.loadWithOverviewMode = true
        contentTextView.settings.useWideViewPort = true
        contentTextView.settings.javaScriptEnabled = true
        contentTextView.settings.defaultTextEncodingName = "utf-8"
        // contentTextView.loadData(Html.fromHtml(html), "text/html", "utf-8")
        contentTextView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        return x
    }

}
