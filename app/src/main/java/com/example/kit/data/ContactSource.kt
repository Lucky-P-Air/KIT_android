package com.example.kit.data

import com.example.kit.model.Contact

class ContactSource {
    /* Builds a list of Contact objects filled with mock data using
    .loadContacts()
     */
    private fun loadContacts(): MutableList<Contact> {
        return mutableListOf(
            Contact("Matt", "Pereira", "mattcpereira@gmail.com", "+13018732741", 2, "weeks"),
            Contact("Lucky", "Pierre", "streetbllplaya@gmail.com", "+12054339837", 4, "days"),
            Contact("Jean", "Weatherax", "fakeJean@gmail.com", "+852028732741", 1, "days"),
            Contact("Isaac", "Askew", "fakeIsaac@gmail.com", "+13038732741", 1, "months")
        )

    }
}