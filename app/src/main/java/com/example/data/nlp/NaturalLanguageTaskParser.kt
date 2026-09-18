package com.example.data.nlp

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

data class ParsedTaskResult(
    val rawText: String,
    val title: String,
    val dueDate: String,
    val dueTime: String,
    val priority: String,
    val estimatedMinutes: Int,
    val category: String,
    val confidence: Float,
    val isAmbiguous: Boolean
)

object NaturalLanguageTaskParser {

    /**
     * Parses input like:
     * "Finish Physics assignment next Friday at 4 PM, high priority, 90 minutes"
     */
    fun parse(input: String): ParsedTaskResult {
        val text = input.trim()
        if (text.isBlank()) {
            return ParsedTaskResult(
                rawText = input,
                title = "New Task",
                dueDate = "",
                dueTime = "",
                priority = "medium",
                estimatedMinutes = 30,
                category = "General",
                confidence = 0.5f,
                isAmbiguous = true
            )
        }

        var workingText = text
        var priority = "medium"
        var durationMinutes = 30
        var dueDate = ""
        var dueTime = ""
        var category = "General"
        var confidence = 0.95f

        // 1. Detect Priority
        val priorityPattern = Pattern.compile("(?i)\\b(urgent|high priority|medium priority|low priority|high|medium|low)\\b")
        val priorityMatcher = priorityPattern.matcher(workingText)
        if (priorityMatcher.find()) {
            val matched = priorityMatcher.group(1).lowercase(Locale.getDefault())
            priority = when {
                matched.contains("urgent") -> "high"
                matched.contains("high") -> "high"
                matched.contains("low") -> "low"
                else -> "medium"
            }
            workingText = workingText.replace(priorityMatcher.group(0), " ").trim()
        }

        // 2. Detect Duration (e.g., 90 minutes, 2 hours, 45m)
        val durationPattern = Pattern.compile("(?i)\\b(\\d+)\\s*(minutes|minute|mins|min|m|hours|hour|hrs|hr|h)\\b")
        val durationMatcher = durationPattern.matcher(workingText)
        if (durationMatcher.find()) {
            val amount = durationMatcher.group(1).toIntOrNull() ?: 30
            val unit = durationMatcher.group(2).lowercase(Locale.getDefault())
            durationMinutes = if (unit.startsWith("h")) amount * 60 else amount
            workingText = workingText.replace(durationMatcher.group(0), " ").trim()
        }

        // 3. Detect Time (e.g. 4 PM, 16:00, 4:30 pm, at 9am)
        val timePattern = Pattern.compile("(?i)(?:at\\s+)?\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\b")
        val timeMatcher = timePattern.matcher(workingText)
        if (timeMatcher.find()) {
            val rawHour = timeMatcher.group(1).toIntOrNull() ?: 12
            val rawMinute = timeMatcher.group(2)?.toIntOrNull() ?: 0
            val amPm = timeMatcher.group(3)?.lowercase(Locale.getDefault())

            var hour = rawHour
            if (amPm != null) {
                if (amPm == "pm" && hour < 12) hour += 12
                if (amPm == "am" && hour == 12) hour = 0
                dueTime = String.format(Locale.getDefault(), "%02d:%02d", hour, rawMinute)
                workingText = workingText.replace(timeMatcher.group(0), " ").trim()
            } else if (timeMatcher.group(2) != null) { // e.g. 14:30
                dueTime = String.format(Locale.getDefault(), "%02d:%02d", rawHour, rawMinute)
                workingText = workingText.replace(timeMatcher.group(0), " ").trim()
            }
        }

        // 4. Detect Date (today, tomorrow, next friday, this sunday, next monday, etc.)
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val lowerWorking = workingText.lowercase(Locale.getDefault())

        val dayNames = listOf("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
        var dateDetected = false

        if (lowerWorking.contains("today")) {
            dueDate = sdf.format(cal.time)
            workingText = workingText.replace("(?i)\\btoday\\b".toRegex(), " ")
            dateDetected = true
        } else if (lowerWorking.contains("tomorrow")) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            dueDate = sdf.format(cal.time)
            workingText = workingText.replace("(?i)\\btomorrow\\b".toRegex(), " ")
            dateDetected = true
        } else {
            // check day names
            for ((index, dayName) in dayNames.withIndex()) {
                val targetDayOfWeek = index + 1
                val pattern = Pattern.compile("(?i)\\b(?:next|this)?\\s*" + dayName + "\\b")
                val m = pattern.matcher(workingText)
                if (m.find()) {
                    var currentDay = cal.get(Calendar.DAY_OF_WEEK)
                    var daysUntil = (targetDayOfWeek - currentDay + 7) % 7
                    if (daysUntil == 0) daysUntil = 7
                    cal.add(Calendar.DAY_OF_YEAR, daysUntil)
                    dueDate = sdf.format(cal.time)
                    workingText = workingText.replace(m.group(0), " ")
                    dateDetected = true
                    break
                }
            }
        }

        if (!dateDetected) {
            // Default to today if no date specified
            dueDate = sdf.format(Calendar.getInstance().time)
        }

        // 5. Clean Title
        var cleanTitle = workingText
            .replace(",", " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        if (cleanTitle.startsWith("at ") || cleanTitle.startsWith("on ")) {
            cleanTitle = cleanTitle.substring(3).trim()
        }
        if (cleanTitle.isBlank()) {
            cleanTitle = text
            confidence = 0.6f
        }

        // 6. Category inference
        val lowerTitle = cleanTitle.lowercase(Locale.getDefault())
        category = when {
            lowerTitle.contains("assignment") || lowerTitle.contains("study") || lowerTitle.contains("exam") || lowerTitle.contains("physics") || lowerTitle.contains("math") -> "Study"
            lowerTitle.contains("meeting") || lowerTitle.contains("sync") || lowerTitle.contains("review") || lowerTitle.contains("roadmap") -> "Work"
            lowerTitle.contains("workout") || lowerTitle.contains("run") || lowerTitle.contains("gym") || lowerTitle.contains("walk") -> "Wellness"
            else -> "General"
        }

        return ParsedTaskResult(
            rawText = input,
            title = cleanTitle,
            dueDate = dueDate,
            dueTime = if (dueTime.isNotBlank()) dueTime else "12:00",
            priority = priority,
            estimatedMinutes = durationMinutes,
            category = category,
            confidence = confidence,
            isAmbiguous = confidence < 0.8f
        )
    }
}
