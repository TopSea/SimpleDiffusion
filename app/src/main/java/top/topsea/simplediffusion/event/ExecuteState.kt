package top.topsea.simplediffusion.event

import top.topsea.simplediffusion.api.dto.RequestErrorDetail

sealed class ExecuteState {
    data class InProgress(val success: Boolean) : ExecuteState()
    data class ExecuteSuccess(val success: Boolean) : ExecuteState()
    data class ExecuteError(val throwable: Throwable) : ExecuteState()
}


sealed class RequestState<T> {
    data class OnAppError<T>(val errorStr: String): RequestState<T>()
    data class OnRequestError<T>(val error: String): RequestState<T>()
    data class OnRequestSuccess<T>(val success: T): RequestState<T>()
    data class OnRequestFailure<T>(val error: String): RequestState<T>()
}
