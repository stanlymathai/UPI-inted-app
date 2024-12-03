package com.example.upiintentapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    // Variable to store transaction response for UI
    private var transactionResponse by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(
                transactionResponse = transactionResponse,
                onPayNowClicked = { initiateUPIPayment() }
            )
        }
    }

    private fun initiateUPIPayment() {
        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
//            .appendQueryParameter("pa", "Motilal2005@hdfcbank") // Payee UPI ID
//            .appendQueryParameter("pa", "ixigo.abhibus@axisbank") // Payee UPI ID
//            .appendQueryParameter("pa", "upiswiggy@icici") // Payee UPI ID
//            .appendQueryParameter("pa", "bookmyshow@axb") // Payee UPI ID
//            .appendQueryParameter("pa", "upiswiggy@icici") // Payee UPI ID

//            .appendQueryParameter("pa", "paytmqr2810050501011af4h254hwzp@paytm") // Payee UPI ID
            .appendQueryParameter("pa", "palathrab123@fbl") // Payee UPI ID
            .appendQueryParameter("mc", "5621") // Payee UPI ID

//            .appendQueryParameter("mc", "siyascs612@oksbi") // Payee UPI ID
            .appendQueryParameter("tr", "leti345rtel") // Payee UPI ID

            .appendQueryParameter("pn", "PALATHRA FASHION BOUTIQUE") // Payee Name
            .appendQueryParameter("tn", "Test Transaction") // Transaction Note
            .appendQueryParameter("am", "1.00") // Amount
            .appendQueryParameter("cu", "INR") // Currency
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, UPI_PAYMENT_REQUEST)
        } else {
            Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPI_PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                val response = data.getStringExtra("response")
                Log.d("UPIIntent", "UPI Response: $response")
                response?.let {
                    transactionResponse = parseAndHandleResponse(it)
                } ?: run {
                    transactionResponse = "No response received"
                }
            } else {
                transactionResponse = "Transaction Cancelled or Failed"
            }
        }
    }

    private fun parseAndHandleResponse(response: String): String {
        val transactionDetails = response.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        return when (transactionDetails["Status"]?.uppercase()) {
            "SUCCESS" -> "Transaction Successful\nRef ID: ${transactionDetails["txnId"] ?: "N/A"}"
            "FAILURE" -> "Transaction Failed"
            "PENDING", "SUBMITTED" -> "Transaction Pending"
            else -> "Unknown Transaction Status"
        }
    }

    companion object {
        private const val UPI_PAYMENT_REQUEST = 1
    }
}

@Composable
fun MyApp(transactionResponse: String, onPayNowClicked: () -> Unit) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to UPI Intent App!",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = onPayNowClicked) {
                Text("Pay Now")
            }
            if (transactionResponse.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = transactionResponse,
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Blue
                )
            }
        }
    }
}
