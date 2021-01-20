package com.example.notes

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.Database.Note
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.note_item.view.*
import java.io.File

class NoteAdapter(
    private val listner:OnItemClickListener
): ListAdapter<Note,NoteAdapter.NoteHolder>(DiffCallback()) {


    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.title.equals(newItem.title)&&
                    oldItem.description.equals(newItem.description)&&
                    oldItem.priority.equals(newItem.priority)&&
                    oldItem.imagePath.equals(newItem.imagePath)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false)

         return NoteHolder(view)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
            val note=getItem(position)
        holder.textview_priority.text=note.priority.toString()
        holder.textview_title.text=note.title.toString()
        holder.textview_description.text=note.description.toString()
        if(note.imagePath!=null) {
            holder.imageview_path.visibility=View.VISIBLE
//            holder.imageview_path.setImageURI(Uri.parse(note.imagePath))
            holder.imageview_path.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
//            Picasso.get().load(File(note.imagePath))
//                .into(holder.imageview_path)
        }

    }

    fun getNoteAt(position:Int):Note
    {
        return getItem(position)
    }

      inner class NoteHolder(itemView: View) :RecyclerView.ViewHolder(itemView) ,View.OnClickListener{
         val textview_priority = itemView.text_view_priority
         val textview_title = itemView.text_view_title
         val textview_description = itemView.text_view_description
          val imageview_path=itemView.image_view_image

          init {
              itemView.setOnClickListener(this)
          }

          override fun onClick(v: View?) {
              val position=adapterPosition;
              if(position!=RecyclerView.NO_POSITION) {
                  val note =  getItem(position)

                  listner.onItemCLick(note)
              }
          }


      }
    interface  OnItemClickListener{
        fun onItemCLick(note: Note)
    }

}