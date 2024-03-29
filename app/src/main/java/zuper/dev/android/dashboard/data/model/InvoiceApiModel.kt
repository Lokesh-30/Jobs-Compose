package zuper.dev.android.dashboard.data.model

import androidx.compose.ui.graphics.Color

data class InvoiceApiModel(
    val invoiceNumber: Int,
    val customerName: String,
    val total: Int,
    val status: InvoiceStatus
)

data class FilteredInvoice(
    val invoices: List<InvoiceApiModel> = emptyList(),
    var total: Int = 0,
    val portions: MutableList<Pair<Color?, Int>> = mutableListOf(),
    val category: MutableMap<InvoiceStatus, Int> = mutableMapOf()
)

enum class InvoiceStatus {
    Draft,
    Pending,
    Paid,
    BadDebt
}
