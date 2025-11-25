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
    private val reposAdapter = ReposAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        // Llamamos a la API
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        binding.repoRecyclerView?.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val apiService = RetrofitClient.githubApiService
        val call = apiService.getRepos()

        Toast.makeText(this, "Conectando a GitHub...", Toast.LENGTH_SHORT).show()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (!repos.isNullOrEmpty()) {
                        reposAdapter.updateRepositories(repos)
                        showMessage("¡Éxito! Se cargaron ${repos.size} repositorios.")
                    } else {
                        showMessage("Conexión exitosa, pero no tienes repositorios para mostrar.")
                    }
                } else {
                    // Manejo detallado de errores
                    val errorMsg = when (response.code()) {
                        401 -> "Error 401: Token inválido o expirado. Revisa local.properties."
                        403 -> "Error 403: Acceso denegado (¿Token sin permisos de repo?)."
                        404 -> "Error 404: No encontrado."
                        else -> "Error del servidor: ${response.code()}"
                    }
                    Log.e("MainActivity", "Error API: $errorMsg")
                    showMessage(errorMsg)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                val mensaje = "Fallo de conexión: ${t.localizedMessage}"
                Log.e("MainActivity", mensaje)
                showMessage(mensaje)
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }
}