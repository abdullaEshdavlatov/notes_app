package uz.abdulla.notes.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.navigation.NavigationView
import uz.abdulla.notes.*
import uz.abdulla.notes.adapters.RecyclerViewAdapter
import uz.abdulla.notes.database.DatabaseHelper
import uz.abdulla.notes.databinding.FragmentMainBinding
import uz.abdulla.notes.databinding.NotificationSetDialogBinding
import uz.abdulla.notes.model.DataNotes
import uz.abdulla.notes.services.AlarmReceiver
import java.util.Calendar


class MainFragment : Fragment(R.layout.fragment_main), OnItemClickListener, OnDateSetListener,
    OnTimeSetListener, NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val list: MutableList<DataNotes> = ArrayList()
    private lateinit var databaseHelper: DatabaseHelper

    private var dialogBinding: NotificationSetDialogBinding? = null

    private var dateAndTime = Calendar.getInstance()
    private var isDateSet = false
    private var isTimeSet = false

    private var alarmReceiver = AlarmReceiver()




    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        databaseHelper = DatabaseHelper(requireActivity())
        initList()


        val adapter = RecyclerViewAdapter(requireContext(), list) { position ->
            val directions =
                MainFragmentDirections.actionMainFragmentToDetailFragment(position, false)
            findNavController().navigate(directions)
        }
        binding.recyclerView.adapter = adapter
        adapter.setOnClickListener(this)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        adapter.notifyDataSetChanged()


        binding.buttonEdit.setOnClickListener {
            val directions = MainFragmentDirections.actionMainFragmentToDetailFragment(list.size, true)
            findNavController().navigate(directions)
        }

        binding.icSearch.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }

        binding.icMenu.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.open()
            else
                binding.drawerLayout.close()
        }
        binding.navigationView.setNavigationItemSelectedListener(this)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    this.isEnabled = false
                    requireActivity().onBackPressed()
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


    }

    private fun initList() {
        list.clear()
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
                    list.add(dataNotes)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCheckedChange(isChecked: Boolean, position: Int) {
        if (isChecked) {
            val id = list[position].id

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
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100)
                }
                else{
                    saveNotification(id,position,dialog)
                }
            }


        }
    }

    private fun saveNotification(id: String,position: Int, dialog: AlertDialog){
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.all_notes -> {
                binding.drawerLayout.close()
            }
            R.id.setting -> {
                findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                binding.drawerLayout.close()
            }
            R.id.about -> {
                findNavController().navigate(R.id.action_mainFragment_to_aboutNoteFragment)
                binding.drawerLayout.close()
            }
            R.id.exit -> {
                activity?.finish()
                binding.drawerLayout.close()
            }
        }
        return true
    }




}