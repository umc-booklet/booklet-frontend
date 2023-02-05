package com.eunjeong.booklet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eunjeong.booklet.databinding.FragmentMyProfileBinding

class MyProfileFragment() : Fragment() {
    private lateinit var viewBinding: FragmentMyProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyProfileBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewBinding.infoName.text = item.name // 이름
        //viewBinding.infoCode.text = item.id.toString() // 코드
        //viewBinding.infoImage.setImageResource(item.profileImage) // 이미지

        viewBinding.infoCopy.setOnClickListener { // 복사하기 눌렀을 때

        }

        viewBinding.infoShare.setOnClickListener { // 공유하기 눌렀을 때

        }
    }
}