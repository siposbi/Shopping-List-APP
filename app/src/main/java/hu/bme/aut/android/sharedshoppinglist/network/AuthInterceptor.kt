package hu.bme.aut.android.sharedshoppinglist.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add auth token to requests
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}

//private val Response.retryCount: Int
//    get() {
//        var currentResponse = priorResponse
//        var result = 0
//        while (currentResponse != null) {
//            result++
//            currentResponse = currentResponse.priorResponse
//        }
//        return result
//    }
//
//class TokenRefreshAuthenticator(val tokenSource: SessionManager): Authenticator {
//    lateinit var client: ShoppingListClient
//
//    override fun authenticate(route: Route?, response: Response): Request? = when {
//        response.retryCount > 2 -> null
//        else -> response.createSignedRequest()
//    }
//
//    private fun Response.createSignedRequest(): Request? = try {
//        val accessToken = authenticationRepository.fetchFreshAccessToken()
//        request.signWithToken(accessToken)
//    } catch (error: Throwable) {
//        null
//    }
//}