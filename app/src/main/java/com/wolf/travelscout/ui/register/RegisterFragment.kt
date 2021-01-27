package com.wolf.travelscout.ui.register

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
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
import com.wolf.travelscout.aws.AWSUtils
import com.wolf.travelscout.aws.AwsConstants
import com.wolf.travelscout.util.RegexUtil
import com.wolf.travelscout.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets.ISO_8859_1
import java.nio.charset.StandardCharsets.UTF_8

class RegisterFragment : Fragment(), AWSUtils.OnAwsImageUploadListener, EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var navController: NavController

    private var subscription = CompositeDisposable()
    private var username = ""
    private var password = ""
    private var firstName = ""
    private var phone = ""
    private var email = ""
    private var profileImage64: String? = ""
    private var upcomingTrip = ""


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
            profileImage64 = SharedPreferencesUtil.profileImageURL

            registerNewUser(username, password, firstName, phone, email, profileImage64!!, upcomingTrip)
        }

        btn_login_next.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }

        btn_pick_profile_image.setOnClickListener {
            //Handle runtime permission
            if (hasExternalStorageWritePermission()) {

                //Open gallery to pick image
                openGallery()

            } else {
                EasyPermissions.requestPermissions(
                        PermissionRequest.Builder(this, 1000, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .setRationale("requires storage permission")
                                .setPositiveButtonText("Grant")
                                .setNegativeButtonText("Cancel")
                                .build()
                )
            }
        }
    }

    override fun showProgressDialog() {

    }

    override fun hideProgressDialog() {

    }

    override fun onSuccess(imgUrl: String) {
        println("Uploaded File Path URL: " + imgUrl)
    }

    override fun onError(errorMsg: String) {
        println("Uploaded File Path URL Error: " + errorMsg)
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val imageUri = data?.data
            val path: String? = getPath(imageUri!!)
            iv_chosen_profile_image.setImageURI(imageUri)

            AWSUtils(requireContext(), path!!, this, AwsConstants.folderPath).beginUpload()
        }
    }

    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class)
    protected fun getPath(uri: Uri): String? {
        var uri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (DocumentsContract.isDocumentUri(requireContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                try {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)
                } catch (e: NumberFormatException) {
                    return null
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = requireContext().contentResolver.query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun hasExternalStorageWritePermission(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //Open gallery to pick image
        openGallery()
    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

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
            profileImage: String,
            upcomingTrip: String){

        val subscribe = viewModel.handleRegisterNewUser(
                username = username,
                password = password,
                firstName = firstName,
                phone = phone,
                email = email,
                profileImage = profileImage,
                upcomingTrip = upcomingTrip
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