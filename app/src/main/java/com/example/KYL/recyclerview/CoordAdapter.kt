package com.example.KYL.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.KYL.R
import com.example.KYL.databinding.CoordListItemBinding
import com.example.KYL.entities.Coordinate
import java.util.Collections


class CoordAdapter(private val listener: Listener) :
    RecyclerView.Adapter<CoordAdapter.ItemHolder>(), ItemTouchHelperAdapter {

    private var coordList = emptyList<Coordinate>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return coordList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return holder.setData(coordList[position], position, listener)
    }

    fun getData(): List<Coordinate>{
        return coordList
    }

    fun setItem(newCoordList: MutableList<Coordinate>){
        this.coordList = newCoordList
        notifyDataSetChanged()
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
            tvNKIP.text = koordinate.operationalnumberKIP
            tvOperKM.text = koordinate.operationalKM

            buttonDel.setOnClickListener {
                listener.onClickDelItem(position)
            }

            cdKoordinate.setOnClickListener {
                listener.onClickCoordinate(koordinate)
            }
        }

        companion object{
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.coord_list_item, parent, false))
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        // перемещаем элементы в списке
        coordList?.let { Collections.swap(it, fromPosition, toPosition) }
        // обновляем RecyclerView
        notifyItemMoved(fromPosition, toPosition)

    }
 //Удаление элемента из списка по свайпу влево или вправо
    override fun onItemDismiss(position: Int) {
        // удаляем элемент из списка
        listener.onClickDelItem(position)
        // обновляем RecyclerView
        notifyItemRemoved(position)
    }

    interface Listener {
        fun onClickDelItem(id: Int)
        fun onClickCoordinate(koordinate: Coordinate)
    }
}