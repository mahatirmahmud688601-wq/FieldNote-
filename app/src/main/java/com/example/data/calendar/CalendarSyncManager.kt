package com.example.data.calendar

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import com.example.data.model.TaskEntity
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object CalendarSyncManager {

    private fun getTaskTimestamps(task: TaskEntity): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        if (task.dueDate.isNotBlank()) {
            val parts = task.dueDate.split("-")
            if (parts.size == 3) {
                val year = parts[0].toIntOrNull() ?: cal.get(Calendar.YEAR)
                val month = (parts[1].toIntOrNull() ?: 1) - 1
                val day = parts[2].toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
                var hour = 9
                var minute = 0
                if (task.dueTime.isNotBlank() && task.dueTime.contains(":")) {
                    val timeParts = task.dueTime.split(":")
                    hour = timeParts[0].toIntOrNull() ?: 9
                    minute = timeParts[1].toIntOrNull() ?: 0
                }
                cal.set(year, month, day, hour, minute, 0)
            }
        }
        val startMillis = cal.timeInMillis
        val endMillis = startMillis + (60 * 60 * 1000) // 1 hour duration
        return Pair(startMillis, endMillis)
    }

    /**
     * Inserts event into Android Calendar Provider (automatically syncs with
     * active Google Calendar or Outlook accounts configured on the phone).
     */
    fun syncTaskToDeviceCalendar(context: Context, task: TaskEntity): Long? {
        try {
            val (startMillis, endMillis) = getTaskTimestamps(task)
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, "[Fieldnote] ${task.title}")
                put(CalendarContract.Events.DESCRIPTION, "${task.description}\nPriority: ${task.priority.uppercase()}")
                put(CalendarContract.Events.CALENDAR_ID, 1)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }
            val uri: Uri? = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            return uri?.lastPathSegment?.toLongOrNull()
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Opens system Calendar Event creation dialog (lets user select Google Calendar, Outlook, etc.)
     */
    fun openAddEventIntent(context: Context, task: TaskEntity) {
        try {
            val (startMillis, endMillis) = getTaskTimestamps(task)
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, task.title)
                .putExtra(CalendarContract.Events.DESCRIPTION, task.description)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback to web
            openGoogleCalendarWeb(context, task)
        }
    }

    /**
     * Seamless Google Calendar Web/App integration with pre-filled title, dates, and details
     */
    fun openGoogleCalendarWeb(context: Context, task: TaskEntity) {
        try {
            val (startMillis, endMillis) = getTaskTimestamps(task)
            val sdfUtc = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val startStr = sdfUtc.format(startMillis)
            val endStr = sdfUtc.format(endMillis)
            val titleEncoded = URLEncoder.encode(task.title, "UTF-8")
            val descEncoded = URLEncoder.encode("${task.description}\n\n[Scheduled via Fieldnote AI Copilot]", "UTF-8")

            val url = "https://calendar.google.com/calendar/render?action=TEMPLATE&text=$titleEncoded&dates=$startStr/$endStr&details=$descEncoded"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    /**
     * Seamless Microsoft Outlook Calendar Web/App integration
     */
    fun openOutlookCalendarWeb(context: Context, task: TaskEntity) {
        try {
            val (startMillis, endMillis) = getTaskTimestamps(task)
            val sdfIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val startStr = sdfIso.format(startMillis)
            val endStr = sdfIso.format(endMillis)
            val titleEncoded = URLEncoder.encode(task.title, "UTF-8")
            val descEncoded = URLEncoder.encode("${task.description}\n\n[Scheduled via Fieldnote AI Copilot]", "UTF-8")

            val url = "https://outlook.live.com/calendar/0/deeplink/compose?subject=$titleEncoded&startdt=$startStr&enddt=$endStr&body=$descEncoded"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    /**
     * Generate iCalendar (.ics) RFC 5545 format string for universal calendar export
     */
    fun generateIcsString(tasks: List<TaskEntity>): String {
        val sdfUtc = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val nowStr = sdfUtc.format(System.currentTimeMillis())

        val sb = StringBuilder()
        sb.append("BEGIN:VCALENDAR\r\n")
        sb.append("VERSION:2.0\r\n")
        sb.append("PRODID:-//Fieldnote//Geometric Balance Productivity//EN\r\n")
        sb.append("CALSCALE:GREGORIAN\r\n")
        sb.append("METHOD:PUBLISH\r\n")

        tasks.filter { it.dueDate.isNotBlank() }.forEach { task ->
            val (startMillis, endMillis) = getTaskTimestamps(task)
            val startStr = sdfUtc.format(startMillis)
            val endStr = sdfUtc.format(endMillis)
            val cleanTitle = task.title.replace("\n", " ").replace(",", "\\,").replace(";", "\\;")
            val cleanDesc = task.description.replace("\n", "\\n").replace(",", "\\,").replace(";", "\\;")

            sb.append("BEGIN:VEVENT\r\n")
            sb.append("UID:fieldnote-${task.id}@fieldnote.app\r\n")
            sb.append("DTSTAMP:$nowStr\r\n")
            sb.append("DTSTART:$startStr\r\n")
            sb.append("DTEND:$endStr\r\n")
            sb.append("SUMMARY:$cleanTitle\r\n")
            sb.append("DESCRIPTION:$cleanDesc\r\n")
            sb.append("STATUS:${if (task.status == "completed") "COMPLETED" else "CONFIRMED"}\r\n")
            sb.append("PRIORITY:${if (task.priority == "high") "1" else if (task.priority == "medium") "5" else "9"}\r\n")
            sb.append("END:VEVENT\r\n")
        }

        sb.append("END:VCALENDAR\r\n")
        return sb.toString()
    }

    fun openCalendarAtDate(context: Context, dateStr: String) {
        try {
            val cal = Calendar.getInstance()
            if (dateStr.isNotBlank()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.parse(dateStr)?.let { cal.time = it }
            }
            val builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            builder.appendPath(cal.timeInMillis.toString())
            val intent = Intent(Intent.ACTION_VIEW).setData(builder.build())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("content://com.android.calendar/time"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }
}
