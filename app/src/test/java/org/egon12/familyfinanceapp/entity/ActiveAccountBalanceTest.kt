package org.egon12.familyfinanceapp.entity

import org.egon12.familyfinanceapp.entity.AccountType.Assets
import org.junit.Test

class ActiveAccountBalanceTest {

    @Test
    fun testActiveAccountBalance() {
        val account = Account(101, "CIH", "", Assets)
        val ledger = FakeLedger()

        val accountBalance = ActiveAccountBalance(account)

        assert(accountBalance.balance == 0) { "Exepct balance 0 got ${accountBalance.balance}" }


    }

}
