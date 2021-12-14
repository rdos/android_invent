package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import ru.smartro.inventory.BuildConfig
import ru.smartro.inventory.base.AbstrActF
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.LoginRequest
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

        val acbLogin = view.findViewById<AppCompatButton>(R.id.acb_login_fragment)
        acbLogin.setOnClickListener{
            log.debug("acbLogin.before")
            acbLogin.isEnabled = false
            try {
                if (isNotCheckedData(tietLogin, tilLogin)) return@setOnClickListener
                if (isNotCheckedData(tietPassword, tilPassword)) return@setOnClickListener
                log.debug("acbLogin.after isCheckedData")
                hideKeyboard()
                val loginEntity =
                    LoginEntity(tietLogin.text.toString(), tietPassword.text.toString())
                val restClient = RestClient()
                val loginRequest = LoginRequest(restClient)
                val con = loginRequest.callAsync(loginEntity)
                con.observe(
                    viewLifecycleOwner,
                    { resultBoolean ->
                        if (resultBoolean) {
                            showFragment(OwnerFragment.newInstance())
                        } else {
                            showErrorToast("Ошибка авторизации")
                        }
                    }
                )
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
