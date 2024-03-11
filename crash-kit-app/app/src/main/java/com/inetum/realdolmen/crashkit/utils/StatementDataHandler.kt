package com.inetum.realdolmen.crashkit.utils

interface StatementDataHandler {
    fun updateUIFromViewModel(model: NewStatementViewModel)
    fun updateViewModelFromUI(model: NewStatementViewModel)
}