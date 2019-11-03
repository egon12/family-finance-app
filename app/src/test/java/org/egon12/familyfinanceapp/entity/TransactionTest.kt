package org.egon12.familyfinanceapp.entity

import org.egon12.familyfinanceapp.entity.AccountType.*
import org.junit.Test
import java.time.Instant

class TransactionTest {

    @Test
    fun testTransfer() {
        val ledger = FakeLedger()
        val accountRepo = FakeAccountRepository()

        val bankAccount = accountRepo.get(102)
        val cashAccount = accountRepo.get(101)

        val u = Transaction(ledger, cashAccount)

        u.transfer(bankAccount, cashAccount, 10_000)

        val timeNow = Instant.now()

        val entries = ledger.entriesBetween(timeNow.minusSeconds(1), timeNow.plusSeconds(1))

        val bankEntries = entries.first { it.account.id == 102}
        assert(bankEntries.credit == 10000) { "Expect -10,000 got ${bankEntries.credit}"}

        val cashEntries = entries.first { it.account.id == 101}
        assert(cashEntries.debit == 10000)  { "Expect 10,000 got ${cashEntries.debit}"}
    }

    @Test
    fun testExpense() {
        val ledger = FakeLedger()

        val cashAccount = Account(101, "Cash in Hand", "", Assets)
        val homeExpenseGroup = Account(401, "Home Expense", "", Expense)

        val u = Transaction(ledger, cashAccount)

        u.expense(homeExpenseGroup, 10_000)

        val timeNow = Instant.now()

        val entries = ledger.entriesBetween(timeNow.minusSeconds(1), timeNow.plusSeconds(1))

        val cashEntries = entries.first { it.account.id == 101}
        assert(cashEntries.credit == 10_000)  { "Expect 10,000 got ${cashEntries.credit}"}

        val homeExpense = entries.first { it.account.id == 401}
        assert(homeExpense.debit == 10_000)  { "Expect 10,000 got ${homeExpense.debit}"}
    }

    inner class FakeAccountRepository : AccountRepository {

        private val accounts = listOf(
            Account(101, "Cash in Hand", "", Assets),
            Account(102, "Bank Account", "", Assets),
            Account(401, "Home", "", Expense),
            Account(402, "Car", "", Expense)
        )

        override fun fetchAll(): List<Account> = accounts

        override fun get(id: Int): Account = accounts.first { it.id == id }
    }

}
