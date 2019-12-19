package com.rnd.customunitview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sarafinmahtab.customunitview2.CustomUnitView
import com.sarafinmahtab.customunitview2.UnitAmountChangeListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customUnitView.setUseFloat(true)
        customUnitView.setChangeFactor(2F)
        customUnitView.setDefaultValue(2F)
        customUnitView.setMaxRange(100F)
        customUnitView.setUnitType(CustomUnitView.HOURS)

        amountTextView.text = customUnitView.getAmount().toString()

        customUnitView.addUnitAmountChangeListener(object : UnitAmountChangeListener {
            override fun onAmountChanged(amount: Float) {
                amountTextView.text = amount.toString()
            }
        })
    }
}
