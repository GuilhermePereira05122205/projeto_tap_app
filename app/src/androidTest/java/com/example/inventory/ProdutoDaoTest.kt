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

package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.ProdutosDatabase
import com.example.inventory.data.Produto
import com.example.inventory.data.ProdutoDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProdutoDaoTest {

    private lateinit var produtoDao: ProdutoDao
    private lateinit var produtosDatabase: ProdutosDatabase
    private val produto1 = Produto(1, "Apples", 10.0, "20", weight = 20.0, quantity = 20)
    private val produto2 = Produto(2, "Bananas", 15.0, "97", weight = 20.0, quantity = 20)

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        produtosDatabase = Room.inMemoryDatabaseBuilder(context, ProdutosDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        produtoDao = produtosDatabase.ProdutoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        produtosDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems = produtoDao.getAllItems().first()
        assertEquals(allItems[0], produto1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = produtoDao.getAllItems().first()
        assertEquals(allItems[0], produto1)
        assertEquals(allItems[1], produto2)
    }


    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemFromDB() = runBlocking {
        addOneItemToDb()
        val item = produtoDao.getItem(1)
        assertEquals(item.first(), produto1)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteItems_deletesAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        produtoDao.delete(produto1)
        produtoDao.delete(produto2)
        val allItems = produtoDao.getAllItems().first()
        assertTrue(allItems.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        addTwoItemsToDb()
        produtoDao.update(Produto(1, "Apples", 15.0, "25", weight = 20.0, quantity = 20))
        produtoDao.update(Produto(2, "Bananas", 5.0, "50", weight = 20.0, quantity = 20))

        val allItems = produtoDao.getAllItems().first()
        assertEquals(allItems[0], Produto(1, "Apples", 15.0, "25", weight = 10.0, quantity = 20))
        assertEquals(allItems[1], Produto(2, "Bananas", 5.0, "50", weight = 10.0, quantity = 20))
    }

    private suspend fun addOneItemToDb() {
        produtoDao.insert(produto1)
    }

    private suspend fun addTwoItemsToDb() {
        produtoDao.insert(produto1)
        produtoDao.insert(produto2)
    }
}
