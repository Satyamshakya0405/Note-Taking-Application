package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.Database.Note
import com.example.notes.ViewModels.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),NoteAdapter.OnItemClickListener,SearchView.OnQueryTextListener
{
    private lateinit var mViewModel:NoteViewModel
    val SAVE_NOTE_REQUEST=1
    val EDIT_NOTE_REQUEST=2
    private lateinit var adapter: NoteAdapter
    private lateinit var snackbarLayout: CoordinatorLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        snackbarLayout=findViewById(R.id.snackbar_layout)




            floatingActionButton.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, AddNote::class.java)
                startActivityForResult(intent, SAVE_NOTE_REQUEST)
            })

        // VIEW MODEL
     mViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(NoteViewModel::class.java)

    mViewModel.getAllNote().observe(this) {
        adapter.submitList(it)
    }

        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                    return false
                }
                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {

                    val note=adapter.getNoteAt(viewHolder.adapterPosition)
                    mViewModel.deleteNote(adapter.getNoteAt(viewHolder.adapterPosition))
                    Snackbar.make(snackbarLayout,"Note Is Deleted",Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            mViewModel.addNote(note)
                        }.show()
                }
            }).attachToRecyclerView(recycler_view)


    }
    fun initRecyclerView()
    {
        val staggeredGridLayoutManager=StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
        recycler_view.layoutManager=staggeredGridLayoutManager
        recycler_view.setHasFixedSize(true)
        adapter=NoteAdapter(this)
        recycler_view.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu,menu)
        val search=menu?.findItem(R.id.main_search)
        val searchView=search?.actionView as SearchView
        searchView.isSubmitButtonEnabled=true
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.delete_all->{ mViewModel.deleteAllNote()
                                        return true}
            else  ->return super.onOptionsItemSelected(item)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==SAVE_NOTE_REQUEST&&resultCode== RESULT_OK)
        {
            if(data!=null){
            val title=data.getStringExtra("EXTRA_TITLE")
            val description= data.getStringExtra("EXTRA_DESCRIPTION")
            val priority=data.getIntExtra("EXTRA_PRIORITY", 1)
            val imagePath=data.getStringExtra("EXTRA_IMAGE_PATH")
                val note:Note=Note(0, title, description, priority,imagePath)
                mViewModel.addNote(note)
                Toast.makeText(this, "Note Saved! ", Toast.LENGTH_SHORT).show()
        }
    }else if(requestCode==EDIT_NOTE_REQUEST&&resultCode== RESULT_OK){

            val id=data!!.getIntExtra("EXTRA_ID",-1)
            if(id==-1)
            {
                Toast.makeText(this, "Note not Saved! ", Toast.LENGTH_SHORT).show()
                return
            }

                val title=data.getStringExtra("EXTRA_TITLE")
                val description= data.getStringExtra("EXTRA_DESCRIPTION")
                val priority=data.getIntExtra("EXTRA_PRIORITY", 1)
            val imagePath=data.getStringExtra("EXTRA_IMAGE_PATH")
            val note:Note=Note(id, title, description, priority,imagePath)
                mViewModel.updateNote(note)
                Toast.makeText(this, "Note Updated! ", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this, "Note not Saved!", Toast.LENGTH_SHORT).show()
        }
}


    override fun onItemCLick(note: Note) {

        val intent=Intent(this,AddNote::class.java)
        intent.putExtra("EXTRA_TITLE",note.title)
        intent.putExtra("EXTRA_DESCRIPTION",note.description)
        intent.putExtra("EXTRA_PRIORITY",note.priority)
        intent.putExtra("EXTRA_ID",note.id)
        intent.putExtra("EXTRA_IMAGE_PATH",note.imagePath)
        if(note.imagePath!=null)
        Log.d("SATTU",note.imagePath)
        else   Log.d("SATTU","null")
        startActivityForResult(intent,EDIT_NOTE_REQUEST)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText!=null)
        {
            searchDatabase(newText);
            Log.d("SATYAM",newText)
        }
        return true;
    }
    private fun searchDatabase(query:String)
    {
        val searchquery="%$query%"
        mViewModel.searchDatabase(searchquery).observe(this,{list->
            list.let {
//            for(item in it) println(item.title)
                adapter.submitList(it)
            }

        })
    }

}