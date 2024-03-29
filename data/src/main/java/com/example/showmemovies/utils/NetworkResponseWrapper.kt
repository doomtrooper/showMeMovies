package com.example.showmemovies.utils

import com.google.gson.annotations.SerializedName
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

sealed interface Result<out S: Any, out E: Any>{
    data class Success<S : Any>(val body: S): Result<S, Nothing>
    data class Error<E: Any>(val body: E): Result<Nothing, E>
}

sealed interface NetworkResponseWrapper<out S : Any> {
    data class ServiceError(val errorBody: ErrorBody) : NetworkResponseWrapper<Nothing>
    data class NetworkError(val t: Throwable?) : NetworkResponseWrapper<Nothing>
    data class Success<S : Any>(val body: S) : NetworkResponseWrapper<S>
    data class UnknownError(val t: Throwable?) : NetworkResponseWrapper<Nothing>
}

data class ErrorBody(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_message") val statusMessage: String,
    @SerializedName("success") val success: Boolean
)

class NetworkResponseWrapperCall<S : Any>(
    private val delegate: Call<S>,
    private val errorConverter: Converter<ResponseBody, ErrorBody>
) : Call<NetworkResponseWrapper<S>> {
    override fun clone(): Call<NetworkResponseWrapper<S>> =
        NetworkResponseWrapperCall(delegate, errorConverter)

    override fun execute(): Response<NetworkResponseWrapper<S>> =
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    override fun enqueue(callback: Callback<NetworkResponseWrapper<S>>) {
        delegate.enqueue(object : Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                val body: S? = response.body()
                val code = response.code()
                val error = response.errorBody()
                if (response.isSuccessful && body != null) {
                    callback.onResponse(
                        this@NetworkResponseWrapperCall,
                        Response.success(code, NetworkResponseWrapper.Success(body))
                    )
                } else {
                    val errorBody: ErrorBody? = when {
                        error == null -> null
                        error.contentLength() == 0L -> null
                        else -> try {
                            errorConverter.convert(error)
                        } catch (ex: Exception) {
                            null
                        }
                    }
                    if (errorBody != null) {
                        callback.onResponse(
                            this@NetworkResponseWrapperCall,
                            Response.success(code, NetworkResponseWrapper.ServiceError(errorBody))
                        )
                    } else {
                        callback.onResponse(
                            this@NetworkResponseWrapperCall,
                            Response.success(code, NetworkResponseWrapper.UnknownError(null))
                        )
                    }
                }
            }

            override fun onFailure(call: Call<S>, throwable: Throwable) {
                val networkResponse = when (throwable) {
                    is IOException -> NetworkResponseWrapper.NetworkError(throwable)
                    else -> NetworkResponseWrapper.UnknownError(throwable)
                }
                callback.onResponse(
                    this@NetworkResponseWrapperCall,
                    Response.success(networkResponse)
                )
            }
        })
    }
}

class NetworkResponseWrapperCallAdapter<S : Any>(
    private val successType: Type,
    private val errorConverter: Converter<ResponseBody, ErrorBody>
) : CallAdapter<S, Call<NetworkResponseWrapper<S>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<NetworkResponseWrapper<S>> =
        NetworkResponseWrapperCall(call, errorConverter)

}


/**
 * @GET("users/{id}") suspend fun user(@Path("id") id: Long): User
 * behind the scenes it becomes like fun user(...): Call<User>.
 * So Actually `suspend fun user(): NetworkResponseWrapper<User>` is
 * `fun user(): Call<NetworkResponseWrapper<User>>`
 */
class NetworkResponseWrapperCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }
        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<NetworkResponseWrapper<<Foo>> or Call<NetworkResponseWrapper<out Foo>>"
        }
        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != NetworkResponseWrapper::class.java) {
            return null
        }

        check(responseType is ParameterizedType) {
            "response must be parameterized as  NetworkResponseWrapper<Foo> or NetworkResponseWrapper<out Foo>"
        }

        val successType = getParameterUpperBound(0, responseType)
        val errorBodyConverter =
            retrofit.nextResponseBodyConverter<ErrorBody>(null, ErrorBody::class.java, annotations)
        return NetworkResponseWrapperCallAdapter<Any>(successType, errorBodyConverter)
    }
}

//class NetworkResponseWrapperConvertorFactory : Converter.Factory(){
//    override fun responseBodyConverter(
//        returnType: Type,
//        annotations: Array<out Annotation>,
//        retrofit: Retrofit
//    ): Converter<ResponseBody, *>? {
//        if (Call::class.java == getRawType(returnType)) {
//            return null
//        }
//        check(returnType is ParameterizedType) {
//            "return type must be parameterized as Call<NetworkResponseWrapper<<Foo>> or Call<NetworkResponseWrapper<out Foo>>"
//        }
//        val responseType = getParameterUpperBound(0, returnType)
//        if (getRawType(responseType) != NetworkResponseWrapper::class.java) {
//            return null
//        }
//
//        check(responseType is ParameterizedType) {
//            "response must be parameterized as  NetworkResponseWrapper<Foo> or NetworkResponseWrapper<out Foo>"
//        }
//        return super.responseBodyConverter(re, annotations, retrofit)
//    }
//}


