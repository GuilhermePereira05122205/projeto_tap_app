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

package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

class OfflineProdutoRepository(private val produtoDao: ProdutoDao) : ProdutosRepository {
    override fun getAllProdutoStream(): Flow<List<Produto>> = produtoDao.getAllItems()

    override fun getProdutoStream(id: Int): Flow<Produto?> = produtoDao.getItem(id)

    override suspend fun insertProduto(produto: Produto) = produtoDao.insert(produto)

    override suspend fun deleteProduto(produto: Produto) = produtoDao.delete(produto)

    override suspend fun updateProduto(produto: Produto) = produtoDao.update(produto)
}
