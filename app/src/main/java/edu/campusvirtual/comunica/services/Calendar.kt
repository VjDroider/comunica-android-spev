package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.CalendarInterface
import edu.campusvirtual.comunica.interfaces.ItemInterface
import edu.campusvirtual.comunica.interfaces.MessageInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.calendar.Event
import edu.campusvirtual.comunica.models.calendar.EventCOM
import edu.campusvirtual.comunica.models.calendar.Response
import edu.campusvirtual.comunica.models.inbox.Report
import edu.campusvirtual.comunica.models.inbox.ResponseDetailReport
import edu.campusvirtual.comunica.models.inbox.ResponseReportCOM
import edu.campusvirtual.comunica.models.inbox.SingleReport
import edu.campusvirtual.comunica.models.item.ResponseCOM
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

/**
 * Created by jonathan on 3/1/18.
 */
fun Service.getEvents(context: Context, completion: (ArrayList<Event>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(CalendarInterface::class.java)

    service.getEvents(Constants.collegeId).enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener los eventos")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.getEventsCOM(context: Context, completion: (ArrayList<EventCOM>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(ItemInterface::class.java)
    val session = SessionManager(context)
    var x = session.getLastGet()

    service.getItemsCOM(Constants.collegeId).enqueue(object: Callback<ResponseCOM> {
        override fun onResponse(call: Call<ResponseCOM>?, response: retrofit2.Response<ResponseCOM>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.eventos)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener los items")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<ResponseCOM>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.getReportDetailsCOM(context: Context, id: Int, completion: (ArrayList<SingleReport>) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(MessageInterface::class.java)
    val session = SessionManager(context)

    service.getReportDetails(id).enqueue(object: Callback<ResponseDetailReport> {
        override fun onFailure(call: Call<ResponseDetailReport>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(
            call: Call<ResponseDetailReport>?,
            response: retrofit2.Response<ResponseDetailReport>?
        ) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.list!!._Lista_Status)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener los items")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

    })
}

fun Service.getReportsCOM(context: Context, start: String, end: String, completion: (ArrayList<Report>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(MessageInterface::class.java)
    val session = SessionManager(context)
    var x = session.getLastGet()

    service.getReports(session.getId(), start, end).enqueue(object: Callback<ResponseReportCOM> {
        override fun onResponse(call: Call<ResponseReportCOM>?, response: retrofit2.Response<ResponseReportCOM>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.list)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener los items")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<ResponseReportCOM>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.confirmAssistance(context: Context, eventId: Int, completion: () -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(CalendarInterface::class.java)

    service.confirmAssistance(eventId.toString()).enqueue(object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Ya agendaste este evento o hubo un error al agendarlo")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

    })
}