package com.openclassrooms.realestatemanager.utils.tools

import java.math.RoundingMode
import kotlin.math.pow

interface Loan {
    fun setLoanAmount(loanAmount: Double)
    fun setInterestRate(interestRate: Double)
    fun setLoanDuration(loanDuration: Int)

    fun getMonthlyPayment(): Double
}

class LoanImpl: Loan {
    private var loanAmount = 0.0
    private var interestRate = 0.0
    private var loanDuration = 0

    override fun setLoanAmount(loanAmount: Double) {
        this.loanAmount = loanAmount
    }

    override fun setInterestRate(interestRate: Double) {
        this.interestRate = interestRate
    }

    override fun setLoanDuration(loanDuration: Int) {
        this.loanDuration = loanDuration
    }

    override fun getMonthlyPayment(): Double {
        val monthlyInterestRate = this.interestRate/1200
        val monthlyPayment = this.loanAmount*monthlyInterestRate/(1-(1/ (1 + monthlyInterestRate).pow(
            this.loanDuration * 12
        )))
        return monthlyPayment.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    }

}