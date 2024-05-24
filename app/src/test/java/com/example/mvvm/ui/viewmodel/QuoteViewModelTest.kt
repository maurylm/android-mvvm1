package com.example.mvvm.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mvvm.domain.GetQuotesUseCase
import com.example.mvvm.domain.GetRandomQuoteUseCase
import com.example.mvvm.domain.model.Quote
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class QuoteViewModelTest {

    @RelaxedMockK
    private lateinit var getQuotesUseCase: GetQuotesUseCase

    @RelaxedMockK
    private lateinit var getRandomQuoteUseCase: GetRandomQuoteUseCase

    private lateinit var quoteViewModel: QuoteViewModel

    @get:Rule
    var rule:InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        quoteViewModel = QuoteViewModel(getQuotesUseCase, getRandomQuoteUseCase)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewmodel is created at the first time get all quotes and set the first value`() =
        runTest {
            // Given
            val quoteList = listOf(Quote("Que onda", "Maury"), Quote("Que mira bobo", "Messi"))
            coEvery { getQuotesUseCase() } returns quoteList

            // When
            quoteViewModel.onCreate()

            // Then
            assert(quoteViewModel.quoteModel.value == quoteList.first())
        }

    @Test
    fun `when randomQuoteUseCase return a quote set on the livedata`() =
        runTest {
        // Given
        val quote = Quote("Que hubole", "Pedro")
        coEvery { getRandomQuoteUseCase() } returns quote

        // When
        quoteViewModel.getRandomQuote()

        // Then
        assert(quoteViewModel.quoteModel.value == quote)
    }

    @Test
    fun `if randomQuoteUseCase return null keep the last value`() =
        runTest {
            // Given
            val quote = Quote("Adios", "Juan")
            quoteViewModel.quoteModel.value = quote
            coEvery { getRandomQuoteUseCase() } returns null

            // When
            quoteViewModel.getRandomQuote()

            // Then
            assert(quoteViewModel.quoteModel.value == quote)
        }
}