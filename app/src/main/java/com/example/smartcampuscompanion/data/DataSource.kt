package com.example.smartcampuscompanion.data

import androidx.annotation.DrawableRes
import com.example.smartcampuscompanion.R

data class Department(
    val name: String,
    val email: String,
    val phone: String,
    val location: String,
    @DrawableRes val iconRes: Int
)

object DataSource {
    val departments = listOf(
        Department(
            "College of Computing Studies", 
            "ccs@example.com", 
            "123-456-7890", 
            "Building A, Room 101", 
            R.drawable.ccs
        ),
        Department(
            "College of Education", 
            "coed@example.com", 
            "123-456-7891", 
            "Building B, Room 202", 
            R.drawable.coed
        ),
        Department(
            "College of Engineering", 
            "coe@example.com", 
            "123-456-7892", 
            "Building C, Room 303", 
            R.drawable.coe
        ),
        Department(
            "College of Health and Allied Sciences", 
            "chas@example.com", 
            "123-456-7893", 
            "Building D, Room 404", 
            R.drawable.chas
        ),
        Department(
            "College of Arts and Sciences", 
            "cas@example.com", 
            "123-456-7894", 
            "Building E, Room 505", 
            R.drawable.cas
        ),
        Department(
            "College of Business, Accountancy and Administration", 
            "cbaa@example.com", 
            "123-456-7895", 
            "Building F, Room 606", 
            R.drawable.cbaa
        )
    )
}
