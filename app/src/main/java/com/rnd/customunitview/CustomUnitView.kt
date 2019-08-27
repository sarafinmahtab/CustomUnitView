package com.rnd.customunitview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.math.RoundingMode
import java.text.DecimalFormat


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

    private var unitTextColor = Color.TRANSPARENT

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
        defaultAmount =
            typedArray.getFloat(R.styleable.CustomUnitView_defaultValue, -1F).roundOffDecimal()
        changeFactor = typedArray.getFloat(R.styleable.CustomUnitView_changeFactor, -1F).roundOffDecimal()

        unitTextColor = typedArray.getColor(R.styleable.CustomUnitView_unitTextColor, Color.TRANSPARENT)

        typedArray.recycle()
    }

    private fun initRequiredAttributes(unit: Int) {
        when (unit) {
            MINUTES -> {
                // minutes
                updateDefaultAmount(60.0F)
                updateChangeFactor(5F)

                unitTypesText = "minutes a day"
            }

            HOURS -> {
                // hours
                updateDefaultAmount(60.0F)
                updateChangeFactor(1F)

                unitTypesText = "hours a day"
            }

            KG -> {
                // kg
                updateDefaultAmount(68.0F)
                updateChangeFactor(0.5F)

                unitTypesText = "kilogram"
            }

            LBS -> {
                // lbs
                updateDefaultAmount(145.2F)
                updateChangeFactor(0.5F)

                unitTypesText = "lbs"
            }
        }

        if (unitTextColor != Color.TRANSPARENT) {
            unitAmountTextView.setTextColor(unitTextColor)
            unitNameTextView.setTextColor(unitTextColor)
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
        currentAmount =
            (currentAmount + changeFactor).roundOffDecimal() //Double and Float addition/subtraction has some bugs
        if (currentAmount > maxRange) {
            currentAmount = maxRange
        }
    }

    private fun decrement() {
        currentAmount =
            (currentAmount - changeFactor).roundOffDecimal() //Double and Float addition/subtraction has some bugs
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
    }
}

interface UnitAmountChangeListener {
    fun onAmountChanged(amount: String)
}


fun Float.roundOffDecimal(): Float {
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toFloat()
}
