package ar.edu.unlam.mobile.scaffolding.data.datasources

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(
        data: T? = null,
    ) : Resource<T>(data)

    class Error<T>(
        data: T? = null,
        message: String,
    ) : Resource<T>(data, message)
}