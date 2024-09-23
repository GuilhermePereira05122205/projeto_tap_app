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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ProdutosRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [ProdutosRepository]'s data source.
 */
class ProdutoEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val produtosRepository: ProdutosRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var produtoUiState by mutableStateOf(ProdutoUiState())
        private set

    private val ProdutoId: Int = checkNotNull(savedStateHandle[ProdutoEditDestination.produtoIdArg])

    init {
        viewModelScope.launch {
            produtoUiState = produtosRepository.getProdutoStream(ProdutoId)
                .filterNotNull()
                .first()
                .toProdutoUiState(true)
        }
    }

    /**
     * Update the item in the [ProdutosRepository]'s data source
     */
    suspend fun produtoItem() {
        if (validateInput(produtoUiState.produtoDetails)) {
            produtosRepository.updateProduto(produtoUiState.produtoDetails.toProduto())
        }
    }

    /**
     * Updates the [produtoUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(produtoDetails: ProdutoDetails) {
        produtoUiState =
            ProdutoUiState(produtoDetails = produtoDetails, isEntryValid = validateInput(produtoDetails))
    }

    private fun validateInput(uiState: ProdutoDetails = produtoUiState.produtoDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
        }
    }
}
