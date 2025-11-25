package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Inicializamos el adapter pasando funciones lambd
    private val reposAdapter = ReposAdapter(
        onEditClick = { repo -> onEditRepo(repo) },
        onDeleteClick = { repo -> onDeleteRepo(repo) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        binding.repoRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null) { // Quitamos isNotEmpty() para que limpie la lista si se borran todos
                        reposAdapter.updateRepositories(repos)
                    } else {
                        reposAdapter.updateRepositories(emptyList())
                        showMessage(msg = "Usted no tiene repositorios")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error: ${response.code()}"
                    }
                    Log.e("MainActivity", "Error: $errorMsg")
                    showMessage(msg = "Error: $errorMsg")
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage(msg = "Error: Error de Conexión")
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })
    }

    private fun onEditRepo(repo: Repo) {
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra("repo_name", repo.name)
            putExtra("repo_description", repo.description)
            putExtra("repo_owner", repo.owner.login)
            putExtra("is_edit_mode", true)
        }
        startActivity(intent)
    }

    private fun onDeleteRepo(repo: Repo) {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.deleteRepository(repo.owner.login, repo.name)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio ${repo.name} eliminado")
                    fetchRepositories() // Refrescar lista
                } else {
                    showMessage("Error al eliminar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Error de conexión al eliminar")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun displayNewRepoForm(){
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }
}