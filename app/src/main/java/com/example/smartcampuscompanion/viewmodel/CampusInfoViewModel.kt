package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.DataSource
import com.example.smartcampuscompanion.data.Department
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CampusInfoViewModel : ViewModel() {

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments

    init {
        loadDepartments()
    }

    private fun loadDepartments() {
        viewModelScope.launch {
            _departments.value = DataSource.departments
        }
    }
}
