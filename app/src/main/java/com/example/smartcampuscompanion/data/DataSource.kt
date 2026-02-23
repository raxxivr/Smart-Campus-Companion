package com.example.smartcampuscompanion.data

data class Department(
    val name: String,
    val head: String,
    val email: String,
    val phone: String,
    val description: String
)

object DataSource {
    val departments = listOf(
        Department(
            name = "Computer Science",
            head = "Dr. Smith",
            email = "cs.hod@university.edu",
            phone = "123-456-7890",
            description = "The Computer Science department offers a range of programs in software development, AI, and cybersecurity."
        ),
        Department(
            name = "Electrical Engineering",
            head = "Dr. Jones",
            email = "ee.hod@university.edu",
            phone = "123-456-7891",
            description = "Focuses on electronics, circuits, and communication systems."
        ),
        Department(
            name = "Mechanical Engineering",
            head = "Dr. Williams",
            email = "me.hod@university.edu",
            phone = "123-456-7892",
            description = "Covers mechanics, thermodynamics, and robotics."
        ),
        Department(
            name = "Civil Engineering",
            head = "Dr. Brown",
            email = "ce.hod@university.edu",
            phone = "123-456-7893",
            description = "Deals with the design, construction, and maintenance of infrastructure."
        ),
        Department(
            name = "Business Administration",
            head = "Dr. Davis",
            email = "ba.hod@university.edu",
            phone = "123-456-7894",
            description = "Offers programs in management, finance, and marketing."
        )
    )
}
