package com.example.myfirstapp.core.domain.mapper

import com.example.myfirstapp.core.model.TodoItem
import com.example.myfirstapp.core.model.TodoSummary

fun TodoItem.toSummary(): TodoSummary =
    TodoSummary(
        id = id,
        title = title,
        isDone = isDone
    )
