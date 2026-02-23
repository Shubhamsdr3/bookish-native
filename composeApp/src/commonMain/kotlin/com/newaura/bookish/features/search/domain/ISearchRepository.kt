package com.newaura.bookish.features.search.domain

import com.newaura.bookish.core.network.ApiResponse
import com.newaura.bookish.features.search.data.model.SearchResultResponse
import kotlinx.coroutines.flow.Flow

interface ISearchRepository {

    suspend fun searchBooks(query: String): Flow<Result<SearchResultResponse>>

}