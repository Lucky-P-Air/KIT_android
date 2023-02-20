package com.example.kit.data

import com.example.kit.model.Contact

class ContactSource() {
    /* Builds a list of Contact objects filled with mock data using
    .loadContacts()
     */
    //var sourceContactList: MutableList<Contact> = loadContacts()

    // Build a list of API-received contacts
    var sourceContactList: MutableList<Contact> = loadContacts()

    private fun loadContacts(): MutableList<Contact> {
        //TODO: Alphabetize the list that's returned
        return mutableListOf(
            Contact("Gibran", "Alcocer", "Idea10@gmail.com", "+18675309", 2, "days"),
            Contact("BamBam", "Ruder", "bambam@gmail.com", "+18675309", 2, "weeks"),
            Contact("Lucky", "Pierre", "threeDeer@gmail.com", "+12058675309", 4, "days"),
            Contact("Brandon", "Sanderson", "truthless@gmail.com", "+852028675309", 1, "days"),
            Contact("Will", "Wight", "iteration110@gmail.com", "+13308675309", 1, "months"),
            Contact("Cixin", "Liu", "threebody@gmail.com", "+12058675309", 4, "days"),
            Contact("Bill", "Watterson", "calvin@hobbes.com", "+852208675309", 1, "days"),
            Contact("Cher", "", "sonnysEx@gmail.com", "+13308675309", 1, "months"),
            Contact("Philter", "", "blossom@chronicles.com", "+13108675309", 2, "weeks"),
            Contact("Meursault", "", "albert@camus.com", "+12508675309", 4, "days"),
            Contact("Eithan", "Aurelius", "iSee@ozriel.com", "+12508675309", 4, "days"),
        )
    }
/* Moved to ViewModel for continuous updating
    fun loadApiContacts(): MutableList<Contact> {
        var listContacts = loadContacts()
        scope.launch {
            try {
                val response = ContactApi.retrofitService.getContacts(Secrets().headers)
                val listContactEntries = response.data
                Log.d("ContactSource", "Get request successful, retrieved ${listContactEntries.size} entries")
                // Transform nested ContactEntry structure to flat Contact class
                listContacts = listContactEntries.map() {
                    Contact(
                        it.attributes.firstName,
                        it.attributes.lastName,
                        it.attributes.email,
                        it.attributes.phoneNumber,
                        it.attributes.intervalNumber,
                        it.attributes.intervalUnit
                    )
                }.toMutableList()
                Log.d("ContactSource", "Transform produced listContacts of size ${listContacts.size} entries")
            //return (response.data).sortedWith(byFirstName).toMutableList()
            } catch (e: java.lang.Exception) {
                Log.d("ContactSource", e.toString())
            }
        }
        return listContacts
    }
*/
}

