package dev.lizainslie.cafemc.chat

import kotlin.test.Test
import kotlin.test.assertEquals


class ValidateNicknameTests {
    @Test
    fun `validation should fail when nickname is greater than 16 chars and includes colors`() {
        val invalidNickname = ChatUtil.translateAmpersand("&00&11&22&33&44&55&66&77&88&99&aa&bb&cc*dd&ee&ff&lFUCK")

        assertEquals(false, validateNickname(invalidNickname))
    }

    @Test
    fun `validation should fail when nickname is greater than 16 characters`() {
        val invalidNickname = ChatUtil.translateAmpersand("0123456789abcdefFUCK")

        assertEquals(false, validateNickname(invalidNickname))
    }

    @Test
    fun `validation should succeed when nickname is 16 characters and includes colors`() {
        val validNickname = ChatUtil.translateAmpersand("&00&11&22&33&44&55&66&77&88&99&aa&bb&cc&dd&ee&ff")

        assertEquals(true, validateNickname(validNickname))
    }

    @Test
    fun `validation should succeed when nickname is 16 characters`() {
        val validNickname = ChatUtil.translateAmpersand("0123456789abcdef")

        assertEquals(true, validateNickname(validNickname))
    }

    @Test
    fun `validation should succeed when nickname is less than 16 characters and includes colors`() {
        val validNickname = ChatUtil.translateAmpersand("&cM&4e&cy")

        assertEquals(true, validateNickname(validNickname))
    }

    @Test
    fun `validation should succeed when nickname is less than 16 characters`() {
        val validNickname = ChatUtil.translateAmpersand("Mey")

        assertEquals(true, validateNickname(validNickname))
    }
}