package com.inetum.realdolmen.crashkit.services

import com.inetum.realdolmen.crashkit.dto.AccidentStatementData
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.LoginData
import com.inetum.realdolmen.crashkit.dto.LoginResponse
import com.inetum.realdolmen.crashkit.dto.PersonalInformationData
import com.inetum.realdolmen.crashkit.dto.PolicyHolderPersonalInformationResponse
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.dto.RegisterData
import com.inetum.realdolmen.crashkit.dto.RegisterResponse
import com.inetum.realdolmen.crashkit.dto.RequestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body registerData: RegisterData): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body loginData: LoginData): Response<LoginResponse>

    @GET("user/profile")
    suspend fun getPolicyHolderProfileInformation(): Response<PolicyHolderResponse>

    @PUT("user/profile/personal")
    suspend fun updatePolicyHolderPersonalInformation(@Body personalInformationData: PersonalInformationData): Response<PolicyHolderPersonalInformationResponse>

    @PUT("user/profile/insurance")
    suspend fun updateInsuranceCertificateInformation(@Body insuranceCertificate: InsuranceCertificate): Response<InsuranceCertificate>

    @POST("statement/create")
    suspend fun createAccidentStatement(@Body accidentStatementData: AccidentStatementData): Response<RequestResponse>

}