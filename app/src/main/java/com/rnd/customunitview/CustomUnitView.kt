package com.rnd.customunitview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout


class CustomUnitView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        setAttributes(context, null)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributes(context, attrs)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttributes(context, attrs)
        initView()
    }

    private lateinit var listener: UnitAmountChangeListener

    private lateinit var customUnitLayout: ConstraintLayout

    private lateinit var unitDecrementImageButton: AppCompatImageButton
    private lateinit var unitAmountTextView: AppCompatTextView
    private lateinit var unitNameTextView: AppCompatTextView
    private lateinit var unitIncrementImageButton: AppCompatImageButton

    private var unitTypes = -1
    private var unitTypesText = ""
    private var defaultAmount = -1
    private var changeFactor = -1

    private var currentUnitAmount = 0

    private val unitLowerLimit = 0
    private val unitUpperLimit = 100000


    private fun setAttributes(context: Context, attrs: AttributeSet?) {

        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomUnitView)

        unitTypes = typedArray.getInteger(R.styleable.CustomUnitView_unitTypes, 0)
        defaultAmount = typedArray.getInteger(R.styleable.CustomUnitView_defaultValue, -1)
        changeFactor = typedArray.getInteger(R.styleable.CustomUnitView_changeFactor, -1)

        // If amount and change factor is not set yet
        when (unitTypes) {
            0 -> {
                // minutes
                updateDefaultAmount(60)
                updateChangeFactor(5)

                unitTypesText = "minutes a day"
            }

            1 -> {
                // hours
                updateDefaultAmount(60)
                updateChangeFactor(1)

                unitTypesText = "hours a day"
            }

            2 -> {
                // kg
                updateDefaultAmount(68)
                updateChangeFactor(2)

                unitTypesText = "kilograms"
            }

            3 -> {
                // lbs
                updateDefaultAmount(60)
                updateChangeFactor(3)

                unitTypesText = "lbs"
            }
        }

        typedArray.recycle()
    }

    private fun updateChangeFactor(factor: Int) {
        if (changeFactor == -1) changeFactor = factor
    }

    private fun updateDefaultAmount(amount: Int) {
        if (defaultAmount == -1) defaultAmount = amount
    }

    private fun initView() {
        customUnitLayout =
            LayoutInflater.from(context).inflate(R.layout.custom_unit_layout, this, true) as ConstraintLayout

        unitDecrementImageButton = customUnitLayout.findViewById(R.id.unitDecrementImageButton)
        unitAmountTextView = customUnitLayout.findViewById(R.id.unitAmountTextView)
        unitNameTextView = customUnitLayout.findViewById(R.id.unitNameTextView)
        unitIncrementImageButton = customUnitLayout.findViewById(R.id.unitIncrementImageButton)

        currentUnitAmount = defaultAmount
        unitAmountTextView.text = defaultAmount.toString()
        unitNameTextView.text = unitTypesText

        unitDecrementImageButton.setOnClickListener {
            decrement()
            updateAmount(currentUnitAmount)
        }

        unitIncrementImageButton.setOnClickListener {
            increment()
            updateAmount(currentUnitAmount)
        }
    }

    private fun increment() {
        currentUnitAmount += changeFactor
        if (currentUnitAmount > unitUpperLimit) {
            currentUnitAmount = unitUpperLimit
        }
    }

    private fun decrement() {
        currentUnitAmount -= changeFactor
        if (currentUnitAmount < unitLowerLimit) {
            currentUnitAmount = unitLowerLimit
        }
    }

    private fun updateAmount(amount: Int) {
        try {
            unitAmountTextView.text = amount.toString()
            listener.onAmountChanged(amount.toString())
        } catch (e: Exception) {
            throw RuntimeException("Amount changed listener is not initialized")
        }
    }

    fun addUnitAmountChangeListener(listener: UnitAmountChangeListener) {
        this.listener = listener
    }

    fun getAmount(): String {
        val amount = unitAmountTextView.text.toString()
        if (amount.isNotEmpty()) {
            return amount
        }

        return defaultAmount.toString()
    }
}

interface UnitAmountChangeListener {
    fun onAmountChanged(amount: String)
}
