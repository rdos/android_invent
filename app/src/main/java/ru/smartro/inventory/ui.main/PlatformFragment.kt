package ru.smartro.inventory.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModel
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstractFragment

class PlatformFragment : AbstractFragment() {

    companion object {
        fun newInstance() = PlatformFragment()
    }

    private lateinit var viewModel: PlatformViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle(R.string.login_fragment__welcome_to_system)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.login_fragment, container, false)


        view.findViewById<AppCompatButton>(R.id.acb_login_fragment).setOnClickListener {
            exitFragment()
        }
        return view
    }

    class PlatformViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}