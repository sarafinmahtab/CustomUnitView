package com.rnd.customunitview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
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

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributes(context, attrs)
        initView()
    }

    private lateinit var listener: UnitAmountChangeListener

    private lateinit var customUnitLayout: ConstraintLayout

    private lateinit var unitDecrementImageButton: AppCompatImageButton
    private lateinit var unitIncrementImageButton: AppCompatImageButton

    private lateinit var unitAmountTextView: AppCompatTextView
    private lateinit var unitNameTextView: AppCompatTextView

    private var countHandler = Handler()
    private val counterDelay = 50L //millis

    private var autoIncrement = false
    private var autoDecrement = false

    // Attributes
    private var useFloat = false
    private var unitTypes = -1
    private var unitTypesText = ""
    private var defaultAmount = -1F
    private var changeFactor = -1

    private var currentAmount = 0F

    private val minRange = 0F
    private val maxRange = 100000000F


    private fun setAttributes(context: Context, attrs: AttributeSet?) {

        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomUnitView)

        useFloat = typedArray.getBoolean(R.styleable.CustomUnitView_useFloat, false)
        unitTypes = typedArray.getInteger(R.styleable.CustomUnitView_unitTypes, 0)
        defaultAmount = typedArray.getFloat(R.styleable.CustomUnitView_defaultValue, -1F)
        changeFactor = typedArray.getInt(R.styleable.CustomUnitView_changeFactor, -1)

        // If amount and change factor is not set yet
        when (unitTypes) {
            0 -> {
                // minutes
                updateDefaultAmount(60F)
                updateChangeFactor(5)

                unitTypesText = "minutes a day"
            }

            1 -> {
                // hours
                updateDefaultAmount(60F)
                updateChangeFactor(1)

                unitTypesText = "hours a day"
            }

            2 -> {
                // kg
                updateDefaultAmount(68F)
                updateChangeFactor(2)

                unitTypesText = "kilogram"
            }

            3 -> {
                // lbs
                updateDefaultAmount(145.2F)
                updateChangeFactor(3)

                unitTypesText = "lbs"
            }
        }

        typedArray.recycle()
    }

    private fun updateChangeFactor(factor: Int) {
        if (changeFactor == -1) changeFactor = factor
    }

    private fun updateDefaultAmount(amount: Float) {
        if (defaultAmount == -1F) defaultAmount = amount
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        customUnitLayout =
            LayoutInflater.from(context).inflate(
                R.layout.custom_unit_layout,
                this,
                true
            ) as ConstraintLayout

        unitDecrementImageButton = customUnitLayout.findViewById(R.id.unitDecrementImageButton)
        unitAmountTextView = customUnitLayout.findViewById(R.id.unitAmountTextView)
        unitNameTextView = customUnitLayout.findViewById(R.id.unitNameTextView)
        unitIncrementImageButton = customUnitLayout.findViewById(R.id.unitIncrementImageButton)

        currentAmount = defaultAmount
        unitNameTextView.text = unitTypesText
        updateAmount(currentAmount)

        // Decrement View listeners
        unitDecrementImageButton.setOnLongClickListener {
            autoDecrement = true
            countHandler.postDelayed(counterRunnable, counterDelay)
            return@setOnLongClickListener false
        }

        unitDecrementImageButton.setOnTouchListener { _, p1 ->
            if (p1?.action == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false
            }
            return@setOnTouchListener false
        }

        unitDecrementImageButton.setOnClickListener {
            decrement()
            updateAmount(currentAmount)
        }


        // Increment View listeners
        unitIncrementImageButton.setOnLongClickListener {
            autoIncrement = true
            countHandler.postDelayed(counterRunnable, counterDelay)
            return@setOnLongClickListener false
        }

        unitIncrementImageButton.setOnTouchListener { _, p1 ->
            if (p1?.action == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false
            }
            return@setOnTouchListener false
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
            if (useFloat) {
                unitAmountTextView.text = amount.toString()
                listener.onAmountChanged(amount.toString())
            } else {
                unitAmountTextView.text = amount.toInt().toString()
                listener.onAmountChanged(amount.toInt().toString())
            }

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

            if (!useFloat) {
                return amount.toInt().toString()
            }

            return amount
        }

        return defaultAmount.toString()
    }

    private val counterRunnable = object : Runnable {
        override fun run() {
            if (autoIncrement) {
                increment()
                updateAmount(currentAmount)
                countHandler.postDelayed(this, counterDelay)
            } else if (autoDecrement) {
                decrement()
                updateAmount(currentAmount)
                countHandler.postDelayed(this, counterDelay)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(counterRunnable)
    }
}

interface UnitAmountChangeListener {
    fun onAmountChanged(amount: String)
}