package com.example.myfirstapp.core.data.mapper

import com.example.myfirstapp.core.database.entity.CategoryEntity
import com.example.myfirstapp.core.model.Category

fun CategoryEntity.toDomain(): Category =
    Category(
        id = id,
        name = name,
        colorHex = colorHex,
        icon = icon,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
