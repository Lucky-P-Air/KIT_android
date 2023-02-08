package com.example.kit.ui.contactlist

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kit.R
import com.example.kit.model.Contact
import java.text.SimpleDateFormat
import java.util.*

class ContactListAdapter(contactList: LiveData<MutableList<Contact>>) :
    RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>() {

    // Load up the list of contacts from ViewModel
    private val list: List<Contact> = contactList.value!!


    class ContactViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val buttonViewContact = view.findViewById<Button>(R.id.button_view)!!
        val cardName = view.findViewById<TextView>(R.id.card_name)!!
        val cardLastContact = view.findViewById<TextView>(R.id.card_last_contact)!!
        val cardNextContact = view.findViewById<TextView>(R.id.card_next_contact)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_contact, parent, false)
        return ContactViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = list.get(position)
        val currentContext = holder.view.context
        val calendar = Calendar.getInstance() // TODO: Delete this placeholder reference to current time
        val calendar2 = Calendar.getInstance()  // TODO: Delete this placeholder time+2weeks
        calendar2.add(Calendar.DATE, 14) // TODO: Delete this placeholder time+2weeks
        holder.apply {
            cardName.text =  currentContext.getString(
                R.string.text_fullname, contact.firstName, contact.lastName)
            cardLastContact.text = formatDates(calendar.time) // contact.intervalTime.toString() // TODO: Placeholder for contact date
            cardNextContact.text = formatDates(calendar2.time) // TODO: Placeholder for contact date
            buttonViewContact.setOnClickListener {
                val action =
                    ContactListFragmentDirections
                        .actionNavigationContactlistToContactDetailFragment(position)  //TODO: replace position with pk(id)
                view.findNavController().navigate(action)
            }
        }
    }

    private fun formatDates(date_value: Date): String {
        val formatter = SimpleDateFormat("MMM d, y", Locale.getDefault())
        return formatter.format(date_value)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
