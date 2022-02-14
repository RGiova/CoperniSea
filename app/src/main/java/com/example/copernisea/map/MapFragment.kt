package com.example.copernisea.map

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.azure.android.maps.control.AzureMap
import com.azure.android.maps.control.data.Position
import com.azure.android.maps.control.layer.ImageLayer
import com.azure.android.maps.control.options.CameraOptions
import com.azure.android.maps.control.options.ImageLayerOptions
import com.bumptech.glide.Glide
import com.example.copernisea.R
import com.example.copernisea.databinding.FragmentMapBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.mapbox.geojson.Point

class MapFragment : Fragment(R.layout.fragment_map) {

    private val args: MapFragmentArgs by navArgs()
    private val viewModel: MapViewModel by viewModels()

    private var binding: FragmentMapBinding? = null

    companion object {
        //Layer offset correction
        const val NORTH = 46.584180000000006
        const val SOUTH = 21.680180000000002
        const val EAST = 45.47506109945809
        const val WEST = -13.151533099458086
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.mapControl?.onCreate(savedInstanceState)

        if (args.toBundle().getString("dataType") != null) {
            val folder = args.toBundle().getString("dataType")!!
            viewModel.init(folder)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.folder_not_found),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)

        binding?.mapControl?.onCreate(savedInstanceState)

        val materialDatePicker = MaterialDatePicker.Builder
            .datePicker()
            .setCalendarConstraints(viewModel.getCalendarConstraints())
            .setTitleText(getString(R.string.select_date_title))
            .build()

        binding?.fabCalendar?.setOnClickListener {
            materialDatePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
        }

        materialDatePicker.addOnPositiveButtonClickListener { millis ->
            millis?.let { viewModel.setDate(it) }
        }

        viewModel.state.observe(viewLifecycleOwner) { handleState(it) }
    }

    private fun handleState(state: MapState) {
        when (state) {
            MapState.Loading -> {
                binding?.loadingProgress?.visibility = View.VISIBLE
                binding?.fabCalendar?.isEnabled = false
            }
            MapState.FinishedLoading -> {
                binding?.loadingProgress?.visibility = View.GONE
                binding?.fabCalendar?.isEnabled = true
            }
            is MapState.ShowLayer -> {
                binding?.selectedDate?.text = state.date
                setScaleImage(state.scaleUrl)
                setLayer(state.layerUrl)
            }
            is MapState.Error -> {
                Toast.makeText(
                    requireContext(),
                    state.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLayer(url: String) {
        val layer = ImageLayer(
            ImageLayerOptions.imageCoordinates(
                arrayOf(
                    Position(WEST, NORTH),  //Top Left Corner
                    Position(EAST, NORTH),  //Top Right Corner
                    Position(EAST, SOUTH),  //Bottom Right Corner
                    Position(WEST, SOUTH) //Bottom Left Corner
                )
            ),
            ImageLayerOptions.setUrl(url)
        )

        //Wait until the map resources are ready.
        binding?.mapControl?.onReady { map: AzureMap ->
            map.layers.remove("layer")
            map.layers.add(layer, "layer")
            map.setCamera(
                CameraOptions.center(Point.fromLngLat(11.03, 38.85)),
                CameraOptions.zoom(3.0),
            )
        }
    }

    private fun setScaleImage(url: String) {
        binding?.scaleImage?.let {
            Glide.with(this)
                .load(url)
                .into(it)
        }
    }

    override fun onStart() {
        super.onStart()
        binding?.mapControl?.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding?.mapControl?.onResume()
    }

    override fun onPause() {
        binding?.mapControl?.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding?.mapControl?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding?.mapControl?.onDestroy()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onLowMemory() {
        binding?.mapControl?.onLowMemory()
        super.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapControl?.onSaveInstanceState(outState)
    }
}
