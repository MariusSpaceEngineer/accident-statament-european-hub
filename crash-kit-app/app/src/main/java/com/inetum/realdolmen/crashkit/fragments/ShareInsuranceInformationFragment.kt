package com.inetum.realdolmen.crashkit.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentShareInsuranceInformationBinding
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.dto.PolicyHolderVehicleBResponse
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class ShareInsuranceInformationFragment : Fragment() {
    private var _binding: FragmentShareInsuranceInformationBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShareInsuranceInformationBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShareInsuranceGenerateQrCode.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiService.getPolicyHolderProfileInformation()
                withContext(Dispatchers.Main) {
                    handlePolicyHolderProfileResponse(response)
                }
            }
        }
    }

    private fun handlePolicyHolderProfileResponse(response: Response<PolicyHolderResponse>) {
        Log.i("Request", "Request code: ${response.code()}")
        if (response.isSuccessful) {
            val personalInformationResponse = response.body()
            if (personalInformationResponse != null) {
                showInsuranceDialog(personalInformationResponse.insuranceCertificates) { selectedCertificate ->
                    // This block of code will be executed after the user has made a selection in the dialog
                    val gson = Gson()
                    val policyHolder = PolicyHolderVehicleBResponse(
                        personalInformationResponse.id,
                        personalInformationResponse.firstName,
                        personalInformationResponse.lastName,
                        personalInformationResponse.email,
                        personalInformationResponse.phoneNumber,
                        personalInformationResponse.address,
                        personalInformationResponse.postalCode,
                        selectedCertificate
                    )
                    val json = gson.toJson(policyHolder)

                    val qrCodeWriter = QRCodeWriter()
                    val width = 1000
                    val height = 1000
                    var bitMatrix: BitMatrix? = null

                    try {
                        val hints: MutableMap<EncodeHintType, Any?> = HashMap()
                        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
                        bitMatrix =
                            qrCodeWriter.encode(json, BarcodeFormat.QR_CODE, width, height, hints)
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }

                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bitmap.setPixel(
                                x,
                                y,
                                if (bitMatrix!![x, y]) Color.BLACK else Color.WHITE
                            )
                        }
                    }
                    binding.ivShareInsuranceQrCode.visibility = View.VISIBLE
                    binding.ivShareInsuranceQrCode.setImageBitmap(bitmap)
                    binding.tvShareInsuranceQrCodeDescription.visibility = View.VISIBLE

                    Toast.makeText(
                        requireContext(),
                        "Import successful",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                val errorMessage = "Error while fetching insurance information"
                requireContext().createSimpleDialog(getString(R.string.error), errorMessage)
            }
        }
    }

    private fun showInsuranceDialog(
        insuranceCertificates: List<InsuranceCertificate>?,
        onCertificateSelected: (InsuranceCertificate?) -> Unit
    ) {
        if (!insuranceCertificates.isNullOrEmpty()) {
            var selectedCertificate: InsuranceCertificate? = null

            val insuranceCertificateStrings =
                insuranceCertificates.map { "Company name: ${it.insuranceCompany?.name}\nAgency name: ${it.insuranceAgency?.name}\nPolicy Number: ${it.policyNumber}" }
                    .toTypedArray()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select an Insurance Certificate")
                .setSingleChoiceItems(insuranceCertificateStrings, -1) { dialog, which ->
                    selectedCertificate = insuranceCertificates[which]
                    dialog.dismiss()

                    // Call the callback with the selected certificate
                    onCertificateSelected(selectedCertificate)
                }
                .show()
        } else {
            onCertificateSelected(null)
        }
    }
}