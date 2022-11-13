/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** menentukan harga untuk 1 bakpia */
private const val PRICE_PER_CUPCAKE = 35000.00

/** tambahan harga untuk pesanan di hari yang sama */
private const val PRICE_FOR_SAME_DAY_PICKUP = 5000.00

/**
 * [OrderViewModel] digunakan untuk menyimpan data pesanan seperti jumlah, rasa, dan tanggal pengambilan bakpia.
 * juga digunakan untuk menghitung total harga berdasarkan rincian pesanan.
 */
class OrderViewModel : ViewModel() {

    // banyaknya bakpia yang di pesan
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    // Rasa bakpia yang dipilih
    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    // opsi tanggal yang bisa dipilih
    val dateOptions: List<String> = getPickupOptions()

    // Data tanggal pengambilan
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    // total harga dari pesanan
    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        // Format the price into the local currency and return this as LiveData<String>
        NumberFormat.getCurrencyInstance().format(it)
    }

    init {
        // Set initial values for the order
        resetOrder()
    }

    /**
     * mengatur jumlah order untuk pesanan.
     *
     * @param numberCupcakes to order
     */
    fun setQuantity(numberCupcakes: Int) {
        _quantity.value = numberCupcakes
        updatePrice()
    }

    /**
     * mengatur rasa yang dapat dipilih untuk pesanan, hanya satu rasa yang dapat dipilih untuk satu pesanan.
     *
     * @param desiredFlavor is the cupcake flavor as a string
     */
    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }

    /**
     * mengatur tanggal pengambilan untuk pesanan.
     *
     * @param pickupDate is the date for pickup as a string
     */
    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    /**
     * Returns true jika pilihan untuk rasa belum di pilih.
     */
    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    /**
     * Reset the order by using initial default values for the quantity, flavor, date, and price.
     */
    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    /**
     * update harga berdasarkan detail pesanan.
     */
    private fun updatePrice() {
        var calculatedPrice = (quantity.value ?: 0) * PRICE_PER_CUPCAKE
        // If the user selected the first option (today) for pickup, add the surcharge
        if (dateOptions[0] == _date.value) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    /**
     * Returns daftar tanggal pengambilan yang bisa dipilih, akan muncul 3 daftar tanggal pengambilan.
     */
    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }
}