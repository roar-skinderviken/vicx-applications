package no.vicx.esport

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.vicx.esport.vm.EsportMatchVm
import no.vicx.esport.vm.MatchType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EsportServiceTest {
    private val esportClient: EsportClient = mockk()
    private val sut = EsportService(esportClient)

    @Test
    fun `given data on remote server then expect result with both running and upcoming matches`() {
        val expectedRunningMatches = createMatches(MatchType.running)
        val expectedUpcomingMatches = createMatches(MatchType.upcoming)

        coEvery { esportClient.getMatches(MatchType.running) } returns expectedRunningMatches
        coEvery { esportClient.getMatches(MatchType.upcoming) } returns expectedUpcomingMatches

        val result = runBlocking { sut.getMatches() }

        assertEquals(expectedRunningMatches, result.runningMatches)
        assertEquals(expectedUpcomingMatches, result.upcomingMatches)
    }

    @Test
    fun `given cached data then expect no further calls to esportClient`() {
        coEvery { esportClient.getMatches(MatchType.running) } returns createMatches(MatchType.running)
        coEvery { esportClient.getMatches(MatchType.upcoming) } returns createMatches(MatchType.upcoming)

        runBlocking { sut.getMatches() }
        runBlocking { sut.getMatches() }

        coVerify(exactly = 1) { esportClient.getMatches(MatchType.running) }
        coVerify(exactly = 1) { esportClient.getMatches(MatchType.upcoming) }
    }

    companion object {
        fun createMatches(matchType: MatchType): List<EsportMatchVm> = listOf(
            EsportMatchVm(1, "~match-1~", "beginAt", matchType.toString())
        )
    }
}