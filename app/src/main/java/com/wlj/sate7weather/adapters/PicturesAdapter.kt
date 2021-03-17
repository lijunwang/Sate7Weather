package com.wlj.sate7weather.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.luck.picture.lib.entity.LocalMedia
import com.wlj.sate7weather.R
import com.wlj.sate7weather.databinding.SelectedPhotoItemBinding
import com.wlj.sate7weather.utils.log

internal const val MAX_SIZE = 8
private const val TYPE_ADD = 0
private const val TYPE_PICTURE = 1
interface AddPictureClickListener{
    fun onAddPictureClicked()
}
class SelectedPictureHolder(val binding: SelectedPhotoItemBinding):RecyclerView.ViewHolder(binding.root)
class PicturesAdapter(val context:Context, private val picturesList:ArrayList<LocalMedia?>): RecyclerView.Adapter<SelectedPictureHolder>() {
    private var addClickListener:AddPictureClickListener? = null
    fun setAddClicked(addPictureClickListener: AddPictureClickListener){
        addClickListener = addPictureClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedPictureHolder {
        return SelectedPictureHolder(SelectedPhotoItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }
    override fun onBindViewHolder(holder: SelectedPictureHolder, position: Int) {

        if(getItemViewType(position) == TYPE_ADD){
            holder.binding.selectedPicture.setImageResource(R.drawable.ic_add_image)
            holder.binding.selectedDelete.visibility = View.GONE
            holder.binding.root.setOnClickListener {
                log("addPictureClicked ....")
                addClickListener?.onAddPictureClicked()
            }
        }else{
            Glide.with(context).load(picturesList[position]?.path).into(holder.binding.selectedPicture)
            holder.binding.selectedDelete.setOnClickListener {
                picturesList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(isAddPosition(position)){
            TYPE_ADD
        }else{
            TYPE_PICTURE
        }
    }

    private fun isAddPosition(position:Int): Boolean {
        return picturesList.size == position
    }

    override fun getItemCount(): Int {
        log("getItemCount ... ${picturesList.size}")
        return if(picturesList.size>= MAX_SIZE){
            picturesList.size
        }else{
            picturesList.size + 1
        }
    }

    fun update(pictures:List<LocalMedia?>,append:Boolean){
        log("picture adapter update ${pictures.size}")
        if(append){
            picturesList.addAll(pictures)
        }else{
            picturesList.clear()
            picturesList.addAll(pictures)
        }
        notifyDataSetChanged()
    }

}