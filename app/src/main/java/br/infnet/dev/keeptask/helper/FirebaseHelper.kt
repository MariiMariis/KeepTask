package br.infnet.dev.keeptask.helper

import br.infnet.dev.keeptask.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    companion object {

        fun getDatabase() = FirebaseDatabase.getInstance().reference

        private fun getAuth() = FirebaseAuth.getInstance()

        fun getUserId() = getAuth().uid

        fun isUserAuthenticated() = getAuth().currentUser != null

        fun validateError(error: String) : Int {
            return when {
                error.contains("There is no user record corresponding to this identifier") -> {
                    R.string.account_not_registered
                }
                error.contains("The email address is badly") -> {
                    R.string.invalid_email
                }
                error.contains("The password is invalid or the user does not have a password") -> {
                    R.string.invalid_password
                }
                error.contains("The email address is already in use by another") -> {
                    R.string.email_already_in_use
                }
                error.contains("The given password is invalid") -> {
                    R.string.weak_password
                }
                else -> {
                    R.string.generic_auth_error
                }
            }
        }
    }
}