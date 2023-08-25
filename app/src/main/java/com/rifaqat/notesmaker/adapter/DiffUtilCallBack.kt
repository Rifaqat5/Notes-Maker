package com.rifaqat.notesmaker.adapter

import androidx.recyclerview.widget.DiffUtil
import com.rifaqat.notesmaker.models.NotesEntity

//This class is call when user update the note
class DiffUtilCallBack: DiffUtil.ItemCallback<NotesEntity>() {
    //This method is call when notes ids are same
    override fun areItemsTheSame(oldItem: NotesEntity, newItem: NotesEntity): Boolean {
        return oldItem.key==newItem.key
    }

//    This method is call when notes contents is same.
    override fun areContentsTheSame(oldItem: NotesEntity, newItem: NotesEntity): Boolean {
        return oldItem.key==newItem.key
    }
}