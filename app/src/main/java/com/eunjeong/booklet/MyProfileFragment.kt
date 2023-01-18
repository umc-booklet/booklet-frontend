package com.eunjeong.booklet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eunjeong.booklet.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {
    private lateinit var viewBinding: FragmentMyProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyProfileBinding.inflate(layoutInflater)
        return viewBinding.root
    }
}