package com.example.databasegps.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.databasegps.R
import com.example.databasegps.databinding.KoordListItemBinding
import com.example.databasegps.entities.Koordinate


class KoordAdapter(private val listener: Listener) : ListAdapter<Koordinate, KoordAdapter.ItemHolder>(ItemComporator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return holder.setData(getItem(position), listener)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = KoordListItemBinding.bind(view)

        fun setData(koordinate: Koordinate, listener: Listener) = with(binding) {
            tvNameSet.text = koordinate.name
            tvLatitudeSet.text = koordinate.latitude
            tvNumListKipSet.text = (koordinate.id!! + 1).toString()
            tvLongitudeSet.text = koordinate.longitude
            tvAccuracySet.text = koordinate.accuracy
            tvHeightSet.text = koordinate.height
            tvSpeedSet.text = koordinate.speed

            buttonDel.setOnClickListener {
                listener.onClickDelItem(koordinate.id!!)
            }
        }

        companion object{
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.koord_list_item, parent, false))
            }
        }
    }

    class ItemComporator : DiffUtil.ItemCallback<Koordinate>(){
        override fun areItemsTheSame(oldItem: Koordinate, newItem: Koordinate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Koordinate, newItem: Koordinate): Boolean {
         return oldItem == newItem
        }
    }

    interface Listener{
        fun onClickDelItem(id: Int)
    }


}