package com.eunjeong.booklet.friendAdd

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.eunjeong.booklet.databinding.FragmentMyProfileBinding

class MyProfileFragment(name: String?, id: String?, img: String?) : Fragment() {
    private lateinit var viewBinding: FragmentMyProfileBinding
    private val namer = name
    private val idr = id
    private val imgr = img


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyProfileBinding.inflate(layoutInflater)

        viewBinding.infoName.text = namer // 이름
        viewBinding.infoCode.text = idr // 코드
        //viewBinding.infoImage.setImageResource(item.profileImage) // 이미지

        Log.d("In MyProfile", "Name : " + namer + "Id : " + idr + "Img : " + imgr)




        // '복사하기' 눌렀을 때 클립보드에 복사.
        viewBinding.infoCopy.setOnClickListener {
            Log.d("CodeContainer", "클릭함")
            val getCode = viewBinding.infoCode.text.toString()
            Log.d("getCode", getCode)

            val clipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("MyCode", getCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(activity, "복사됨", Toast.LENGTH_SHORT).show()
        }

        // '공유하기' 눌렀을 때
        // "이름 : " + viewBinding.infoName.text + "\n" + "코드 : " + viewBinding.infoCode.text
        viewBinding.infoShare.setOnClickListener {
            try {
                val sendText = "이름 : " + viewBinding.infoName.text + "\n" + "코드 : " + viewBinding.infoCode.text // 공유하려는 내용
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, sendText)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, "Share"))
            } catch (ignored: ActivityNotFoundException) {
                Log.d("test", "ignored : $ignored")
            }
        }
        return viewBinding.root
    }
}