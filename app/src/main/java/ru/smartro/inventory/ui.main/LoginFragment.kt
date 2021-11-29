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
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.LoginRequest


class LoginFragment : AbstractFragment(){

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



        val acbLogin = view.findViewById<AppCompatButton>(R.id.acb_login_fragment)
        acbLogin.setOnClickListener{
            log.debug("acbLogin.before")
            acbLogin.isEnabled = false
            try {
                if (isNotCheckedData(tietLogin)) return@setOnClickListener
                if (isNotCheckedData(tietPassword)) return@setOnClickListener
                log.debug("acbLogin.after isCheckedData")
                val loginEntity =
                    LoginEntity(tietLogin.text.toString(), tietPassword.text.toString())
                val restClient = RestClient()
                val loginRequest = LoginRequest(restClient)
                val con = loginRequest.callAsync(loginEntity)
                con.observe(
                    viewLifecycleOwner,
                    { configEntity ->
                        //                cats?.let {
                        //                    Log.d(TAG, "mafka: 0)ne ${it.size}")
                        //                    photoRecyclerView.adapter = PhotoAdapter(it, context)
                        //                }
                        log.info(configEntity.toString())
                        showFragment(OwnerFragment.newInstance())
                    }
                )
            } finally {
                acbLogin.isEnabled = true
            }
        }
        acbLogin.setOnLongClickListener {
            log.debug("acbLogin.setOnLongClickListener")
            Log.w("AAA", "acbLogin")
            tietLogin.setText("alexander.rdos@gmail.com")
            tietPassword.setText("Ww123456")
            //        tietPassword.setText("xot1ieG5ro~hoa,ng4Sh")
            //        tietLogin.setText("admin@smartro.ru")
            true
        }
    }



    class LoginViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}