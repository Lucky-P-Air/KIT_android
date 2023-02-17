package com.example.kit.ui.contactlist

import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kit.databinding.ListContactBinding
import com.example.kit.model.Contact
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
//class ContactListAdapter(contactList: LiveData<MutableList<Contact>>) : // Removed for ListAdapter conversion
class ContactListAdapter(val clickListener: ContactListListener) :
    ListAdapter<Contact, ContactListAdapter.ContactViewHolder>(DiffCallback) {

    // Data binding ViewHolder to list_contact.xml of individual list item
    class ContactViewHolder(var binding: ListContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ContactListListener, contact: Contact) {
            binding.contact = contact
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            val check:Boolean = oldItem.firstName == newItem.firstName // TODO && ID check
            Log.d("ContactListAdapter", "DiffCallback areItemsTheSame: $check")
            return check // TODO && ID check
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            val check: Boolean = (oldItem.firstName == newItem.firstName && //TODO ID check
                    oldItem.lastName == newItem.lastName &&
                    oldItem.email == newItem.email &&
                    oldItem.phoneNumber == newItem.phoneNumber &&
                    oldItem.intervalUnit == newItem.intervalUnit &&
                    oldItem.intervalTime == newItem.intervalTime
                    )
            Log.d("ContactListAdapter", "DiffCallback areContentsTheSame: $check")
            return check
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListAdapter.ContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(
            ListContactBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        val calendar = Calendar.getInstance() // TODO: Delete this placeholder reference to current time
        val calendar2 = Calendar.getInstance()  // TODO: Delete this placeholder time+2weeks
        calendar2.add(Calendar.DATE, 14) // TODO: Delete this placeholder time+2weeks
        holder.bind(clickListener, contact)
        holder.binding.apply {
            cardLastContact.text = formatDates(calendar.time) // contact.intervalTime.toString() // TODO: Placeholder for contact date
            cardNextContact.text = formatDates(calendar2.time) // TODO: Placeholder for contact date
        }
    }

    private fun formatDates(date_value: Date): String {
        val formatter = SimpleDateFormat("MMM d, y", Locale.getDefault())
        return formatter.format(date_value)
    }
}

class ContactListListener(val clickListener: (contact: Contact) -> Unit){
    fun onClick(contact: Contact) = clickListener(contact)
}