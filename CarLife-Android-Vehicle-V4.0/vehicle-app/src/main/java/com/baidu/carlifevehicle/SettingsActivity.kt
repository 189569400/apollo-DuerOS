package com.baidu.carlifevehicle

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.baidu.carlife.sdk.receiver.CarLife
import com.baidu.carlife.sdk.sender.display.DisplaySpec

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setTitle("设置")
        }

        val fragemnt = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.v_container, fragemnt)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    class SettingsFragment: PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)

            findPreference<ListPreference>("resolutions")?.let {
                it.onPreferenceChangeListener = this
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            Log.d("SettingsActivity", "preference $preference newValue $newValue")
            when (newValue) {
                "1920x1080" -> CarLife.receiver().setDisplaySpec(DisplaySpec(activity!!, 1920, 1080, 30))
                "1280x720" -> CarLife.receiver().setDisplaySpec(DisplaySpec(activity!!, 1280, 720, 30))
                "1024x576" -> CarLife.receiver().setDisplaySpec(DisplaySpec(activity!!, 1024, 576, 30))
                "1280x480" -> CarLife.receiver().setDisplaySpec(DisplaySpec(activity!!, 1280, 480, 30))
            }
            return true
        }
    }
}