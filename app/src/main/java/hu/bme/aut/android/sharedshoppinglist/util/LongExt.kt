package hu.bme.aut.android.sharedshoppinglist.util

import android.content.Context
import android.icu.text.DecimalFormatSymbols
import hu.bme.aut.android.sharedshoppinglist.R
import kotlin.math.absoluteValue

fun Long.getPriceAsString(context: Context): String {
    val priceAsString = this.absoluteValue.toString().padStart(4, '0')
    var dollars = priceAsString.substring(0, priceAsString.length - 2).trimStart('0')
    if (dollars.isEmpty()) dollars = "0"
    val cents = priceAsString.substring(priceAsString.length - 2)
    val separator = DecimalFormatSymbols.getInstance().decimalSeparator
    return context.getString(
        R.string.item_member_export_price,
        context.getString(R.string.dollar_sign),
        dollars,
        separator,
        cents
    )
}

fun Long.getPriceAsStringWithoutSign(context: Context): String {
    val priceAsString = this.absoluteValue.toString().padStart(4, '0')
    var dollars = priceAsString.substring(0, priceAsString.length - 2).trimStart('0')
    if (dollars.isEmpty()) dollars = "0"
    val cents = priceAsString.substring(priceAsString.length - 2)
    val separator = DecimalFormatSymbols.getInstance().decimalSeparator
    return context.getString(R.string.item_member_export_price, "", dollars, separator, cents)
}