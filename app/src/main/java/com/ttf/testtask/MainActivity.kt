package com.ttf.testtask

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ttf.testtask.databinding.ActivityMainBinding
import com.ttf.testtask.util.DBHelper
import com.ttf.testtask.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val dbHelper by lazy { DBHelper(this) }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Not granted", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            val stringBuilder = StringBuilder()

            withContext(Dispatchers.IO) {
                val db = dbHelper.readableDatabase

                val cursor = db.rawQuery("SELECT * FROM ${DBHelper.TABLE_NAME} ORDER BY id ASC", null)

                if (cursor.moveToFirst())  {
                    do {
                        val idColumnIndex = cursor.getColumnIndex("id")
                        val id = cursor.getInt(idColumnIndex)

                        val timeColumnIndex = cursor.getColumnIndex("time")
                        val time = cursor.getString(timeColumnIndex)

                        val text = getString(R.string.uiPlaceholder)
                            .replace("{position}", id.toString())
                            .replace("{timestamp}", time)

                        stringBuilder.append(text)
                        stringBuilder.append("\n")
                    } while (cursor.moveToNext())
                }

                cursor.close()
            }

            if (stringBuilder.isEmpty()) {
                binding.textTime.text = getString(R.string.noBoots)
            } else {
                binding.textTime.text = stringBuilder.toString()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        Util.scheduleNotifications(this)

    }
}