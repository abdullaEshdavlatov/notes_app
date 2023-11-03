package uz.abdulla.notes.model

data class DataNotes(
     val id: String,
     val title: String,
     val description: String,
     val color: Int,
     val createdDate: String,
     val notification: Int
)