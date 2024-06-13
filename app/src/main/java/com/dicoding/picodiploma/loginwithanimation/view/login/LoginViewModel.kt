import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.remote.data.LoginResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.login(email, password)
                _loginResult.value = Result.success(response)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            try {
                repository.saveSession(user)
            } catch (e: Exception) {
            }
        }
    }

    fun getSession(): LiveData<Result<UserModel>> {
        val userLiveData = MutableLiveData<Result<UserModel>>()
        viewModelScope.launch {
            try {
                val user = repository.getSession().first()
                userLiveData.postValue(Result.success(user))
            } catch (e: Exception) {
                userLiveData.postValue(Result.failure(e))
            }
        }
        return userLiveData
    }
}
