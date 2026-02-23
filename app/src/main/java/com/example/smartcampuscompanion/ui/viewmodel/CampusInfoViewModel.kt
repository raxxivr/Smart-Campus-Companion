package com.example.smartcampuscompanion.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartcampuscompanion.data.Department
import com.example.smartcampuscompanion.data.DataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CampusInfoUiState(
    val departments: List<Department> = emptyList(),
    val searchQuery: String = "",
    val expandedDepartmentIds: Set<String> = emptySet()
)

class CampusInfoViewModel : ViewModel() {

    private val allDepartments = DataSource.departments

    private val _uiState = MutableStateFlow(CampusInfoUiState(departments = allDepartments))
    val uiState: StateFlow<CampusInfoUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            val filteredDepartments = if (query.isBlank()) {
                allDepartments
            } else {
                allDepartments.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                departments = filteredDepartments
            )
        }
    }

    fun onDepartmentClicked(departmentName: String) {
        _uiState.update { currentState ->
            val expandedIds = currentState.expandedDepartmentIds.toMutableSet()
            if (expandedIds.contains(departmentName)) {
                expandedIds.remove(departmentName)
            } else {
                expandedIds.add(departmentName)
            }
            currentState.copy(expandedDepartmentIds = expandedIds)
        }
    }
}
