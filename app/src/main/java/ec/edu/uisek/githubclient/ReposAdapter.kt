package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

// 1. Clase ViewHolder: Contiene las referencias a las vistas d un solo ítem.
class RepoViewHolder(
    private val binding: FragmentRepoItemBinding,
    private val onEditClick: (Repo) -> Unit,
    private val onDeleteClick: (Repo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description ?: "El repositorio no tiene descripción"
        binding.repoLang.text = repo.language ?: "Lenguaje no especificado"
        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.repoOwnerImage)

        binding.editButton.setOnClickListener { onEditClick(repo) }
        binding.deleteButton.setOnClickListener { onDeleteClick(repo) }
    }
}

// 3. Clase Adapter: Gestiona la creación y actualización de los ViewHolders.
class ReposAdapter(
    private val onEditClick: (Repo) -> Unit,
    private val onDeleteClick: (Repo) -> Unit
) : RecyclerView.Adapter<RepoViewHolder>() {

    private var repositories: List<Repo> = emptyList()
    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    fun updateRepositories(newRepos: List<Repo>) {
        repositories = newRepos
        notifyDataSetChanged()
    }
}