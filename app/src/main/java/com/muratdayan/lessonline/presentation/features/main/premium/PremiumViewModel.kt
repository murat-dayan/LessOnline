package com.muratdayan.lessonline.presentation.features.main.premium

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetailsParams
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    @ApplicationContext private val context:Context
) : ViewModel(){

    private val _purchaseCompleted = MutableStateFlow(false)
    val purchaseCompleted: StateFlow<Boolean> get() = _purchaseCompleted

    private lateinit var billingClient: BillingClient

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient(){
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null){
                    handlePurchases(purchases)
                }
            }
            .build()

        billingClient.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                // Billing client connection failed
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                    // Billing client connected successfully
                }
            }
        })
    }

    fun initiatePurchase(){
        val skuDetailParams = SkuDetailsParams.newBuilder()
            .setSkusList(listOf("premium_feature_id"))
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(skuDetailParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()){
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList[0])
                    .build()
                billingClient.launchBillingFlow(context as Activity,flowParams)
            }
        }
    }

    private  fun handlePurchases(purchases:List<Purchase>){
        for (purchase in purchases){
            if (purchase.purchaseState  == Purchase.PurchaseState.PURCHASED){
                if (!purchase.isAcknowledged){
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams){billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                            _purchaseCompleted.value = true
                        }
                    }
                }
            }
        }
    }


}