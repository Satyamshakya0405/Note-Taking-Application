package com.example.notes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.notes.Database.Note
import com.example.notes.Database.NoteDatabase
import com.example.notes.ViewModels.AddNoteViewModel
import com.example.notes.ViewModels.NoteViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_PERMISSION_CODE = 0;
const val REQUEST_IMAGE_SELECT_CODE = 1;
const val REQUEST_TAKE_PHOTO_CODE = 2;

class AddNote : AppCompatActivity() {

    private lateinit var photoFile: File
    private lateinit var imageFile: File
    private var imagePath:String?=null
    private lateinit var addNoteViewModel:AddNoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        addNoteViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(AddNoteViewModel::class.java)
        number_picker.minValue = 1
        number_picker.maxValue = 10
        if (intent.hasExtra("EXTRA_ID")) {
            supportActionBar?.title = "Edit Note"
            et_title.text =
                Editable.Factory.getInstance().newEditable(intent.getStringExtra("EXTRA_TITLE"))
            et_description.text = Editable.Factory.getInstance()
                .newEditable(intent.getStringExtra("EXTRA_DESCRIPTION"))
            number_picker.value = intent.getIntExtra("EXTRA_PRIORITY", 1)
            if(intent.getStringExtra("EXTRA_IMAGE_PATH")!=null)
            {
                action_image.visibility=View.VISIBLE
                val uri=intent.getStringExtra("EXTRA_IMAGE_PATH")
                Picasso.get().load(File(uri)).into(action_image)
            }
        } else {
            supportActionBar?.title = "Add Note"
        }
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)


        take_photo.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile()
            val fileProvider = FileProvider.getUriForFile(
                this,
                "com.example.notes.fileProvider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(this.packageManager) != null)
                startActivityForResult(intent, REQUEST_TAKE_PHOTO_CODE)
        }
        gallery_image.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@AddNote,
                    Array(1) { Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_PERMISSION_CODE
                )
            } else
                selectImage()


        }
        share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, et_description.text.toString())
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

    }



override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    if (requestCode == REQUEST_TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {

        val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
//         action_image.visibility=View.VISIBLE
        val clickedImageUri = Uri.fromFile(photoFile)
        imagePath=getPathFromUri(clickedImageUri)
            photoFile = File(imagePath)
        action_image.visibility=View.VISIBLE
            Picasso.get().load(photoFile).into(action_image)

    }
    else if(requestCode== REQUEST_IMAGE_SELECT_CODE&&resultCode==Activity.RESULT_OK)
    {
            val selectedImageUri=data?.data
            if(selectedImageUri!=null) {
                try {
                    imagePath=getPathFromUri(selectedImageUri)
                    imageFile = File(imagePath)
                    action_image.visibility=View.VISIBLE
                    Picasso.get().load(imageFile).into(action_image)
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG)
                }

            }
    }else
        super.onActivityResult(requestCode, resultCode, data)


}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode== REQUEST_PERMISSION_CODE&&grantResults.isNotEmpty())
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                selectImage()
            }
            else
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG)
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.add_note_menu, menu)
    return true
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {

    return when (item.itemId) {
        R.id.save_note -> {
            savenote()
            true
        }
        else -> super.onOptionsItemSelected(item)

    }
}

private fun savenote() {
    val title = et_title.text.toString()
    val description = et_description.text.toString()
    val priority = number_picker.value
    if (title.trim().isEmpty() || description.trim().isEmpty()) {
        Toast.makeText(this, "Please Enter Title or Description", Toast.LENGTH_SHORT).show()
        return
    }
//        val note=Note(0,title,description ,priority)
    val intent = Intent()
    intent.putExtra("EXTRA_TITLE", title)
    intent.putExtra("EXTRA_DESCRIPTION", description)
    intent.putExtra("EXTRA_PRIORITY", priority)

    val id = getIntent().getIntExtra("EXTRA_ID", -1)
    Log.d("this", id.toString())
    if (id != -1) {
        intent.putExtra("EXTRA_ID", id)
    }
    if(imagePath==null&&getIntent().getStringExtra("EXTRA_IMAGE_PATH")!=null)
        imagePath=getIntent().getStringExtra("EXTRA_IMAGE_PATH")
    intent.putExtra("EXTRA_IMAGE_PATH",imagePath)

    if(imagePath!=null) Log.d("SATTU",imagePath)
    else Log.d("SATTU","NULL IMAGE PATH")
    setResult(RESULT_OK, intent)
    finish()
}

private fun getPhotoFile(): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

private fun getPathFromUri(uri: Uri): String {
    val filePath: String
    val cursor = contentResolver.query(uri, null, null, null)
    if (cursor == null) {
        filePath = uri.path.toString()
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex("_data")
        filePath = cursor.getString(idx)
        cursor.close()
    }

    return filePath
}

private fun selectImage() {

    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    if (intent.resolveActivity(this.packageManager) != null) {
        startActivityForResult(intent, REQUEST_IMAGE_SELECT_CODE)
    }
}
}

