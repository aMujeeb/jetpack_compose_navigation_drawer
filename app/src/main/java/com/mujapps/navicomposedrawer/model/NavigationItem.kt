package com.mujapps.navicomposedrawer.model

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(val title: String, val selectedIcon: ImageVector, val unSelectedIcon: ImageVector, val badgeCount: Int? = null)
