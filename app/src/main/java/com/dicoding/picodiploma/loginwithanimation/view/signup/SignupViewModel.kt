import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.remote.data.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.remote.data.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun register(name: String, email: String, password: String, onResult: (RegisterResponse) -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = userRepository.register(name, email, password)
                onResult(response)
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                onResult(RegisterResponse(true, errorBody.message ?: "Unknown error"))
            } catch (e: Exception) {
                onResult(RegisterResponse(true, e.message ?: "Unknown error"))
            } finally {
                _loading.value = false
            }
        }
    }
}
