package com.mafia.aistudio

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.ContentValues
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
import android.widget.*
import android.content.res.ColorStateList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(11, 11, 19)
    private val panel = Color.rgb(28, 28, 42)
    private val purple = Color.rgb(131, 83, 255)
    private lateinit var page: LinearLayout
    private var image: Uri? = null
    private var video: Uri? = null
    private var mode = "Photo to Video"
    private var style = "Cinematic"
    private var duration = "5 seconds"
    private var ratio = "9:16"
    private val prefs by lazy { getSharedPreferences("mafia_studio", MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        home()
    }
    private fun text(s: String, size: Float = 15f, bold: Boolean = false): TextView = TextView(this).apply {
        text = s; textSize = size; setTextColor(Color.WHITE)
        if (bold) typeface = Typeface.DEFAULT_BOLD
        setPadding(0, 8, 0, 9)
    }
    private fun section(s: String) { page.addView(text(s, 20f, true)) }
    private fun button(s: String, action: () -> Unit): Button = Button(this).apply {
        text = s; isAllCaps = false; setTextColor(Color.WHITE)
        backgroundTintList = ColorStateList.valueOf(purple)
        setOnClickListener { action() }
    }
    private fun hint(s: String) { page.addView(text(s, 13f).apply { setTextColor(Color.LTGRAY) }) }
    private fun build(title: String) {
        val outer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg) }
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(12, 8, 12, 8) }
        listOf("Home", "Create", "Library", "Settings").forEach { name ->
            top.addView(Button(this).apply {
                text = name; textSize = 11f; isAllCaps = false; setTextColor(Color.WHITE)
                backgroundTintList = ColorStateList.valueOf(if (title == name) purple else panel)
                setOnClickListener { when(name) { "Home" -> home(); "Create" -> create(); "Library" -> library(); else -> settings() } }
            }, LinearLayout.LayoutParams(0, 48, 1f))
        }
        outer.addView(top)
        val scroll = ScrollView(this).apply { fillViewport = true }
        page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(22, 12, 22, 36) }
        scroll.addView(page); outer.addView(scroll); setContentView(outer)
        page.addView(text("MAFIA ✦ AI STUDIO", 27f, true))
    }
    private fun home() {
        build("Home")
        hint("Your creative studio • Create videos, explore effects, and enhance photos")
        section("Start creating")
        listOf("Photo to Video", "Text to Video", "AI Effects", "Photo Enhancer", "Video Editor").forEach { option ->
            page.addView(button("✦  $option") { mode = option; create() })
        }
        section("Trending templates")
        listOf("Neon Dance", "Movie Trailer", "Anime Motion", "Retro VHS", "Dreamscape", "Slow Motion", "Fantasy Portal", "Superhero Entrance", "Birthday Celebration", "Cinematic Portrait").forEach { preset ->
            page.addView(button("▶  $preset") { style = preset; mode = "AI Effects"; create() })
        }
        hint("AI video rendering requires a provider connection. This version never fabricates generated videos.")
    }
    private fun spinner(items: List<String>, chosen: String, update: (String) -> Unit) {
        val spin = Spinner(this)
        spin.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spin.setSelection(items.indexOf(chosen).coerceAtLeast(0))
        spin.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) { update(items[pos]) }
        }
        page.addView(spin)
    }
    private fun create() {
        build("Create")
        section("Create something amazing")
        spinner(listOf("Photo to Video", "Text to Video", "AI Effects", "Photo Enhancer", "Video Editor"), mode) { mode = it }
        val photoPreview = ImageView(this).apply {
            setBackgroundColor(panel); minimumHeight = 220; scaleType = ImageView.ScaleType.CENTER_CROP
            image?.let { setImageURI(it) } ?: setImageResource(android.R.drawable.ic_menu_gallery)
        }
        page.addView(photoPreview, LinearLayout.LayoutParams(-1, 220))
        page.addView(button("+ Choose photo") { pick("image/*", 101) })
        page.addView(button("+ Choose existing video") { pick("video/*", 102) })
        hint("Selected video: ${video?.lastPathSegment ?: "None"}")
        section("Describe your scene")
        val prompt = EditText(this).apply {
            setText(prefs.getString("draft", "")); hint = "Describe movement, camera, lighting and style…"
            setHintTextColor(Color.GRAY); setTextColor(Color.WHITE); minLines = 3; gravity = Gravity.TOP
            setPadding(16, 16, 16, 16); setBackgroundColor(panel)
        }
        page.addView(prompt)
        section("Animation style")
        spinner(listOf("Cinematic", "Dance", "Anime", "Dream", "Action", "Retro", "Neon Dance", "Movie Trailer", "Anime Motion", "Retro VHS", "Dreamscape", "Slow Motion", "Fantasy Portal", "Superhero Entrance", "Birthday Celebration", "Cinematic Portrait"), style) { style = it }
        section("Video length")
        spinner(listOf("5 seconds", "10 seconds", "15 seconds"), duration) { duration = it }
        section("Aspect ratio")
        spinner(listOf("9:16", "16:9", "1:1"), ratio) { ratio = it }
        val result = text("Ready. Video generation is not connected yet.", 14f)
        page.addView(button("✦ Generate AI Video") {
            val p = prompt.text.toString().trim()
            prefs.edit().putString("draft", p).apply()
            if (mode != "Text to Video" && image == null && mode != "Video Editor") {
                result.text = "Choose a photo to continue."
            } else if (p.isEmpty() && mode != "Photo Enhancer" && mode != "Video Editor") {
                result.text = "Describe your video first."
            } else {
                val item = "${SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date())} • $mode • $style • $duration • $ratio • $p"
                prefs.edit().putString("projects", item + "\n" + prefs.getString("projects", "")).apply()
                result.text = "Project saved to Library. Connect a video-generation API/backend to render a real MP4."
            }
        })
        page.addView(result)
        if (mode == "Photo Enhancer") {
            page.addView(button("Enhance & save photo locally") { enhance(result) })
            hint("Local enhancement adjusts contrast and saturation; it is not AI upscaling.")
        }
        if (mode == "Video Editor") {
            page.addView(button("Open selected video in phone editor") {
                val v = video
                if (v == null) result.text = "Choose a video first."
                else try { startActivity(Intent(Intent.ACTION_EDIT).apply { setDataAndType(v, "video/*"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }) }
                catch (_: Exception) { result.text = "No compatible video editor installed." }
            })
        }
    }
    private fun pick(type: String, code: Int) {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); this.type = type
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }, code)
    }
    private fun enhance(status: TextView) {
        val source = image ?: run { status.text = "Choose a photo first."; return }
        try {
            val bitmap = contentResolver.openInputStream(source).use { BitmapFactory.decodeStream(it) }
                ?: throw IllegalStateException("Cannot read photo")
            val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val matrix = ColorMatrix(floatArrayOf(1.12f,0f,0f,0f,-8f, 0f,1.12f,0f,0f,-8f, 0f,0f,1.12f,0f,-8f, 0f,0f,0f,1f,0f))
            val saturation = ColorMatrix().apply { setSaturation(1.16f) }
            matrix.postConcat(saturation)
            canvas.drawBitmap(bitmap, 0f, 0f, Paint().apply { colorFilter = ColorMatrixColorFilter(matrix) })
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "MAFIA_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MAFIA AI Studio")
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Cannot create output")
            contentResolver.openOutputStream(uri).use { output.compress(Bitmap.CompressFormat.JPEG, 94, it) }
            status.text = "Enhanced photo saved in Pictures/MAFIA AI Studio."
        } catch (e: Exception) { status.text = "Enhancement failed: ${e.localizedMessage}" }
    }
    private fun library() {
        build("Library"); section("My creations")
        hint("Saved project drafts. Finished AI videos will appear after a generation backend is connected.")
        val entries = prefs.getString("projects", "")!!.lines().filter { it.isNotBlank() }
        if (entries.isEmpty()) hint("No projects yet. Open Create to get started.")
        entries.forEach { page.addView(text("✦  $it", 14f)) }
        page.addView(button("Clear project history") { prefs.edit().remove("projects").apply(); library() })
    }
    private fun settings() {
        build("Settings"); section("About MAFIA AI Studio")
        hint("Version 2.0 • Original Android prototype")
        section("AI provider")
        hint("Not connected. Production image-to-video and text-to-video require a secure server and paid model provider. Never embed a secret API key in an Android app.")
        section("Privacy")
        hint("Photos are selected with Android's system picker. Project drafts are saved on your device. No images are uploaded by this prototype.")
        section("Export")
        hint("Locally enhanced photos can be saved to Pictures. MP4 export will be available when real generation is integrated.")
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data?.data != null) {
            val uri = data.data!!
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            if (requestCode == 101) image = uri
            if (requestCode == 102) video = uri
            create()
        }
    }
}
