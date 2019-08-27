package com.rnd.customunitview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        customUnitView.setUseFloat(false)
//        customUnitView.setChangeFactor(2F)
//        customUnitView.setDefaultValue(2F)
//        customUnitView.setUnitType(CustomUnitView.HOURS)

        amountTextView.text = customUnitView.getAmount()

        customUnitView.addUnitAmountChangeListener(object : UnitAmountChangeListener {
            override fun onAmountChanged(amount: String) {
                amountTextView.text = amount
            }
        })
    }
}
