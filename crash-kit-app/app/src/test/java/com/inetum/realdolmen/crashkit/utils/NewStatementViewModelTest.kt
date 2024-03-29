package com.inetum.realdolmen.crashkit.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class NewStatementViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NewStatementViewModel

    private lateinit var statementDataObserver: Observer<StatementData>

    @Before
    fun setUp() {
        statementDataObserver = mockk(relaxUnitFun = true)

        viewModel = NewStatementViewModel().apply {
            statementData.observeForever(statementDataObserver)
        }
    }

    @Test
    fun `verify initial state of statementData`() {
        val expectedData = StatementData()
        verify { statementDataObserver.onChanged(expectedData) }
    }

}
