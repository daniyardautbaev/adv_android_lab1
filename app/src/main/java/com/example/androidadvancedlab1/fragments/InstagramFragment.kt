package com.example.androidadvancedlab1.fragments

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.androidadvancedlab1.R

class InstagramFragment : Fragment(R.layout.fragment_instagram) {

    private lateinit var image: ImageView
    private lateinit var imageURI: Uri
    private lateinit var selectBtn: Button
    private lateinit var sendBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectBtn = view.findViewById(R.id.select)
        sendBtn = view.findViewById(R.id.send)
        image = view.findViewById(R.id.imageView)

        sendBtn.isEnabled = false // Отключаем кнопку "Отправить" до выбора изображения

        selectBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), 200)
        }

        sendBtn.setOnClickListener {
            if (::imageURI.isInitialized) {
                shareToInstagram()
            } else {
                Toast.makeText(requireContext(), "Выберите изображение перед отправкой", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 200) {
            val selectedImg = data?.data
            if (selectedImg != null) {
                image.setImageURI(selectedImg)
                imageURI = selectedImg
                sendBtn.isEnabled = true // Активируем кнопку "Отправить"
                selectBtn.text = "✅ Изображение выбрано"
                image.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).withEndAction {
                    image.animate().scaleX(1f).scaleY(1f).setDuration(300)
                }
                Toast.makeText(requireContext(), "Изображение загружено!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareToInstagram() {
        val intent = Intent("com.instagram.share.ADD_TO_STORY")
        intent.setDataAndType(imageURI, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val resolveInfo = requireContext().packageManager.queryIntentActivities(intent, 0)

        if (resolveInfo.isNotEmpty()) {
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Instagram не установлен", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Приложение Instagram не найдено", Toast.LENGTH_SHORT).show()
        }
    }
}