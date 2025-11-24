package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var repoFormBinding: ActivityRepoFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repoFormBinding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(repoFormBinding.root)

        repoFormBinding.cancelButton.setOnClickListener { finish() }
        repoFormBinding.saveButton.setOnClickListener { createRepo() }
    }

    private fun validateForm(): Boolean {
        val repoName = repoFormBinding.repoNameInput.text.toString()

        if (repoName.isBlank()) {
            repoFormBinding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }

        if (repoName.contains(" ")) {
            repoFormBinding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }

        return true
    }

    private fun createRepo() {
        if (!validateForm()) {
            return
        }

        val repoName = repoFormBinding.repoNameInput.text.toString()
        val repoDescription = repoFormBinding.repoDescriptionInput.text.toString()

        val repoRequest = RepoRequest(
            name = repoName,
            description = repoDescription
        )

        val apiService = RetrofitClient.githubApiService
        val call = apiService.addRepository(repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado correctamente")
                    finish()
                } else {
                    showMessage("Error al crear el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Error de red: ${t.message}")
                Log.e("RepoForm", "Error de red: ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}