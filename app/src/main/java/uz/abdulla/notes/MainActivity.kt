package uz.abdulla.notes

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var language = "English"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        sharedPreferences = applicationContext!!.getSharedPreferences("SHARED_PREF",
            Context.MODE_PRIVATE)
        language = sharedPreferences.getString("language","English")!!

        when(language){
            "English" -> setLanguage("en")
            "Uzbek" -> setLanguage("uz")
            "Russia" -> setLanguage("ru")

        }
    }

    private fun  setLanguage(language: String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}