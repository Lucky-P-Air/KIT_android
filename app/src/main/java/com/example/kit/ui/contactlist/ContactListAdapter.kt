package com.example.kit.ui.contactlist

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kit.databinding.ListContactBinding
import com.example.kit.model.Contact
import com.example.kit.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(
            ListContactBinding.inflate(layoutInflater, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(clickListener, contact)
        //TODO Delete
        //val dateStringParser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        //val dateFormatter = DateTimeFormatter.ofPattern("MMM d, y")

        // LocalDates of createdAt and lastContact
        val lastContactLocalDate = contact.lastContacted?.let {DateUtils().asLocalDate(parseDates(it)!!)}
        val createdLocalDate = DateUtils().asLocalDate(parseDates(contact.createdAt)!!)
        // Set starting point for nextContactDate based on lastContact or createdAt
        var nextContactLocalDate = lastContactLocalDate ?: createdLocalDate
        // Increment nextContactLocalDate
        when (contact.intervalUnit) {
            "days" -> nextContactLocalDate = nextContactLocalDate.plusDays(contact.intervalTime.toLong())
            "weeks" -> nextContactLocalDate = nextContactLocalDate.plusWeeks(contact.intervalTime.toLong())
            "months" -> nextContactLocalDate = nextContactLocalDate.plusMonths(contact.intervalTime.toLong())
            "years" -> nextContactLocalDate = nextContactLocalDate.plusYears(contact.intervalTime.toLong())
        }

        holder.binding.apply {
            cardLastContact.text = contact.lastContacted?.let { formatDates(parseDates(it)!!) } ?: "Never"
            cardNextContact.text = formatDates(DateUtils().asDate(nextContactLocalDate))
        }
    }

    private fun formatDates(date_value: Date): String {
        /** Return a String, formatted "MMM d, y", from an input Date object
         * */
        val formatter = SimpleDateFormat("MMM d, y", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        //val formatter = SimpleDateFormat("MMM d, y", )
        return formatter.format(date_value)
    }
    private fun parseDates(date_string: String): Date? {
        /** Return a Date? object from an input Datetime String that is based in UTC
         * */
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        //val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        return formatter.parse(date_string)
    }
}

class ContactListListener(val clickListener: (contact: Contact) -> Unit){
    fun onClick(contact: Contact) = clickListener(contact)
}