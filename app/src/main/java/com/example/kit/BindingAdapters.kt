package com.example.kit

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kit.model.Contact
import com.example.kit.ui.contactlist.ContactListAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: MutableList<Contact>?) {
    val adapter = recyclerView.adapter as ContactListAdapter
    adapter.submitList(data)
}