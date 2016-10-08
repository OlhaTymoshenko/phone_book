package ua.com.amicablesoft.phonebook.model

import java.io.Serializable

/**
 * Created by lapa on 04.10.16.
 */
data class Contact (val id: String, val name: String, val lastName: String, val phone: String, val photoPath: String?): Serializable