package com.example.helloworld

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.helloworld.ui.CustomDialog
import com.example.helloworld.ui.FilamentTestActivity
import com.example.helloworld.ui.FilamentTestActivity.Companion.BUNDLE_RENDER_TYPE
import com.example.helloworld.ui.RadioButtonContent
import com.example.helloworld.ui.WebViewActivity
import com.google.android.filament.Filament
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        init {
            Filament.init()
        }
    }

    private lateinit var dialog: CustomDialog

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra("data")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_go_webview.setOnClickListener(this)
        button_go_filament.setOnClickListener(this)

        initDialog()
    }

    private fun initDialog() {
        dialog = CustomDialog(this, "Render Type").apply {
            initRadioGroup(
                RadioButtonContent(
                    FilamentFactory.RenderType.TRIANGLE,
                    R.id.render_type_triangle
                ),
                RadioButtonContent(
                    FilamentFactory.RenderType.RECT,
                    R.id.render_type_rect
                ),
                RadioButtonContent(
                    FilamentFactory.RenderType.IBL,
                    R.id.render_type_ibl
                )
            )
            setConfirmListener {
                if (this.isChecked()) {
                    val renderType = getCheckedType()
                    val intent = Intent(this@MainActivity, FilamentTestActivity::class.java)
                    intent.putExtra(BUNDLE_RENDER_TYPE, renderType.text)
                    launcher.launch(intent)
                }
                dialog.dismiss()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_go_webview -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra("url", "https://www.bilibili.com")
                launcher.launch(intent)
            }
            R.id.button_go_filament -> {
                dialog.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}