package com.example.wafflestudio_toyproject

import android.content.SharedPreferences
import com.example.wafflestudio_toyproject.network.LoginRequest
import com.example.wafflestudio_toyproject.network.LoginResponse
import com.example.wafflestudio_toyproject.network.SignupRequest
import com.example.wafflestudio_toyproject.network.SignupResponse
import com.example.wafflestudio_toyproject.network.UserApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: UserApi,
    private val sharedPreferences: SharedPreferences
) {
    fun signup(
        username: String,
        email: String,
        password: String,
        name: String,
        college: Int,
        onSuccess: (SignupResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = SignupRequest(
            userid = username,
            email = email,
            password = password,
            name = name,
            college = college
        )

        api.signup(request).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    } ?: onError("응답이 비어 있습니다.")
                } else {
                    onError("회원가입 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                onError("네트워크 에러: ${t.message}")
            }
        })
    }

    fun login(
        userid: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = LoginRequest(
            userid = userid,
            password = password
        )

        api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    } ?: onError("응답이 비어 있습니다.")
                } else {
                    onError("로그인 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("네트워크 에러: ${t.message}")
            }
        })
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.deleteAccount().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("회원 탈퇴에 실패했습니다. (${response.code()})")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError("네트워크 오류가 발생했습니다.")
            }
        })
    }
}