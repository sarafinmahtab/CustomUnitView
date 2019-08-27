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
    private var defaultAmount = -1f
    private var changeFactor = -1f

    private var currentAmount = 0f

    private val minRange = 0f
    private val maxRange = 1000000000f


    private fun setAttributes(context: Context, attrs: AttributeSet?) {

        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomUnitView)

        unitTypes = typedArray.getInteger(R.styleable.CustomUnitView_unitTypes, 0)
        defaultAmount = typedArray.getFloat(R.styleable.CustomUnitView_defaultValue, -1f)
        changeFactor = typedArray.getFloat(R.styleable.CustomUnitView_changeFactor, -1f)

        // If amount and change factor is not set yet
        when (unitTypes) {
            0 -> {
                // minutes
                updateDefaultAmount(60f)
                updateChangeFactor(5f)

                unitTypesText = "minutes a day"
            }

            1 -> {
                // hours
                updateDefaultAmount(60f)
                updateChangeFactor(1f)

                unitTypesText = "hours a day"
            }

            2 -> {
                // kg
                updateDefaultAmount(68f)
                updateChangeFactor(2f)

                unitTypesText = "kilogram"
            }

            3 -> {
                // lbs
                updateDefaultAmount(145.2f)
                updateChangeFactor(3f)

                unitTypesText = "lbs"
            }
        }

        typedArray.recycle()
    }

    private fun updateChangeFactor(factor: Float) {
        if (changeFactor == -1f) changeFactor = factor
    }

    private fun updateDefaultAmount(amount: Float) {
        if (defaultAmount == -1f) defaultAmount = amount
    }

    private fun initView() {
        customUnitLayout =
            LayoutInflater.from(context).inflate(R.layout.custom_unit_layout, this, true) as ConstraintLayout

        unitDecrementImageButton = customUnitLayout.findViewById(R.id.unitDecrementImageButton)
        unitAmountTextView = customUnitLayout.findViewById(R.id.unitAmountTextView)
        unitNameTextView = customUnitLayout.findViewById(R.id.unitNameTextView)
        unitIncrementImageButton = customUnitLayout.findViewById(R.id.unitIncrementImageButton)

        currentAmount = defaultAmount
        unitNameTextView.text = unitTypesText
        updateAmount(currentAmount)

        unitDecrementImageButton.setOnClickListener {
            decrement()
            updateAmount(currentAmount)
        }

        unitIncrementImageButton.setOnClickListener {
            increment()
            updateAmount(currentAmount)
        }
    }

    private fun increment() {
        currentAmount += changeFactor
        if (currentAmount > maxRange) {
            currentAmount = maxRange
        }
    }

    private fun decrement() {
        currentAmount -= changeFactor
        if (currentAmount < minRange) {
            currentAmount = minRange
        }
    }

    private fun updateAmount(amount: Float) {
        try {
            unitAmountTextView.text = amount.toString()
            listener.onAmountChanged(amount.toString())
        } catch (e: Exception) {
//            throw RuntimeException("Amount changed listener is not initialized")
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
