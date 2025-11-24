package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName
import retrofit2.http.Url

//vamos a usar llamadas a la api de github

data class RepoOwner(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url") // para que tome en cuenta la convencion sneak_case y no camelCase
    val avatarUrl: String,
)