package com.example.smartcampuscompanion.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartcampuscompanion.data.Department
import com.example.smartcampuscompanion.data.DataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CampusInfoUiState(
    val departments: List<Department> = emptyList()
)

class CampusInfoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CampusInfoUiState(departments = DataSource.departments))
    val uiState: StateFlow<CampusInfoUiState> = _uiState.asStateFlow()

}
