package id.pemudakreatif.storyapp_pandupratama

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiService {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<ApiResponse>

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

//    @GET("stories")
//    suspend fun getAllStories(
//        @Header("Authorization") token: String,
//        @Query("page") page: Int = 1,
//        @Query("size") size: Int = 10,
//        @Query("location") location: Int = 0
//    ): Response<StoryResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoryResponse>

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") storyId: String,
        @Header("Authorization") token: String
    ): Response<StoryDetailResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Response<ApiResponse>
}
object ApiClient {
    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
        }.build())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
