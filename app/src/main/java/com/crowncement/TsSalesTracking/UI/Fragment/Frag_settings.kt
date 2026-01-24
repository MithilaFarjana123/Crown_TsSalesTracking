package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getValueByKey
import com.crowncement.TsSalesTracking.common.Utility.setAppLanguage
import com.crowncement.TsSalesTracking.common.Utility.storeKeyValue
import com.crowncement.TsSalesTracking.databinding.FragSettingsBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class Frag_settings : Fragment() {


 //   private lateinit var rootView: View

    private var _binding: FragSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

     //   rootView = inflater.inflate(R.layout.frag_settings, container, false)


        _binding = FragSettingsBinding.inflate(inflater, container, false)
/*

        getCurrentLanguage()

        onEventClickListener()


 */
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely

        getCurrentLanguage()

        onEventClickListener()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }



    private fun onEventClickListener() {

        binding.swLn?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                storeKeyValue(requireActivity(), "language", "bn")
                setAppLanguage(requireActivity(), "bn")
            } else {
                storeKeyValue(requireActivity(), "language", "en")
                setAppLanguage(requireActivity(), "en")
            }
        }

        binding.imShare.setOnClickListener{

            val downloadLink=getValueByKey(requireActivity(),"download_url").toString()

            if(downloadLink!=""){
                shareLink(downloadLink)
            }
            else{
                Utility.commonToast(requireActivity(),"Sorry, feature not available","Failed to generate QR code",0)
            }

        }
    }

    private fun getCurrentLanguage() {
        val lang = getValueByKey(requireActivity(), "language")
        if (!lang.isNullOrBlank()) {
            binding.swLn.isChecked = lang == "bn"
        }
    }

    private fun shareLink(link:String) {
        val v: View = layoutInflater.inflate(R.layout.share_crownfeat, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnQrOk = v.findViewById<Button>(R.id.btnQrOk)
        val imgQrCode = v.findViewById<ImageView>(R.id.imgQrCode)

        imgQrCode.setImageBitmap(Utility.generateQRCode(link,500))
        //Increase Screen brightness
        val layoutParams = dialog.window!!.attributes
        layoutParams.screenBrightness = 1.0f
        dialog.window!!.attributes = layoutParams

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        btnQrOk.setOnClickListener {

            val contentResolver = requireContext()!!.contentResolver
            val brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            val newBrightness = brightness - 50 // decrease brightness by 50 units
           // val layoutParams = dialog.window!!.attributes
            layoutParams.screenBrightness = newBrightness / 255f
            dialog.window!!.attributes = layoutParams

            dialog.hide()
        }

    }

    //toolbar home
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                //  Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_dashboard)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}