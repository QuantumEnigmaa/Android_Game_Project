package fr.isen.monsterfighter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import fr.isen.monsterfighter.Extensions.Extensions.dialog
import fr.isen.monsterfighter.Extensions.Extensions.toast
import fr.isen.monsterfighter.Model.User
import fr.isen.monsterfighter.databinding.ActivityProfileBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.firebaseAuth
import fr.isen.monsterfighter.utils.FirebaseUtils.storageRef
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef
import java.util.*

class ProfileActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_CHOOSE = 1
    }

    private lateinit var binding: ActivityProfileBinding
    private lateinit var filepath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Getting user's data and managing UI
        loadUserData()

        // Changing Profile image
        binding.profileImage.setOnClickListener {
            binding.imageUploadProgress.apply {
                progressMax = 100f
                progressBarWidth = 5f
                backgroundProgressBarWidth = 2f
                progressBarColor = Color.BLUE
            }
            dialog("Voulez-vous changer votre image de profil ?", "Image de profil", false) { loadChosenPicture() }
        }

        // User signing out
        binding.profileDisconnectButton.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            toast("Vous avez été déconnecté")
            finish()
        }
    }

    private fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }

    private fun setUI(user: User) {
        binding.profilePseudo.text = user.userName
        if (user.profileImageUrl != "") {
            Picasso.get().load(user.profileImageUrl).placeholder(R.drawable.searching).into(binding.profileImage)
        } else {
            Picasso.get().load(R.drawable.searching).into(binding.profileImage)
        }
    }

    private fun loadUserData() {
        userRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.firstOrNull { it.key.toString() == getUserId() }?.let {
                        it.getValue(User::class.java)?.let { u ->
                            setUI(u)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("LoadUserDataError", error.toString())
                }
            }
        )
    }

    private fun uploadData() {
        if (::filepath.isInitialized) {
            binding.imageUploadProgress.visibility = View.VISIBLE
            val imgFile: StorageReference = storageRef.child(UUID.randomUUID().toString())
            imgFile.putFile(filepath).addOnSuccessListener {
                imgFile.downloadUrl.addOnSuccessListener {
                    // Adding image url to the user in database
                    userRef.child(getUserId()).child("profileImageUrl").setValue(it.toString())
                }.addOnFailureListener {
                    it.message?.let { it1 -> toast(it1) }
                }
            }.addOnFailureListener {
                it.message?.let { it1 -> toast(it1) }
            }.addOnProgressListener {
                var prog: Long = (100 * it.bytesTransferred/it.totalByteCount)
                binding.imageUploadProgress.setProgressWithAnimation(prog.toFloat(), 1000)
            }.addOnCompleteListener {
                binding.imageUploadProgress.visibility = View.GONE
            }
        } else {
            toast("Erreur lors du chargement de l'image")
        }
    }

    private fun loadChosenPicture() {
        val choosePictureIntent = Intent().setAction(Intent.ACTION_GET_CONTENT)
        choosePictureIntent.type = "image/*"
        startActivityForResult(Intent.createChooser(choosePictureIntent, "choisissez une image"), REQUEST_IMAGE_CHOOSE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CHOOSE ->
                if (resultCode == RESULT_OK) {
                    filepath = data?.data!!
                    Picasso.get().load(filepath).placeholder(R.drawable.searching).into(binding.profileImage)
                    uploadData()
                }
        }
    }
}