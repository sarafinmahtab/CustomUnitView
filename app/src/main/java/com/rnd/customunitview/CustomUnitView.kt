package com.rnd.customunitview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
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

    private lateinit var decrementImageButton: ImageButton
    private lateinit var incrementImageButton: ImageButton

    private lateinit var unitAmountTextView: TextView
    private lateinit var unitNameTextView: TextView

    private var countHandler = Handler()
    private val counterDelay = 50L //millis

    private var autoIncrement = false
    private var autoDecrement = false

    // Attributes
    private var useFloat = false
    private var unitType = -1
    private var unitTypesText = ""
    private var defaultAmount = -1F
    private var changeFactor = -1F

    private var titleTextColor = Color.TRANSPARENT
    private var descTextColor = Color.TRANSPARENT

    private var titleTextSize = -1
    private var descTextSize = -1

    private var currentAmount = 0F

    private val minRange = 0F
    private val maxRange = 100000000F


    private fun setAttributes(context: Context, attrs: AttributeSet?) {

        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomUnitView)

        useFloat = typedArray.getBoolean(R.styleable.CustomUnitView_useFloat, false)
        unitType = typedArray.getInteger(R.styleable.CustomUnitView_unitTypes, 0)
        defaultAmount = typedArray.getFloat(R.styleable.CustomUnitView_defaultValue, -1F)
        changeFactor = typedArray.getFloat(R.styleable.CustomUnitView_changeFactor, -1F)

        titleTextColor = typedArray.getColor(R.styleable.CustomUnitView_titleTextColor, Color.TRANSPARENT)
        descTextColor = typedArray.getColor(R.styleable.CustomUnitView_descTextColor, Color.TRANSPARENT)

        titleTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomUnitView_titleTextSize, -1)
        descTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomUnitView_descTextSize, -1)

        typedArray.recycle()
    }

    private fun initRequiredAttributes(unit: Int) {
        when (unit) {
            MINUTES -> {
                // minutes
                updateDefaultAmount(defaultMinutesAmount)
                updateChangeFactor(defaultMinutesChangeFactor)

                unitTypesText = context.getString(R.string.minute_desc)
            }

            HOURS -> {
                // hours
                updateDefaultAmount(defaultHoursAmount)
                updateChangeFactor(defaultHoursChangeFactor)

                unitTypesText = context.getString(R.string.hours_desc)
            }

            KG -> {
                // kg
                updateDefaultAmount(defaultKgAmount)
                updateChangeFactor(defaultKgChangeFactor)

                unitTypesText = context.getString(R.string.kg_desc)
            }

            LBS -> {
                // lbs
                updateDefaultAmount(defaultLbsAmount)
                updateChangeFactor(defaultLbsChangeFactor)

                unitTypesText = context.getString(R.string.lbs_desc)
            }
        }

        if (titleTextColor != Color.TRANSPARENT) {
            unitAmountTextView.setTextColor(titleTextColor)
            unitNameTextView.setTextColor(titleTextColor) // Always will take the title color
        }

        if (descTextColor != Color.TRANSPARENT) {
            unitNameTextView.setTextColor(descTextColor) // Can change the color from here
        }

        if (titleTextSize != -1) {
            unitAmountTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())
        }

        if (descTextSize != -1) {
            unitNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, descTextSize.toFloat())
        }

        currentAmount = defaultAmount
        unitNameTextView.text = unitTypesText
        updateAmount(currentAmount)
    }

    private fun updateChangeFactor(factor: Float) {
        if (changeFactor == -1F) changeFactor = factor
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

        decrementImageButton = customUnitLayout.findViewById(R.id.unitDecrementImageButton)
        unitAmountTextView = customUnitLayout.findViewById(R.id.unitAmountTextView)
        unitNameTextView = customUnitLayout.findViewById(R.id.unitNameTextView)
        incrementImageButton = customUnitLayout.findViewById(R.id.unitIncrementImageButton)

        // If amount and change factor is not set yet, will choose default
        initRequiredAttributes(unitType)

        // Decrement View listeners
        decrementImageButton.setOnLongClickListener {
            autoDecrement = true
            countHandler.postDelayed(counterRunnable, counterDelay)
            return@setOnLongClickListener false
        }

        decrementImageButton.setOnTouchListener { _, p1 ->
            if (p1?.action == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false
            }
            return@setOnTouchListener false
        }

        decrementImageButton.setOnClickListener {
            decrement()
            updateAmount(currentAmount)
        }


        // Increment View listeners
        incrementImageButton.setOnLongClickListener {
            autoIncrement = true
            countHandler.postDelayed(counterRunnable, counterDelay)
            return@setOnLongClickListener false
        }

        incrementImageButton.setOnTouchListener { _, p1 ->
            if (p1?.action == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false
            }
            return@setOnTouchListener false
        }

        incrementImageButton.setOnClickListener {
            increment()
            updateAmount(currentAmount)
        }
    }

    private fun increment() {
        currentAmount += changeFactor
        currentAmount = currentAmount.roundOffDecimal()

        if (currentAmount > maxRange) {
            currentAmount = maxRange
        }
    }

    private fun decrement() {
        currentAmount -= changeFactor
        currentAmount = currentAmount.roundOffDecimal()

        if (currentAmount < minRange) {
            currentAmount = minRange
        }
    }

    private fun updateAmount(amount: Float) {
        try {
            if (useFloat) {
                unitAmountTextView.text = amount.toString()
                listener.onAmountChanged(amount)
            } else {
                unitAmountTextView.text = amount.toInt().toString()
                listener.onAmountChanged(amount)
            }

        } catch (e: Exception) {
//            throw RuntimeException("Amount changed listener is not initialized")
        }
    }

    fun addUnitAmountChangeListener(listener: UnitAmountChangeListener) {
        this.listener = listener
    }

    fun getAmount(): Float {
        val amount = unitAmountTextView.text.toString()
        if (amount.isNotEmpty()) {

            if (!useFloat) {
                return amount.toFloat()
            }

            return amount.toFloat()
        }

        return defaultAmount
    }

    fun setUseFloat(useFloat: Boolean) {
        this.useFloat = useFloat
    }

    fun setChangeFactor(factor: Float) {
        this.changeFactor = factor
    }

    fun setDefaultValue(value: Float) {
        this.defaultAmount = value
        updateAmount(value)
    }

    fun setUnitType(unit: Int) {
        this.unitType = unit
        initRequiredAttributes(unitType)
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
        countHandler.removeCallbacks(counterRunnable)
    }

    companion object {
        const val MINUTES = 0
        const val HOURS = 1
        const val KG = 2
        const val LBS = 3

        const val defaultMinutesAmount = 60.0F
        const val defaultMinutesChangeFactor = 5F

        const val defaultHoursAmount = 60.0F
        const val defaultHoursChangeFactor = 1F

        const val defaultKgAmount = 68.0F
        const val defaultKgChangeFactor = 0.5F

        const val defaultLbsAmount = 145.2F
        const val defaultLbsChangeFactor = 0.5F
    }
}

interface UnitAmountChangeListener {
    fun onAmountChanged(amount: Float)
}


fun Float.roundOffDecimal(): Float {
    return String.format("%.2f", this).toFloat()
}
