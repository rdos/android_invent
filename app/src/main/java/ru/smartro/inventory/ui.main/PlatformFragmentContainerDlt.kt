package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.smartro.inventory.R

import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.database.ContainerEntityRealm


class PlatformFragmentContainerDlt : AbstractFragment(){

    companion object {
        fun newInstance() = PlatformFragmentContainerDlt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.platform_fragment_container__rv__dtl, container, false)
        return view
    }

    class PlatformFragmentContainerDltViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}

