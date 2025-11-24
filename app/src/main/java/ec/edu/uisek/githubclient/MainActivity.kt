package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFab()

        // Solo cargar repositorios si no hay un estado guardado (para evitar recargar al rotar)
        if (savedInstanceState == null) {
            fetchRepositories()
        }

        // Escuchar cambios en la pila de fragments para mostrar/ocultar el FAB
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                // CAMBIO 1: fabAddProject -> fabNewTask
                binding.fabNewTask.hide()
            } else {
                // CAMBIO 1: fabAddProject -> fabNewTask
                binding.fabNewTask.show()
            }
        }
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter()
        binding.repoRecyclerView.apply {
            adapter = reposAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupFab() {
        // CAMBIO 1: fabAddProject -> fabNewTask
        binding.fabNewTask.setOnClickListener {
            // CAMBIO 2: NewProjectFragment (ya estaba correcto en tu código)
            val fragment = NewProjectFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchRepositories() {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Error de autenticación. Revisa tu token."
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error: ${response.code()}"
                    }
                    Log.e("MainActivity", errorMsg)
                    showMessage(errorMsg)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                //no hay conexion a red
                Log.e("MainActivity", "Error de conexión", t)
                showMessage("Error de conexión: ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}