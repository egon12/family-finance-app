package org.egon12.familyfinanceapp.entity

import org.egon12.familyfinanceapp.entity.AccountType.*
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

class FakeLedger : Ledger {

    override val entries = mutableListOf<Entry>()

    override val lastEntry: Entry
        get() = entries.last()

    override fun open(balances: List<AccountBalance>) {
        val uuid = UUID.randomUUID()
        balances.forEach {
            val entry = Entry(Instant.now(), it.account, uuid, "Opening Balance", it.balance, 0)
            entries.add(entry)
        }
    }

    override fun close(): List<AccountBalance> {
        return entries.groupBy { it.account }
            .map { (account, entries) ->
                val func = funcSums(account.type)
                val balance = entries.sumBy { func(it) }
                PassiveAccountBalance(account, entries, balance)
            }
    }

    override fun entriesBetween(from: Instant, to: Instant): List<Entry> {
        val fromLastDay = from.minus(1, DAYS)
        return entries.filterBetween(fromLastDay, to)
    }

    override fun entriesIn(account: Account): List<Entry> {
        return entries.filter { it.account == account }
    }

    override fun add(entry: Entry) {
        entries.add(entry)
    }

    override fun add(journal: Journal) {
        journal.entries.forEach { add(it) }
    }

    private fun List<Entry>.filterBetween(from: Instant, to: Instant): List<Entry> {
        return this.filter { it.isBetween(from, to) }
    }

    private fun Entry.isBetween(from: Instant, to: Instant): Boolean {
        return this.date.isAfter(from) && this.date.isBefore(to)
    }

    private val debitPlus: (entry: Entry) -> Int = { it.debit - it.credit }

    private val creditPlus: (entry: Entry) -> Int = { it.credit - it.debit }

    private fun funcSums(type: AccountType): (Entry) -> Int {
        return when (type) {
            Assets -> debitPlus
            Liabilities -> debitPlus
            Equity -> creditPlus
            Expense -> debitPlus
            Income -> creditPlus
        }
    }
}
