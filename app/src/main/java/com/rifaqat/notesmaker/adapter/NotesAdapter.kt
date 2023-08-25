package com.rifaqat.notesmaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rifaqat.notesmaker.R
import com.rifaqat.notesmaker.databinding.NotesSampleBinding
import com.rifaqat.notesmaker.fragments.NoteFragmentDirections
import com.rifaqat.notesmaker.models.NotesEntity
import com.rifaqat.notesmaker.utils.hideKeyboard
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak

class NotesAdapter:ListAdapter<NotesEntity,NotesAdapter.NotesViewHolder>(DiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notes_sample,parent,false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        getItem(position).let {note ->
            holder.apply {
                parent.transitionName="recyclerView_${note.key}"
                title.text=note.title
                markWon.setMarkdown(description,note.description)
                date.text=note.date
                parent.setCardBackgroundColor(note.colorCode)

                itemView.setOnClickListener{
                    val action = NoteFragmentDirections.actionNoteFragment2ToSaveOrUpdateFragment22(note)
                    val extras = FragmentNavigatorExtras(itemView to "recyclerView_${note.key}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action, extras)
                }
                description.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragment2ToSaveOrUpdateFragment22(note)
                    val extras = FragmentNavigatorExtras(itemView to "recyclerView_${note.key}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action, extras)
                }
            }
        }
    }



    inner class NotesViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val binding=NotesSampleBinding.bind(itemView)
        val title: TextView =binding.title
        val description:TextView=binding.description
        val date:TextView=binding.date
        val parent:CardView=binding.itemSampleParent
        val markWon=Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin(){
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(SoftLineBreak::class.java){visitor,_->visitor.forceNewLine()}
                }
            })
            .build()
    }
}