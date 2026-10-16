
package com.mafia.aistudio

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.widget.*
import android.view.ViewGroup

class MainActivity : Activity() {
    private var selectedPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 24)
            setBackgroundColor(Color.rgb(18, 18, 25))
        }

        fun label(value: String) = TextView(this).apply {
            text = value
            textSize = 19f
            setTextColor(Color.WHITE)
            setPadding(0, 14, 0, 14)
        }

        root.addView(label("MAFIA AI STUDIO"))
        root.addView(label("Create videos from your photos"))

        val photo = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 450
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(Color.DKGRAY)
        }
        root.addView(photo)

        val choose = Button(this).apply {
            text = "Choose Photo"
        }
        root.addView(choose)

        choose.setOnClickListener {
            startActivityForResult(
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }, 10
            )
        }

        val prompt = EditText(this).apply {
            hint = "Describe how your photo should move"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
        }
        root.addView(prompt)

        val effects = Spinner(this)
        val styles = listOf(
            "Cinematic", "Dance", "Anime",
            "Dream", "Slow Motion", "Fantasy"
        )
        effects.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            styles
        )
        root.addView(effects)

        val generate = Button(this).apply {
            text = "Generate Video"
        }
        root.addView(generate)

        generate.setOnClickListener {
            Toast.makeText(
                this,
                "AI video generation needs a provider connection.",
                Toast.LENGTH_LONG
            ).show()
        }

        setContentView(ScrollView(this).apply {
            addView(root)
        })
    }

    @Deprecated("Deprecated in Android")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == RESULT_OK) {
            selectedPhoto = data?.data
            val root = (
                findViewById<ViewGroup>(android.R.id.content)
                    .getChildAt(0) as ScrollView
                ).getChildAt(0) as LinearLayout

            (root.getChildAt(2) as ImageView)
                .setImageURI(selectedPhoto)
        }
    }
}
