package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.Adapter.DoAddedItemAdapter
import com.crowncement.TsSalesTracking.data.Adapter.DoItemAdapter
import com.crowncement.TsSalesTracking.data.Model.DOInfo
import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.databinding.FragDeliveryProgramBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.util.*

class Frag_Delivery_Program : Fragment() {


    private var _binding: FragDeliveryProgramBinding? = null
    private val binding get() = _binding!!

  //  lateinit var rootView: View
    lateinit var delAddressList: ArrayList<String>

    var delFrom = ""

    lateinit var products: ArrayList<DOInfo>

    var selectedProducts: ArrayList<DOInfo> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       // rootView = inflater.inflate(R.layout.frag_delivery_program, container, false)
        _binding = FragDeliveryProgramBinding.inflate(inflater, container, false)

/*
        loadItemRv(getItemInfo())

 */

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        loadItemRv(getItemInfo())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    private fun getItemInfo(): ArrayList<DOInfo> {
        products = ArrayList()
        products.add(DOInfo("DO-122342342", "PC", 600, 0, "", "", 0.0, 0.0, 0.0, 0.0))
        products.add(DOInfo("DO-233544343", "PCC", 500, 0, "", "", 0.0, 0.0, 0.0, 0.0))
        return products
    }

    private fun loadItemRv(itemList: ArrayList<DOInfo>) {
        if (itemList.size > 0) {
            binding.rdItem.visibility = View.VISIBLE
            binding.txtItemError.visibility = View.GONE
            val itemAdapter = DoItemAdapter(itemList)
            val manager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rdItem.layoutManager = manager
            binding.rdItem.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object : DoItemAdapter.OnAdapterItemClickListener {

                override fun onSelectItem(v: View?, position: Int) {

                    addItem(itemList[position], position)
                }
            })

        } else {
            binding.rdItem.visibility = View.GONE
            binding.txtItemError.visibility = View.VISIBLE
        }

    }


    private fun loadDeliveryAddress(ac: AutoCompleteTextView) {
        delAddressList = ArrayList()
        delAddressList.add("Select delivery address")
        delAddressList.add("Factory")
        delAddressList.add("Kazirhat")
        delAddressList.add("Pagla Ghat")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            delAddressList
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


    private fun addItem(item: DOInfo, pos: Int) {
        val v: View = layoutInflater.inflate(R.layout.add_item_for_program, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val imgItemImage: ImageView = v.findViewById(R.id.imgItemImage)
        val txtItemName: TextView = v.findViewById(R.id.txtItemName)
        val txtItemQuantity: TextView = v.findViewById(R.id.txtItemQuantity)

        val lauoutDelF: TextInputLayout = v.findViewById(R.id.lauoutDelF)
        val layoutQty: TextInputLayout = v.findViewById(R.id.layoutQty)
        val layoutDelT: TextInputLayout = v.findViewById(R.id.layoutDelT)
        val acDeliveryFrom: AutoCompleteTextView = v.findViewById(R.id.acDeliveryFrom)


        val txtQty: TextInputEditText = v.findViewById(R.id.txtQty)
        val txtDelTo: TextInputEditText = v.findViewById(R.id.txtDelTo)

        loadDeliveryAddress(acDeliveryFrom)

        txtItemName.text = "Product Name: " + item.item_name
        txtItemQuantity.text = "Available Quantity: " + item.stock_quantity.toString()

        if (item.item_name == "PC") {
            imgItemImage.setImageResource(R.drawable.pc)
        } else {
            imgItemImage.setImageResource(R.drawable.pcc)
        }

        val btnAddCementItem: MaterialButton = v.findViewById(R.id.btnAddCementItem)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()


        acDeliveryFrom.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                delFrom = parent.getItemAtPosition(position).toString()
            } else {
                delFrom = ""
            }
        }


        txtQty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                layoutQty.error = null
                layoutQty.isErrorEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s!!.isNotEmpty()) {
                    val enterQty = s.toString().toInt()
                    if (enterQty > item.stock_quantity) {
                        layoutQty.error = "Required quantity is greater than DO quantity"
                        layoutQty.isErrorEnabled = true
                    }
                }
            }
        })

        btnAddCementItem.setOnClickListener {

            var result = false
            layoutQty.isErrorEnabled = false
            layoutQty.error = null

            lauoutDelF.isErrorEnabled = false
            lauoutDelF.error = null

            layoutDelT.isErrorEnabled = false
            layoutDelT.error = null

            //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> UI-VALIDATION
            if (txtQty.text.toString().isEmpty() || txtQty.text.toString()
                    .toInt() > item.stock_quantity
            ) {
                result = false
                layoutQty.isErrorEnabled = true
                layoutQty.error = "Provide valid quantity"
            } else if (delFrom == "") {
                result = false
                lauoutDelF.isErrorEnabled = true
                lauoutDelF.error = "Please provide delivery from address"
            } else if (txtDelTo.text.toString().isEmpty()) {
                result = false
                layoutDelT.isErrorEnabled = true
                layoutDelT.error = "অনুগ্রহ করে ডেলিভারির ঠিকানাটি বাংলায় লিখুন"
            } else {
                result = true
            }
            //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> UI-VALIDATION END

            if (result) {
                selectedProducts.add(
                    DOInfo(
                        item.do_number,
                        item.item_name,
                        item.stock_quantity,
                        txtQty.text.toString().toInt(),
                        txtDelTo.text.toString(),
                        delFrom,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                    )
                )
                products.removeAt(pos)
                loadItemRv(products)
                loadSelectedItemRV(selectedProducts)
                dialog.dismiss()
            }

        }

    }

    private fun loadSelectedItemRV(itemList: ArrayList<DOInfo>) {
        if (itemList.size > 0) {
            getRetailerInformation()
            binding.rdAddedItem.visibility = View.VISIBLE
            binding.cardAddedItem.visibility = View.VISIBLE
            val itemAdapter = DoAddedItemAdapter(itemList)
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rdAddedItem.layoutManager = manager
            binding.rdAddedItem.adapter = itemAdapter

            val dividerItemDecoration = DividerItemDecoration(
                binding.rdAddedItem.context,
                manager.orientation
            )
            binding.rdAddedItem.addItemDecoration(dividerItemDecoration)

            itemAdapter.setOnItemClickListener(object :
                DoAddedItemAdapter.OnAdapterItemClickListener {

                override fun onSelectItem(v: View?, position: Int) {
                }
            })

        } else {
            binding.rdAddedItem.visibility = View.GONE
            binding.cardAddedItem.visibility = View.GONE
        }

    }

    private fun getRetailerInformation(){

        val retailer: RetailerChild =convertRetailerInfo(requireActivity())!!
        binding.txtRetailer.text=retailer.partyName.toString()
        binding.txtRetailerN.text=retailer.ownerName.toString()
        binding.txtPAdd.text=retailer.partyAdd1.toString()
        binding.txtPPho.text=retailer.partyMobile.toString()

    }
    private fun convertRetailerInfo(activity: Activity): RetailerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedRetailer", "")
        return gson.fromJson(json, RetailerChild::class.java)
    }


}