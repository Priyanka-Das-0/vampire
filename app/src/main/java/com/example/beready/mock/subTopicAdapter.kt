import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beready.databinding.SubCategoryItemBinding
import com.example.beready.mock.subTopic

class subTopicAdapter(
    private val subCategories: List<subTopic?>,  // List can have nullable subTopic
    private val onItemClick: (subTopic?) -> Unit  // Handle nullable subTopic
) : RecyclerView.Adapter<subTopicAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: SubCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subCategory: subTopic?) {
            if (subCategory != null) {
                // Bind data if subCategory is not null
                binding.subCategoryTitle.text = subCategory.setTitle
                binding.root.setOnClickListener {
                    onItemClick(subCategory)
                }
            } else {
                // Handle null case
                binding.subCategoryTitle.text = "Unknown"
                binding.root.setOnClickListener(null)  // Disable click for null items
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SubCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subCategories[position])
    }

    override fun getItemCount(): Int = subCategories.size
}
