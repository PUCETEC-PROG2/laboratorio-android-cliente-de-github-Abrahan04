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
    private var isEditMode = false
    private var originalRepoName: String? = null
    private var repoOwner: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repoFormBinding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(repoFormBinding.root)

        checkEditMode()

        repoFormBinding.cancelButton.setOnClickListener { finish() }
        repoFormBinding.saveButton.setOnClickListener { saveRepo() }
    }

    private fun checkEditMode() {
        isEditMode = intent.getBooleanExtra("is_edit_mode", false)
        if (isEditMode) {
            originalRepoName = intent.getStringExtra("repo_name")
            val repoDesc = intent.getStringExtra("repo_description")
            repoOwner = intent.getStringExtra("repo_owner")

            repoFormBinding.repoNameInput.setText(originalRepoName)
            repoFormBinding.repoDescriptionInput.setText(repoDesc)

            // Requerimiento: Nombre del proyecto no debe ser editable en edición
            repoFormBinding.repoNameInput.isEnabled = false
            repoFormBinding.saveButton.text = "Actualizar"
            supportActionBar?.title = "Editar Repositorio"
        } else {
            supportActionBar?.title = "Nuevo Repositorio"
        }
    }

    private fun validateForm(): Boolean {
        val repoName = repoFormBinding.repoNameInput.text.toString()
        if (repoName.isBlank()){
            repoFormBinding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }

        if (repoName.contains(" ")){
            repoFormBinding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }
        return true
    }

    private fun saveRepo(){
        if (!validateForm()) {
            return
        }

        val repoName = repoFormBinding.repoNameInput.text.toString()
        val repoDescription = repoFormBinding.repoDescriptionInput.text.toString()

        val repoRequest = RepoRequest(
            name = repoName,
            description = repoDescription
        )

        val apiService = RetrofitClient.gitHubApiService

        if (isEditMode) {
            // Modo Edición: PATCH
            if (originalRepoName != null && repoOwner != null) {
                val call = apiService.updateRepository(repoOwner!!, originalRepoName!!, repoRequest)
                call.enqueue(object : Callback<Repo> {
                    override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                        if (response.isSuccessful) {
                            showMessage("Repositorio actualizado exitosamente")
                            finish()
                        } else {
                            handleError(response.code())
                        }
                    }

                    override fun onFailure(call: Call<Repo>, t: Throwable) {
                        handleFailure(t)
                    }
                })
            } else {
                showMessage("Error: faltan datos del repositorio original")
            }

        } else {
            // Modo Creación: POST
            val call = apiService.addRepository(repoRequest)
            call.enqueue(object : Callback<Repo> {
                override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                    if (response.isSuccessful) {
                        showMessage("El repositorio $repoName se creó exitosamente")
                        finish()
                    } else {
                        handleError(response.code())
                    }
                }

                override fun onFailure(call: Call<Repo>, t: Throwable) {
                    handleFailure(t)
                }
            })
        }
    }

    private fun handleError(code: Int) {
        val errorMsg = when (code) {
            401 -> "No autorizado"
            403 -> "Prohibido"
            404 -> "No encontrado"
            422 -> "Error de validación (¿nombre duplicado?)"
            else -> "Error: $code"
        }
        Log.e("RepoForm", "Error: $errorMsg")
        showMessage(msg = "Error: $errorMsg")
    }

    private fun handleFailure(t: Throwable) {
        Log.e("RepoForm", "Error de red: ${t.message}")
        showMessage("Error de red: ${t.message}")
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}