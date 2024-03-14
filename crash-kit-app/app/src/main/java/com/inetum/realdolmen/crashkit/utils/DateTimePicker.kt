package com.inetum.realdolmen.crashkit.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TimePicker
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class DateTimePicker(private val context: Context) : DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private var day = LocalDate.now().dayOfMonth
    private var month = LocalDate.now().month.value
    private var year = LocalDate.now().year
    private var hour = LocalDateTime.now().hour
    private var minute = LocalDateTime.now().minute

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    private val changeSupport = PropertyChangeSupport(this)

    public var dateTime: LocalDateTime? = null
        set(newValue) {
            val oldValue = field
            field = newValue

            // Notify listeners about the change
            changeSupport.firePropertyChange("dateTime", oldValue, newValue)
        }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(context, this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        dateTime = LocalDateTime.of(savedYear, savedMonth, savedDay, savedHour, savedMinute)
    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }


    public fun pickDateTime() {
        getDateTimeCalendar()
        DatePickerDialog(context, this, year, month, day).show()

    }

    public fun getFormattedDateTime(localDateTime: LocalDateTime?): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        return localDateTime?.format(formatter) ?: ""
    }

    fun addDateChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

}
