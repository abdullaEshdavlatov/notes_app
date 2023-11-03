package uz.abdulla.notes.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import uz.abdulla.notes.model.DataNotes
import uz.abdulla.notes.database.DatabaseHelper
import uz.abdulla.notes.R
import uz.abdulla.notes.databinding.CustomDeleteDialogBinding
import uz.abdulla.notes.databinding.FragmentDetailBinding
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val list: MutableList<DataNotes> = ArrayList()
    private lateinit var databaseHelper: DatabaseHelper

    private val arguments by navArgs<DetailFragmentArgs>()
    private var checkInsert:Boolean? = null




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)

        databaseHelper = DatabaseHelper(requireContext())
        initList()

        checkInsert = arguments.checkInsert


        if (!checkInsert!!){
            val position = arguments.position
            binding.textTitle.setText(list[position].title)
            binding.textDescription.setText(list[position].description)
        }
        binding.icSave.setOnClickListener{
           saveNote()
        }
        binding.icDelete.setOnClickListener{
            if (checkInsert!!){
                Toast.makeText(requireContext(), "O'chirib tashlanadigan eslatmalar yuq", Toast.LENGTH_SHORT).show()
            } else{
                val position = arguments.position
                val builder = AlertDialog.Builder(requireContext())
                val dialogBinding = CustomDeleteDialogBinding.inflate(layoutInflater)
                val dialog = builder.create()
                val window = dialog.window
                if (window != null) {
                    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val params: WindowManager.LayoutParams? = window.attributes
                    if (params != null) {
                        params.verticalMargin = 0.05F
                    }
                }
                window?.setGravity(Gravity.BOTTOM)
                dialog.setView(dialogBinding.root)
                dialogBinding.buttonCancel.setOnClickListener{
                    dialog.dismiss()
                }
                dialogBinding.buttonDelete.setOnClickListener{
                    databaseHelper.deleteNote(list[position].id)
                    list.removeAt(position)
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_detailFragment_to_mainFragment)
                }
                dialog.show()
            }

        }
        binding.icBack.setOnClickListener{
            findNavController().navigate(R.id.action_detailFragment_to_mainFragment)
        }
        binding.icShare.setOnClickListener{
            val note = binding.textDescription.text.toString()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT,note)
            val chooser = Intent.createChooser(shareIntent,"Share using...")
            startActivity(chooser)
        }
        

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun initNote(){
        if (binding.textTitle.text.isNotEmpty() && binding.textDescription.text.isNotEmpty()){
            val title = binding.textTitle.text.toString().trim()
            val description = binding.textDescription.text.toString().trim()
            var color: Int
            val random = (0..6).random()
            when(random){
                0 -> color = R.color.orange
                1 -> color = R.color.yellow
                2 -> color = R.color.violet
                3 -> color = R.color.light_green
                4 -> color = R.color.light_blue
                5 -> color = R.color.blue
                6 -> color = R.color.pink_and_red
                else -> color = R.color.orange_and_red
            }
            val calendar = Calendar.getInstance().time
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG).format(calendar).toString()
            val createdDate: String = dateFormat
            databaseHelper.insertNote(title,description,color,createdDate,0)
            checkInsert = false
            Toast.makeText(requireContext(), "Saqlandi", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring", Toast.LENGTH_SHORT).show()
    }
    private fun initList() {
        val cursor = databaseHelper.readNote()
        cursor?.let {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val description = cursor.getString(2)
                    val color = cursor.getInt(3)
                    val createdDate = cursor.getString(4)
                    val notification = cursor.getInt(5)
                    val dataNotes = DataNotes(id, title, description,color,createdDate,notification)
                    list.add(dataNotes)
                }
            }
        }
    }

    fun saveNote(){
        if (checkInsert!!){
            initNote()
        }
        else{
            list.clear()
            initList()
            if (binding.textTitle.text.isNotEmpty() && binding.textDescription.text.isNotEmpty()){
                val position = arguments.position
                val id = list[position].id
                val title = binding.textTitle.text.toString().trim()
                val description = binding.textDescription.text.toString().trim()
                databaseHelper.updateNote(id,title,description)
                Toast.makeText(requireContext(), "Saqlandi", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring", Toast.LENGTH_SHORT).show()

        }
    }

}