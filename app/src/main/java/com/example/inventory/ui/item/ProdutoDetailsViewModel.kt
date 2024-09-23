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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ProdutosRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve, update and delete an item from the [ProdutosRepository]'s data source.
 */
class ProdutoDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val produtosRepository: ProdutosRepository,
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[ProdutoDetailsDestination.produtoIdArg])

    /**
     * Holds the item details ui state. The data is retrieved from [ProdutosRepository] and mapped to
     * the UI state.
     */
    val uiState: StateFlow<ProdutoDetailsUiState> =
        produtosRepository.getProdutoStream(itemId)
            .filterNotNull()
            .map {
                ProdutoDetailsUiState(outOfStock = it.quantity <= 0, produtoDetails = it.toProdutoDetails())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProdutoDetailsUiState()
            )
    /**
     * Reduces the item quantity by one and update the [ProdutosRepository]'s data source.
     */
    fun reduceQuantityByOne() {
        viewModelScope.launch {
            val currentItem = uiState.value.produtoDetails.toProduto()
            if (currentItem.quantity > 0) {
                produtosRepository.updateProduto(currentItem.copy(quantity = currentItem.quantity - 1))
            }
        }
    }

    /**
     * Deletes the item from the [ProdutosRepository]'s data source.
     */
    suspend fun deleteProduto() {
        produtosRepository.deleteProduto(uiState.value.produtoDetails.toProduto())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for ItemDetailsScreen
 */
data class ProdutoDetailsUiState(
    val outOfStock: Boolean = true,
    val produtoDetails: ProdutoDetails = ProdutoDetails()
)
