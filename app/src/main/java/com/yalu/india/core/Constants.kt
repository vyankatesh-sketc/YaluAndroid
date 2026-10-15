package com.yalu.india.core

object Constants {
    const val BASE_URL = "https://www.yalu.in/api/v1/"
    const val WEBSITE_URL = "https://www.yalu.in"
    
    // Preference Keys
    const val PREFS_FILE = "yalu_secure_prefs"
    const val KEY_AUTH_TOKEN = "key_auth_token"
    const val KEY_USER_ID = "key_user_id"
    const val KEY_USER_NAME = "key_user_name"
    const val KEY_USER_EMAIL = "key_user_email"
    const val KEY_USER_PHONE = "key_user_phone"
    const val KEY_SELECTED_CITY = "key_selected_city"
    
    // Network Timeouts
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}
