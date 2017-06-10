package me.danbeneventano.weather

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_location.view.*

class LocationDialog(val activity: MainActivity) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_location, null)
        builder.setView(view)
                .setPositiveButton("OK") { dialog, id ->
                    activity.viewModel.changeLocation(view.location_edittext.text.toString())
                    activity.refresh()
                    this.dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    this.dialog.dismiss()
                }
                .setTitle("Search Location")
        return builder.create()
    }
}