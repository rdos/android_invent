package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.smartro.inventory.core.LoginEntity
import ru.smartro.inventory.R

import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import ru.smartro.inventory.BuildConfig
import ru.smartro.inventory.Inull
import ru.smartro.inventory.base.AbstrActF
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.HttpErrorBody
import ru.smartro.inventory.core.LoginRequest
import ru.smartro.inventory.database.Config
import ru.smartro.inventory.showErrorToast

class LoginFragment : AbstrActF(){

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        return view
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
        showHideActionBar(true)
//        baseview.setOnClickListener {
//            MyUtil.hideKeyboard(this)
//        }
        val tilLogin = view.findViewById<TextInputLayout>(R.id.til_login_fragment__login)
        val tietLogin = view.findViewById<TextInputEditText>(R.id.tiet_login_fragment__login)

        val tilPassword = view.findViewById<TextInputLayout>(R.id.til_login_fragment__password)
        val tietPassword = view.findViewById<TextInputEditText>(R.id.tiet_login_fragment__password)

        val actvVersion = view.findViewById<AppCompatTextView>(R.id.actv_login_fragment__version)
        actvVersion.text = BuildConfig.VERSION_NAME

        val actvAttempts = view.findViewById<AppCompatTextView>(R.id.actv__login_fragment__attempts)
        val currentAttempts = db().loadConfigInt("LoginAttempts")
        when (currentAttempts) {
            Inull -> {
                actvAttempts?.visibility = View.GONE
            }
                in 1..4 -> {
                actvAttempts?.text = "Осталось попыток входа: ${5 - currentAttempts}"
            }
                else -> {
                actvAttempts?.text = getString(R.string.user_is_blocked)
            }
        }

        val acbLogin = view.findViewById<AppCompatButton>(R.id.acb_login_fragment)
        acbLogin.setOnClickListener{
            log.debug("acbLogin.before")
            acbLogin.isEnabled = false
            try {
                if (isNotCheckedData(tietLogin, tilLogin)) return@setOnClickListener
                if (isNotCheckedData(tietPassword, tilPassword)) return@setOnClickListener
                log.debug("acbLogin.after isCheckedData")
                hideKeyboard()
                val loginEntity = LoginEntity(tietLogin.text.toString(), tietPassword.text.toString())
                val restClient = RestClient()
                val loginRequest = LoginRequest(restClient)
                val con = loginRequest.callAsync(loginEntity)
                con.observe(
                    viewLifecycleOwner
                ) { response ->
                    when {
                        response.isLoading -> {
                             // show progress)))
                        }
                        response.isLoggedIn -> {
                            val configLoginAttempts = Config("LoginAttempts", Inull.toString())
                            db().saveConfig(configLoginAttempts)

                            val configOwnerDate = Config("OwnerDate",  getCurrentTimeSEC().toString())
                            db().saveConfig(configOwnerDate)

                            showFragment(OwnerFragment.newInstance())
                        }
                        response.error != null -> {
                            val code = response.error!!.first
                            try {
                                if(code == 422) {
                                    val dbAttempts = db().loadConfigInt("LoginAttempts")
                                    val newAttempts = if (dbAttempts != Inull) dbAttempts + 1 else 1
                                    db().saveConfig(Config("LoginAttempts", newAttempts.toString()))

                                    when (newAttempts) {
                                        in 1..4 -> {
                                            actvAttempts?.visibility = View.VISIBLE
                                            actvAttempts?.text =
                                                "Осталось попыток входа: ${5 - newAttempts}"
                                        }
                                        else -> {
                                            actvAttempts?.visibility = View.VISIBLE
                                            actvAttempts?.text = getString(R.string.user_is_blocked)
                                        }
                                    }
                                }
                                val errBOdy = response.error!!.second
                                log.debug("errBOdy=${errBOdy}")

                                val builder = GsonBuilder()
                                val gson = builder.create()
                                val responseObj = gson.fromJson(errBOdy, HttpErrorBody::class.java)

                                log.debug("responseObj.message=${responseObj.message}")
                                showErrorToast(responseObj.message)
                            } catch (e: Exception) {
                                val messageShowForUser = "Ошибка преобразования данных.\nПожалуйста, перезагрузите приложение"
                                showErrorToast(messageShowForUser)
                            }
                        }
                    }
                }
            } finally {
                acbLogin.isEnabled = true
            }
        }

        if (BuildConfig.BUILD_TYPE != "PROD") {
            acbLogin.setOnLongClickListener {
                log.debug("acbLogin.setOnLongClickListener")
                tietLogin.setText("admin@smartro.ru")
                tietPassword.setText("xot1ieG5ro~hoa,ng4Sh")
                //        tietPassword.setText("")
                //        tietLogin.setText("")
                true
            }
        }
    }



    class LoginViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}
