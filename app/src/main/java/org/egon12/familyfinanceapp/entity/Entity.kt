package org.egon12.familyfinanceapp.entity

import org.egon12.familyfinanceapp.entity.AccountType.*
import org.egon12.familyfinanceapp.entity.DebitCreditType.CREDIT
import org.egon12.familyfinanceapp.entity.DebitCreditType.DEBIT
import java.time.Instant
import java.util.*

data class Account(
    val id: Int,
    val name: String,
    val image: String,
    val type: AccountType
)

enum class AccountType(val id: Int, val commonField: DebitCreditType) {
    Assets(100, DEBIT),
    Liabilities(200, CREDIT),
    Equity(300, CREDIT),
    Expense(400, DEBIT),
    Income(500, CREDIT)
}

enum class DebitCreditType {
    DEBIT,
    CREDIT
}

fun getSumBalanceFunc(type: AccountType): (Entry) -> Int {
    return when (type.commonField) {
        DEBIT -> {
            { it.debit - it.credit }
        }
        CREDIT -> {
            { it.credit - it.debit }
        }
    }
}


class ActiveAccountBalance(
    override val account: Account
) : AccountBalance {

    private val _entries = mutableListOf<Entry>()

    private val sumBalanceFunc = getSumBalanceFunc(account.type)

    override val entries: List<Entry> = _entries

    override val balance: Int = entries.sumBy { sumBalanceFunc(it) }

    override fun add(entry: Entry) {
        _entries.add(entry)
    }
}


interface ITransaction {
    fun expense(account: Account, amount: Int, memo: String = "")
    fun transfer(from: Account, to: Account, amount: Int)
}

class Transaction(
    private val ledger: Ledger,
    private val defaultCash: Account
) : ITransaction {

    override fun expense(account: Account, amount: Int, memo: String) {
        val entryMemo = if (memo.isNotBlank()) memo else createMemo(account)
        val uuid = UUID.randomUUID()
        val time = Instant.now()
        val entry1 = Entry(time, defaultCash, uuid, entryMemo, 0, amount)
        val entry2 = Entry(time, account, uuid, entryMemo, amount, 0)
        ledger.add(entry1)
        ledger.add(entry2)
    }

    override fun transfer(from: Account, to: Account, amount: Int) {
        val time = Instant.now()
        val uuid = UUID.randomUUID()
        val entry1 = Entry(time, from, uuid, "Transfer to ${to.name}", 0, amount)
        val entry2 = Entry(time, to, uuid, "Transfer from ${from.name}", amount, 0)
        ledger.add(entry1)
        ledger.add(entry2)
    }

    private fun createMemo(account: Account): String {
        return "Expense at ${account.name}"
    }
}


/**
 * The smallest unit of accounting
 */
data class Entry(
    val date: Instant,
    val account: Account,
    val journalId: UUID,
    val memo: String,
    val debit: Int,
    val credit: Int
)

/**
 *
 */
abstract class Journal {
    open val id: UUID = UUID.randomUUID()
    open val date: Instant = Instant.now()
    abstract val memo: String
    abstract val entries: List<Entry>

    private var invalidMessage = ""

    open fun isValid(): Boolean {
        val validator = AccountValidator()
        val ok = validator.isValid(this)
        if (!ok) {
            invalidMessage = validator.getMessage()
        }
        return ok
    }

    open fun invalidReason(): String = invalidMessage
}

class TransferJournal(
    from: Account,
    to: Account,
    amount: Int,
    override val date: Instant = Instant.now(),
    override val memo: String = "Transfer from \"${from.name}\" to \"${to.name}\"",
    override val id: UUID = UUID.randomUUID()
) : Journal() {
    override val entries: List<Entry> = listOf(
        Entry(date, from, id, memo, 0, amount),
        Entry(date, to, id, memo, amount, 0)
    )
}

class AccountValidator() {

    private var invalidMessage = ""

    fun isValid(journal: Journal): Boolean {
        return isValid(journal.entries)
    }

    fun getMessage(): String = invalidMessage

    private fun isValid(entries: List<Entry>): Boolean {
        var ok = isEquationValid(entries)
        if (!ok) {
            invalidMessage = "Account equation is false"
            return false
        }

        ok = isDebitCreditValid(entries)
        if (!ok) {
            invalidMessage = "Debit != Credit"
            return false
        }

        return true
    }

    private fun isEquationValid(entries: List<Entry>): Boolean {
        val m = entries.groupBy { it.account.type }
            .mapValues { it.value.sumBalance() }
            .toMap()

        val left = m.getOrDefault(Assets, 0) +
                m.getOrDefault(Liabilities, 0)

        val right = m.getOrDefault(Equity, 0) +
                m.getOrDefault(Income, 0) -
                m.getOrDefault(Expense, 0)

        return left == right
    }

    private fun isDebitCreditValid(entries: List<Entry>): Boolean {
        val allDebit = entries.sumBy { it.debit }
        val allCredit = entries.sumBy { it.credit }
        return allDebit == allCredit
    }
}

/**
 * Ledger is EntryRepository
 */
interface Ledger {
    fun open(balances: List<AccountBalance>)
    fun close(): List<AccountBalance>

    val entries: List<Entry>
    val lastEntry: Entry
    fun entriesBetween(from: Instant, to: Instant): List<Entry>
    fun entriesIn(account: Account): List<Entry>
    fun add(entry: Entry)
    fun add(journal: Journal)
}

/**
 * Account Balance is
 */
interface AccountBalance {
    val account: Account
    val entries: List<Entry>
    val balance: Int
    fun add(entry: Entry)
}

class PassiveAccountBalance(
    override val account: Account,
    override val entries: List<Entry>,
    override val balance: Int
) : AccountBalance {
    override fun add(entry: Entry) {
        error("Cannot add in PassiveAccountBalance")
    }
}

interface AccountRepository {
    fun fetchAll(): List<Account>
    fun get(id: Int): Account
}

fun List<Entry>.sumBalance(): Int = this.sumBy { getSumBalanceFunc(it.account.type)(it) }

