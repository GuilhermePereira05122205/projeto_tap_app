/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Produto
import com.example.inventory.data.ProdutosRepository
import java.text.NumberFormat


class ProdutoEntryViewModel(private val produtosRepository: ProdutosRepository) : ViewModel() {


    var produtoUiState by mutableStateOf(ProdutoUiState())
        private set


    fun updateUiState(produtoDetails: ProdutoDetails) {
        produtoUiState =
            ProdutoUiState(produtoDetails = produtoDetails, isEntryValid = validateInput(produtoDetails))
    }


    suspend fun saveProduto() {
        if (validateInput()) {
            produtosRepository.insertProduto(produtoUiState.produtoDetails.toProduto())
        }
    }

    private fun validateInput(uiState: ProdutoDetails = produtoUiState.produtoDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
        }
    }
}

data class ProdutoUiState(
    val produtoDetails: ProdutoDetails = ProdutoDetails(),
    val isEntryValid: Boolean = false
)

data class ProdutoDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val description:String = "",
    val weight: String = "",
    val quantity: String = ""
)


fun ProdutoDetails.toProduto(): Produto = Produto(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    description = description,
    weight = weight.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0
)

fun Produto.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [Produto] to [ProdutoUiState]
 */
fun Produto.toProdutoUiState(isEntryValid: Boolean = false): ProdutoUiState = ProdutoUiState(
    produtoDetails = this.toProdutoDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Produto] to [ProdutoDetails]
 */
fun Produto.toProdutoDetails(): ProdutoDetails = ProdutoDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString(),
    description = description,
    weight = weight.toString(),

)
