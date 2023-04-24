package com.example.myapplication

import java.util.*

data class ToDoListItem(
    var id: String? = null, // Manually generated ID field
    var userId : String ? = null,
    var itemName: String? = null,
    var dueDate: String? = null,
    var done: Boolean? = null
)