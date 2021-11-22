package hu.bme.aut.android.sharedshoppinglist.util

import android.content.Context
import hu.bme.aut.android.sharedshoppinglist.R
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

fun MaterialTapTargetPrompt.Builder.setAppColors(context: Context): MaterialTapTargetPrompt.Builder {
    backgroundColour = context.getColor(R.color.primaryColor)
    primaryTextColour = context.getColor(R.color.primaryTextColor)
    return this
}