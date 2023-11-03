package uz.abdulla.notes.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import uz.abdulla.notes.services.AlarmReceiver
import uz.abdulla.notes.model.DataNotes
import uz.abdulla.notes.database.DatabaseHelper
import uz.abdulla.notes.OnItemClickListener
import uz.abdulla.notes.R
import uz.abdulla.notes.adapters.RecyclerViewAdapter
import uz.abdulla.notes.databinding.FragmentSearchBinding
import uz.abdulla.notes.databinding.NotificationSetDialogBinding
import java.util.ArrayList
import java.util.Calendar


class SearchFragment : Fragment(R.layout.fragment_search), OnItemClickListener,
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val allNotesList: MutableList<DataNotes> = ArrayList()
    private var listForSearch: MutableList<DataNotes> = ArrayList()
    private lateinit var databaseHelper: DatabaseHelper
    private var adapter: RecyclerViewAdapter? = null

    private var dialogBinding: NotificationSetDialogBinding? = null

    private var dateAndTime = Calendar.getInstance()
    private var isDateSet = false
    private var isTimeSet = false

    private var alarmReceiver = AlarmReceiver()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        databaseHelper = DatabaseHelper(requireActivity())
        initList()
        val adapter = RecyclerViewAdapter(requireContext(), listForSearch) { position ->
            var position1 = position
            for (i in allNotesList.indices){
                if (listForSearch[position].id == allNotesList[i].id)
                    position1 = i
            }
            val directions = SearchFragmentDirections.actionSearchFragmentToDetailFragment(position1, false)
            findNavController().navigate(directions)
        }

//        binding.editTextSearch.setOnEditorActionListener(){_,actionId,_ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH){
//                listForSearch.clear()
//                if (binding.editTextSearch.text.isNotEmpty()){
//                    val searchedText = binding.editTextSearch.text.toString()
//                    listForSearch.addAll(
//                        allNotesList.filter { note ->
//                            note.title.contains(searchedText)
//                        }
//                    )
//                }
//                adapter.notifyDataSetChanged()
//            }
//            true
//
//        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(searchedText: Editable?) {
                listForSearch.clear()
                if (searchedText!!.isNotEmpty()){
                    binding.editTextCleaner.visibility = View.VISIBLE
                    listForSearch.addAll(
                        allNotesList.filter { note ->
                            note.title.contains(searchedText)
                        }
                    )
                }
                else
                    binding.editTextCleaner.visibility = View.INVISIBLE
                adapter.notifyDataSetChanged()
            }

        })

        binding.recyclerView.adapter = adapter
        adapter?.setOnClickListener(this)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        adapter?.notifyDataSetChanged()

        binding.editTextCleaner.setOnClickListener{
            binding.editTextSearch.text.clear()
        }

        binding.icBack.setOnClickListener{
            findNavController().navigate(R.id.action_searchFragment_to_mainFragment)
        }


    }

    private fun initList() {
        allNotesList.clear()
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
                    val dataNotes = DataNotes(id, title, description, color, createdDate,notification)
                    allNotesList.add(dataNotes)
                }
            }
        }
    }


    override fun onCheckedChange(isChecked: Boolean, position: Int) {
        if (isChecked) {
            val id = listForSearch[position].id

            val builder = AlertDialog.Builder(requireContext())
            dialogBinding = NotificationSetDialogBinding.inflate(layoutInflater)
            val dialog = builder.create()
            dialogBinding?.let {

                dialog.setView(dialogBinding?.root)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                it.icCalendar.setOnClickListener {
                    val dialog = DatePickerDialog(
                        requireContext(), this,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    )
                    dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
                    dialog.show()

                }
                it.icTime.setOnClickListener {
                    TimePickerDialog(
                        requireContext(), this,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        false
                    ).show()

                }
                dialog.show()
            }

            dialogBinding?.buttonSave?.setOnClickListener {
                if (isDateSet && isTimeSet) {
                    Toast.makeText(requireContext(), "Your alarm is set", Toast.LENGTH_SHORT).show()
                    databaseHelper.updateNote(id,1)
                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(requireContext(), AlarmReceiver::class.java)
                    intent.putExtra("id",id)
                    Log.i("TTT", "onCheckedChange: $position")
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireActivity().applicationContext, id.toInt(), intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        dateAndTime.timeInMillis,
                        pendingIntent
                    )
                    dialog.dismiss()
                }
            }


        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        isDateSet = true
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, month)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setDate()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        isTimeSet = true

        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        dateAndTime.set(Calendar.MINUTE, minute)
        setTime()
    }

    fun setDate() {
        dialogBinding?.inputDate?.setText(
            DateUtils.formatDateTime(
                requireContext(),
                dateAndTime.timeInMillis,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )
        )
    }

    fun setTime() {
        dialogBinding?.inputTime?.setText(
            DateUtils.formatDateTime(
                requireContext(),
                dateAndTime.timeInMillis,
                DateUtils.FORMAT_SHOW_TIME
            )
        )
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("alarm_receiver")
        requireActivity().registerReceiver(alarmReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(alarmReceiver)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}