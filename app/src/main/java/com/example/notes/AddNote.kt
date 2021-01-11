package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.NumberPicker
import android.widget.Toast
import com.example.notes.Database.Note
import com.example.notes.Database.NoteDatabase
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNote : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        number_picker.minValue=1
        number_picker.maxValue=10
                if(intent.hasExtra("EXTRA_ID")) {
//                    Log.d("this","${et_description.text} + ${et_title.text}")
                    supportActionBar?.title = "Edit Note"
                    et_title.text= Editable.Factory.getInstance().newEditable(intent.getStringExtra("EXTRA_TITLE"))
                    et_description.text=Editable.Factory.getInstance().newEditable(intent.getStringExtra("EXTRA_DESCRIPTION"))
                    number_picker.value=intent.getIntExtra("EXTRA_PRIORITY",1)
//                    Log.d("this","${et_description.text} + ${et_title.text}")
//                    Toast.makeText(this, "${et_description.text} + ${et_title.text}", Toast.LENGTH_SHORT).show()
                }else {
                    supportActionBar?.title = "Add Note"
                }
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cancel)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val menuInflater=MenuInflater(this)
        menuInflater.inflate(R.menu.add_note_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

       when(item.itemId)
       {
           R.id.save_note->{
                            savenote()
                            return true }
           else ->   return super.onOptionsItemSelected(item)


       }
    }

    private fun savenote() {
       val title=et_title.text.toString()
        val description=et_description.text.toString()
        val priority=number_picker.value
        if(title.trim().isEmpty()||description.trim().isEmpty())
        {
            Toast.makeText(this, "Please Enter Title or Description", Toast.LENGTH_SHORT).show()
            return
        }
//        val note=Note(0,title,description ,priority)
            val intent=Intent()
            intent.putExtra("EXTRA_TITLE",title)
            intent.putExtra("EXTRA_DESCRIPTION",description)
            intent.putExtra("EXTRA_PRIORITY",priority)
            val id=getIntent().getIntExtra("EXTRA_ID",-1)
        Log.d("this",id.toString())
            if(id!=-1) {
                intent.putExtra("EXTRA_ID", id)
            }
            setResult(RESULT_OK,intent)
            finish()
    }
}

