package com.wolf.travelscout.ui.register

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.wolf.travelscout.R
import com.wolf.travelscout.util.RegexUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets.ISO_8859_1
import java.nio.charset.StandardCharsets.UTF_8


class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var navController: NavController

    private var subscription = CompositeDisposable()
    private var username = ""
    private var password = ""
    private var firstName = ""
    private var phone = ""
    private var email = ""
    private var profileImage64: String? = ""


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        navController = Navigation.findNavController(view)
        setupComponent()

        btn_register.setOnClickListener {

            username = et_new_username.text.toString()
            password = et_password.text.toString()
            firstName = et_firstName.text.toString()
            phone = et_phone.text.toString()
            email = et_email.text.toString()

            registerNewUser(username, password, firstName, phone, email, profileImage64!!)
        }

        btn_login_next.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }

        btn_pick_profile_image.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
           //iv_chosen_profile_image.setImageURI(data?.data)


           val imageUri: Uri? = data?.data
           val imageStream: InputStream? = context?.contentResolver?.openInputStream(imageUri!!)
           val selectedImage = BitmapFactory.decodeStream(imageStream)
           //profileImage64 = encodeImage(selectedImage)
           val encodeResult = encodeImage(selectedImage)

           val decodedByte = Base64.decode(encodeResult, 0)
//           Log.i("BYTE[]", decodedByte.toString())
           //profileImage64 = encodeResult
           //val imgBitMap = BitmapFactory.decodeByteArray(decRes, 0, decRes.size)

           //val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
           //iv_chosen_profile_image.setImageBitmap(imgBitMap)

       }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)

        // WRITE BASE64 TO EXTERNAL FILE
//        val path = context?.getExternalFilesDir(null)
//        val letDirectory = File(path, "LET")
//        letDirectory.mkdirs()
//        val file = File(letDirectory, "Records.txt")
//        file.appendText(encoded)
//
//        val decodedString: ByteArray = Base64.decode(encoded, Base64.DEFAULT)
//        val imgBitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//
//        iv_chosen_profile_image.setImageBitmap(imgBitMap)

    }

    private fun setupComponent(){
        btn_register.isEnabled = false

        et_new_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEditTextLength()
            }
        })
        et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEditTextLength()
            }
        })
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEditTextLength()
            }
        })
        et_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEditTextLength()
            }
        })
        et_email.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!RegexUtil.validateEmailAddress(et_email.text.toString().trim())) {
                    et_email.error = "Email Invalid"
                }
            }
        }
    }

    private fun validateEditTextLength(){
        btn_register.isEnabled =
                        et_new_username.text!!.isNotEmpty() &&
                        et_password.text!!.isNotEmpty() &&
                        et_firstName.text!!.isNotEmpty() &&
                        et_phone.text!!.isNotEmpty() &&
                        et_email.text!!.isNotEmpty() &&
                                RegexUtil.validateEmailAddress(et_email.text.toString().trim())
    }


    private fun registerNewUser(
            username: String,
            password: String,
            firstName: String,
            phone: String,
            email: String,
            profileImage: String){

        val subscribe = viewModel.handleRegisterNewUser(
                username = username,
                password = password,
                firstName = firstName,
                phone = phone,
                email = email,
                profileImage = profileImage
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i("REGISTER", "SUCCESS !")
            }, { err ->
                var msg = err.localizedMessage
                Log.i("DATA", msg.toString())
            })
        subscription.add(subscribe)
    }

}