package com.example.kit.ui.contactlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kit.databinding.ListContactBinding
import com.example.kit.model.Contact
import com.example.kit.utils.formatLocalDates
import com.example.kit.utils.getNextContactLocalDate

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
            val check:Boolean = oldItem.id == newItem.id
            Log.d("ContactListAdapter", "DiffCallback areItemsTheSame: $check")
            return check
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            val check: Boolean = (oldItem.firstName == newItem.firstName && //TODO ID check
                    oldItem.lastName == newItem.lastName &&
                    oldItem.email == newItem.email &&
                    oldItem.phoneNumber == newItem.phoneNumber &&
                    oldItem.remindersEnabled == newItem.remindersEnabled &&
                    oldItem.intervalUnit == newItem.intervalUnit &&
                    oldItem.intervalTime == newItem.intervalTime &&
                    oldItem.status == newItem.status
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

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        val nextContactLocalDate = getNextContactLocalDate(contact)
        holder.bind(clickListener, contact)
        holder.binding.apply {
            cardLastContact.text = contact.lastContacted?.let {
                formatLocalDates(it) } ?: "Never"
            cardNextContact.text = formatLocalDates(nextContactLocalDate)
        }
    }
}

class ContactListListener(val clickListener: (contact: Contact) -> Unit){
    fun onClick(contact: Contact) = clickListener(contact)
}