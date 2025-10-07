package com.example.KYL.recyclerview

import com.example.KYL.entities.Coordinate

interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemDismiss(position: Int)
}