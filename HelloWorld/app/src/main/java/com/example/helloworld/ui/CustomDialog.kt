package com.example.helloworld.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.IdRes
import com.example.helloworld.FilamentFactory
import com.example.helloworld.R
import kotlinx.android.synthetic.main.custom_dialog_layout.*

class CustomDialog(context: Context, private val title: String? = null) :
    Dialog(context, R.style.Dialog) {

    private lateinit var buttonGroup: RadioGroup
    private var buttonContents: Array<out RadioButtonContent>? = null
    private var confirmListener: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_layout)
        title?.takeIf {
            it.isNotBlank()
        }.apply {
            dialog_title.text = this
        }

        dialog_cancel?.setOnClickListener {
            dismiss()
        }

        dialog_confirm?.setOnClickListener(confirmListener)
        setupRadioGroup()
    }

    fun setConfirmListener(l: View.OnClickListener) {
        confirmListener = l
    }

    fun initRadioGroup(vararg contents: RadioButtonContent) {
        buttonContents = contents
    }

    fun getCheckedType(): FilamentFactory.RenderType {
        val checkedId = buttonGroup.checkedRadioButtonId
        val button = buttonGroup.findViewById<RadioButton>(checkedId)
        return FilamentFactory.RenderType.fromString(button?.text?.toString() ?: "")
    }

    fun isChecked(): Boolean {
        return buttonGroup.childCount > 0 && buttonGroup.checkedRadioButtonId != View.NO_ID
    }

    private fun setupRadioGroup() {
        buttonGroup = button_group
        buttonContents.takeIf {
            it != null && it.isNotEmpty()
        }.apply {
            this ?: return
            for (i in 0 until this.size) {
                val button = RadioButton(context)
                val lp = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                if (i < this.size - 1) {
                    lp.rightMargin = 50
                }
                button.layoutParams = lp
                button.id = this[i].id
                button.text = this[i].content.text
                button.setTextColor(context.resources.getColor(R.color.fadeBlack))
                buttonGroup.addView(button)

                if (i == 0) {
                    button.isChecked = true
                }
            }
        }

    }
}

class RadioButtonContent(val content: FilamentFactory.RenderType, @IdRes val id: Int)