package com.inetum.realdolmen.crashkit.utils

import android.graphics.Point
import android.widget.CheckBox
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.inetum.realdolmen.crashkit.accidentsketch.IAccidentDrawable
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
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
    private lateinit var vehicleACircumstancesObserver: Observer<List<CheckBox>>
    private lateinit var accidentSketchShapesObserver: Observer<List<Triple<IAccidentDrawable, Point, TextView?>>>
    private lateinit var pointOfImpactVehicleASketchShapesObserver: Observer<List<Pair<IAccidentDrawable, Point>>>
    private lateinit var pointOfImpactVehicleBSketchShapesObserver: Observer<List<Pair<IAccidentDrawable, Point>>>

    @Before
    fun setUp() {
        statementDataObserver = mockk(relaxUnitFun = true)
        vehicleACircumstancesObserver = mockk(relaxUnitFun = true)
        accidentSketchShapesObserver = mockk(relaxUnitFun = true)
        pointOfImpactVehicleASketchShapesObserver = mockk(relaxUnitFun = true)
        pointOfImpactVehicleBSketchShapesObserver = mockk(relaxUnitFun = true)

        viewModel = NewStatementViewModel().apply {
            // Observe LiveData objects
            statementData.observeForever(statementDataObserver)
            vehicleACircumstances.observeForever(vehicleACircumstancesObserver)
            accidentSketchShapes.observeForever(accidentSketchShapesObserver)
            pointOfImpactVehicleASketchShapes.observeForever(pointOfImpactVehicleASketchShapesObserver)
            pointOfImpactVehicleBSketchShapes.observeForever(pointOfImpactVehicleBSketchShapesObserver)
        }
    }

    @Test
    fun verifyInitialStateOfStatementData() {
        val expectedData = StatementData()
        verify { statementDataObserver.onChanged(expectedData) }
    }

    @Test
    fun verifyInitialStateOfVehicleACircumstances() {
        val expectedData = emptyList<CheckBox>()
        verify { vehicleACircumstancesObserver.onChanged(expectedData) }
    }

    @Test
    fun verifyInitialStateOfAccidentSketchShapes() {
        val expectedData = emptyList<Triple<IAccidentDrawable, Point, TextView?>>()
        verify { accidentSketchShapesObserver.onChanged(expectedData) }
    }

    @Test
    fun verifyInitialStateOfPointOfImpactVehicleASketchShapes() {
        val expectedData = emptyList<Pair<IAccidentDrawable, Point>>()
        verify { pointOfImpactVehicleASketchShapesObserver.onChanged(expectedData) }
    }

    @Test
    fun verifyInitialStateOfPointOfImpactVehicleBSketchShapes() {
        val expectedData = emptyList<Pair<IAccidentDrawable, Point>>()
        verify { pointOfImpactVehicleBSketchShapesObserver.onChanged(expectedData) }
    }

    @After
    fun tearDown() {
        viewModel.statementData.removeObserver(statementDataObserver)
        viewModel.vehicleACircumstances.removeObserver(vehicleACircumstancesObserver)
        viewModel.accidentSketchShapes.removeObserver(accidentSketchShapesObserver)
        viewModel.pointOfImpactVehicleASketchShapes.removeObserver(pointOfImpactVehicleASketchShapesObserver)
        viewModel.pointOfImpactVehicleBSketchShapes.removeObserver(pointOfImpactVehicleBSketchShapesObserver)
    }
}


