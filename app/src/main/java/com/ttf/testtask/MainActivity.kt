package com.ttf.testtask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        Util.scheduleNotifications(this)

    }
}