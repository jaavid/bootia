package com.controladad.boutia_pms.models.shared_preferences_models

object SharedPreferencesModels {

    data class SharedPreferencesUserModel(val uid: String,
                                          val name: String,
                                          val role: String,
                                          val phone: String,
                                          val pictureUri: String,
                                          val groupName: String)

    data class SharedPreferencesCredentials(val token: String,
                                            val cookie: String,
                                            val imei: String)
}