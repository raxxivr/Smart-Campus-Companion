package com.example.smartcampuscompanion.data

import androidx.annotation.DrawableRes
import com.example.smartcampuscompanion.R
import java.util.Calendar

data class Department(
    val name: String,
    val email: String,
    val phone: String,
    val location: String,
    @DrawableRes val iconRes: Int,
    val description: String,
    val programs: List<String>,
    val officeHoursStart: Int, // Hour in 24h format
    val officeHoursEnd: Int,   // Hour in 24h format
    val dean: String,
    val additionalContacts: List<String> = emptyList()
) {
    fun isOpen(): Boolean {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        // Assuming closed on weekends (Saturday and Sunday)
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false
        }
        
        return currentHour in officeHoursStart until officeHoursEnd
    }

    val officeHoursString: String
        get() {
            val start = if (officeHoursStart > 12) "${officeHoursStart - 12}:00 PM" else "$officeHoursStart:00 AM"
            val end = if (officeHoursEnd > 12) "${officeHoursEnd - 12}:00 PM" else "$officeHoursEnd:00 AM"
            return "$start – $end"
        }
}

object DataSource {
    val departments = listOf(
        Department(
            name = "College of Computing Studies",
            email = "ccs@example.com",
            phone = "123-456-7890",
            location = "Building A, Room 101",
            iconRes = R.drawable.ccs,
            description = "The College of Computing Studies is dedicated to excellence in computing education and research, preparing students for leadership in the digital world.",
            programs = listOf("BS Computer Science", "BS Information Technology", "BS Information Systems"),
            officeHoursStart = 8,
            officeHoursEnd = 17,
            dean = "Dr. Jane Doe",
            additionalContacts = listOf("Dept Secretary: 123-456-7899", "IT Lab: 123-456-7888")
        ),
        Department(
            name = "College of Education",
            email = "coed@example.com",
            phone = "123-456-7891",
            location = "Building B, Room 202",
            iconRes = R.drawable.coed,
            description = "Empowering future educators with the knowledge and skills to inspire the next generation.",
            programs = listOf("Bachelor of Elementary Education", "Bachelor of Secondary Education"),
            officeHoursStart = 8,
            officeHoursEnd = 17,
            dean = "Dr. John Smith"
        ),
        Department(
            name = "College of Engineering",
            email = "coe@example.com",
            phone = "123-456-7892",
            location = "Building C, Room 303",
            iconRes = R.drawable.coe,
            description = "Fostering innovation and technical expertise to solve complex engineering challenges.",
            programs = listOf("BS Civil Engineering", "BS Electrical Engineering", "BS Mechanical Engineering", "BS Computer Engineering"),
            officeHoursStart = 8,
            officeHoursEnd = 17,
            dean = "Engr. Robert Brown"
        ),
        Department(
            name = "College of Health and Allied Sciences",
            email = "chas@example.com",
            phone = "123-456-7893",
            location = "Building D, Room 404",
            iconRes = R.drawable.chas,
            description = "Providing high-quality health education to produce compassionate and skilled health professionals.",
            programs = listOf("BS Nursing", "BS Medical Technology", "BS Pharmacy"),
            officeHoursStart = 8,
            officeHoursEnd = 17,
            dean = "Dr. Maria Garcia"
        ),
        Department(
            name = "College of Arts and Sciences",
            email = "cas@example.com",
            phone = "123-456-7894",
            location = "Building E, Room 505",
            iconRes = R.drawable.cas,
            description = "Promoting critical thinking and a broad understanding of the arts and sciences.",
            programs = listOf("AB Psychology", "AB Communication", "BS Biology", "BS Mathematics"),
            officeHoursStart = 8,
            officeHoursEnd = 17,
            dean = "Dr. William Davis"
        ),
        Department(
            name = "College of Business, Accountancy and Administration",
            email = "cbaa@example.com",
            phone = "123-456-7895",
            location = "Building F, Room 606",
            iconRes = R.drawable.cbaa,
            description = "Developing future business leaders and professionals in accountancy and administration.",
            programs = listOf("BS Accountancy", "BS Business Administration", "BS Tourism Management"),
            officeHoursStart = 8,
            officeHoursEnd = 17,
            dean = "Dr. Linda Wilson"
        )
    )
}
