package com.example.smartcampuscompanion.data

data class Department(
    val name: String,
    val email: String,
    val phone: String,
    val office: String
)

val departmentList = listOf(
    Department("College of Computing Studies", "ccs@example.com", "123-456-7890", "Building A, Room 101"),
    Department("College of Education", "coed@example.com", "123-456-7891", "Building B, Room 202"),
    Department("College of Engineering", "coe@example.com", "123-456-7892", "Building C, Room 303"),
    Department("College of Health and Allied Sciences", "chas@example.com", "123-456-7893", "Building D, Room 404"),
    Department("College of Arts and Sciences", "cas@example.com", "123-456-7894", "Building E, Room 505"),
    Department("College of Business, Accountancy and Administration", "cbaa@example.com", "123-456-7895", "Building F, Room 606")
)
