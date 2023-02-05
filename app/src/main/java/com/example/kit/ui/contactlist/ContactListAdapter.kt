package com.example.kit.ui.contactlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kit.R
import com.example.kit.data.ContactSource
import com.example.kit.model.Contact

class ContactListAdapter :
    RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>() {

    //TODO( REVISE ContactListAdapter to point to ViewModel for data)
    private val list: MutableList<Contact> = ContactSource().loadContacts()

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
        val currentContext = holder.cardName.context
        holder.cardName.text =  currentContext.getString(
            R.string.text_fullname, contact.firstName, contact.lastName)
        holder.cardLastContact.text = contact.intervalTime.toString() // TODO: Placeholder for contact date
        holder.cardNextContact.text = contact.intervalUnit // TODO: Placeholder for contact date
        holder.buttonViewContact.setOnClickListener {
            val action =
                ContactListFragmentDirections
                    .actionNavigationContactlistToContactDetailFragment(position)  //TODO: replace position with pk(id)
            holder.view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
