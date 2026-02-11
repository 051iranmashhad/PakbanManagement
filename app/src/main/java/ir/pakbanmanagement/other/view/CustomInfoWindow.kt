package ir.pakbanmanagement.other.view

import android.widget.TextView
import ir.pakbanmanagement.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(mapView: MapView) : InfoWindow(R.layout.view_marker_info, mapView) {
    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val tvInfo = mView.findViewById<TextView>(R.id.tvInfo)
        tvInfo.text = marker.title
    }

    override fun onClose() {}
}
