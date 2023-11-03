package uz.abdulla.notes.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.abdulla.notes.model.DataNotes
import uz.abdulla.notes.OnItemClickListener
import uz.abdulla.notes.R
import uz.abdulla.notes.database.DatabaseHelper
import uz.abdulla.notes.databinding.CustomDeleteDialogBinding
import uz.abdulla.notes.databinding.RvItemBinding

class RecyclerViewAdapter(val context: Context, private val list: MutableList<DataNotes>, private val onClick: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val databaseHelper = DatabaseHelper(context)


    private lateinit var listener: OnItemClickListener
    inner class ViewHolder(private val binding: RvItemBinding): RecyclerView.ViewHolder(binding.root){
       fun onBind(dataNotes: DataNotes){
           binding.textTitle.text = dataNotes.title
           binding.textDescription.text = dataNotes.description
           binding.root.setOnClickListener{
               onClick(adapterPosition)
           }
           binding.itemBackground.setBackgroundColor(ContextCompat.getColor(context,dataNotes.color))
           binding.createdDate.text = dataNotes.createdDate
           binding.notificationIcon.isChecked = dataNotes.notification != 0
           if (!binding.notificationIcon.isChecked){
               binding.notificationIcon.setOnCheckedChangeListener{ _, isChecked ->
                   listener.onCheckedChange(position = adapterPosition,isChecked = isChecked)
               }
           }
           else
               binding.notificationIcon.setOnCheckedChangeListener(null)
           binding.root.setOnLongClickListener {
               popupMenu(it)
               true
           }
       }

        private fun popupMenu(view: View) {
            val popupMenu = PopupMenu(context,view)
            popupMenu.inflate(R.menu.rv_item_menu)
            val position = adapterPosition

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.blue -> {
                        updatedNote(R.color.blue)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.light_blue -> {
                        updatedNote(R.color.light_blue)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.light_green -> {
                        updatedNote(R.color.light_green)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.orange -> {
                        updatedNote(R.color.orange)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.orange_and_red -> {
                        updatedNote(R.color.orange_and_red)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.pink_and_red -> {
                        updatedNote(R.color.pink_and_red)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.violet -> {
                        updatedNote(R.color.violet)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.yellow -> {
                        updatedNote(R.color.yellow)
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.delete -> {
                        val builder = AlertDialog.Builder(context)
                        val dialogBinding = CustomDeleteDialogBinding.inflate(LayoutInflater.from(context),null,false)
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
                            databaseHelper.deleteNote(list[adapterPosition].id)
                            list.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            dialog.dismiss()
                        }
                        dialog.show()



                    }
                }
                true
            }
            popupMenu.gravity = Gravity.CENTER
            popupMenu.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenu)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java).invoke(menu,true)
        }

        fun updatedNote(color: Int){
            val updatedNote = DataNotes(list[adapterPosition].id,list[adapterPosition].title,list[adapterPosition].description,color,list[adapterPosition].createdDate,list[adapterPosition].notification)
            databaseHelper.updateColor(list[adapterPosition].id,color)
            list[adapterPosition] = updatedNote

        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int{
        return list.size
    }

    fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}