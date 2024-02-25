package com.mz.cannainfinity.model

import androidx.compose.ui.graphics.Color
import com.mz.cannainfinity.R
import com.mz.cannainfinity.ui.theme.NeutralColor

enum class CannaType(
    val icon: Int,
    val contentColor: Color,
    val containerColor: Color
){
    Seedling(
        icon = R.drawable.ic_launcher_background,
        contentColor = Color.Black ,
        containerColor = NeutralColor
    ),
    Vegetative(
        icon = R.drawable.ic_launcher_background,
        contentColor = Color.Green ,
        containerColor = NeutralColor
    ),
    Flowering(
        icon = R.drawable.ic_launcher_background,
        contentColor = Color.Blue ,
        containerColor = NeutralColor
    ),
    Curing(
        icon = R.drawable.ic_launcher_background,
        contentColor = Color.Gray ,
        containerColor = NeutralColor
    ),

}
