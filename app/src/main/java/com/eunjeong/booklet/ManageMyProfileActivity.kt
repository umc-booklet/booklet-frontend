package com.eunjeong.booklet

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eunjeong.booklet.databinding.ActivityManageMyProfileBinding
import kotlinx.android.synthetic.main.dialog_member_out.*
import kotlinx.android.synthetic.main.dialog_member_out.view.*

class ManageMyProfileActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityManageMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityManageMyProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 회원탈퇴 버튼을 누르면
        viewBinding.memberOutBtn.setOnClickListener {
            val view = View.inflate(this@ManageMyProfileActivity, R.layout.dialog_member_out, null)
            val builder = AlertDialog.Builder(this@ManageMyProfileActivity)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // 회원탈퇴에서 '예'를 누르면
            val yesBtn = dialog.alertYesBtn
            yesBtn.setOnClickListener {
                // 정말 회원탈퇴가 되도록
                Toast.makeText(this, "회원탈퇴 완료", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            // 회원탈퇴에서 '아니요'를 누르면
            val noBtn = dialog.alertNoBtn
            noBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        // 프로필 편집을 누르면
        viewBinding.editProfileBtn.setOnClickListener{
            Toast.makeText(this, "프로필 편집", Toast.LENGTH_SHORT).show()
        }

        //확인 버튼을 누르면
        viewBinding.finishBtn.setOnClickListener {
            Toast.makeText(this, "완료", Toast.LENGTH_SHORT).show()
        }
    }
}