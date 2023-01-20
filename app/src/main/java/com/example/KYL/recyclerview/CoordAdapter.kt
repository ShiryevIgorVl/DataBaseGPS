package com.example.KYL.recyclerview
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.KYL.R
import com.example.KYL.databinding.CoordListItemBinding
import com.example.KYL.entities.Coordinate


class CoordAdapter(private val listener: Listener) : ListAdapter<Coordinate, CoordAdapter.ItemHolder>(
    ItemComporator()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return holder.setData(getItem(position), position, listener)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CoordListItemBinding.bind(view)

        fun setData(koordinate: Coordinate, position: Int, listener: Listener) = with(binding) {
            tvNameSet.text = koordinate.name
            tvUtsSet.text = koordinate.utsPipe
            tvNumListKipSet.text = (1 + position).toString()
            tvDepthSet.text = koordinate.depthPipe
            tvAccuracySet.text = koordinate.accuracy
            tvUppSet.text = koordinate.uppPipe
            tvCurrentSet.text = koordinate.iPipe
            tvDistanceSet.text = koordinate.distance.toString()

            buttonDel.setOnClickListener {
                listener.onClickDelItem(koordinate.id!!)
            }
        }

        companion object{
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.coord_list_item, parent, false))
            }
        }
    }

    class ItemComporator : DiffUtil.ItemCallback<Coordinate>(){
        override fun areItemsTheSame(oldItem: Coordinate, newItem: Coordinate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Coordinate, newItem: Coordinate): Boolean {
         return oldItem == newItem
        }
    }

    interface Listener{
        fun onClickDelItem(id: Int)
    }


}