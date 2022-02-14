package com.example.copernisea.map.data

import com.example.copernisea.map.utils.ApiResult
import com.example.copernisea.map.utils.getResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface FileListService {

    @GET("{dataType}")
    suspend fun getFileLists(@Path("dataType") dataType:String): List<Folder> //There is always one, it's a list because of the structure of the mock server response
}

class FileListDataSource(
    client: Retrofit
) {

    private val service = client.create(FileListService::class.java)

    suspend fun getFileLists(dataType: String): ApiResult<Folder> {
        return withContext(Dispatchers.IO) {
            getResult {
                service
                    .getFileLists(dataType)[0]
            }
        }
    }
}

fun provideFileListDataSource() = FileListDataSource(RetrofitClient.apiClient)