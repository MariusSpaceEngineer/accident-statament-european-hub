package com.inetum.realdolmen.crashkit.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentShareInsuranceInformationBinding
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.MotorDTO
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.dto.PolicyHolderVehicleBResponse
import com.inetum.realdolmen.crashkit.dto.TrailerDTO
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ShareInsuranceInformationFragment : Fragment() {
    private var _binding: FragmentShareInsuranceInformationBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService
    private val gson = CrashKitApp.gson

    private val qrCodeWidth: Int = 1000
    private val qrCodeHeight: Int = 1000

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
            fetchPolicyHolderInformation()
        }
    }

    private fun fetchPolicyHolderInformation() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getPolicyHolderProfileInformation()
            withContext(Dispatchers.Main) {
                handlePolicyHolderProfileResponse(response)
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
                    val json =
                        convertResponseToJSON(personalInformationResponse, selectedCertificate)

                    Log.i("qr-code data", json.toString())

                    val bitmap = generateQRCode(json, qrCodeWidth, qrCodeHeight)

                    binding.ivShareInsuranceQrCode.visibility = View.VISIBLE
                    binding.ivShareInsuranceQrCode.setImageBitmap(bitmap)
                    binding.tvShareInsuranceQrCodeDescription.visibility = View.VISIBLE

                    requireContext().showToast("Import successful")
                }
            } else {
                requireContext().createSimpleDialog(
                    getString(R.string.error),
                    getString(R.string.error_network)
                )
            }
        }
    }

    private fun convertResponseToJSON(
        personalInformationResponse: PolicyHolderResponse,
        selectedCertificate: InsuranceCertificate?
    ): String? {
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
        return gson.toJson(policyHolder)
    }

    private fun generateQRCode(
        json: String?,
        width: Int,
        height: Int
    ): Bitmap {
        val qrCodeWriter = QRCodeWriter()
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
        return bitmap
    }

    private fun showInsuranceDialog(
        insuranceCertificates: List<InsuranceCertificate>?,
        onCertificateSelected: (InsuranceCertificate?) -> Unit
    ) {
        if (!insuranceCertificates.isNullOrEmpty()) {
            var selectedCertificate: InsuranceCertificate?

            val insuranceCertificateStrings = insuranceCertificates.map { certificate ->
                val vehicle = certificate.vehicle
                val vehicleType = when (vehicle) {
                    is MotorDTO -> "Motor"
                    is TrailerDTO -> "Trailer"
                    else -> "Unknown"
                }
                val markType = if (vehicle is MotorDTO) vehicle.markType else "N/A"

                "Vehicle Type: $vehicleType\n" + "Company name: ${certificate.insuranceCompany?.name}\n" +
                        "Agency name: ${certificate.insuranceAgency?.name}\n" +
                        "Policy Number: ${certificate.policyNumber}\n" +
                        "Mark Type: $markType\n" +
                        "License Plate: ${vehicle?.licensePlate}\n" +
                        "Country of Registration: ${vehicle?.countryOfRegistration}\n"
            }.toMutableList()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select an Insurance Certificate")
                .setSingleChoiceItems(
                    insuranceCertificateStrings.toTypedArray(),
                    -1) { dialog, which ->
                    selectedCertificate = insuranceCertificates[which]

                    // Call the callback with the selected certificate
                    onCertificateSelected(selectedCertificate)
                    dialog.dismiss()

                }
                .show()
        } else {
            onCertificateSelected(null)
        }
    }
}