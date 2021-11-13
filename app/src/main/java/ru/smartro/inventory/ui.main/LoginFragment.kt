package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.smartro.inventory.database.LoginEntity
import ru.smartro.inventory.R

import androidx.appcompat.widget.AppCompatButton
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

//        baseview.setOnClickListener {
//            MyUtil.hideKeyboard(this)
//        }
        val tilLogin = view.findViewById<TextInputLayout>(R.id.til_login_fragment__login)
        val tietLogin = view.findViewById<TextInputEditText>(R.id.tiet_login_fragment__login)
        tietLogin.setText("admin@smartro.ruR")
        val tilPassword = view.findViewById<TextInputLayout>(R.id.til_login_fragment__password)
        val tietPassword = view.findViewById<TextInputEditText>(R.id.tiet_login_fragment__password)
        tietPassword.setText("xot1ieG5ro~hoa,ng4Sh")

        view.findViewById<AppCompatButton>(R.id.btn_login_fragment).setOnClickListener{
            val loginEntity = LoginEntity(tietLogin.text.toString(), tietPassword.text.toString())
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
        }

    }
}