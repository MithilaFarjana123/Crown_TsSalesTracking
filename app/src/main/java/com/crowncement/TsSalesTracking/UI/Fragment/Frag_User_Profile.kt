package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat
import com.crowncement.TsSalesTracking.common.Utility.findDifference
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.databinding.FragUserProfileBinding


class Frag_User_Profile : Fragment() {

  //  lateinit var rootView: View

    private var _binding: FragUserProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  rootView =  inflater.inflate(R.layout.frag_user_profile, container, false)

        _binding = FragUserProfileBinding.inflate(inflater, container, false)


/*
        setUserProfileInfo()

 */

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely
        setUserProfileInfo()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setUserProfileInfo() {
        val profile: Data = Utility.getUserInfo(requireActivity())!!
        Glide
            .with(this)
            .load(EndPoint.IMAGE_BASE_URL + profile.userProfileImagePath)
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(binding.imgProfileUser)

        binding.txtProfileName.text = profile.userName
        binding.txtProfileDesignation.text = "Designation: "+profile.userDesig

        val duration = findDifference(
            changeDateFormat(profile.userJoin, "yyyy-MM-dd", "dd-MM-yyyy").toString(),
            getCurrentDate("dd-MM-yyyy")
        )

        binding.txtProfileJoining.text ="Joining date: "+
            changeDateFormat(profile.userJoin, "yyyy-MM-dd", "EEE, dd MMM yyyy") + "\n" + duration


        binding.txtProfileLocation.text = "Job location: "+profile.userLocation
        binding.txtProfileDepartment.text = profile.userDept
        binding.txtProfileEmail.text = profile.userId

        val empID = Utility.getValueByKey(requireActivity(), "username")

        if (!empID!!.isEmpty()) {
            binding.txtEmpId.text = "EMP ID: $empID"
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